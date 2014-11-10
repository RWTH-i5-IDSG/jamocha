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

import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jamocha.dn.ConflictSet.RuleAndToken;
import org.jamocha.dn.ConstructCache.Deffacts;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.TranslatedPath;
import org.jamocha.dn.compiler.PathFilterConsolidator;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryFactToFactIdentifier;
import org.jamocha.dn.memory.MemoryFactory;
import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.dn.nodes.BetaNode;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.dn.nodes.RootNode;
import org.jamocha.dn.nodes.TerminalNode;
import org.jamocha.filter.FilterFunctionCompare;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.languages.clips.ClipsLogFormatter;
import org.jamocha.languages.common.RuleConditionProcessor;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.logging.LayoutAdapter;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.OutstreamAppender;
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
	private final ConflictSet conflictSet;

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

	@Getter(onMethod = @__(@Override))
	private final ScopeStack scope = new ScopeStack();

	@Getter(onMethod = @__(@Override))
	private Template initialFactTemplate;

	@Getter(onMethod = @__(@Override))
	final EnumMap<SlotType, Object> defaultValues = new EnumMap<>(SlotType.class);

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
		this.conflictSet = new ConflictSet(this);
		this.rootNode = new RootNode();
		this.logFormatter = logFormatter;
		createInitialDeffact();
		createDummyTemplate();
		reset();
		{
			for (final SlotType type : EnumSet.allOf(SlotType.class)) {
				switch (type) {
				case BOOLEAN:
					defaultValues.put(type, Boolean.FALSE);
					break;
				case DATETIME:
					defaultValues.put(type, ZonedDateTime.now());
					break;
				case DOUBLE:
					defaultValues.put(type, Double.valueOf(0.0));
					break;
				case LONG:
					defaultValues.put(type, Long.valueOf(0));
					break;
				case NIL:
					defaultValues.put(type, null);
					break;
				case STRING:
					defaultValues.put(type, "");
					break;
				case SYMBOL:
					defaultValues.put(type, this.getScope().getOrCreateSymbol("nil"));
					break;
				case FACTADDRESS:
					// should be handled by createDummyFact()
					assert null != defaultValues.get(SlotType.FACTADDRESS);
					break;
				}
			}
		}
		FunctionDictionary.load();
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
					ConsoleAppender.createAppender(LayoutAdapter.createLayout(config),
							(Filter) null, Target.SYSTEM_OUT.name(), "consoleAppender", "true",
							"true");
			// loggerConfig.getAppenders().forEach((n, a) -> loggerConfig.removeAppender(n));
			// loggerConfig.setAdditive(false);
			loggerConfig.setLevel(Level.ALL);
			loggerConfig.addFilter(typedFilter);
			loggerConfig.addAppender(appender, Level.ALL, (Filter) null);
			config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
			// This causes all loggers to re-fetch information from their LoggerConfig
			ctx.updateLoggers();
		}
	}

	private void createInitialDeffact() {
		this.initialFactTemplate = defTemplate("initial-fact", "");
		defFacts("initial-fact", "", new TemplateContainer(initialFactTemplate));
	}

	private void createDummyTemplate() {
		this.defTemplate("dummy-fact", "used as default value for FACT-ADDRESS");
	}

	private void createDummyFact() {
		final FactIdentifier[] factIdentifiers =
				assertFacts(new TemplateContainer(this.constructCache.getTemplate("dummy-fact"))
						.toFact());
		assert 1 == factIdentifiers.length;
		this.defaultValues.put(SlotType.FACTADDRESS, factIdentifiers[0]);
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
		final Path[] paths = PathCollector.newLinkedHashSet().collectAll(filter).getPathsArray();

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
		for (final Node candidate : candidates) {
			// check if filter matches
			final Map<Path, FactAddress> map =
					FilterFunctionCompare.equals(candidate, normalisedFilter);
			if (null == map)
				continue;

			candidate.shareNode(map, paths);
			return true;
		}
		return false;
	}

	private LinkedHashSet<Node> identifyShareCandidates(final LinkedHashSet<Node> filterPathNodes) {
		final LinkedHashSet<Node> candidates = new LinkedHashSet<>();
		assert filterPathNodes.size() > 0;
		final Iterator<Node> filterPathNodesIterator = filterPathNodes.iterator();

		// TODO existential edges???
		// add all children of the first node
		final Collection<Edge> firstNodesOutgoingEdges =
				filterPathNodesIterator.next().getOutgoingEdges();
		for (final Edge edge : firstNodesOutgoingEdges) {
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
	public TerminalNode buildRule(final Defrule.TranslatedPath translatedPath,
			final PathFilter... filters) {
		final LinkedHashSet<Path> allPaths;
		{
			final PathCollector<LinkedHashSet<Path>> collector = PathCollector.newLinkedHashSet();
			for (final PathFilter filter : filters) {
				collector.collectAll(filter);
			}
			this.rootNode.addPaths(this, collector.getPathsArray());
			allPaths = collector.getPaths();
		}
		for (final PathFilter filter : filters) {
			if (!tryToShareNode(filter))
				if (PathCollector.newLinkedHashSet().collectAll(filter).getPaths().size() == 1) {
					new AlphaNode(this, filter);
				} else {
					new BetaNode(this, filter);
				}
		}
		final Node lowestNode = allPaths.iterator().next().getCurrentlyLowestNode();
		assert allPaths.stream().map(Path::getCurrentlyLowestNode).distinct().count() == 1;
		return new TerminalNode(this, lowestNode, translatedPath);
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
	public MemoryFactToFactIdentifier getMemoryFactToFactIdentifier() {
		return getRootNode();
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
			this.compileRule(defrule);
			for (final TranslatedPath translated : defrule.getTranslatedPathVersions()) {
				buildRule(translated, toArray(translated.getCondition(), PathFilter[]::new));
			}
			// add the rule and the contained translated versions to the construct cache
			this.constructCache.addRule(defrule);
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
		createDummyFact();
		// assert all deffacts
		assertFacts(toArray(
				this.constructCache.getDeffacts().stream()
						.flatMap(def -> def.getContainers().stream())
						.map(TemplateContainer::toFact), Fact[]::new));
	}

	@Override
	public void clear() {
		this.rootNode.clear();
		this.conflictSet.flush();
		this.constructCache.clear();
		createInitialDeffact();
		createDummyTemplate();
		reset();
	}

	@Override
	public void run(final long maxNumRules) {
		long numRules = 0;
		do {
			this.scheduler.waitForNoUnfinishedJobs();
			conflictSet.deleteRevokedEntries();
			final Optional<RuleAndToken> optional =
					ConflictResolutionStrategy.random.pick(this.conflictSet);
			if (!optional.isPresent())
				break;
			final RuleAndToken ruleAndToken = optional.get();
			this.logFormatter.messageRuleFiring(this, ruleAndToken.getRule(),
					(Assert) ruleAndToken.getToken());
			ruleAndToken.getRule().getActionList().evaluate(ruleAndToken.getToken());
			this.conflictSet.remove(ruleAndToken);
			++numRules;
		} while (0L == maxNumRules || numRules < maxNumRules);
	}

	public void shutdown() {
		scheduler.shutdown();
	}

	public void shutdownNow() {
		scheduler.shutdownNow();
	}

	private void compileRule(final Defrule rule) {
		// Preprocess CEs
		RuleConditionProcessor.flatten(rule.getCondition());
		// Transform TestCEs to PathFilters
		new PathFilterConsolidator(this.initialFactTemplate, rule).consolidate();
	}

	/**
	 * Adds an appender to the logging framework.
	 * 
	 * @param out
	 *            output stream
	 * @param plain
	 *            true for just the content, false for log-style additional infos in front of the
	 *            content
	 */
	public void addAppender(final OutputStream out, final boolean plain) {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		final LoggerConfig loggerConfig =
				config.getLoggerConfig(this.getInteractiveEventsLogger().getName());
		loggerConfig.addAppender(OutstreamAppender.newInstance(out,
				LayoutAdapter.createLayout(config, plain), null, true), Level.ALL, (Filter) null);
		// This causes all loggers to re-fetch information from their LoggerConfig
		ctx.updateLoggers();
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