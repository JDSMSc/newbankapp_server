package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{
	
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
						out.println("Log In Successful. What do you want to do?");
						while (true) {
							String request = in.readLine();
							System.out.println("Request from " + customerToLogin.getUserName());
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

}
