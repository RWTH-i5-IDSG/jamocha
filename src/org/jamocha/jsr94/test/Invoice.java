package org.jamocha.jsr94.test;

public class Invoice {

	private String description;

	private int amount;

	public Invoice() {
		super();
	}

	public Invoice(String description, int amount, String status) {
		this();
		this.description = description;
		this.amount = amount;
		this.status = status;
	}

	private String status;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String toString() {
		return "[invoice '"+description+"' amount:"+amount+" status:"+status+"]";
	}

	
}
