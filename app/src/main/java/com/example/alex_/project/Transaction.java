package com.example.alex_.project;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Transaction {

	private int tID;
	private String tPaidTo;
	private float tValue;
	private String tDesc;
	private LocalDate tDate;
	private Timestamp tDateCreated;
	private int tBudget;

	public Transaction() {
	}

	public Transaction(int id, String paidTo, float value, String desc, String date, Timestamp dateCreated, int budget) {
		tID = id;
		tPaidTo = paidTo;
		tValue = value;
		tDesc = desc;
		tDate = dateParser(date);
		tDateCreated = dateCreated;
		tBudget = budget;
	}

	public Transaction(int id, String paidTo, float value, String desc, LocalDate tDate, Timestamp dateCreated, int budget) {
		this.tID = id;
		this.tPaidTo = paidTo;
		this.tValue = value;
		this.tDesc = desc;
		this.tDate = tDate;
		this.tDateCreated = dateCreated;
		this.tBudget = budget;
	}

	public Transaction(float value, String paidTo, String desc, String date, Timestamp dateCreated, int budget) {
		tValue = value;
		tPaidTo = paidTo;
		tDesc = desc;
		tDate = dateParser(date);
		tDateCreated = dateCreated;
		tBudget = budget;
	}

	public int getID() {
		return tID;
	}

	public void setID(int id) {
		tID = id;
	}

	public String getPaidTo() {
		return tPaidTo;
	}

	public void setPaidTo(String paidTo) {
		tPaidTo = paidTo;
	}

	public float getValue() {
		return tValue;
	}

	public void setValue(float value) {
		tValue = value;
	}

	public String getDesc() {
		return tDesc;
	}

	public void setDesc(String desc) {
		tDesc = desc;
	}

	public Boolean isWithdrawal() {
		return tValue < 0;
	}

	public Boolean isDeposit() {
		return tValue >= 0;
	}

	public LocalDate getDate() {
		return tDate;
	}

	public void setDate(String date) {
		tDate = dateParser(date);
	}

	public Timestamp getDateCreated() {
		return tDateCreated;
	}

	public void setDateCreated(Timestamp dateCreated) {
		tDateCreated = dateCreated;
	}

	public int getBudget() {
		return tBudget;
	}

	public void setBudget(int budget) {
		tBudget = budget;
	}

	public boolean isThisWeek() {
		if (tDate != null) {
			return tDate.plusWeeks(1).isAfter(LocalDate.now());
		}
		return false;
	}

	private static LocalDate dateParser(String date) {
		// date of form dd/MM/yyyy
		DateTimeFormatter formatterDisplayed = DateTimeFormatter.ofPattern(DBHelper.DATE_FORMAT_DISPLAYED);
		return LocalDate.parse(date, formatterDisplayed);
	}

	public String dateToString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DBHelper.DATE_FORMAT_DISPLAYED);
		return tDate.format(formatter);
	}

	public static float valueParser(float value, String type) {
		if (type.equals("Withdrawal")) {
			return -1 * value;
		}
		return value;
	}
}
