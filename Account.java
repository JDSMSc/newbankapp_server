package newbank.server;

public class Account {

    private int accountType;
    private double openingBalance;

    private String accountName;

    //Set account type to 1-"CURRENT", 2-"SAVING" or 3-"MULTI"
    public Account(String accountName, double openingBalance, int type) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.accountType = type;
    }

    public String toString() {
        return (accountName + ": " + openingBalance);
    }

    //Returns the type of account to the program.
    public String getType(){
        String returnStr = "";
        switch (accountType){
            case 1:
                returnStr = "This is a current account.";
            case 2:
                returnStr = "This is a savings account.";
            case 3:
                returnStr =  "This is a multi-currency account";
        }
        return returnStr;
    }

}
