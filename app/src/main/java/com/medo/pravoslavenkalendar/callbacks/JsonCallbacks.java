package com.medo.pravoslavenkalendar.callbacks;


import com.medo.pravoslavenkalendar.model.OrthodoxDay;

import java.util.List;


public interface JsonCallbacks {

  public void onCalendarReady(List<OrthodoxDay> orthodoxDays);

  public void onError(Exception ex);
}
