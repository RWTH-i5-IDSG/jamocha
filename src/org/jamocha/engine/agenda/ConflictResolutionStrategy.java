/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.engine.agenda;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Josef Alexander Hahn
 */
public abstract class ConflictResolutionStrategy implements
		Comparator<Activation> {

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
					(Object[]) new Class[0]);
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
		registerStrategy(DepthStrategy.class);
		registerStrategy(BreadthStrategy.class);
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
		return (ConflictResolutionStrategy) stratCls.newInstance();
	}

	public static Set<String> getStrategies() {
		init();
		return map.keySet();
	}

}
