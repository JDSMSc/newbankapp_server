package newbank.server;

import newbank.server.NewBank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private static BufferedReader in;
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
					Customer currentCustomer = this.callLogin();
					// if the user is authenticated then get requests from the user and process them with the
					// appropriate customer object.
					if (currentCustomer != null) {
						out.println("Log In Successful. What do you want to do?");
						while (true) {
							String request = in.readLine();

							//If user wishes to log out, reset currentCustomer and break, print status.
							if (request.equals("LOGOUT")) {
								System.out.println("Request from " + currentCustomer.getUserName());
								currentCustomer = null;
								out.println("Logging out...");
								break;
							}

							//Authorisation for password change happens in client handler itself,
							//The actual password change is done by the bank in NewBank.
							//3 attempts are given to change the password to prevent brute force attack.
							if (request.equals("CHANGEPASS")) {
								out.println("Enter current password");
								String attempt = in.readLine();
								if (currentCustomer.getPassword().equals(attempt)) {
									out.println("...\nEnter new password of at least length 7.");
									String result = bank.processRequest(currentCustomer, "CHANGEPASS");
									out.println(result);
									//The password is changed at root in the NewBank class so need to update local records.
									this.bank = NewBank.getBank();
									out.println(getBankResponse(currentCustomer, request));
									request = null;
								}
							}

							if (request != null) {
								out.println(getBankResponse(currentCustomer, request));
							}

							//System.out.println("Request from " + currentCustomer.getUserName());
							//String response = bank.processRequest(currentCustomer, request);
							//out.println(response);
						}
					} else {
						out.println("Log In Failed\n");
						//currentCustomer = this.callLogin();
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
			// ask for user name
			out.println("Enter Username");
			String userName = in.readLine();
			// ask for password
			out.println("Enter Password");
			String password = in.readLine();
			out.println("Checking Details...");
			// authenticate user and get customer object from bank for use in subsequent requests
			Customer customer = bank.checkLogInDetails(userName, password);
			return customer;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	//Static method to allow the NewBank class to get user input without creating additional buffered readers.
	//Returns the input string to the method that calls it.
	public static String getInput() {
		try {
			String input = in.readLine();
			return input;
		}
		catch (IOException exception) {
			//In event of failure
			return null;
		}
	}

	//
	public String getBankResponse(Customer customer, String request) {
		System.out.println("Request from " + customer.getUserName());
		String response = bank.processRequest(customer, request);
		return response;
	}

}
