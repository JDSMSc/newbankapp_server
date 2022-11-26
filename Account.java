package newbank.server;

import java.util.Random;

public class Account {

    private int accountType;
    private double openingBalance;

    private String accountName;
    private int idNumber;

    //Set account type to 1-"CURRENT", 2-"SAVING" or 3-"MULTI"
    public Account(String accountName, double openingBalance, int type) {
        this.accountName = accountName;
        this.openingBalance = openingBalance;
        this.accountType = type;

        //Generate new random number for Account idNumber.
        Random rand = new Random();
        int randNum = rand.nextInt(9000000) + 1000000;
        this.idNumber = randNum;
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
