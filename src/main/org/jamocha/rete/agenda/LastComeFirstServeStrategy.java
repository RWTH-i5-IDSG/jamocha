package org.jamocha.rete.agenda;

import java.util.LinkedList;
import java.util.List;

public class LastComeFirstServeStrategy extends ConflictResolutionStrategy {

	public void addActivation(List<Activation> activations, Activation a) {
		activations.add(0, a);
	}

	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

	public String getName() {
		return "LastComeFirstServeStrategy";
	}

	public static String getNameStatic() {
		return "LastComeFirstServeStrategy";
	}
	
	public List<Activation> getEmptyActivationList(int initialSize) {
		return new LinkedList<Activation>();
	}

}
