package newbankapp_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class NewBankClientHandler extends Thread {

    private NewBank bank;
    private BufferedReader in;
    private PrintWriter out;


    public NewBankClientHandler(Socket s) throws IOException {
        bank = NewBank.getBank();
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
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
                    out.println("1. Show My Accounts");
                    out.println("2. New Account");
                    out.println("3. Move");
                    out.println("4. Pay");
                    out.println("5. MicroLoan Service");
                    out.println("6. Change Password");
                    out.println("7. Log Out");
                    while (true) {
                        String request = in.readLine();
                        //If user wishes to log out, reset customerToLogin and break, print status.
                        if (request.equals("2")) { //If user wishes to create a new account
                            addAccount(customerToLogin);
                            System.out.println("Request from " + customerToLogin.getUserName());
                        }
                        // If user wishes to move funds between their own accouns
                        if (request.equals("3")) {
                            moveFunds(customerToLogin);
                        }
                        if (request.equals("5")) {
                            microLoanService(customerToLogin);
                        }

                        if (request.equals("6")) { //If user wishes to change password
                            changePassword(customerToLogin);
                        }
                        if (request.equals("7")) {
                            System.out.println("Request from " + customerToLogin.getUserName());
                            customerToLogin = null;
                            out.println("Logging out.\n");
                            break;
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
                out.println("Enter Username (Your first and lastname is your username eg BhagySmith)");
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

    // method to move funds between accounts owned by the logged in user
    private void moveFunds(Customer customer) {

        // set i to 1
        int i = 1;

        // init hashmap for linking i to an account
        HashMap<Integer, Account> numberedAccount = new HashMap<>();


        // loop over all owned accounts
        for (Account account : customer.getAccounts()) {
            if (account.getType() != "Micro Loan Account") {
                // print i followed by an account
                out.print(i + ". ");
                out.println(account.toString());

                // add to hashmap
                numberedAccount.put(i, account);

                // increment
                i++;
            }

        }

        try {

            // get account user wants to send FROM
            out.println("Please select an account to move FROM: ");
            int from = Integer.parseInt(in.readLine());

            // get account user wants to send TO
            out.println("Please select an account to move TO: ");
            int to = Integer.parseInt(in.readLine());

            // check that the inputs are mapped to accounts
            if (!(numberedAccount.containsKey(from)) || !(numberedAccount.containsKey(to))) {
                out.println("One or more invalid account selected, try again.");
                moveFunds(customer);
            }

            // get the account objects using hashmap created earlier in method
            Account account_from = numberedAccount.get(from);
            Account account_to = numberedAccount.get(to);

            // init amount to enter while loop
            double amount = -1;

            // keep asking for amount until it is acceptable
            while (amount < 0 || amount > account_from.getBalance()) {
                out.print("Available balance: ");
                out.println(account_from.getBalance());
                out.println("Please enter the amount you wish to move: ");
                amount = Double.parseDouble(in.readLine());
            }

            // update balances
            account_from.updateBalance(account_from.getBalance() - amount);
            account_to.updateBalance(account_to.getBalance() + amount);

            // confirm to user
            out.println(amount + " moved from " + account_from.getType() + " to " + account_to.getType());


            // print new balances
            out.println("Account(ID): " + account_from.getAccountID() + " new balance: " + account_from.getBalance());
            out.println("Account(ID): " + account_to.getAccountID() + " new balance: " + account_to.getBalance());


            // catch exceptions
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            out.println("Error, try again.");
            moveFunds(customer);
        }
    }

    private void addAccount(Customer customer) throws IOException {
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
    }

    private void microLoanService(Customer customer) {
        // set i to 1
        int i = 1;

        // init hashmap for linking i to an account
        HashMap<Integer, Account> numberedAccount = new HashMap<>();


        // loop over all owned accounts
        for (Account account : customer.getAccounts()) {

            // print i followed by an account
            out.print(i + ". ");
            out.println(account.toString());

            // add to hashmap
            numberedAccount.put(i, account);

            // increment
            i++;
        }
        try {
            // get account user wants to send FROM
            out.println("Please select an account to loan FROM: ");
            int loanFrom = Integer.parseInt(in.readLine());
            // check that the inputs are mapped to accounts
            if (!(numberedAccount.containsKey(loanFrom))) {
                out.println("One or more invalid account selected, try again.");
                microLoanService(customer);
            }
            Account account_loan_from = numberedAccount.get(loanFrom);
            out.println("Enter amount to list for loan");
            double loanAmount = Double.parseDouble(in.readLine());
            if (loanAmount >= account_loan_from.getBalance()) {
                out.println("Loan amount must be less than account balance");
                microLoanService(customer);
            } else {
                account_loan_from.updateBalance(account_loan_from.getBalance() - loanAmount);
            }
            out.println("Enter number of months to repay");
            int loanMonths = Integer.parseInt(in.readLine());
            out.println("Enter interest rate");
            double interest = Double.parseDouble(in.readLine());
            customer.addAccount(new Account("Micro Loan", loanAmount, 4, "GBP", loanMonths, interest));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            out.println("Error, try again.");
            microLoanService(customer);
        }
    }

}
