package org.jamocha.rete.agenda;

import java.util.List;

public class FirstComeFirstServeStrategy extends ConflictResolutionStrategy {

	public void addActivation(List<Activation> activations, Activation a) {
		activations.add(a);
	}

	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

	public String getName() {
		return "FirstComeFirstServeStrategy";
	}

	public static String getNameStatic() {
		return "FirstComeFirstServeStrategy";
	}

}
