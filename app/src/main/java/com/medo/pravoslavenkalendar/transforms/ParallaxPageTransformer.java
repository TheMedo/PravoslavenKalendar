package com.medo.pravoslavenkalendar.transforms;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class ParallaxPageTransformer implements ViewPager.PageTransformer {

  private List<ParallaxTransformInformation> mViewsToParallax = new ArrayList<>();

  public ParallaxPageTransformer() {

  }

  public ParallaxPageTransformer(List<ParallaxTransformInformation> viewsToParallax) {

    mViewsToParallax = viewsToParallax;
  }

  public ParallaxPageTransformer addViewToParallax(ParallaxTransformInformation viewInfo) {

    if (mViewsToParallax != null) {
      mViewsToParallax.add(viewInfo);
    }
    return this;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void transformPage(View view, float position) {

    int pageWidth = view.getWidth();

    if (position < -1) {
      // This page is way off-screen to the left.
      view.setAlpha(1);

    }
    else if (position <= 1 && mViewsToParallax != null) { // [-1,1]
      for (ParallaxTransformInformation parallaxTransformInformation : mViewsToParallax) {
        applyParallaxEffect(view, position, pageWidth, parallaxTransformInformation, position > 0);
      }
    }
    else {
      // This page is way off-screen to the right.
      view.setAlpha(1);
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void applyParallaxEffect(View view, float position, int pageWidth, ParallaxTransformInformation information, boolean isEnter) {

    if (information.isValid() && view.findViewById(information.resource) != null) {
      if (isEnter && !information.isEnterDefault()) {
        view.findViewById(information.resource).setTranslationX(-position * (pageWidth / information.parallaxEnterEffect));
      }
      else if (!isEnter && !information.isExitDefault()) {
        view.findViewById(information.resource).setTranslationX(-position * (pageWidth / information.parallaxExitEffect));
      }
    }
  }
}
