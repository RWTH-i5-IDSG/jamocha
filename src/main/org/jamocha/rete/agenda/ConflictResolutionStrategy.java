package org.jamocha.rete.agenda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Josef Alexander Hahn
 */
public abstract class ConflictResolutionStrategy {

	public abstract void addActivation(List<Activation> activations, Activation a);
	
	public abstract void removeActivation(List<Activation> activations, Activation a);
	

	public abstract String getName();
	
	private static boolean initialized;
	
	private static Map<String,Class> map;
	
	private static void registerStrategy(Class strategy){
		map.put(strategy.getName(), strategy);
	}
	
	private static void init() {
		if (initialized) return;
		map = new HashMap<String, Class>();
		registerStrategy(FifoConflictResolutionStrategy.class);
		initialized=true;
	}
	
	public static ConflictResolutionStrategy getStrategy(String strategy) throws InstantiationException, IllegalAccessException {
		init();
		Class stratCls = map.get(strategy);
		return (ConflictResolutionStrategy)(stratCls.newInstance());
	}
	
	public static Set<String> getStrategies() {
		init();
		return map.keySet();
	}
	
}
