package com.medo.pravoslavenkalendar.asyncs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.medo.pravoslavenkalendar.callbacks.BitmapCallback;


public class BlurBitmapTask extends AsyncTask<Bitmap, Void, Bitmap> {

  private final Context context;
  private final BitmapCallback callback;

  public BlurBitmapTask(Context context, BitmapCallback callback) {

    this.context = context;
    this.callback = callback;
  }

  @Override
  protected Bitmap doInBackground(Bitmap... params) {

    // create another bitmap that will hold the results of the filter.
    Bitmap blurredBitmap = Bitmap.createBitmap(params[0]);

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

    return blurredBitmap;
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {

    // return the loaded bitmap
    if (bitmap != null) {
      callback.onSuccess(bitmap);
    }
    else {
      callback.onFailure();
    }
    super.onPostExecute(bitmap);
  }
}

