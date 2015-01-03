package com.medo.pravoslavenkalendar.transforms;

/**
 * Information to make the parallax effect in a concrete view.
 * <p/>
 * parallaxEffect positive values reduces the speed of the view in the translation
 * ParallaxEffect negative values increase the speed of the view in the translation
 * Try values to see the different effects. I recommend 2, 0.75 and 0.5
 */
public class ParallaxTransformInformation {

  public static final float PARALLAX_EFFECT_DEFAULT = -101.1986f;

  int resource = -1;
  float parallaxEnterEffect = 1f;
  float parallaxExitEffect = 1f;

  public ParallaxTransformInformation(int resource, float parallaxEnterEffect, float parallaxExitEffect) {

    this.resource = resource;
    this.parallaxEnterEffect = parallaxEnterEffect;
    this.parallaxExitEffect = parallaxExitEffect;
  }

  public boolean isValid() {

    return parallaxEnterEffect != 0 && parallaxExitEffect != 0 && resource != -1;
  }

  public boolean isEnterDefault() {

    return parallaxEnterEffect == PARALLAX_EFFECT_DEFAULT;
  }

  public boolean isExitDefault() {

    return parallaxExitEffect == PARALLAX_EFFECT_DEFAULT;
  }
}