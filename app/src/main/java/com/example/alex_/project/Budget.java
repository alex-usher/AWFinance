package com.example.alex_.project;

import java.sql.Timestamp;

public class Budget {

  private int bID;
  private String bName;
  private float bAmount;
  //    private float bSpent;
  private BudgetType bType; //1-daily, 2-weekly, 3-monthly, 4-yearly
  private Timestamp bDateCreated;
  private String bColour;

  public Budget() {
  }

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

  public void setID(int id) {
    bID = id;
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

  public void setAmount(float amount) {
    bAmount = amount;
  }

//    public float getSpent(){
//        return bSpent;
//    }

//    public void setSpent(float spent){
//        bSpent = spent;
//    }

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

  public void setDateCreated(Timestamp dateCreated) {
    bDateCreated = dateCreated;
  }

  public String getColour() {
    return bColour;
  }

  public void setColour(String colour) {
    bColour = colour;
  }

  public static int typeToInt(String typeStr) {
    int typeInt = 1;
    switch (typeStr) {
      case "Daily":
        typeInt = 0;
        break;
      case "Monthly":
        typeInt = 2;
        break;
      case "Yearly":
        typeInt = 3;
        break;
      default:
        break;
    } //converting type as string to corresponding integer value

    return typeInt;
  }
}
