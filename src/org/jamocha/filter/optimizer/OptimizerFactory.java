/*
 * Copyright 2002-2015 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.filter.optimizer;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jamocha.classloading.Loader;

import lombok.extern.log4j.Log4j2;

/**
 * Factory for {@link Optimizer}s. Optimizers have to register using
 * {@link OptimizerFactory#addImpl(String, Supplier)}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class OptimizerFactory {

	private static Map<String, Supplier<Optimizer>> suppliers = new HashMap<>();

	static {
		Loader.loadClasses("org/jamocha/filter/optimizer");
	}

	public static void load() {
		try {
			Class.forName(OptimizerFactory.class.getName());
		} catch (final ClassNotFoundException ex) {
			log.catching(ex);
		}
	}

	public static void addImpl(final String optimizerName, final Supplier<Optimizer> supplier) {
		suppliers.put(optimizerName, supplier);
	}

	public static class Configuration {
		final Collection<Supplier<Optimizer>> optimizers;

		public Configuration(final Collection<String> names) {
			OptimizerFactory.load();
			this.optimizers = names.stream().map(suppliers::get).collect(toList());
		}

		public Configuration() {
			this(Arrays.asList(
					/*
					 * node sharing has to be considered first
					 */
					/*
					 * TODO use NodeShareOptimizer.name,
					 */
					/*
					 * filters using the same paths can be combined
					 */
					SamePathsFilterCombiningOptimizer.name,
					/*
					 * filter elements using the same paths can be combined
					 */
					SamePathsFEsCombiningOptimizer.name,
					/*
					 * now perform the actual optimization of the path filter order
					 */
					PathFilterOrderOptimizer.name,
					/*
					 * now that the order of the filters is fixed, we can combine filters using only
					 * a subset of the paths of their predecessors
					 */
					SubsetPathsFilterCombiningOptimizer.name));
		}

		public Collection<Optimizer> getOptimizers() {
			return optimizers.stream().map(Supplier::get).collect(toList());
		}
	}
}
