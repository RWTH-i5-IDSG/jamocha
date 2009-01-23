package org.jamocha.jsr94.test;

public class Customer {

	private String name;
	
	private int creditLimit;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(int creditLimit) {
		this.creditLimit = creditLimit;
	}

	public Customer(String name, int creditLimit) {
		this();
		this.name = name;
		this.creditLimit = creditLimit;
	}

	public Customer() {
		super();
	}
	
	public String toString() {
		return "[customer '"+name+"' creditLimit:"+creditLimit+"]";
	}
	
}
