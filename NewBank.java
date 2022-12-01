package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer("bhagy@gmail.com", "password", "Bhagy", "Smith");
		bhagy.addAccount(new Account("Main", 1000.0, 1));
		bhagy.addAccount(new Account("Secondary", 100000.0, 2));
		customers.put(bhagy.getUserName(), bhagy);
		
		Customer christina = new Customer("christina@gmail.com", "password", "Christina", "Smith");
		christina.addAccount(new Account("Savings", 1500.0, 2));
		customers.put(christina.getUserName(), christina);
		
		Customer john = new Customer("john@gmail.com", "password", "John", "Smith");
		john.addAccount(new Account("Checking", 250.0, 3));
		customers.put(john.getUserName(), john);
	}
	
	public static NewBank getBank() {
		return bank;
	}

	//If the customers hashmap contains the first name of the customer then retrieve that Customer
	// and validate the password. Returns the customer object to the ClientHandler
	public synchronized Customer checkLogInDetails(String userName, String password) {

		if(customers.containsKey(userName)) {
			Customer cust = customers.get(userName);
			if (cust.getPassword().equals(password)) {
				return cust;
			}
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	//Commands are verified by checking the hashmap of contains contains a key equal to the firstname of the customer
	//This can be updated to the username in a future sprint
	public synchronized String processRequest(Customer customer, String request) {
		if(customers.containsKey(customer.getFirstName())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(Customer customer) {
		return (customers.get(customer.getFirstName())).accountsToString();
	}

}
