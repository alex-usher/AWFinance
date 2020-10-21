package com.example.alex_.project;

import java.time.temporal.ChronoUnit;

// a comprehensive enum storing the possible frequencies for budgets to occur
public enum BudgetType {
  Daily,
  Weekly,
  Monthly,
  Yearly;

  public static int stringToInt(String string) {
    return typeToInt(stringToBudgetType(string));
  }

  public static BudgetType stringToBudgetType(String string) {
    switch (string) {
      case "Daily":
        return Daily;
      case "Monthly":
        return Monthly;
      case "Yearly":
        return Yearly;
      default:
        return Weekly;
    }
  }

  public static int typeToInt(BudgetType type) {
    switch (type) {
      case Daily:
        return 0;
      case Weekly:
        return 1;
      case Monthly:
        return 2;
      case Yearly:
        return 3;
      default:
        return -1;
    }
  }

  public static BudgetType intToType(int i) {
    switch (i) {
      case 0:
        return Daily;
      case 2:
        return Monthly;
      case 3:
        return Yearly;
      default:
        return Weekly;
    }
  }

  public static ChronoUnit typeToChronoUnit(BudgetType type) {
    switch (type) {
      case Daily:
        return ChronoUnit.DAYS;
      case Monthly:
        return ChronoUnit.MONTHS;
      case Yearly:
        return ChronoUnit.YEARS;
      default:
        return ChronoUnit.WEEKS;
    }
  }
}
