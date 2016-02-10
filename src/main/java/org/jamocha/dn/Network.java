/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jamocha.dn.ConflictSet.RuleAndToken;
import org.jamocha.dn.ConstructCache.Deffacts;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.compiler.RuleCompiler;
import org.jamocha.dn.memory.*;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.nodes.*;
import org.jamocha.filter.*;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.impls.predicates.DummyPredicate;
import org.jamocha.languages.clips.ClipsLogFormatter;
import org.jamocha.languages.clips.parser.SFPToCETranslator;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.clips.parser.generated.SimpleNode;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.logging.LayoutAdapter;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.OutstreamAppender;
import org.jamocha.logging.TypedFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;
import static org.jamocha.util.ToArray.toArray;

/**
 * The Network class encapsulates the central objects for {@link MemoryFactory} and {@link Scheduler} which are required
 * all over the whole discrimination network.
 *
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see MemoryFactory
 * @see Scheduler
 * @see Node
 */
@Getter
public class Network implements ParserToNetwork, SideEffectFunctionToNetwork {

    /**
     * -- GETTER --
     *
     * Gets the memoryFactory to generate the nodes {@link MemoryHandlerMain} and {@link MemoryHandlerPlusTemp}.
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

    /**
     * -- GETTER --
     *
     * Gets the {@link Set} of {@link TerminalNode TerminalNodes}.
     *
     * @return {@link Set} of {@link TerminalNode TerminalNodes}.
     */
    private final Set<TerminalNode> terminalNodes = new HashSet<>();

    @Getter
    private final ConstructCache constructCache = new ConstructCache();

    @Getter(AccessLevel.PRIVATE)
    private static int loggerDiscriminator = 0;
    @Getter(onMethod = @__(@Override))
    private final Logger interactiveEventsLogger =
            LogManager.getLogger(this.getClass().getCanonicalName() + Integer.toString(loggerDiscriminator++));

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

    @Setter(onMethod = @__(@Override))
    private RuleCompiler ruleCompiler = RuleCompiler.ECBLOCKS;

    boolean haltWasCalled = false;

    /**
     * Creates a new network object.
     *
     * @param memoryFactory
     *         the {@link MemoryFactory} to use in the created network
     * @param logFormatter
     *         log formatter
     * @param tokenQueueCapacity
     *         the capacity of the token queues in all token processing {@link Node nodes}
     * @param scheduler
     *         the {@link Scheduler} to handle the dispatching of token processing
     */
    public Network(final MemoryFactory memoryFactory, final LogFormatter logFormatter, final int tokenQueueCapacity,
            final Scheduler scheduler) {
        this.memoryFactory = memoryFactory;
        this.tokenQueueCapacity = tokenQueueCapacity;
        this.scheduler = scheduler;
        this.conflictSet = new ConflictSet(this, ConflictResolutionStrategy.DEPTH);
        this.rootNode = new RootNode(memoryFactory);
        this.logFormatter = logFormatter;
        createInitialDeffact();
        createDummyTemplate();
        reset();
        {
            for (final SlotType type : EnumSet.allOf(SlotType.class)) {
                switch (type) {
                case BOOLEAN:
                    this.defaultValues.put(type, Boolean.FALSE);
                    break;
                case DATETIME:
                    this.defaultValues.put(type, ZonedDateTime.now());
                    break;
                case DOUBLE:
                    this.defaultValues.put(type, Double.valueOf(0.0));
                    break;
                case LONG:
                    this.defaultValues.put(type, Long.valueOf(0));
                    break;
                case NIL:
                    this.defaultValues.put(type, null);
                    break;
                case STRING:
                    this.defaultValues.put(type, "");
                    break;
                case SYMBOL:
                    this.defaultValues.put(type, this.getScope().getOrCreateSymbol("nil"));
                    break;
                case FACTADDRESS:
                    // should be handled by createDummyFact()
                    assert null != this.defaultValues.get(SlotType.FACTADDRESS);
                    break;
                case BOOLEANS:
                case DATETIMES:
                case DOUBLES:
                case FACTADDRESSES:
                case LONGS:
                case NILS:
                case STRINGS:
                case SYMBOLS:
                    this.defaultValues.put(type, Array.newInstance(type.getJavaClass(), 0));
                    break;
                }
            }
        }
        {
            // there seem to be two different log levels: one in the logger (aka in the
            // PrivateConfig of the logger) and one in the LoggerConfig (which may be shared); the
            // two values can be synchronized via LoggerContext::updateLoggers (shared -> private)

            // making a change (additive, appender, filter) to the logger leads to the creation of
            // an own logger config for our logger
            ((org.apache.logging.log4j.core.Logger) this.interactiveEventsLogger).setAdditive(false);
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final Configuration config = ctx.getConfiguration();
            final LoggerConfig loggerConfig = config.getLoggerConfig(this.getInteractiveEventsLogger().getName());
            // the normal constructor is private, thus we have to use the plugin-level access
            final Appender appender = ConsoleAppender
                    .createAppender(LayoutAdapter.createLayout(config), null, Target.SYSTEM_OUT.name(),
                            "consoleAppender", "true", "true");
            appender.start();
            // loggerConfig.getAppenders().forEach((n, a) -> loggerConfig.removeAppender(n));
            // loggerConfig.setAdditive(false);
            loggerConfig.setLevel(Level.ALL);
            loggerConfig.addFilter(this.typedFilter);
            loggerConfig.addAppender(appender, Level.ALL, null);
            config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
            // This causes all loggers to re-fetch information from their LoggerConfig
            ctx.updateLoggers();
        }
        FunctionDictionary.load();
    }

