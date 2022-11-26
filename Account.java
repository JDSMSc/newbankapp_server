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
        return (this.getType() + ": " + accountName + ": " + openingBalance);
    }

    //Returns the type of account to the program.
    public String getType(){
        String returnStr = "";
        switch (accountType){
            case 1:
                returnStr = "current account";
            case 2:
                returnStr = "savings account";
            case 3:
                returnStr =  "multi-currency account";
        }
        return returnStr;
    }

}
