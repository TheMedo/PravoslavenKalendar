package com.medo.pravoslavenkalendar.model;

public class OrthodoxHoliday {

  // the name of the holiday / saint
  private String name;
  // the priority of the holiday (the color of the letters in the calendar)
  private String color;
  // the description url from http://www.crkvenikalendar.com/index_mk.php
  private String descriptionUrl;

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getColor() {

    return color;
  }

  public void setColor(String color) {

    this.color = color;
  }

  public String getDescriptionUrl() {

    return descriptionUrl;
  }

  public void setDescriptionUrl(String descriptionUrl) {

    this.descriptionUrl = descriptionUrl;
  }
}
