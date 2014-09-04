package org.jamocha.dn.compiler;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.SingleFactVariable;

/**
 * Collect all PathFilters inside all children of an OrFunctionConditionalElement, returning a List
 * of Lists. Each inner List contains the PathFilters of one child.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class PathFilterConsolidator implements DefaultConditionalElementsVisitor {

	private final SingleFactVariable initialFactVariable;

	@Getter
	private List<List<PathFilter>> pathFilters = null;

	public PathFilterConsolidator consolidate(final ConditionalElement ce) {
		return ce.accept(this);
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		// If there is no OrFunctionConditionalElement just proceed with the CE as it were
		// the only child of an OrFunctionConditionalElement.
		pathFilters =
				Collections.singletonList(ce.accept(
						new NoORsPFC(initialFactVariable, FactVariableCollector.collectPaths(ce)))
						.getPathFilters());
	}

	@Override
	public void visit(final OrFunctionConditionalElement ce) {
		// For each child of the OrCE ...
		pathFilters =
				ce.getChildren()
						.stream()
						.map(child -> child.accept(
						// ... collect all PathFilters in the child
								new NoORsPFC(initialFactVariable, FactVariableCollector
										.collectPaths(child))).getPathFilters())
						.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
	 */
	@RequiredArgsConstructor
	static class NoORsPFC implements DefaultConditionalElementsVisitor {

		private final SingleFactVariable initialFactVariable;
		private final Map<SingleFactVariable, Path> paths;
		private final boolean negated;

		@Getter
		private List<PathFilter> pathFilters = null;

		public NoORsPFC(final SingleFactVariable initialFactVariable,
				final Map<SingleFactVariable, Path> paths) {
			this(initialFactVariable, paths, false);
		}

		private <T extends ConditionalElement> NoORsPFC collect(final T ce) {
			return ce.accept(this);
		}

		static List<PathFilter> processExistentialCondition(
				final SingleFactVariable initialFactVariable, final ConditionalElement ce,
				final Map<SingleFactVariable, Path> fact2Path, final boolean isPositive) {
			// Collect the existential FactVariables and corresponding paths from the existentialCE
			final Map<SingleFactVariable, Path> existentialFact2Path =
					FactVariableCollector.collectPaths(ce);

			// combine existential FactVariables and Paths with non existential ones for PathFilter
			// generation
			final Map<SingleFactVariable, Path> combinedFact2Path =
					new HashMap<SingleFactVariable, Path>(fact2Path);
			combinedFact2Path.putAll(existentialFact2Path);

			// Only existential Paths without Variables
			final Set<Path> existentialPaths = new HashSet<>(existentialFact2Path.values());

			// Generate PathFilters from CE (recurse)
			final List<PathFilter> filters =
					new NoORsPFC(initialFactVariable, combinedFact2Path).collect(ce)
							.getPathFilters();

			// Collect all used Paths for every PathFilter
			final Map<PathFilter, Set<Path>> filter2Paths =
					filters.stream().collect(
							Collectors.toMap(Function.identity(), filter -> PathCollector
									.newHashSet().collect(filter).getPaths()));

			// Split PathFilters into those only using existential Paths and those also using non
			// existential Paths
			final Map<Boolean, LinkedList<PathFilter>> tmp =
					filters.stream().collect(
							Collectors.partitioningBy(filter -> existentialPaths
									.containsAll(filter2Paths.get(filter)),
									toCollection(LinkedList::new)));
			final LinkedList<PathFilter> pureExistentialFilters = tmp.get(Boolean.TRUE);
			final LinkedList<PathFilter> nonPureExistentialFilters = tmp.get(Boolean.FALSE);

			// Add all pureExistentialFilters to result List because they don't have to be combined
			// or ordered
			final List<PathFilter> resultFilters = new ArrayList<>(pureExistentialFilters);

			if (nonPureExistentialFilters.isEmpty()) {
				// if there are only existential filters, append one combining them with an initial
				// fact path
				resultFilters.add(new PathFilter(isPositive, existentialPaths,
						new PathFilter.DummyPathFilterElement(fact2Path.computeIfAbsent(
								initialFactVariable,
								(x) -> new Path(initialFactVariable.getTemplate())))));
				return resultFilters;
			}

			// Construct HashMap from Paths to Filters
			final Map<Path, Set<PathFilter>> path2Filters = new HashMap<>();
			filter2Paths.forEach((pathFilter, paths) -> paths.forEach(path -> path2Filters
					.computeIfAbsent(path, x -> new HashSet<>()).add(pathFilter)));

			// Find connected components of the existential Paths
			final Map<Path, Set<Path>> joinedExistentialPaths = new HashMap<>();
			final Set<Path> processedExistentialPaths = new HashSet<>();
			// While there are unjoined Filters continue
			while (!pureExistentialFilters.isEmpty()) {
				// Take one arbitrary filter
				final LinkedList<PathFilter> collectedFilters =
						new LinkedList<>(Collections.singletonList(pureExistentialFilters.poll()));
				Set<PathFilter> newCollectedFilters = new HashSet<>(collectedFilters);
				final Set<Path> collectedPaths = new HashSet<>();
				// While we found new PathFilters in the last round
				while (!newCollectedFilters.isEmpty()) {
					// search for all Paths used by the new Filters
					final Set<Path> newCollectedPaths =
							newCollectedFilters.stream().flatMap(f -> filter2Paths.get(f).stream())
									.collect(Collectors.toSet());
					// removed already known paths
					newCollectedPaths.removeAll(collectedPaths);
					// add the new ones to the collect set
					collectedPaths.addAll(newCollectedPaths);
					// search for all filters containing the new found paths
					newCollectedFilters =
							newCollectedPaths.stream()
									.flatMap(path -> path2Filters.get(path).stream())
									.collect(toSet());
					// remove already known filters
					newCollectedFilters.removeAll(collectedFilters);
					// add them all to the collect set
					collectedFilters.addAll(newCollectedFilters);
					// remove them from the set of unassigned filters
					pureExistentialFilters.removeAll(newCollectedFilters);
				}
				// save the connected components
				for (final Path path : collectedPaths) {
					joinedExistentialPaths.put(path, collectedPaths);
				}
				// mark the paths as processed
				processedExistentialPaths.addAll(collectedPaths);
			}

			// Combine nonPureExistentialFilters if necessary and add them to result List
			while (!nonPureExistentialFilters.isEmpty()) {
				final List<PathFilter> collectedFilters =
						new ArrayList<>(Collections.singletonList(nonPureExistentialFilters.poll()));
				Set<PathFilter> newCollectedFilters = new HashSet<>(collectedFilters);
				final Set<Path> collectedExistentialPaths = new HashSet<>();

				while (!newCollectedFilters.isEmpty()) {
					// search for all existential Paths used by the new Filters
					final Set<Path> newCollectedExistentialPaths =
							newCollectedFilters.stream()
									.flatMap((final PathFilter f) -> filter2Paths.get(f).stream())
									.collect(toSet());
					newCollectedExistentialPaths.retainAll(existentialPaths);
					// removed already known paths
					newCollectedExistentialPaths.removeAll(collectedExistentialPaths);
					// add all existential paths already joined with the new paths
					{
						final Set<Path> toDeplete = new HashSet<>(newCollectedExistentialPaths);
						while (!toDeplete.isEmpty()) {
							final Path path = toDeplete.iterator().next();
							final Set<Path> joined = joinedExistentialPaths.get(path);
							toDeplete.removeAll(joined);
							newCollectedExistentialPaths.addAll(joined);
						}
					}
					// add the new ones to the collect set
					collectedExistentialPaths.addAll(newCollectedExistentialPaths);
					// search for all filters containing the new found paths
					newCollectedFilters =
							newCollectedExistentialPaths.stream()
									.flatMap(path -> path2Filters.get(path).stream())
									.collect(toSet());
					newCollectedFilters.retainAll(nonPureExistentialFilters);
					// remove already known filters
					newCollectedFilters.removeAll(collectedFilters);
					// add them all to the collect set
					collectedFilters.addAll(newCollectedFilters);
					// remove them from the set of unassigned filters
					nonPureExistentialFilters.removeAll(newCollectedFilters);
				}
				final List<PathFilterElement> filterElements = new ArrayList<>();
				for (final PathFilter filter : collectedFilters) {
					filterElements.addAll(Arrays.asList(filter.getFilterElements()));
				}
				resultFilters.add(new PathFilter(isPositive, collectedExistentialPaths, toArray(
						filterElements, PathFilterElement[]::new)));
				processedExistentialPaths.addAll(collectedExistentialPaths);
			}

			{
				// if not all paths within this existential CE have been used in some test, add a
				// dummy filter element to have them joined, too
				final Set<Path> unprocessedExistentialPaths = new HashSet<>(existentialPaths);
				unprocessedExistentialPaths.removeAll(processedExistentialPaths);
				if (!unprocessedExistentialPaths.isEmpty()) {
					resultFilters.add(new PathFilter(isPositive, unprocessedExistentialPaths,
							new PathFilter.DummyPathFilterElement()));
				}
			}

			return resultFilters;
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			// Just ignore. InittialFactCEs and TemplateCEs already did their job during
			// FactVariable collection
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			pathFilters =
					ce.getChildren()
							.stream()
							// Process all children CEs
							.map(child -> child.accept(new NoORsPFC(initialFactVariable, paths))
									.getPathFilters())
							// merge Lists
							.flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
		}

		@Override
		public void visit(final OrFunctionConditionalElement ce) {
			throw new Error("There should not be any OrFunctionCEs at this level.");
		}

		@Override
		public void visit(final ExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.pathFilters =
					processExistentialCondition(initialFactVariable, ce.getChildren().get(0),
							paths, true);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.pathFilters =
					processExistentialCondition(initialFactVariable, ce.getChildren().get(0),
							paths, false);
		}

		@Override
		public void visit(final NotFunctionConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			// Call a PathFilterCollector for the child of the NotFunctionCE with toggled negated
			// flag.
			this.pathFilters =
					ce.getChildren().get(0)
							.accept(new NoORsPFC(initialFactVariable, paths, !negated))
							.getPathFilters();
		}

		@Override
		public void visit(final SharedConditionalElementWrapper ce) {
			// Just ignore the SharedCEWrapper and continue with the inner CE.
			// TODO maybe it will be required to mark the resulting PathFilters for later
			// optimization
			ce.getCe().accept(this);
		}

		@Override
		public void visit(final TestConditionalElement ce) {
			this.pathFilters =
					Collections.singletonList(new PathFilter(
							new PathFilter.PathFilterElement(SymbolToPathTranslator.translate(
									ce.getPredicateWithArguments(), paths))));
		}
	}
}