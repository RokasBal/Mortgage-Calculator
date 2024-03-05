package Utility;

/**
 * Class utilized for storing
 */

public class TableData {
    public int month;
    public float monthlyPayment;
    public float interestPayment;
    public float remainingBalance;

    public TableData(int month, float monthlyPayment, float interestPayment, float remainingBalance) {
        this.month = month;
        this.monthlyPayment = monthlyPayment;
        this.interestPayment = interestPayment;
        this.remainingBalance = remainingBalance;
    }

    public int getMonth() {
        return month;
    }

    public float getMonthlyPayment() {
        return monthlyPayment;
    }

    public float getRemainingBalance() {
        return remainingBalance;
    }
    public float getInterestPayment() {
        return interestPayment;
    }
}
