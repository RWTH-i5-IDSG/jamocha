package org.jamocha.rete.agenda;

public class DepthStrategy extends ConflictResolutionStrategy {

	public int compare(Activation act1, Activation act2) {
		int sal1 = act1.getRule().getSalience();
		int sal2 = act2.getRule().getSalience();
		if (sal1 == sal2) {
			long time1 = act1.getAggregatedTime();
			long time2 = act2.getAggregatedTime();
			if (time1 > time2) {
				return -1;
			} else if (time1 < time2) {
				return 1;
			} else {
				return 0;
			}
		} else if (sal1 > sal2) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public String getName() {
		return getNameStatic();
	}

	public static String getNameStatic() {
		return "DepthStrategy";
	}

}