    private void createInitialDeffact() {
        this.initialFactTemplate = defTemplate("initial-fact", "");
        defFacts("initial-fact", "",
                Collections.singletonList(new TemplateContainer<ParameterLeaf>(this.initialFactTemplate)));
    }

    private void createDummyTemplate() {
        this.defTemplate("dummy-fact", "used as default value for FACT-ADDRESS");
    }

    private void createDummyFact() {
        final FactIdentifier[] factIdentifiers =
                assertFacts(new TemplateContainer<>(this.constructCache.getTemplate("dummy-fact")).toFact());
        assert 1 == factIdentifiers.length;
        this.defaultValues.put(SlotType.FACTADDRESS, factIdentifiers[0]);
    }

    /**
     * Creates a new network object using the {@link ClipsLogFormatter}.
     *
     * @param memoryFactory
     *         memory factory
     * @param tokenQueueCapacity
     *         the capacity of the token queues in all token processing {@link Node nodes}
     * @param scheduler
     *         the {@link Scheduler} to handle the dispatching of token processing
     */
    public Network(final MemoryFactory memoryFactory, final int tokenQueueCapacity, final Scheduler scheduler) {
        this(memoryFactory, ClipsLogFormatter.getMessageFormatter(), tokenQueueCapacity, scheduler);
    }

    /**
     * Creates a new network object with the {@link org.jamocha.dn.memory.javaimpl default memory implementation}.
     *
     * @param tokenQueueCapacity
     *         the capacity of the token queues in all token processing {@link Node nodes}
     * @param scheduler
     *         the {@link Scheduler} to handle the dispatching of token processing
     */
    public Network(final int tokenQueueCapacity, final Scheduler scheduler) {
        this(org.jamocha.dn.memory.javaimpl.MemoryFactory.getMemoryFactory(), ClipsLogFormatter.getMessageFormatter(),
                tokenQueueCapacity, scheduler);
    }

    /**
     * Creates a new network object with the {@link org.jamocha.dn.memory.javaimpl default memory implementation} and
     * {@link ThreadPoolScheduler scheduler}.
     */
    public Network() {
        this(Integer.MAX_VALUE, new ThreadPoolScheduler(10));
    }

