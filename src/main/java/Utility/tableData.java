package Utility;

public class tableData {
    public int month;
    public float monthlyPayment;
    public float remainingBalance;

    public tableData(int month, float monthlyPayment, float remainingBalance) {
        this.month = month;
        this.monthlyPayment = monthlyPayment;
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
}
