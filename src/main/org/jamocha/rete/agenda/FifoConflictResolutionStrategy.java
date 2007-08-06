package org.jamocha.rete.agenda;

import java.util.List;

public class FifoConflictResolutionStrategy implements
		ConflictResolutionStrategy {

	public void addActivation(List<Activation> activations, Activation a) {
		activations.add(a);
	}

	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

}
