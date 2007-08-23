package org.jamocha.rete.agenda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Josef Alexander Hahn
 */
public abstract class ConflictResolutionStrategy {

	public abstract void addActivation(List<Activation> activations,
			Activation a);

	public abstract void removeActivation(List<Activation> activations,
			Activation a);

	public abstract String getName();

	public static String getNameStatic() {
		return "abstract-strategy";
	}

	private static boolean initialized;

	@SuppressWarnings("unchecked")
	private static Map<String, Class> map;

	@SuppressWarnings("unchecked")
	private static void registerStrategy(Class strategy) {
		try {
			String stratName = (String) strategy.getMethod("getNameStatic",
					new Class[0]).invoke(ConflictResolutionStrategy.class,
					new Class[0]);
			map.put(stratName, strategy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void init() {
		if (initialized)
			return;
		map = new HashMap<String, Class>();
		registerStrategy(LastComeFirstServeStrategy.class);
		registerStrategy(FirstComeFirstServeStrategy.class);
		registerStrategy(HighestPriorityFirstStrategy.class);
		registerStrategy(HighestComplexityFirstStrategy.class);
		initialized = true;
	}

	@SuppressWarnings("unchecked")
	public static ConflictResolutionStrategy getStrategy(String strategy)
			throws InstantiationException, IllegalAccessException {
		init();
		Class stratCls = map.get(strategy);
		if (stratCls == null)
			return null;
		return (ConflictResolutionStrategy) (stratCls.newInstance());
	}

	public static Set<String> getStrategies() {
		init();
		return map.keySet();
	}

}