    /**
     * Tries to find a node performing the same filtering as the given filter and calls {@link
     * Node#shareNode(PathNodeFilterSet, Map, Path...)} or creates a new {@link Node} for the given {@link
     * PathNodeFilterSet filter}. Returns true iff a {@link Node} to share was found.
     *
     * @param filter
     *         {@link PathNodeFilterSet} to find a corresponding {@link Node} for
     * @return true iff a {@link Node} to share was found
     * @throws IllegalArgumentException
     *         thrown if one of the {@link Path}s in the {@link PathNodeFilterSet} was not mapped to a {@link Node}
     */
    public boolean tryToShareNode(final PathNodeFilterSet filter) throws IllegalArgumentException {
        final Path[] paths = PathCollector.newHashSet().collectAllInLists(filter).getPathsArray();

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
        final PathNodeFilterSet normalisedFilter = filter.normalise();

        // check candidates for possible node sharing
        for (final Node candidate : candidates) {
            // check if filter matches
            final Map<Path, FactAddress> map = FilterFunctionCompare.equals(candidate, normalisedFilter);
            if (null == map) continue;

            candidate.shareNode(filter, map, paths);
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
        final Collection<Edge> firstNodesOutgoingEdges = filterPathNodesIterator.next().getOutgoingEdges();
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

        for (final Iterator<Node> iterator = candidates.iterator(); iterator.hasNext(); ) {
            final Node candidate = iterator.next();
            final Edge[] incomingEdges = candidate.getIncomingEdges();
            for (final Edge incomingEdge : incomingEdges) {
                if (!filterPathNodes.contains(incomingEdge.getSourceNode())) {
                    iterator.remove();
                    break;
                }
            }
        }
        return candidates;
    }

    /**
     * Creates network nodes for one rule, consisting of the passed filters.
     *
     * @param pathRule
     *         translated version of the defrule we have to build the condition for
     * @return created TerminalNode for the constructed rule
     */
    public TerminalNode buildRule(final Defrule.PathRule pathRule) {
        final PathFilterList filters = pathRule.getCondition();
        final HashSet<Path> regularPaths;
        {
            final PathCollector<HashSet<Path>> allPathsCollector = PathCollector.newHashSet();
            final PathCollector<HashSet<Path>> regularPathsCollector = PathCollector.newHashSet();
            for (final PathNodeFilterSet filter : filters) {
                allPathsCollector.collectAllInLists(filter);
                regularPathsCollector.collectRegularPaths(filter);
            }
            allPathsCollector.getPaths().addAll(pathRule.getResultPaths());
            regularPathsCollector.getPaths().addAll(pathRule.getResultPaths());
            this.rootNode.addPaths(this, allPathsCollector.getPathsArray());
            regularPaths = regularPathsCollector.getPaths();
        }
        final ArrayList<Node> nodes = new ArrayList<>();
        for (final PathNodeFilterSet filter : filters) {
            if (tryToShareNode(filter)) {
                continue;
            }
            if (PathCollector.newHashSet().collectAllInLists(filter).getPaths().stream()
                    .flatMap(p -> p.getJoinedWith().stream()).distinct().count() == 1) {
                nodes.add(new AlphaNode(this, filter));
            } else {
                nodes.add(new BetaNode(this, filter));
            }
        }
        final Map<Node, List<Path>> nodeToJoinedPaths =
                regularPaths.stream().collect(groupingBy(Path::getCurrentlyLowestNode));
        if (nodeToJoinedPaths.keySet().size() > 1) {
            // TBD improve by minimizing the intermediate results: easily done when the size of
            // the nodes (keySet of the map) is known approximately
            final PathLeaf[] representatives = toArray(nodeToJoinedPaths.values().stream()
                            .map(l -> new PathLeaf(l.stream().filter(pathRule.resultPaths::contains).findAny().get(),
                                    null)),
                    PathLeaf[]::new);
            final PathNodeFilterSet filter = PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
                    new PredicateWithArgumentsComposite<PathLeaf>(DummyPredicate.INSTANCE, representatives)));
            if (!tryToShareNode(filter)) nodes.add(new BetaNode(this, filter));
        }
        assert regularPaths.stream().map(Path::getCurrentlyLowestNode).distinct().count() == 1;
        final Node lowestNode = regularPaths.iterator().next().getCurrentlyLowestNode();
        final TerminalNode terminalNode = new TerminalNode(this, lowestNode, pathRule);
        for (final Node node : nodes) {
            node.activateTokenQueue();
        }
        this.terminalNodes.add(terminalNode);
        return terminalNode;
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
        final ObjectTypeNode otn = new ObjectTypeNode(this, template);
        getRootNode().putOTN(otn);
        otn.activateTokenQueue();
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
            final List<TemplateContainer<ParameterLeaf>> containers) {
        final Deffacts deffacts = new Deffacts(name, description, containers);
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
    public void defRules(final List<Defrule> defrules) {
        defrules.forEach(this.constructCache::addRule);
        defrules.forEach(rule -> System.out.println("Compiling " + rule.getName()));
        final Collection<PathRule> rules = this.ruleCompiler.compileRules(this.initialFactTemplate, defrules);
        for (final PathRule rule : rules) {
            System.out.println(rule.getParent().getName());
            for (final PathNodeFilterSet pathNodeFilterSet : rule.getCondition()) {
                System.out.println(pathNodeFilterSet.getFilters());
            }
            System.out.println();
            buildRule(rule);
        }
        // final NumberFormat formatter = new DecimalFormat("0.######E0");
        // System.out.println("Network cpu cost: " + formatter.format(new RatingProvider((a, b) -> a).rateNetwork
        // (this)));
        // System.out.println("Network mem cost: " + formatter.format(new RatingProvider((a, b) -> b).rateNetwork
        // (this)));
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
        assertFacts(toArray(this.constructCache.getDeffacts().stream().flatMap(def -> def.getContainers().stream())
                .map(TemplateContainer::toFact), Fact[]::new));
    }

    @Override
    public void clear() {
        this.rootNode.clear();
        this.conflictSet.flush();
        this.constructCache.clear();
        this.terminalNodes.clear();
        createInitialDeffact();
        createDummyTemplate();
        reset();
    }

    @Override
    public void run(final long maxNumRules) {
        this.haltWasCalled = false;
        long numRules = 0;
        do {
            this.scheduler.waitForNoUnfinishedJobs();
            this.conflictSet.deleteRevokedEntries();
            final RuleAndToken ruleAndToken = this.conflictSet.getCurrentlySelectedRuleAndToken();
            if (null == ruleAndToken) break;
            this.logFormatter.messageRuleFiring(this, ruleAndToken.getRule(), (Assert) ruleAndToken.getToken());
            ruleAndToken.getRule().getActionList().evaluate(ruleAndToken.getToken());
            this.conflictSet.remove(ruleAndToken);
            ++numRules;
        } while (!this.haltWasCalled && 0L == maxNumRules || numRules < maxNumRules);
    }

    @Override
    public void halt() {
        this.haltWasCalled = true;
    }

    public void shutdown() {
        this.scheduler.shutdown();
    }

    public void shutdownNow() {
        this.scheduler.shutdownNow();
    }

    @Override
    public ConflictResolutionStrategy getConflictResolutionStrategy() {
        return this.conflictSet.getConflictResolutionStrategy();
    }

    @Override
    public void setConflictResolutionStrategy(final ConflictResolutionStrategy conflictResolutionStrategy) {
        this.conflictSet.setConflictResolutionStrategy(conflictResolutionStrategy);
    }

    @Override
    public Symbol createTopLevelSymbol(final String image) {
        return this.scope.getOrCreateTopLevelSymbol(image);
    }

    @Override
    public boolean loadFromFile(final String path, final boolean progressInformation) {
        final SFPToCETranslator visitor = new SFPToCETranslator(this, this);
        try (final FileInputStream inputStream = new FileInputStream(new File(path))) {
            final SFPParser parser = new SFPParser(inputStream);
            while (true) {
                final SFPStart n = parser.Start();
                if (null == n) break;
                n.jjtAccept(visitor, null);
            }
            return true;
        } catch (final IOException | ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveToFile(final String path) {
        // TBD save constructs to file
        return false;
    }

    @Override
    public boolean loadFactsFromFile(final String path) {
        final SFPToCETranslator visitor = new SFPToCETranslator(this, this);
        try (final FileInputStream inputStream = new FileInputStream(new File(path))) {
            final SFPParser parser = new SFPParser(inputStream);
            while (true) {
                final SimpleNode n = parser.RHSPattern();
                if (null == n) break;
                n.jjtAccept(visitor, null);
            }
            return true;
        } catch (final IOException | ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveFactsToFile(final String path) {
        // TBD save facts to file
        return false;
    }

    /**
     * Clears all appenders of the logging framework.
     */
    public void clearAppender() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = config.getLoggerConfig(this.getInteractiveEventsLogger().getName());
        loggerConfig.getAppenders().forEach((n, a) -> loggerConfig.removeAppender(n));
        // This causes all loggers to re-fetch information from their LoggerConfig
        ctx.updateLoggers();
    }

    /**
     * Adds an appender to the logging framework.
     *
     * @param out
     *         output stream
     * @param plain
     *         true for just the content, false for log-style additional infos in front of the content
     */
    public void addAppender(final OutputStream out, final boolean plain) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = config.getLoggerConfig(this.getInteractiveEventsLogger().getName());
        loggerConfig
                .addAppender(OutstreamAppender.newInstance(out, LayoutAdapter.createLayout(config, plain), null, true),
                        Level.ALL, null);
        // This causes all loggers to re-fetch information from their LoggerConfig
        ctx.updateLoggers();
    }

    /**
     * A default network object with a basic setup, used for testing and other quick and dirty networks.
     */
    public static final Network DEFAULTNETWORK = new Network(Integer.MAX_VALUE,
            // new ThreadPoolScheduler(10)
            new PlainScheduler());
}
