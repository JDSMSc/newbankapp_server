package newbankapp_server;

import java.util.Random;
import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {

    private int idNumber;
    private int senderID;
    private int recipientID;
    private double amount;
    private LocalDate date;
    private LocalTime time;
    
    public Transaction(int senderID, int recipientID, double amount){

        this.senderID = senderID;
        this.recipientID = recipientID;
        this.amount = amount;

        //Generate new random 7 digit number for transaction idNumber.
        Random rand = new Random();
        int randNum = rand.nextInt(9000000) + 1000000;
        this.idNumber = randNum;

        this.date = LocalDate.now();
        this.time = LocalTime.now();

    }

}
