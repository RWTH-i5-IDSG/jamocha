package org.jamocha.rete.agenda;

import java.lang.reflect.InvocationTargetException;
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
	
	public static String getName() {return "abstract-strategy";}
	
	private static boolean initialized;
	
	private static Map<String,Class> map;
	
	private static void registerStrategy(Class strategy) {
		try {
			String stratName = (String)strategy.getMethod("getName", new Class[0]).invoke(ConflictResolutionStrategy.class, new Class[0]);
			map.put(stratName, strategy);
		} catch(Exception e){
			e.printStackTrace();
		}
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
