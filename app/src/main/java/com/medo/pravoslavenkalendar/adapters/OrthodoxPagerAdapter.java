package com.medo.pravoslavenkalendar.adapters;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.medo.pravoslavenkalendar.compat.OrthodoxFragmentCompat;
import com.medo.pravoslavenkalendar.fragments.OrthodoxFragment;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.utils.SystemUtils;

import java.util.List;


// we use fragment state pager adapter to make the view pager act like a horizontal list view
// fragments that are not visible may be destroyed to conserve memory
public class OrthodoxPagerAdapter extends FragmentStatePagerAdapter {

  private final List<OrthodoxDay> orthodoxDays;
  private final Gson gson;

  public OrthodoxPagerAdapter(FragmentManager fm, List<OrthodoxDay> orthodoxDays) {

    super(fm);
    // initialize the adapter with the needed variables
    this.orthodoxDays = orthodoxDays;
    this.gson = new Gson();
  }

  @Override
  public Fragment getItem(int position) {
    // getItem is called to instantiate the fragment for the given page
    // return a new instance of the OrthodoxFragment
    if (SystemUtils.versionAtLeast(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
      return OrthodoxFragment.newInstance(gson.toJson(orthodoxDays.get(position), OrthodoxDay.class));
    }
    else {
      return OrthodoxFragmentCompat.newInstance(gson.toJson(orthodoxDays.get(position), OrthodoxDay.class));
    }
  }

  @Override
  public int getCount() {
    // the number of pages should represent the number of days we have
    return orthodoxDays.size();
  }
}
