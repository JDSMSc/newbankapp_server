package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

    private NewBank bank;
    private BufferedReader in;
    private PrintWriter out;


    public NewBankClientHandler(Socket s) throws IOException {
        bank = NewBank.getBank();
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
    }

    private String addAccount(Customer customer) throws IOException {
        out.println("Account Type: 1. Current 2. Savings 3. Multi-Currency");
        int accountType = Integer.parseInt(in.readLine());
        String currency = "GBP"; //Default currency
        if (accountType == 3) {
            out.println("Enter Currency");
            currency = in.readLine();
        }
        out.println("Enter Account Name");
        String accountName = in.readLine();
        out.println("Enter Initial Deposit");
        String initialDeposit = in.readLine();
        customer.addAccount(new Account(accountName, Double.parseDouble(initialDeposit), accountType, currency));
        return "Account Created";
    }

    public void run() {
        //While loop to continue taking login attempts until success. Once successful it will
        // keep getting requests from the client and processing them.
        try {
            while (true) {
                Customer customerToLogin = this.callLogin();
                // if the user is authenticated then get requests from the user and process them with the
                // appropriate customer object.
                if (customerToLogin != null) {
                    out.println("Log In Successful! \nWhat do you want to do?");
                    while (true) {
                        String request = in.readLine();
                        //If user wishes to log out, reset customerToLogin and break, print status.
                        if (request.equals("LOGOUT")) {
                            System.out.println("Request from " + customerToLogin.getUserName());
                            customerToLogin = null;
                            out.println("Logging out.\n");
                            break;
                        }
                        if (request.equals("ADDACCOUNT")) {
                            addAccount(customerToLogin);
                        }

                        if (request.equals("CHANGEPASS")) {
                            changePassword(customerToLogin);
                        }

                        System.out.println("Request from " + customerToLogin.getUserName() + " requesting " + request);
                        String response = bank.processRequest(customerToLogin, request);
                        out.println(response);
                    }
                } else {
                    out.println("Log In Failed\n");
                    //customerToLogin = this.callLogin();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }

    //Generalised The login process to be able to be called whenever needed.
    private Customer callLogin() {
        try {
            out.println("Select 1 to log in or 2 to register");
            String loginOrRegister = in.readLine();
            if (loginOrRegister.equals("1")) {
                out.println("Enter Username");
                String userName = in.readLine();
                out.println("Enter Password");
                String password = in.readLine();
                // authenticate user and get customer object from bank for use in subsequent requests
                return bank.checkLogInDetails(userName, password);
            } else if (loginOrRegister.equals("2")) {
                out.println("Enter First Name");
                String firstName = in.readLine();
                out.println("Enter Last Name");
                String lastName = in.readLine();
                out.println("Enter Email");
                String email = in.readLine();
                out.println("Enter Password");
                String password = in.readLine();
                out.println("Account Type: 1. Current 2. Savings 3. Multi-Currency");
                int accountType = Integer.parseInt(in.readLine());
                String currency = "GBP"; //Default currency
                if (accountType == 3) {
                    out.println("Enter Currency");
                    currency = in.readLine();
                }
                out.println("Account Name");
                String accountName = in.readLine();
                out.println("Enter Initial Deposit");
                String initialDeposit = in.readLine();
                return bank.addCustomer(firstName, lastName, email, password, accountType, accountName, Double.parseDouble(initialDeposit), currency);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Change password on the customerToLogin, if a correct current password is entered.
    //3 attempts given, at which point the method ends.
    private void changePassword(Customer customer) {
        out.println("Enter current password: ");
        try {
            String currentPass = in.readLine();
            if (currentPass.equals(customer.getPassword())) {
                int tries = 3;
                while (tries > 0) {
                    out.println("Enter new password of at least length 7...");
                    String newPass = in.readLine();
                    if (newPass.length() >= 7) {
                        customer.setPassword(newPass);
                        out.println("Password change confirmed.");
                        break;
                    }
                    tries -= 1;
                    if (tries == 0) {
                        out.println("3 invalid attempts made, ending password change.");
                    }
                }
            } else {
                out.println("\nIncorrect current password entered, returning to menu.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
