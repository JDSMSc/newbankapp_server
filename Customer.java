package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String userName;

	public String getUserName() {
		return this.userName;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getEmail() {
		return this.email;
	}
	public String getPassword() {
		return this.password;
	}



	public Customer(String email, String password, String firstName, String lastName)
	{
		accounts = new ArrayList<>();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = firstName + lastName;
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
