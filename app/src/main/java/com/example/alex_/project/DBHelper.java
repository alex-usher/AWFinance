package com.example.alex_.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
	public static final int DB_VERSION = 1;
	public static final String DATABASE_NAME = "Finance";
	public static final String TABLE_TRANSACT = "Transact";
	public static final String TABLE_BUDGET = "Budget";
	public static final String DATE_FORMAT_STORED = "yyyy-MM-dd";
	public static final String DATE_FORMAT_DISPLAYED = "dd/MM/yyyy";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	public void onCreate(SQLiteDatabase database) {
		String createTransaction = "CREATE TABLE " + TABLE_TRANSACT + "(TransactionID INTEGER PRIMARY KEY AUTOINCREMENT, PaidTo VARCHAR(20), Value DECIMAL NOT NULL DEFAULT '0.0', " +
			"Description VARCHAR(50), TransactionDate DATE, DateCreated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
			"Budget INT NOT NULL DEFAULT '0')";
		String createBudget = "CREATE TABLE " + TABLE_BUDGET + "(BudgetID INTEGER PRIMARY KEY AUTOINCREMENT, BudgetName VARCHAR(25), " +
			"BudgetAmount DECIMAL NOT NULL DEFAULT '0.0', " +
			"BudgetType INT NOT NULL DEFAULT '1', DateCreated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
			"Colour VARCHAR(9))";
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

	//add single transaction to db
	public void addTransaction(Transaction transaction) {
		try {
			SQLiteDatabase db = this.getWritableDatabase(); //establish db connection

			//set up values to insert to db using instance of transaction class
			ContentValues values = new ContentValues();
			values.put("PaidTo", transaction.getPaidTo());
			values.put("Value", transaction.getValue());
			values.put("Description", transaction.getDesc());
			values.put("TransactionDate", transaction.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)));
			values.put("DateCreated", transaction.getDateCreated().toString());
			values.put("Budget", transaction.getBudget());

			//insert row into db
			db.insert(TABLE_TRANSACT, null, values);

			//close db connection
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//get single transaction from db by id
	public Transaction getTransaction(int id) {
		try {
			SQLiteDatabase db = this.getReadableDatabase(); //establish db connection

			Cursor cursor = db.query(TABLE_TRANSACT, new String[]{"TransactionID", "PaidTo", "Value", "Description", "TransactionDate", "DateCreated", "Budget"},
				"TransactionID=?", new String[]{String.valueOf(id)}, null, null, null, null);

			if (cursor != null) {
				cursor.moveToFirst();

				Transaction transaction = readTransaction(cursor);

				cursor.close();
				db.close();

				return transaction;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public int updateTransaction(Transaction transaction) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("PaidTo", transaction.getPaidTo());
		values.put("Value", transaction.getValue());
		values.put("Description", transaction.getDesc());
		values.put("TransactionDate", transaction.getDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT_STORED)));
		values.put("DateCreated", transaction.getDateCreated().toString());
		values.put("Budget", transaction.getBudget());

		int update = db.update(TABLE_TRANSACT, values, "TransactionID=?", new String[]{String.valueOf(transaction.getID())});
		db.close();
		return update;
	}

	public List<Transaction> getAllTransactions() {
		List<Transaction> transactionList = new ArrayList<>();

		try {
			String query = "SELECT * FROM " + TABLE_TRANSACT;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(query, null);

			if (cursor.moveToFirst()) {
				do {
					transactionList.add(readTransaction(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return transactionList;
	}

	public List<Transaction> getAllTransactionsInBudget(Budget budget) {
		List<Transaction> transactions = new ArrayList<>();

		try {
			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.query(TABLE_TRANSACT, new String[]{"TransactionID", "PaidTo", "Value", "Description", "TransactionDate", "DateCreated", "Budget"},
				"Budget=?", new String[]{String.valueOf(budget.getID())}, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					transactions.add(readTransaction(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return transactions;
	}

	private Transaction readTransaction(Cursor cursor) {
		int id = Integer.parseInt(cursor.getString(0));
		String paidTo = cursor.getString(1);
		float value = Float.parseFloat(cursor.getString(2));
		String desc = cursor.getString(3);
		LocalDate date = LocalDate.parse(cursor.getString(4), DateTimeFormatter.ofPattern(DATE_FORMAT_STORED));
		Timestamp dateCreated = Timestamp.valueOf(cursor.getString(5));
		int budgetId = Integer.parseInt(cursor.getString(6));

		return new Transaction(id, paidTo, value, desc, date, dateCreated, budgetId);
	}

	public void deleteTransaction(Transaction transaction) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_TRANSACT, "TransactionID=?", new String[]{String.valueOf(transaction.getID())});
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Budget getBudget(int id) {
		try {
			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.query(TABLE_BUDGET, new String[]{"BudgetID", "BudgetName", "BudgetAmount", "BudgetType", "DateCreated", "Colour"}, "BudgetID=?",
				new String[]{String.valueOf(id)}, null, null, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					Budget budget = readBudget(cursor);
					return budget;
				}

				cursor.close();
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public int getBudgetID(String budgetName) {
		try {
			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.query(TABLE_BUDGET, new String[]{"BudgetID"}, "BudgetName=?", new String[]{budgetName},
				null, null, null);

			if (cursor != null && cursor.moveToFirst()) {
				int id = Integer.parseInt(cursor.getString(0));
				cursor.close();
				db.close();
				return id;
			}

			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public int updateBudget(Budget budget) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put("BudgetName", budget.getName());
			values.put("BudgetAmount", String.valueOf(budget.getAmount()));
			values.put("BudgetType", String.valueOf(budget.getType()));
			values.put("Colour", budget.getColour());

			int update = db.update(TABLE_BUDGET, values, "BudgetID=?", new String[]{String.valueOf(budget.getID())});
			db.close();
			return update;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public List<Budget> getAllBudgets() {
		List<Budget> budgetList = new ArrayList<>();

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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return budgetList;
	}

	private Budget readBudget(Cursor cursor) {
		int ID = Integer.parseInt(cursor.getString(0));
		String name = cursor.getString(1);
		float amount = Float.parseFloat(cursor.getString(2));
		int type = Integer.parseInt(cursor.getString(3));
		Timestamp dateCreated = Timestamp.valueOf(cursor.getString(4));
		String colour = cursor.getString(5);

		return new Budget(ID, name, amount, BudgetType.intToType(type), dateCreated, colour);
	}

	public float getAmountSpent(int budgetId, BudgetType type) {
		float amount = 0;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
//			Cursor cursor = db.rawQuery("SELECT COALESCE(SUM(Value), 0.0) FROM " + TABLE_TRANSACT + " WHERE Budget='"
//					+ budgetId + "' AND DATE(TransactionDate) BETWEEN DATE('now') AND DATE('now', '+1 " + BudgetType.typeToDuration(type) + "')",
//				null);
      Cursor cursor = db.rawQuery("SELECT COALESCE(SUM(Value), 0.0) FROM " + TABLE_TRANSACT + " WHERE Budget='" + budgetId + "'", null);

			if (cursor != null && cursor.moveToFirst()) {
				System.out.println(cursor.getString(0));
				amount = Float.parseFloat(cursor.getString(0));
				cursor.close();
			}

			db.close();
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		return amount;
	}

	public float getTotalBudgetSpending() {
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT BudgetID, BudgetType FROM " + TABLE_BUDGET, null);

			float total = 0;

			if (cursor.moveToFirst()) {
				do {
					int id = Integer.parseInt(cursor.getString(0));
					BudgetType type = BudgetType.stringToBudgetType(cursor.getString(1));
					total += getAmountSpent(id, type);
				} while (cursor.moveToNext());
			}

			cursor.close();
			db.close();

			return total;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void deleteBudget(Budget budget) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			List<Transaction> transactions = getAllTransactionsInBudget(budget);

			for (int i = 0; i < transactions.size(); i++) {
				deleteTransaction(transactions.get(i));
			}

			db = this.getWritableDatabase();
			db.delete(TABLE_BUDGET, "BudgetID=?", new String[]{String.valueOf(budget.getID())});
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//    public void updateAllBudgetsSpent(){
//        try {
//            List<Transaction> transactions = getAllTransactions();
//
//            clearAllBudgetsSpent();
//
//            for(int i=0; i<transactions.size(); i++){
//                Transaction t = transactions.get(i);
//                Budget b = getBudget(t.getBudget());
//
//                Calendar ct = Calendar.getInstance();
//                ct.setTimeInMillis(new SimpleDateFormat("dd/MM/yyyy").parse(t.getDate()).getTime());
//
//                Date dt = new SimpleDateFormat("dd/MM/yyyy").parse(t.getDate());
//
//                Calendar cb = Calendar.getInstance();
//                cb.setTimeInMillis(b.getDateCreated().getTime());
//
//                Calendar end = Calendar.getInstance();
//                Calendar start = Calendar.getInstance();
//
//                //reconsider logic
//                switch(b.getType()){
//                    case 1://daily
//                        todayMidnight(start);
//                        end.setTimeInMillis(start.getTimeInMillis() + (24 * 60 * 60 * 1000)); //midnight next day
//
//                        compareAndUpdate(ct, start, end, t, b);
//
//                        break;
//                    case 2://weekly
//                        int dayOfWeek = cb.get(Calendar.DAY_OF_WEEK);
//
//                        todayMidnight(start);
//
//                        do {
//                            start.setTimeInMillis(start.getTimeInMillis() - (24 * 60 * 60 * 1000));
//                        } while(start.get(Calendar.DAY_OF_WEEK) != dayOfWeek);
//
//                        end.setTimeInMillis(start.getTimeInMillis() + (7 * 24 * 60 * 60 * 1000));
//
//                        compareAndUpdate(ct, start, end, t, b);
//
//                        break;
//                    case 3://monthly
//                        int dayOfMonth = cb.get(Calendar.DAY_OF_MONTH);
//
//                        todayMidnight(start);
//
//                        do {
//                            start.setTimeInMillis(start.getTimeInMillis() - (24 * 60 * 60 * 1000));
//                        } while(start.get(Calendar.DAY_OF_MONTH) != dayOfMonth);
//
//                        end.setTimeInMillis(start.getTimeInMillis());
//                        end.set(Calendar.MONTH, (end.get(Calendar.MONTH)+1) % 12);
//
//                        compareAndUpdate(ct, start, end, t, b);
//
//                        break;
//                    case 4://yearly
//                        int dayOfYear = cb.get(Calendar.DAY_OF_YEAR);
//
//                        do {
//                            start.setTimeInMillis(start.getTimeInMillis() - (24 * 60 * 60 * 1000));
//                        } while(start.get(Calendar.DAY_OF_YEAR) != dayOfYear);
//
//                        end.setTimeInMillis(start.getTimeInMillis());
//                        end.set(Calendar.YEAR, end.get(Calendar.YEAR)+1);
//
//                        compareAndUpdate(ct, start, end, t, b);
//
//                        break;
//                    default:
//                        break;
//                }
//            }
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }

//    private int clearAllBudgetsSpent(){
//        try {
//            SQLiteDatabase db = this.getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put("AmountSpent", 0.0);
//
//            return db.update(TABLE_BUDGET, values,null, null);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//
//        return 0;
//    }

//    private int compareAndUpdate(Calendar ct, Calendar start, Calendar end, Transaction t, Budget b){
//        if((ct.getTimeInMillis() >= start.getTimeInMillis()) && (ct.getTimeInMillis() <= end.getTimeInMillis())) {
//            if (t.getType()) {
//                b.setSpent(b.getSpent() - t.getValue());
//            } else {
//                b.setSpent(b.getSpent() + t.getValue());
//            }
//
//            return updateBudget(b);
//        }
//        return 0;
//    }

//  private void todayMidnight(Calendar c) {
//    c.setTime(Calendar.getInstance().getTime()); //midnight of current date
//    c.set(Calendar.HOUR_OF_DAY, 0);
//    c.set(Calendar.MINUTE, 0);
//    c.set(Calendar.SECOND, 0);
//    c.set(Calendar.MILLISECOND, 0);
//  }
}