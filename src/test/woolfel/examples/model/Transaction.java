package woolfel.examples.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Peter Lin
 *
 */
public class Transaction extends Security {

	protected String accountId = null;
	protected double buyPrice;
	protected String purchaseDate = null;
	protected double shares;
	protected double total;
	protected String transactionId = null;
			
    protected ArrayList listeners = new ArrayList();

    public Transaction() {
		super();
	}

    public void setAccountId(String id) {
    	if (!id.equals(this.accountId)) {
    		String old = this.accountId;
    		this.accountId = id;
    		this.notifyListener("accountId",old,this.accountId);
    	}
    }
    
    public String getAccountId() {
    	return this.accountId;
    }
    
    public void setBuyPrice(double price) {
    	if (price != this.buyPrice) {
    		Double old = new Double(this.buyPrice);
    		this.buyPrice = price;
    		this.notifyListener("buyPrice",old,new Double(this.buyPrice));
    	}
    }
    
    public double getBuyPrice() {
    	return this.buyPrice;
    }
    
    public void setPurchaseDate(String date) {
    	if (!date.equals(this.purchaseDate)) {
    		String old = this.purchaseDate;
    		this.purchaseDate = date;
    		this.notifyListener("purchaseDate",old,this.purchaseDate);
    	}
    }
    
    public String getPurchaseDate() {
    	return this.purchaseDate;
    }
    
    public void setShares(double shares) {
    	if (shares != this.shares) {
    		Double old = new Double(this.shares);
    		this.shares = shares;
    		this.notifyListener("shares",old,new Double(this.shares));
    	}
    }
    
    public double getShares() {
    	return this.shares;
    }
    
    public void setTotal(double value) {
    	if (value != this.total) {
    		Double old = new Double(this.total);
    		this.total = value;
    		this.notifyListener("total",old,new Double(this.total));
    	}
    }
    
    public double getTotal() {
    	return this.total;
    }
    
    public void setTransactionId(String id) {
    	if (!id.equals(this.transactionId)) {
    		String old = this.transactionId;
    		this.transactionId = id;
    		this.notifyListener("transactionId",old,this.transactionId);
    	}
    }
    
    public String getTransactionId() {
    	return this.transactionId;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.listeners.add(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.listeners.remove(listener);
    }
    
    protected void notifyListener(String field, Object oldValue, Object newValue){
        if (listeners == null || listeners.size() == 0) {
			return;
		} else {
			PropertyChangeEvent event = new PropertyChangeEvent(this, field,
					oldValue, newValue);

			for (int i = 0; i < listeners.size(); i++) {
				((java.beans.PropertyChangeListener) listeners.get(i))
						.propertyChange(event);
			}
		}
        
    }
}
