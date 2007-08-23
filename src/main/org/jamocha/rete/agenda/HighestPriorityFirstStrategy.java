package org.jamocha.rete.agenda;

import java.util.List;

public class HighestPriorityFirstStrategy extends
		ConflictResolutionStrategy {

	@Override
	public void addActivation(List<Activation> activations, Activation a) {
		int salience = a.getRule().getSalience();
		for (int i = 0; i < activations.size(); ++i) {
			if(salience > activations.get(i).getRule().getSalience()) {
				activations.add(i,a);
				return;
			}
		}
		activations.add(a);
	}

	@Override
	public String getName() {
		return "HighestPriorityFirstStrategy";
	}

	@Override
	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

	public static String getNameStatic() {
		return "HighestPriorityFirstStrategy";
	}

}
