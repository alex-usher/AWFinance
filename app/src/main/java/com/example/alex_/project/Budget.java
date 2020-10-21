package com.example.alex_.project;

import java.sql.Timestamp;

public class Budget {

  private int bID; // budget id
  private String bName; // string name given to budget
  private float bAmount; // total amount for the budget
  private BudgetType bType; // 1-daily, 2-weekly, 3-monthly, 4-yearly
  private Timestamp bDateCreated; // the date the budget was created
  private String bColour; // the colour used to display the budget, stored as a hex value

  public Budget(int id, String name, float amount, BudgetType type, Timestamp dateCreated, String colour) {
    bID = id;
    bName = name;
    bAmount = amount;
    bType = type;
    bDateCreated = dateCreated;
    bColour = colour;
  }

  public Budget(String name, float amount, BudgetType type, Timestamp dateCreated, String colour) {
    this.bName = name;
    this.bAmount = amount;
    this.bType = type;
    this.bDateCreated = dateCreated;
    this.bColour = colour;
  }

  public int getID() {
    return bID;
  }

  public String getName() {
    return bName;
  }

  public void setName(String name) {
    bName = name;
  }

  public float getAmount() {
    return bAmount;
  }

  public BudgetType getType() {
    return bType;
  }

  public void setType(BudgetType type) {
    bType = type;
  }

  public float getSpent(DBHelper helper) {
    return helper.getAmountSpent(this);
  }

  public Timestamp getDateCreated() {
    return bDateCreated;
  }

  public String getColour() {
    return bColour;
  }
}
