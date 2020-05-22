package com.example.alex_.project;

public enum BudgetType {
    Daily,
    Weekly,
    Monthly,
    Yearly;

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

    public static String typeToDuration(BudgetType type) {
        switch (type) {
            case Daily:
                return "day";
            case Monthly:
                return "month";
            case Yearly:
                return "year";
            default:
                return "week";
        }
    }
}

