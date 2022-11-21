package newbank.server;

public class Account {

    private String accountName;

    private String accountType;
    private double openingBalance;

    //Set account type to 1-"CURRENT", 2-"SAVING" or 3-"MULTI"
    public Account(String accountName, double openingBalance, int type) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.accountType = type;
    }

    public String toString() {
        return (accountName + ": " + openingBalance);
    }

    public String getType(){
        switch (accountType){
            case 1:
                return "This is a current account.";
            case 2:
                return "This is a savings account.";
            case 3:
                return "This is a multi-currency account";
        }
    }

}