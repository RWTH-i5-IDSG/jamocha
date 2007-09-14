package org.jamocha.rete.rulecompiler.hokifisch;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jamocha.rete.Constants;

public class BindingAddressTable {
	private Map<String, Vector<BindingAddress>> row = new HashMap<String, Vector<BindingAddress>>();

	public BindingAddressTable() {
		super();
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		for (String key : row.keySet()) {
			result.append(key).append("  :  ");
			for (BindingAddress ba : row.get(key)) {
				result.append(ba.toString()).append(" ; ");
			}
			result.append("\n");
		}
		return result.toString();
	}

	public void addBindingAddress(BindingAddress ba, String variable) {
		Vector<BindingAddress> vector = row.get(variable);
		if (vector == null) {
			row.put(variable, vector = new Vector<BindingAddress>());
		}
		vector.add(ba);
	}



}
