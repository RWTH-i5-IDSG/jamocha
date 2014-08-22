/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.dn;

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Charsets;
import org.jamocha.dn.ConstructCache.Deffacts;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryFactory;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.FactAddressCollector;
import org.jamocha.filter.Filter.FilterElement;
import org.jamocha.filter.FilterFunctionCompare;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.clips.ClipsLogFormatter;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.ConditionalElementsVisitor;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.RuleConditionProcessor;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.TypedFilter;

/**
 * The Network class encapsulates the central objects for {@link MemoryFactory} and
 * {@link Scheduler} which are required all over the whole discrimination network.
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @see MemoryFactory
 * @see Scheduler
 * @see Node
 */
@Getter
public class Network implements ParserToNetwork, SideEffectFunctionToNetwork {

	/**
	 * -- GETTER --
	 * 
	 * Gets the memoryFactory to generate the nodes {@link MemoryHandlerMain} and
	 * {@link MemoryHandlerPlusTemp}.
	 * 
	 * @return the networks memory Factory
	 */
	private final MemoryFactory memoryFactory;

	/**
	 * -- GETTER --
	 * 
	 * Gets the capacity of the token queues in all token processing {@link Node nodes}.
	 * 
	 * @return the capacity for token queues
	 */
	private final int tokenQueueCapacity;

	/**
	 * -- GETTER --
	 * 
	 * Gets the scheduler handling the dispatching of token processing to different threads.
	 * 
	 * @return the networks scheduler
	 */
	private final Scheduler scheduler;

	/**
	 * -- GETTER --
	 * 
	 * Gets the {@link RootNode} of the network.
	 * 
	 * @return the {@link RootNode} of the network
	 */
	private final RootNode rootNode;

	/**
	 * -- GETTER --
	 * 
	 * Gets the {@link ConflictSet conflict set}.
	 * 
	 * @return conflict set
	 */
	private final ConflictSet conflictSet = new ConflictSet();

	@Getter
	private final ConstructCache constructCache = new ConstructCache();

	@Getter(AccessLevel.PRIVATE)
	private static int loggerDiscriminator = 0;
	@Getter(onMethod = @__(@Override))
	private final Logger interactiveEventsLogger = LogManager.getLogger(this.getClass()
			.getCanonicalName() + Integer.valueOf(loggerDiscriminator++).toString());

	@Getter(onMethod = @__(@Override))
	private final TypedFilter typedFilter = new TypedFilter(true);

	@Getter(onMethod = @__(@Override))
	private final LogFormatter logFormatter;

	/**
	 * Creates a new network object.
	 * 
	 * @param memoryFactory
	 *            the {@link MemoryFactory} to use in the created network
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing {@link Node nodes}
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token processing
	 */
	public Network(final MemoryFactory memoryFactory, final LogFormatter logFormatter,
			final int tokenQueueCapacity, final Scheduler scheduler) {
		this.memoryFactory = memoryFactory;
		this.tokenQueueCapacity = tokenQueueCapacity;
		this.scheduler = scheduler;
		this.rootNode = new RootNode();
		this.logFormatter = logFormatter;
		{
			// there seem to be two different log levels: one in the logger (aka in the
			// PrivateConfig of the logger) and one in the LoggerConfig (which may be shared); the
			// two values can be synchronized via LoggerContext::updateLoggers (shared -> private)

			// making a change (additive, appender, filter) to the logger leads to the creation of
			// an own logger config for our logger
			((org.apache.logging.log4j.core.Logger) this.interactiveEventsLogger)
					.setAdditive(false);
			final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			final Configuration config = ctx.getConfiguration();
			final LoggerConfig loggerConfig =
					config.getLoggerConfig(this.getInteractiveEventsLogger().getName());
			// the normal constructor is private, thus we have to use the plugin-level access
			final Appender appender =
					ConsoleAppender.createAppender(PatternLayout.createLayout(
							// PatternLayout.SIMPLE_CONVERSION_PATTERN
							PatternLayout.DEFAULT_CONVERSION_PATTERN, config, null, Charsets.UTF_8,
							true, true, "", ""), null, Target.SYSTEM_OUT.name(), "consoleAppender",
							"true", "true");
			// loggerConfig.getAppenders().forEach((n, a) -> loggerConfig.removeAppender(n));
			// loggerConfig.setAdditive(false);
			loggerConfig.setLevel(Level.ALL);
			loggerConfig.addFilter(typedFilter);
			loggerConfig.addAppender(appender, Level.ALL, null);
			config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
			// This causes all loggers to re-fetch information from their LoggerConfig
			ctx.updateLoggers();
		}
	}

