package org.jamocha.rete.rulecompiler.hokifisch;

import java.util.List;
import java.util.Stack;

public class Scope {
	
	Stack<BindingAddressTable> layers;
	
	public void newScopeLayer() {
		layers.add(new BindingAddressTable());
	}
	
	public void removeScopeLayer() {
		layers.pop();
	}
	
	public Scope() {
		layers = new Stack<BindingAddressTable>();
		newScopeLayer();
	}
	
	public void addBindingAddress(BindingAddress ba, String variable) {
		layers.firstElement().addBindingAddress(ba, variable);
	}
	
	public List<BindingAddress> getBindingAddresses(String variable) {
		return null;
	}
	
	public BindingAddress getPivot (String variable) {
		return null;
	}
	
	
}
