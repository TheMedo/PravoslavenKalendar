package com.medo.pravoslavenkalendar.asyncs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.medo.pravoslavenkalendar.callbacks.BitmapCallback;
import com.medo.pravoslavenkalendar.utils.BitmapUtils;
import com.medo.pravoslavenkalendar.utils.FileUtils;

import java.io.File;


public class BlurAndSaveBitmapTask extends AsyncTask<Bitmap, Void, File> {

  private final Context context;
  private final BitmapCallback callback;
  private final int dayOfYear;

  public BlurAndSaveBitmapTask(Context context, BitmapCallback callback, int dayOfYear) {

    this.context = context;
    this.callback = callback;
    this.dayOfYear = dayOfYear;
  }

  @Override
  protected File doInBackground(Bitmap... params) {

    // cache the normal image on the SD
    File normalOutputFile = FileUtils.getOutputPictureFile(context, dayOfYear, false);
    if (normalOutputFile == null) {
      return null;
    }
    if (!normalOutputFile.exists()) {
      BitmapUtils.persistImage(params[0], normalOutputFile);
    }

    // cache the normal image on the SD
    File blurredOutputFile = FileUtils.getOutputPictureFile(context, dayOfYear, true);
    if (!blurredOutputFile.exists()) {
      // create another bitmap that will hold the results of the filter.
      Bitmap blurredBitmap = Bitmap.createBitmap(params[0].getWidth(), params[0].getHeight(), Bitmap.Config.ARGB_8888);

      // create the Renderscript instance that will do the work.
      RenderScript renderScript = RenderScript.create(context);

      // allocate memory for Renderscript to work with
      Allocation input = Allocation.createFromBitmap(renderScript, params[0], Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);
      Allocation output = Allocation.createTyped(renderScript, input.getType());

      // load up an instance of the specific script that we want to use.
      ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
      script.setInput(input);
      // set the blur radius
      script.setRadius(25);
      // start the ScriptIntrinsicBlur
      script.forEach(output);
      // copy the output to the blurred bitmap
      output.copyTo(blurredBitmap);

      BitmapUtils.persistImage(blurredBitmap, blurredOutputFile);
    }

    return blurredOutputFile;
  }

  @Override
  protected void onPostExecute(File blurredOutputFile) {

    // return the loaded bitmap
    if (blurredOutputFile != null) {
      callback.onSuccess(blurredOutputFile);
    }
    else {
      callback.onFailure();
    }
    super.onPostExecute(blurredOutputFile);
  }
}

