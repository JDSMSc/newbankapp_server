package newbankapp_server;

import java.util.Random;

public class Account {

    private int loanMonths = 12;
    private double interest;
    private int accountType;
    private double openingBalance;

    private String accountName;
    private int idNumber;
    private String accountCurrency;

    //Set account type to 1-"CURRENT", 2-"SAVING" or 3-"MULTI"
    public Account(String accountName, double openingBalance, int type, String accountCurrency) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.accountType = type;
        this.accountCurrency = accountCurrency;

        //Generate new random 7 digit number for Account idNumber.
        Random rand = new Random();
        int randNum = rand.nextInt(9000000) + 1000000;
        this.idNumber = randNum;
    }

    public Account(String accountName, double openingBalance, int type, String accountCurrency, int loanMonths, double interest) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.accountType = type;
        this.accountCurrency = accountCurrency;

        //Generate new random 7 digit number for Account idNumber.
        Random rand = new Random();
        int randNum = rand.nextInt(9000000) + 1000000;
        this.idNumber = randNum;
        //Set loan months and interest rate
        this.loanMonths = loanMonths;
        this.interest = interest;
    }


    public String toString() {
        if(accountType == 4) {
            return "Account Name: " + accountName + " |  ID: " + idNumber + " |  Balance: " + openingBalance + " |  Currency: " + accountCurrency+ " | Loan Months: " + loanMonths + " | Interest Rate: " + interest;
        } else {
            return "Account Name: " + accountName + " |  ID: " + idNumber + " |  Balance: " + openingBalance + " |  Currency: " + accountCurrency;
        }
        //return (this.getType() + ": " + accountName + ": " + openingBalance + " " + accountCurrency);
    }

    // method to return the account ID number
    public int getAccountID(){
        return this.idNumber;
    }

    // method to return account balance
    public double getBalance(){
        return this.openingBalance;
    }

    // method to update the balance
    public double updateBalance(double balance){
        this.openingBalance = balance;
        return this.openingBalance;

    }

    //Returns the type of account to the program.
    //Updated Switch statement for default case incase of failure.
    public String getType(){
        String returnStr = "";
        switch (this.accountType){
            case 1:
                returnStr = "Current Account";
                break;
            case 2:
                returnStr = "Savings Account";
                break;
            case 3:
                returnStr =  "Multi-Currency Account";
                break;
            case 4:
                returnStr =  "Micro Loan Account";
                break;
            default:
                returnStr = "Bank Account";
        }
        return returnStr;
    }

}
