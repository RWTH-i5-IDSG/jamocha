package org.jamocha.rete.agenda;

import java.util.List;

public class LifoConflictResolutionStrategy extends ConflictResolutionStrategy {

	public void addActivation(List<Activation> activations, Activation a) {
		activations.add(0, a);

	}

	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

	public static String getName() {
		return "LifoStrategy";
	}

}
