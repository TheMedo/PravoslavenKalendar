package com.medo.pravoslavenkalendar.model;


import java.util.List;


public class OrthodoxDay {

  // numeric value for the day of year 1 - 365
  private int dayOfYear;
  // is this a national holiday i.e. non working day
  // leave null for normal day, specify the name if a national holiday
  private String nationalHoliday;
  // link to the image (leave empty for the time being)
  private String imageUrl;
  // list of holidays for the current day
  private List<OrthodoxHoliday> holidays;
  // the list of available foods (fish, oil, dairy, water)
  // with any combination of them or null if a normal day
  private List<String> fastingFoods;

  public int getDayOfYear() {

    return dayOfYear;
  }

  public void setDayOfYear(int dayOfYear) {

    this.dayOfYear = dayOfYear;
  }

  public String getNationalHoliday() {

    return nationalHoliday;
  }

  public void setNationalHoliday(String nationalHoliday) {

    this.nationalHoliday = nationalHoliday;
  }

  public String getImageUrl() {

    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {

    this.imageUrl = imageUrl;
  }

  public List<OrthodoxHoliday> getHolidays() {

    return holidays;
  }

  public void setHolidays(List<OrthodoxHoliday> holidays) {

    this.holidays = holidays;
  }

  public List<String> getFastingFoods() {

    return fastingFoods;
  }

  public void setFastingFoods(List<String> fastingFoods) {

    this.fastingFoods = fastingFoods;
  }
}
