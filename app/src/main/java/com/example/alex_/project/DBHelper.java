package com.example.alex_.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBHelper extends SQLiteOpenHelper {
  private static final int DB_VERSION = 1;
  private static final String DATABASE_NAME = "Finance";
  private static final String TABLE_TRANSACT = "Transact";
  private static final String TABLE_BUDGET = "Budget";
  public static final String DATE_FORMAT_STORED = "yyyy-MM-dd";
  public static final String DATE_FORMAT_DISPLAYED = "dd/MM/yyyy";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DB_VERSION);
  }

  public void onCreate(SQLiteDatabase database) {
    String createTransaction =
        "CREATE TABLE "
            + TABLE_TRANSACT
            + "(TransactionID INTEGER PRIMARY KEY AUTOINCREMENT, PaidTo VARCHAR(20), Value DECIMAL NOT NULL DEFAULT '0.0', "
            + "Description VARCHAR(50), TransactionDate DATE, DateCreated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "Budget INT NOT NULL DEFAULT '0')";
    String createBudget =
        "CREATE TABLE "
            + TABLE_BUDGET
            + "(BudgetID INTEGER PRIMARY KEY AUTOINCREMENT, BudgetName VARCHAR(25), "
            + "BudgetAmount DECIMAL NOT NULL DEFAULT '0.0', "
            + "BudgetType INT NOT NULL DEFAULT '1', DateCreated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "Colour VARCHAR(9))";
    database.execSQL(createTransaction);
    database.execSQL(createBudget);
  }

  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    database.execSQL("DROP TABLE IF EXISTS Transact");
    database.execSQL("DROP TABLE IF EXISTS Budget");
    onCreate(database);
  }

  public Boolean testConnection() {
    SQLiteDatabase db = this.getReadableDatabase();
    if (db != null) {
      db.close();
      return true;
    }

    return false;
  }

  /**
   * Takes a transaction and adds it to the database
   *
   * @param transaction - the transaction to add to the database
   */
  public void addTransaction(Transaction transaction) {
    SQLiteDatabase db = this.getWritableDatabase(); // establish db connection

    // set up values to insert to db using instance of transaction class
    ContentValues values = new ContentValues();
    values.put("PaidTo", transaction.getPaidTo());
    values.put("Value", transaction.getValue());
    values.put("Description", transaction.getDesc());
    values.put(
        "TransactionDate",
        transaction.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)));
    values.put("DateCreated", transaction.getDateCreated().toString());
    values.put("Budget", transaction.getBudget());

    // insert row into db
    db.insert(TABLE_TRANSACT, null, values);

    // close db connection
    db.close();
  }

  /**
   * Takes a transaction and updates it within the database. Undefined behaviour if the transaction
   * doesn't exist already in the database
   *
   * @param transaction - the transaction to update
   */
  public void updateTransaction(Transaction transaction) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("PaidTo", transaction.getPaidTo());
    values.put("Value", transaction.getValue());
    values.put("Description", transaction.getDesc());
    values.put(
        "TransactionDate",
        transaction.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)));
    values.put("DateCreated", transaction.getDateCreated().toString());
    values.put("Budget", transaction.getBudget());

    db.update(
        TABLE_TRANSACT,
        values,
        "TransactionID=?",
        new String[] {String.valueOf(transaction.getID())});
    db.close();
  }

  /**
   * Gets all the transactions in the database
   *
   * @return - all transactions from the database, stored in a HashSet
   */
  public Set<Transaction> getAllTransactions() {
    Set<Transaction> transactionList = new HashSet<>();

    String query = "SELECT * FROM " + TABLE_TRANSACT + " ORDER BY TransactionDate DESC";

    try {
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor cursor = db.rawQuery(query, null);

      if (cursor.moveToFirst()) {
        do {
          transactionList.add(readTransaction(cursor));
        } while (cursor.moveToNext());
      }

      cursor.close();
      db.close();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    return transactionList;
  }

  /**
   * Gets all transactions contained in the given budget from the database. Undefined behaviour for
   * the given budget being null
   *
   * @param budget - the budget to find the corresponding transactions for
   * @return - a HashSet containing all transactions for the given budget.
   */
  public Set<Transaction> getAllTransactionsInBudget(Budget budget) {
    Set<Transaction> transactions = new HashSet<>();

    try {
      SQLiteDatabase db = this.getReadableDatabase();

      Cursor cursor =
          db.query(
              TABLE_TRANSACT,
              new String[] {
                "TransactionID",
                "PaidTo",
                "Value",
                "Description",
                "TransactionDate",
                "DateCreated",
                "Budget"
              },
              "Budget=?",
              new String[] {String.valueOf(budget.getID())},
              null,
              null,
              "TransactionDate DESC",
              null);

      if (cursor.moveToFirst()) {
        do {
          transactions.add(readTransaction(cursor));
        } while (cursor.moveToNext());
      }

      cursor.close();
      db.close();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    return transactions;
  }

  // a helper method to reduce duplication in reading transactions from the database
  private Transaction readTransaction(Cursor cursor) {
    // get the values from the cursor
    int id = Integer.parseInt(cursor.getString(0));
    String paidTo = cursor.getString(1);
    float value = Float.parseFloat(cursor.getString(2));
    String desc = cursor.getString(3);
    LocalDate date =
        LocalDate.parse(cursor.getString(4), DateTimeFormatter.ofPattern(DATE_FORMAT_STORED));
    Timestamp dateCreated = Timestamp.valueOf(cursor.getString(5));
    int budgetId = Integer.parseInt(cursor.getString(6));

    return new Transaction(id, paidTo, value, desc, date, dateCreated, budgetId);
  }

  /**
   * Deletes the given transaction from the database if it exists
   *
   * @param transaction - the transaction to delete from the database
   */
  public void deleteTransaction(Transaction transaction) {
    try {
      SQLiteDatabase db = this.getWritableDatabase();
      db.delete(
          TABLE_TRANSACT, "TransactionID=?", new String[] {String.valueOf(transaction.getID())});
      db.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds the given budget to the database
   *
   * @param budget - the budget to add to the database
   */
  public void addBudget(Budget budget) {
    try {
      SQLiteDatabase db = this.getWritableDatabase();

      ContentValues values = new ContentValues();
      values.put("BudgetName", budget.getName());
      values.put("BudgetAmount", budget.getAmount());
      values.put("BudgetType", BudgetType.typeToInt(budget.getType()));
      values.put("Colour", budget.getColour());
      values.put("DateCreated", budget.getDateCreated().toString());

      db.insert(TABLE_BUDGET, null, values);

      db.close();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the budget of the given id from the database
   *
   * @param id - the id of the budget to get from the database
   * @return - the budget of the given id, or null otherwise
   */
  public Budget getBudget(int id) {
    try {
      SQLiteDatabase db = this.getReadableDatabase();

      Cursor cursor =
          db.query(
              TABLE_BUDGET,
              new String[] {
                "BudgetID", "BudgetName", "BudgetAmount", "BudgetType", "DateCreated", "Colour"
              },
              "BudgetID=?",
              new String[] {String.valueOf(id)},
              null,
              null,
              null);

      if (cursor != null) {
        if (cursor.moveToFirst()) {
          return readBudget(cursor);
        }

        cursor.close();
        db.close();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Gets the budget id for the budget with the given name
   *
   * @param budgetName - the name of the budget to get the id for
   * @return - the id of the budget of the given name, or -1 otherwise to indicate an error
   */
  public int getBudgetID(String budgetName) {
    try {
      SQLiteDatabase db = this.getReadableDatabase();

      Cursor cursor =
          db.query(
              TABLE_BUDGET,
              new String[] {"BudgetID"},
              "BudgetName=?",
              new String[] {budgetName},
              null,
              null,
              null);

      if (cursor != null && cursor.moveToFirst()) {
        int id = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return id;
      }

      db.close();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    return -1;
  }

  /**
   * Updates the given budget within the database.
   *
   * @param budget - the budget to update
   */
  public void updateBudget(Budget budget) {
    try {
      SQLiteDatabase db = this.getWritableDatabase();

      ContentValues values = new ContentValues();
      values.put("BudgetName", budget.getName());
      values.put("BudgetAmount", String.valueOf(budget.getAmount()));
      values.put("BudgetType", String.valueOf(budget.getType()));
      values.put("Colour", budget.getColour());

      db.update(TABLE_BUDGET, values, "BudgetID=?", new String[] {String.valueOf(budget.getID())});
      db.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets all the budgets stored in the database
   *
   * @return - a HashSet containing all budgets stored in the database
   */
  public Set<Budget> getAllBudgets() {
    Set<Budget> budgetList = new HashSet<>();

    try {
      String query = "SELECT * FROM " + TABLE_BUDGET;

      SQLiteDatabase db = this.getReadableDatabase();
      Cursor cursor = db.rawQuery(query, null);

      if (cursor.moveToFirst()) {
        do {
          budgetList.add(readBudget(cursor));
        } while (cursor.moveToNext());
      }

      cursor.close();
      db.close();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    return budgetList;
  }

  // a private helper method to reduce duplication when getting budgets from the database.
  private Budget readBudget(Cursor cursor) {
    int ID = Integer.parseInt(cursor.getString(0));
    String name = cursor.getString(1);
    float amount = Float.parseFloat(cursor.getString(2));
    int type = BudgetType.stringToInt(cursor.getString(3));
    Timestamp dateCreated = Timestamp.valueOf(cursor.getString(4));
    String colour = cursor.getString(5);

    return new Budget(ID, name, amount, BudgetType.intToType(type), dateCreated, colour);
  }

  /**
   * Calculates the amount spent for the given budget within its given time period
   *
   * @param budget - the budget to calculate how much has been spent
   * @return - the amount spent in the budget
   */
  public float getAmountSpent(Budget budget) {
    float amount = 0;
    try {
      SQLiteDatabase db = this.getReadableDatabase();

      LocalDate currentDate = LocalDate.now();
      LocalDate previousDate = getPreviousIterationDate(budget.getDateCreated(), budget.getType());

      Cursor cursor =
          db.rawQuery(
              "SELECT Value, TransactionDate FROM "
                  + TABLE_TRANSACT
                  + " WHERE Budget="
                  + budget.getID(),
              null);

      if (cursor != null && cursor.moveToFirst()) {
        do {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_STORED);
          LocalDate transactionDate = LocalDate.parse(cursor.getString(1), formatter);

          if (dateIsBetween(transactionDate, previousDate, currentDate)) {
            amount += Float.parseFloat(cursor.getString(0));
          }
        } while (cursor.moveToNext());

        cursor.close();
      }

      db.close();
    } catch (NullPointerException e) {
      // using individual catches would allow different error handling for different cases
      e.printStackTrace();
    } catch (SQLiteException e) {
      e.printStackTrace();
    }

    return amount;
  }

  /**
   * A static helper method to compare two dates. Returns true if the given date is in between the
   * start date and the end date
   *
   * @param comparisonDate - the date we are comparing
   * @param startDate - the start date
   * @param endDate - the end date
   * @return - true if the comparison date is in between the start date and end date
   */
  public static boolean dateIsBetween(
      LocalDate comparisonDate, LocalDate startDate, LocalDate endDate) {
    return comparisonDate.compareTo(startDate) >= 0 && comparisonDate.compareTo(endDate) <= 0;
  }

  /* a helper method to get the start of the previous period for the given budget, given the
   * date on which it was created */
  private LocalDate getPreviousIterationDate(Timestamp dateCreated, BudgetType type) {
    LocalDate currentTime = LocalDate.now();
    LocalDate budgetDate = dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    while (budgetDate.isBefore(currentTime)) {
      budgetDate = budgetDate.plus(1, BudgetType.typeToChronoUnit(type));
    }

    budgetDate = budgetDate.minus(1, BudgetType.typeToChronoUnit(type));

    return budgetDate;
  }

  /**
   * Gets the total amount spent across all budgets in their most recent time period
   *
   * @return - the total amount spent across all budgets
   */
  public float getTotalBudgetSpending() {
    try {
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUDGET, null);

      float total = 0;

      if (cursor != null && cursor.moveToFirst()) {
        do {
          int id = Integer.parseInt(cursor.getString(0));
          String name = cursor.getString(1);
          float amount = Float.parseFloat(cursor.getString(2));
          BudgetType type = BudgetType.stringToBudgetType(cursor.getString(3));
          Timestamp timestamp = Timestamp.valueOf(cursor.getString(4));
          String colour = cursor.getString(5);

          Budget budget = new Budget(id, name, amount, type, timestamp, colour);

          total += getAmountSpent(budget);
        } while (cursor.moveToNext());
        cursor.close();
      }

      db.close();

      return total;
    } catch (NullPointerException e) {
      e.printStackTrace();
    }

    return 0;
  }

  /**
   * Deletes the given budget and all transactions found in the budget
   *
   * @param budget - the budget to delete
   */
  public void deleteBudget(Budget budget) {
    try {
      Set<Transaction> transactions = getAllTransactionsInBudget(budget);

      for (Transaction transaction : transactions) {
        deleteTransaction(transaction);
      }

      SQLiteDatabase db = this.getWritableDatabase();
      db.delete(TABLE_BUDGET, "BudgetID=?", new String[] {String.valueOf(budget.getID())});
      db.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