	/**
	 * Creates a new network object using the {@link ClipsLogFormatter}.
	 * 
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing {@link Node nodes}
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token processing
	 */
	public Network(final MemoryFactory memoryFactory, final int tokenQueueCapacity,
			final Scheduler scheduler) {
		this(memoryFactory, ClipsLogFormatter.getMessageFormatter(), tokenQueueCapacity, scheduler);
	}

	/**
	 * Creates a new network object with the {@link org.jamocha.dn.memory.javaimpl default memory
	 * implementation}.
	 * 
	 * @param tokenQueueCapacity
	 *            the capacity of the token queues in all token processing {@link Node nodes}
	 * @param scheduler
	 *            the {@link Scheduler} to handle the dispatching of token processing
	 */
	public Network(final int tokenQueueCapacity, final Scheduler scheduler) {
		this(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), ClipsLogFormatter
				.getMessageFormatter(), tokenQueueCapacity, scheduler);
	}

	/**
	 * Creates a new network object with the {@link org.jamocha.dn.memory.javaimpl default memory
	 * implementation} and {@link ThreadPoolScheduler scheduler}.
	 */
	public Network() {
		this(Integer.MAX_VALUE, new ThreadPoolScheduler(10));
	}

	/**
	 * Tries to find a node performing the same filtering as the given filter and calls
	 * {@link Node#shareNode(Path...)} or creates a new {@link Node} for the given
	 * {@link PathFilter filter}. Returns true iff a {@link Node} to share was found.
	 * 
	 * @param filter
	 *            {@link PathFilter} to find a corresponding {@link Node} for
	 * @return true iff a {@link Node} to share was found
	 * @throws IllegalArgumentException
	 *             thrown if one of the {@link Path}s in the {@link PathFilter} was not mapped to a
	 *             {@link Node}
	 */
	public boolean tryToShareNode(final PathFilter filter) throws IllegalArgumentException {
		final Path[] paths = PathCollector.newLinkedHashSet().collect(filter).getPathsArray();

		// collect the nodes of the paths
		final LinkedHashSet<Node> filterPathNodes = new LinkedHashSet<>();
		for (final Path path : paths) {
			filterPathNodes.add(path.getCurrentlyLowestNode());
		}
		if (filterPathNodes.contains(null)) {
			throw new IllegalArgumentException("Paths did not point to any nodes.");
		}
		// collect all nodes which have edges to all of the paths nodes as candidates
		final LinkedHashSet<Node> candidates = identifyShareCandidates(filterPathNodes);

		// get normal version of filter to share
		final PathFilter normalisedFilter = filter.normalise();

		// check candidates for possible node sharing
		candidateLoop: for (final Node candidate : candidates) {
			final AddressFilter candidateFilter = candidate.getFilter();

			// check if filter matches
			if (!FilterFunctionCompare.equals(candidate, normalisedFilter))
				continue candidateLoop;

			final FactAddress[] addressesInTarget =
					FactAddressCollector.newLinkedHashSet().collect(candidateFilter)
							.getAddressesArray();
			assert addressesInTarget.length == paths.length;
			for (int i = 0; i < addressesInTarget.length; ++i) {
				final FactAddress address = addressesInTarget[i];
				final Path path = paths[i];
				// de-localize address
				if (candidate.delocalizeAddress(address).getAddress() != path
						.getFactAddressInCurrentlyLowestNode())
					continue candidateLoop;
			}
			candidate.shareNode(paths);
			return true;
		}
		return false;
	}

	private LinkedHashSet<Node> identifyShareCandidates(final LinkedHashSet<Node> filterPathNodes) {
		final LinkedHashSet<Node> candidates = new LinkedHashSet<>();
		assert filterPathNodes.size() > 0;
		final Iterator<Node> filterPathNodesIterator = filterPathNodes.iterator();

		// add all children of the first node
		final Collection<Edge> firstNodesOutgoingPositiveEdges =
				filterPathNodesIterator.next().getOutgoingEdges();
		for (final Edge edge : firstNodesOutgoingPositiveEdges) {
			try {
				candidates.add(edge.getTargetNode());
			} catch (final UnsupportedOperationException e) {
				// triggered by terminal node, just don't add it
			}
		}

		// remove all nodes which aren't children of all other nodes
		while (filterPathNodesIterator.hasNext()) {
			final Node node = filterPathNodesIterator.next();
			final HashSet<Node> cutSet = new HashSet<>();
			for (final Edge edge : node.getOutgoingEdges()) {
				try {
					cutSet.add(edge.getTargetNode());
				} catch (final UnsupportedOperationException e) {
					// triggered by terminal node, just don't add it
				}
			}
			candidates.retainAll(cutSet);
		}
		return candidates;
	}

	/**
	 * Creates network nodes for one rule, consisting of the passed filters.
	 * 
	 * @param filters
	 *            list of filters in order of implementation in the network. Each filter is
	 *            implemented in a separate node. Node-Sharing is used if possible
	 * @return created TerminalNode for the constructed rule
	 */
	public TerminalNode buildRule(final PathFilter... filters) {
		final LinkedHashSet<Path> allPaths = new LinkedHashSet<>();
		{
			for (final PathFilter filter : filters) {
				final LinkedHashSet<Path> paths =
						PathCollector.newLinkedHashSet().collect(filter).getPaths();
				allPaths.addAll(paths);
			}
			final Path[] pathArray = toArray(allPaths, Path[]::new);
			this.rootNode.addPaths(this, pathArray);
		}
		for (final PathFilter filter : filters) {
			if (!tryToShareNode(filter))
				if (PathCollector.newLinkedHashSet().collect(filter).getPaths().size() == 1) {
					new AlphaNode(this, filter);
				} else {
					new BetaNode(this, filter);
				}
		}
		final Node lowestNode = allPaths.iterator().next().getCurrentlyLowestNode();
		return new TerminalNode(this, lowestNode);
	}

	@Override
	public FactIdentifier[] assertFacts(final Fact... facts) {
		final FactIdentifier[] assertedFacts = getRootNode().assertFacts(facts);
		getLogFormatter().messageFactAssertions(this, assertedFacts);
		return assertedFacts;
	}

	@Override
	public void retractFacts(final FactIdentifier... factIdentifiers) {
		getLogFormatter().messageFactRetractions(this, factIdentifiers);
		getRootNode().retractFacts(factIdentifiers);
	}

	@Override
	public MemoryFact getMemoryFact(final FactIdentifier factIdentifier) {
		return getRootNode().getMemoryFact(factIdentifier);
	}

	@Override
	public Map<FactIdentifier, MemoryFact> getMemoryFacts() {
		return getRootNode().getMemoryFacts();
	}

	@Override
	public Template defTemplate(final String name, final String description, final Slot... slots) {
		final Template template = getMemoryFactory().newTemplate(name, description, slots);
		getRootNode().putOTN(new ObjectTypeNode(this, template));
		this.constructCache.addTemplate(template);
		return template;
	}

	@Override
	public Template getTemplate(final String name) {
		return this.constructCache.getTemplate(name);
	}

	@Override
	public Collection<Template> getTemplates() {
		return this.constructCache.getTemplates();
	}

	@Override
	public Deffacts defFacts(final String name, final String description,
			final TemplateContainer... containers) {
		final List<TemplateContainer> conList = Arrays.asList(containers);
		final Deffacts deffacts = new Deffacts(name, description, conList);
		this.constructCache.addDeffacts(deffacts);
		assertFacts(toArray(conList.stream().map(TemplateContainer::toFact), Fact[]::new));
		return deffacts;
	}

	@Override
	public Deffacts getDeffacts(final String name) {
		return this.constructCache.getDeffacts(name);
	}

	@Override
	public Collection<Deffacts> getDeffacts() {
		return this.constructCache.getDeffacts();
	}

	@Override
	public void defRules(final Defrule... defrules) {
		for (final Defrule defrule : defrules) {
			// TODO compile
			this.constructCache.addRule(defrule);
			for (List<PathFilter> filters : this.compileRule(defrule.getCondition())) {
				this.buildRule(filters.toArray(new PathFilter[filters.size()]));
			}
		}
	}

	@Override
	public Defrule getRule(final String name) {
		return this.constructCache.getRule(name);
	}

	@Override
	public Collection<Defrule> getRules() {
		return this.constructCache.getRules();
	}

	@Override
	public void reset() {
		getRootNode().reset();
		// assert all deffacts
		assertFacts(toArray(
				this.constructCache.getDeffacts().stream()
						.flatMap(def -> def.getContainers().stream())
						.map(TemplateContainer::toFact), Fact[]::new));
	}

	private List<List<PathFilter>> compileRule(RuleCondition condition) {
		final List<ConditionalElement> conditionalElements = condition.getConditionalElements();
		// Preprocess CEs
		RuleConditionProcessor.flatten(conditionalElements);
		assert conditionalElements.size() == 1;
		// Transform TestCEs to PathFilters and collect them
		return conditionalElements.get(0).accept(new OrPathFilterCollector()).getPathFilters();
	}

	/*
	 * Collect all PathFilters inside all children of an OrFunctionConditionalElement, returning a
	 * List of Lists. Each inner List contains the PathFilters of one child.
	 */
	private static class OrPathFilterCollector implements DefaultConditionalElementsVisitor {

		@Getter
		private List<List<PathFilter>> pathFilters = null;

		@Override
		public void defaultAction(ConditionalElement ce) {
			// If there is no OrFunctionConditionalElement just proceed with the CE as it were
			// the only child of an OrFunctionConditionalElement.
			pathFilters =
					Arrays.asList(ce.accept(
							new PathFilterCollector(FactVariableCollector.collectPaths(ce)))
							.getPathFilters());
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			// For each child of the OrCE ...
			pathFilters =
					ce.getChildren()
							.stream()
							.map(child -> child.accept(
							// ... collect all PathFilters in the child
									new PathFilterCollector(FactVariableCollector
											.collectPaths(child))).getPathFilters())
							.collect(Collectors.toCollection(ArrayList::new));
		}
	}

	@RequiredArgsConstructor
	private static class PathFilterCollector implements DefaultConditionalElementsVisitor {

		private final Map<SingleFactVariable, Path> paths;
		private final boolean negated;

		@Getter
		private List<PathFilter> pathFilters = null;

		public PathFilterCollector(Map<SingleFactVariable, Path> paths) {
			this.paths = paths;
			this.negated = false;
		}

		public static List<PathFilter> processExistentialCondition(final ConditionalElement ce,
				final Map<SingleFactVariable, Path> fact2Path, final boolean isPositive) {
			// FIXME Fix to work if we only have existential Paths

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

			// Generate PathFilters from CE
			final List<PathFilter> filters =
					ce.accept(new PathFilterCollector(combinedFact2Path)).getPathFilters();

			// Collect all used Paths for every PathFilter
			final Map<PathFilter, Set<Path>> filter2Paths =
					filters.stream().collect(
							Collectors.toMap(filter -> filter, filter -> PathCollector.newHashSet()
									.collect(filter).getPaths()));

			// Split PathFilters into those only using existential Paths and those also using non
			// existential Paths
			final Map<Boolean, List<PathFilter>> tmp =
					filters.stream().collect(
							Collectors.partitioningBy(filter -> existentialPaths
									.containsAll(filter2Paths.get(filter))));
			final List<PathFilter> nonPureExistentialFilters = tmp.get(false);
			final List<PathFilter> pureExistentialFilters = tmp.get(true);

			// Add all pureExistentialFilters to result List because they don't have to be combined
			// or ordered
			final List<PathFilter> resultFilters = new ArrayList<>(pureExistentialFilters);

			// Construct Hashmap from Paths to Filters
			final Map<Path, Set<PathFilter>> path2Filters = new HashMap<>();
			for (Entry<PathFilter, Set<Path>> filterAndPaths : filter2Paths.entrySet()) {
				for (final Path path : filterAndPaths.getValue()) {
					Set<PathFilter> value = path2Filters.get(path);
					if (value == null) {
						value = new HashSet<>();
						path2Filters.put(path, value);
					}
					value.add(filterAndPaths.getKey());
				}
			}

			// Find connected components of the existential Paths
			final Set<Set<Path>> joinedExistentialPaths = new HashSet<>();
			// While there are unjoined Filters continue
			final Set<PathFilter> reductionSet = new HashSet<>(pureExistentialFilters);
			while (!reductionSet.isEmpty()) {
				// Take one arbitrary filter
				final Iterator<PathFilter> i = reductionSet.iterator();
				final Set<PathFilter> collectFilters = new HashSet<>();
				collectFilters.add(i.next());
				i.remove();
				Set<PathFilter> newCollectFilters = new HashSet<>(collectFilters);
				final Set<Path> collectPaths = new HashSet<>();
				// While we found new PathFilters in the last round
				while (!newCollectFilters.isEmpty()) {
					// search for all Paths used by the new Filters
					final Set<Path> newCollectPaths =
							newCollectFilters.stream().flatMap(f -> filter2Paths.get(f).stream())
									.collect(Collectors.toSet());
					// removed already known paths
					newCollectPaths.removeAll(collectPaths);
					// add the new ones to the collect set
					collectPaths.addAll(newCollectPaths);
					// search for all filters containing the new found paths
					newCollectFilters =
							newCollectPaths.stream()
									.flatMap(path -> path2Filters.get(path).stream())
									.collect(Collectors.toSet());
					// remove already known filters
					newCollectFilters.removeAll(collectFilters);
					// add them all to the collect set
					collectFilters.addAll(newCollectFilters);
					// remove them from the set of unassigned filters
					reductionSet.removeAll(newCollectFilters);
				}
				// added the join set to the result
				joinedExistentialPaths.add(collectPaths);
			}

			// Combine nonPureExistentialFilters if necessary and add them to result List
			while (!nonPureExistentialFilters.isEmpty()) {
				final Iterator<PathFilter> i = nonPureExistentialFilters.iterator();
				final List<PathFilter> collectFilters = Arrays.asList(i.next());
				i.remove();
				List<PathFilter> newCollectFilters = new ArrayList<>(collectFilters);
				final Set<Path> collectExistentialPaths = new HashSet<>();

				while (!newCollectFilters.isEmpty()) {
					// search for all existential Paths used by the new Filters
					final Set<Path> newCollectExistentialPaths =
							newCollectFilters.stream()
									.flatMap((PathFilter f) -> filter2Paths.get(f).stream())
									.collect(Collectors.toSet());
					// removed already known paths
					newCollectExistentialPaths.retainAll(existentialPaths);
					newCollectExistentialPaths.removeAll(collectExistentialPaths);
					// add the new ones to the collect set
					collectExistentialPaths.addAll(newCollectExistentialPaths);
					// search for all filters containing the new found paths
					newCollectFilters =
							newCollectExistentialPaths.stream()
									.flatMap(path -> path2Filters.get(path).stream())
									.collect(Collectors.toList());
					// remove already known filters
					newCollectFilters.retainAll(nonPureExistentialFilters);
					newCollectFilters.removeAll(collectFilters);
					// add them all to the collect set
					collectFilters.addAll(newCollectFilters);
					// remove them from the set of unassigned filters
					reductionSet.removeAll(newCollectFilters);
				}
				List<PathFilterElement> filterElements = new ArrayList<>();
				for (final PathFilter filter : collectFilters) {
					filterElements.addAll(Arrays.asList(filter.getFilterElements()));
				}

				if (isPositive)
					resultFilters.add(new PathFilter(collectExistentialPaths, new HashSet<>(),
							toArray(filterElements, PathFilterElement[]::new)));
				else
					resultFilters.add(new PathFilter(new HashSet<>(), collectExistentialPaths,
							toArray(filterElements, PathFilterElement[]::new)));
			}

			return resultFilters;
		}

		@Override
		public void defaultAction(ConditionalElement ce) {
			// Just ignore. InittialFactCEs and TemplateCEs already did their job during
			// FactVariable collection
		}

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			pathFilters = new ArrayList<>();
			pathFilters = ce.getChildren().stream()
			// Process all children CEs
					.map(child -> child.accept(new PathFilterCollector(paths)).getPathFilters())
					// merge Lists
					.flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			throw new Error("There should not be any OrFunctionCEs at this level.");
		}

		@Override
		public void visit(ExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.pathFilters = processExistentialCondition(ce.getChildren().get(0), paths, true);
		}

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.pathFilters = processExistentialCondition(ce.getChildren().get(0), paths, false);
		}

		@Override
		public void visit(NotFunctionConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			// Call a PathFilterCollector for the child of the NotFunctionCE with toggeled negated
			// flag.
			this.pathFilters =
					ce.getChildren().get(0).accept(new PathFilterCollector(paths, !negated))
							.getPathFilters();
		}

		@Override
		public void visit(SharedConditionalElementWrapper ce) {
			// Just ignore the SharedCEWrapper and continue with the inner CE.
			// TODO maybe it will be required to mark the resulting PathFilters for later
			// optimization
			ce.getCe().accept(this);
		}

		@Override
		public void visit(TestConditionalElement ce) {
			// FIXME just copied from old version
			assert (ce.getFwa() instanceof PredicateWithArguments);
			// TODO remove cast
			PredicateWithArguments pwa = ce.getFwa();
			pwa.accept(new SymbolToPathTranslator(paths));
			this.pathFilters = Arrays.asList(new PathFilter(new PathFilter.PathFilterElement(pwa)));
		}
	}

	private static class FactVariableCollector implements DefaultConditionalElementsVisitor {

		@Getter
		private List<SingleFactVariable> factVariables = null;

		private static Map<SingleFactVariable, Path> collectPaths(ConditionalElement ce) {
			// Collect all FactVariables defined in the CEs TemplateCEs and InitialFactCEs
			return ce
					.accept(new FactVariableCollector())
					.getFactVariables()
					.stream()
					// Create Paths with the corresponding Templates
					// for all collected FactVariables
					.collect(
							Collectors.toMap(
									variable -> variable,
									(SingleFactVariable variable) -> new Path(variable
											.getTemplate())));
		}

		@Override
		public void defaultAction(ConditionalElement ce) {
			// Just ignore all other ConditionalElements
		}

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			throw new Error("There should not be any Or ConditionalElements at this level.");
		}

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			ce.getChildren().stream()
					.map(child -> child.accept(new FactVariableCollector()).getFactVariables())
					.flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
		}

		@Override
		public void visit(SharedConditionalElementWrapper ce) {
			ce.getCe().accept(this);
		}

		@Override
		public void visit(TemplatePatternConditionalElement ce) {
			factVariables = Arrays.asList(ce.getFactVariable());
		}

		@Override
		public void visit(InitialFactConditionalElement ce) {
			factVariables = Arrays.asList(ce.getInitialFactVariable());
		}
	}

	private static class SymbolToPathTranslator implements FunctionWithArgumentsVisitor {

		@Getter
		private FunctionWithArguments result;
		private final Map<SingleFactVariable, Path> paths;

		public SymbolToPathTranslator(Map<SingleFactVariable, Path> paths) {
			this.paths = paths;
		}

		@Override
		public void visit(FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			FunctionWithArguments[] args = functionWithArgumentsComposite.getArgs();
			for (int i = 0; i < args.length; i++) {
				SymbolToPathTranslator trans = new SymbolToPathTranslator(paths);
				args[i].accept(trans);
				args[i] = trans.getResult();
			}
			result = functionWithArgumentsComposite;
		}

		@Override
		public void visit(PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			FunctionWithArguments[] args = predicateWithArgumentsComposite.getArgs();
			for (int i = 0; i < args.length; i++) {
				SymbolToPathTranslator trans = new SymbolToPathTranslator(paths);
				args[i].accept(trans);
				args[i] = trans.getResult();
			}
			result = predicateWithArgumentsComposite;
		}

		@Override
		public void visit(ConstantLeaf constantLeaf) {
			result = constantLeaf;
		}

		@Override
		public void visit(ParameterLeaf parameterLeaf) {
			throw new Error("ParameterLeaf should not exists at this stage");
		}

		@Override
		public void visit(PathLeaf pathLeaf) {
			throw new Error("PathLeaf should not exists at this stage");
		}

		@Override
		public void visit(Assert fwa) {
			throw new Error("Assert in predicate");
		}

		@Override
		public void visit(TemplateContainer fwa) {
			throw new Error("TemplateContainer should not exists at this stage");
		}

		@Override
		public void visit(Retract fwa) {
			throw new Error("Retract in predicate");
		}

		@Override
		public void visit(Modify fwa) {
			throw new Error("Modify in predicate");
		}

		@Override
		public void visit(SlotAndValue fwa) {
			throw new Error("SlotAndValue in predicate");
		}

		@Override
		public void visit(SymbolLeaf fwa) {
			assert (fwa.getSymbol().getPositiveSlotVariables().size() > 0);
			final SingleSlotVariable variable = fwa.getSymbol().getPositiveSlotVariables().get(0);
			final Path path = this.paths.get(variable.getFactVariable());
			result = new PathLeaf(path, variable.getSlot());
		}
	}

	/**
	 * A default network object with a basic setup, used for testing and other quick and dirty
	 * networks.
	 */
	public final static Network DEFAULTNETWORK = new Network(
			org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(),
			ClipsLogFormatter.getMessageFormatter(), Integer.MAX_VALUE,
			// new ThreadPoolScheduler(10)
			new PlainScheduler());
}