package org.jamocha.rete.agenda;

import java.util.List;

public class HighestComplexityFirstStrategy extends ConflictResolutionStrategy {

	@Override
	public void addActivation(List<Activation> activations, Activation a) {
		if (activations.isEmpty()) {
			activations.add(a);
			return;
		}
		int complexity = a.getRule().getTotalComplexity();
		int low = 0;
		int high = activations.size() - 1;
		int mid;
		boolean found = false;
		do {
			mid = (low + high) / 2;
			if (high < low) {
				mid = low;
				found = true;
			} else if (activations.get(mid).getRule().getTotalComplexity() >= complexity) {
				low = mid + 1;
			}
			// could have used else here without if, but this is better readable
			else if (activations.get(mid).getRule().getTotalComplexity() < complexity) {
				high = mid - 1;
			} else {
				// we'll never come here
			}
		} while (!found);
		activations.add(mid, a);
	}

	@Override
	public String getName() {
		return "HighestComplexityFirstStrategy";
	}

	@Override
	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

	public static String getNameStatic() {
		return "HighestComplexityFirstStrategy";
	}

}
