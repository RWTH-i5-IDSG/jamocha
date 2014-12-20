package org.jamocha.dn.compiler;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.TranslatedPath;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.FWACollector;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathCollector;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathFilterList.PathFilterSharedListWrapper;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.filter.SymbolInSymbolLeafsCollector;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.FunctionNormaliser;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwatransformer.FWADeepCopy;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.function.impls.predicates.Not;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;
import org.jamocha.languages.common.errors.VariableNotDeclaredError;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Collect all PathFilters inside all children of an OrFunctionConditionalElement, returning a List
 * of Lists. Each inner List contains the PathFilters of one child.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class PathFilterConsolidator implements DefaultConditionalElementsVisitor {

	private final Template initialFactTemplate;
	private final Defrule rule;
	private final Map<Path, Set<Path>> pathToJoinedWith = new HashMap<>();
	@Getter
	private List<Defrule.TranslatedPath> translateds = null;

	public List<Defrule.TranslatedPath> consolidate() {
		assert rule.getCondition().getConditionalElements().size() == 1;
		return rule.getCondition().getConditionalElements().get(0).accept(this).translateds;
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		// If there is no OrFunctionConditionalElement just proceed with the CE as it were
		// the only child of an OrFunctionConditionalElement.
		final Map<VariableSymbol, EquivalenceClass> symbolToEC =
				new SymbolCollector(rule.getCondition()).getSymbols().stream()
						.collect(toMap(Function.identity(), VariableSymbol::getEqual));
		translateds =
				Collections.singletonList(NoORsPFC.consolidate(initialFactTemplate, rule, pathToJoinedWith, ce,
						symbolToEC));
	}

	@Override
	public void visit(final OrFunctionConditionalElement ce) {
		final Map<VariableSymbol, EquivalenceClass> symbolToEC =
				new SymbolCollector(rule.getCondition()).getSymbols().stream()
						.collect(toMap(Function.identity(), VariableSymbol::getEqual));
		// For each child of the OrCE ...
		this.translateds =
				ce.getChildren().stream().map(child ->
				// ... collect all PathFilters in the child
						NoORsPFC.consolidate(initialFactTemplate, rule, pathToJoinedWith, child, symbolToEC))
						.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static class NoORsPFC implements DefaultConditionalElementsVisitor {

		private final Template initialFactTemplate;
		private final Path initialFactPath;
		private final Map<SingleFactVariable, Path> paths;
		private final Map<Path, Set<Path>> pathToJoinedWith;
		private final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf;
		private final boolean negated;

		@Getter
		private List<PathFilterList> pathFilters = new ArrayList<>();

		public static TranslatedPath consolidate(final Template initialFactTemplate, final Defrule rule,
				final Map<Path, Set<Path>> pathToJoinedWith, final ConditionalElement ce,
				final Map<VariableSymbol, EquivalenceClass> symbolToECbackup) {
			final Set<VariableSymbol> symbols = symbolToECbackup.keySet();
			// copy the equivalence classes
			final BiMap<EquivalenceClass, EquivalenceClass> oldToNew =
					HashBiMap
							.create(symbolToECbackup
									.values()
									.stream()
									.collect(
											toMap(Function.identity(),
													(final EquivalenceClass ec) -> new EquivalenceClass(ec))));
			replaceEC(symbols, oldToNew);

			final HashSet<FunctionWithArguments<SymbolLeaf>> occurringFWAs =
					FWACollector.newHashSet().collect(ce).getFwas();
			final HashSet<SingleFactVariable> occurringFactVariables =
					new HashSet<>(DeepFactVariableCollector.collect(ce));
			/* inspect the equivalence class hierarchy for sections not contained in this rule part */
			// for every symbol in the CE
			for (final VariableSymbol vs : symbols) {
				// and thus for every equivalence class
				final EquivalenceClass ec = vs.getEqual();
				// check whether the fact variable is bound via a TPCE
				final Optional<SingleFactVariable> fv = ec.getFactVariable();
				if (fv.isPresent() && !occurringFactVariables.contains(fv.get())) {
					// the fact variable for the EC is not contained in the CE, remove it
					ec.removeFactVariable();
				}
				// for every slot variable, check whether the corresponding fact variable is
				// contained in the CE
				for (final Iterator<SingleSlotVariable> iterator = ec.getEqualSlotVariables().iterator(); iterator
						.hasNext();) {
					final SingleSlotVariable sv = iterator.next();
					if (!occurringFactVariables.contains(sv.getFactVariable())) {
						// not contained, remove the SV and its reference to this EC
						iterator.remove();
						sv.getEqual().remove(ec);
					}
				}
				// for every FWA, check whether the FWA occurs in the CE
				ec.getEqualFWAs().removeIf(fwa -> !occurringFWAs.contains(fwa));
			}

			/* merge (bind)s and overlapping SlotVariables */
			for (final VariableSymbol vs : symbols) {
				final EquivalenceClass ec = vs.getEqual();
				for (final SingleSlotVariable sv : ec.getEqualSlotVariables()) {
					if (sv.getEqual().size() <= 1)
						continue;
					final Iterator<EquivalenceClass> ecIter = sv.getEqual().iterator();
					final EquivalenceClass first = ecIter.next();
					while (ecIter.hasNext()) {
						final EquivalenceClass other = ecIter.next();
						first.add(other);
						replaceEC(symbols, Collections.singletonMap(other, first));
					}
				}
			}

			/* check that all variables are bound */
			final Set<VariableSymbol> symbolsInLeafs = new SymbolInSymbolLeafsCollector(ce).getSymbols();
			for (final VariableSymbol vs : symbols) {
				final EquivalenceClass ec = vs.getEqual();
				if (!ec.getFactVariable().isPresent() && ec.getEqualSlotVariables().isEmpty()) {
					if (!ec.getUnequalEquivalenceClasses().isEmpty() || symbolsInLeafs.contains(vs))
						// vs is not bound
						throw new VariableNotDeclaredError(vs);
				}
			}

			/* merge equals test conditional elements arguments */
			ce.accept(new CEEquivalenceClassBuilder(symbols, false));

			final TranslatedPath result =
					consolidateOnCopiedEquivalenceClasses(initialFactTemplate, rule, pathToJoinedWith, ce, symbols
							.stream().map(VariableSymbol::getEqual).collect(toSet()));

			// reset the symbol - equivalence class mapping
			symbolToECbackup.forEach((vs, ec) -> vs.setEqual(ec));
			replaceEC(symbols, oldToNew.inverse());
			return result;
		}

		// there are references to EquivalenceClass in:
		// VariableSymbol.equal
		// SingleFactVariable.equal
		// SingleSlotVariable.equal (Set)
		// EquivalenceClass.unequalEquivalenceClasses (Set)
		private static void replaceEC(final Set<VariableSymbol> symbols,
				final Map<EquivalenceClass, EquivalenceClass> map) {
			// VariableSymbol.equal
			symbols.forEach(sym -> sym.setEqual(map.getOrDefault(sym.getEqual(), sym.getEqual())));
			for (final Map.Entry<EquivalenceClass, EquivalenceClass> entry : map.entrySet()) {
				final EquivalenceClass oldEC = entry.getKey();
				final EquivalenceClass newEC = entry.getValue();
				// SingleFactVariable.equal
				oldEC.getFactVariable().ifPresent(fv -> fv.setEqual(newEC));
				// SingleSlotVariable.equal (Set)
				for (final SingleSlotVariable sv : oldEC.getEqualSlotVariables()) {
					final Set<EquivalenceClass> equalSet = sv.getEqual();
					for (final Map.Entry<EquivalenceClass, EquivalenceClass> innerEntry : map.entrySet()) {
						if (equalSet.remove(innerEntry.getKey()))
							equalSet.add(innerEntry.getValue());
					}
				}
				// EquivalenceClass.unequalEquivalenceClasses (Set)
				newEC.replace(map);
			}
		}

		static class CEEquivalenceClassBuilder implements DefaultConditionalElementsVisitor {
			final Set<VariableSymbol> occurringSymbols;
			final Map<FunctionWithArguments<SymbolLeaf>, EquivalenceClass> equivalenceClasses;
			final FWAEquivalenceClassBuilder fwaBuilder = new FWAEquivalenceClassBuilder();
			boolean negated = false;

			public CEEquivalenceClassBuilder(final Set<VariableSymbol> occurringSymbols, final boolean negated) {
				super();
				this.negated = negated;
				this.occurringSymbols = occurringSymbols;
				this.equivalenceClasses =
						occurringSymbols.stream().collect(toMap(SymbolLeaf::new, VariableSymbol::getEqual));
			}

			@RequiredArgsConstructor
			class FWAEquivalenceClassBuilder implements DefaultFunctionWithArgumentsVisitor<SymbolLeaf> {

				private EquivalenceClass getEC(final FunctionWithArguments<SymbolLeaf> fwa) {
					return equivalenceClasses.computeIfAbsent(fwa, f -> new EquivalenceClass(f));
				}

				@Override
				public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> fwa) {
					if (fwa.getFunction().inClips().equals(Equals.inClips)) {
						final FunctionWithArguments<SymbolLeaf>[] args =
								FunctionNormaliser.normalise(FWADeepCopy.copy(fwa)).getArgs();
						if (negated) {
							final EquivalenceClass left = getEC(args[0]);
							for (int i = 1; i < args.length; ++i) {
								final EquivalenceClass right = getEC(args[i]);
								left.addNegatedEdge(right);
							}
						} else {
							final EquivalenceClass left = getEC(args[0]);
							for (int i = 1; i < args.length; i++) {
								final FunctionWithArguments<SymbolLeaf> arg = args[i];
								final EquivalenceClass right = getEC(arg);
								left.add(right);
								equivalenceClasses.put(arg, left);
								replaceEC(occurringSymbols, Collections.singletonMap(right, left));
							}
						}
					}
				}

				@Override
				public void defaultAction(final FunctionWithArguments<SymbolLeaf> function) {
				}
			}

			@Override
			public void defaultAction(final ConditionalElement ce) {
				ce.getChildren().forEach(c -> c.accept(this));
			}

			@Override
			public void visit(final TestConditionalElement ce) {
				ce.getPredicateWithArguments().accept(fwaBuilder);
			}

			@Override
			public void visit(final NotFunctionConditionalElement ce) {
				negated = !negated;
				defaultAction(ce);
				negated = !negated;
			}
		}

		@SuppressWarnings("unchecked")
		private static TranslatedPath consolidateOnCopiedEquivalenceClasses(final Template initialFactTemplate,
				final Defrule rule, final Map<Path, Set<Path>> pathToJoinedWith, final ConditionalElement ce,
				final Set<EquivalenceClass> equivalenceClasses) {
			final Pair<Path, Map<SingleFactVariable, Path>> initialFactAndPathMap =
					ShallowFactVariableCollector.generatePaths(initialFactTemplate, ce);
			final Map<SingleFactVariable, Path> pathMap = initialFactAndPathMap.getRight();
			final Set<Path> allPaths = new HashSet<>(pathMap.values());

			final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf = new HashMap<>();
			for (final EquivalenceClass equiv : equivalenceClasses) {
				equivalenceClassToPathLeaf.put(equiv, equiv.getPathLeaf(pathMap));
			}

			final NoORsPFC instance =
					new NoORsPFC(initialFactTemplate, initialFactAndPathMap.getLeft(), pathMap, pathToJoinedWith,
							equivalenceClassToPathLeaf, false).collect(ce);
			final List<PathFilterList> pathFilters = instance.getPathFilters();

			// add the tests for the equivalence classes
			{
				final Set<EquivalenceClass> neqAlreadyDone = new HashSet<>();
				// TODO improve the test generation by doing something smart

				for (final EquivalenceClass equiv : equivalenceClasses) {
					if (!neqAlreadyDone.add(equiv))
						continue;
					final FunctionWithArguments<PathLeaf> element = equivalenceClassToPathLeaf.get(equiv);
					if (null == element)
						continue;

					if (equiv.getEqualSlotVariables().isEmpty()) {
						assert equiv.getFactVariable().isPresent();
					} else {
						for (final SingleSlotVariable sv : equiv.getEqualSlotVariables()) {
							assert equivalenceClassToPathLeaf.containsKey(sv.getFactVariable().getEqual());
						}
						final Set<FunctionWithArguments<PathLeaf>> equalPathLeafs =
								equiv.getEqualSlotVariables().stream()
										.map(sv -> equivalenceClassToPathLeaf.get(sv.getFactVariable().getEqual()))
										.collect(toSet());
						equiv.getFactVariable().map(fv -> fv.getPathLeaf(pathMap)).ifPresent(equalPathLeafs::add);
						if (equalPathLeafs.size() > 1) {
							final PathFilter eqTest =
									new PathFilter(new PathFilterElement(new PredicateWithArgumentsComposite<PathLeaf>(
											FunctionDictionary.lookupPredicate(Equals.inClips,
													SlotType.nCopies(equiv.getType(), equalPathLeafs.size())), toArray(
													equalPathLeafs, FunctionWithArguments[]::new))));
							joinPaths(pathToJoinedWith, eqTest);
							pathFilters.add(eqTest);
						}
					}
					if (!equiv.getEqualFWAs().isEmpty()) {
						final FunctionWithArguments<PathLeaf>[] equalFWAsArray =
								new FunctionWithArguments[equiv.getEqualFWAs().size() + 1];
						equalFWAsArray[0] = element;
						int i = 1;
						for (final FunctionWithArguments<SymbolLeaf> fwa : equiv.getEqualFWAs()) {
							equalFWAsArray[i++] = SymbolToPathTranslator.translate(fwa, equivalenceClassToPathLeaf);
						}
						final PathFilter eqTest =
								new PathFilter(new PathFilterElement(new PredicateWithArgumentsComposite<PathLeaf>(
										FunctionDictionary.lookupPredicate(Equals.inClips,
												SlotType.nCopies(equiv.getType(), equalFWAsArray.length + 1)),
										equalFWAsArray)));
						joinPaths(pathToJoinedWith, eqTest);
						pathFilters.add(eqTest);
					}
					if (!equiv.getUnequalEquivalenceClasses().isEmpty()) {
						final Predicate not = FunctionDictionary.lookupPredicate(Not.inClips, SlotType.BOOLEAN);
						for (final EquivalenceClass nequiv : equiv.getUnequalEquivalenceClasses()) {
							if (!neqAlreadyDone.contains(nequiv)) {
								final PathLeaf pathLeaf = equivalenceClassToPathLeaf.get(nequiv);
								final PathFilter neqTest =
										new PathFilter(new PathFilterElement(
												new PredicateWithArgumentsComposite<PathLeaf>(not,
														new PredicateWithArgumentsComposite<PathLeaf>(
																FunctionDictionary.lookupPredicate(Equals.inClips,
																		element.getReturnType(),
																		pathLeaf.getReturnType()), element, pathLeaf))));
								joinPaths(pathToJoinedWith, neqTest);
								pathFilters.add(neqTest);
							}
						}
					}
				}
			}

			final Set<Path> collectedPaths =
					(pathFilters.isEmpty() ? Collections.<Path> emptySet() : PathCollector.newHashSet()
							.collectOnlyInFilterElements(pathFilters.get(pathFilters.size() - 1)).getPaths().stream()
							.flatMap((p) -> pathToJoinedWith.get(p).stream()).collect(toSet()));

			final TranslatedPath translated =
					rule.newTranslated(new PathFilterSharedListWrapper().newSharedElement(pathFilters),
							equivalenceClassToPathLeaf);
			if (collectedPaths.containsAll(allPaths)) {
				return translated;
			}
			allPaths.addAll(collectedPaths);
			final PathFilter dummy =
					new PathFilter(new PathFilter.DummyPathFilterElement(toArray(allPaths, Path[]::new)));
			joinPaths(pathToJoinedWith, dummy);
			pathFilters.add(dummy);
			return translated;
		}

		private <T extends ConditionalElement> NoORsPFC collect(final T ce) {
			return ce.accept(this);
		}

		static List<PathFilterList> processExistentialCondition(final Template initialFactTemplate,
				final Path initialFactPath, final ConditionalElement ce, final Map<SingleFactVariable, Path> fact2Path,
				final Map<Path, Set<Path>> pathToJoinedWith,
				final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf, final boolean isPositive) {
			// Collect the existential FactVariables and corresponding paths from the existentialCE
			final Pair<Path, Map<SingleFactVariable, Path>> initialFactAndPathMap =
					ShallowFactVariableCollector.generatePaths(initialFactTemplate, ce);
			final Map<SingleFactVariable, Path> existentialFact2Path = initialFactAndPathMap.getRight();

			// combine existential FactVariables and Paths with non existential ones for PathFilter
			// generation
			final Map<SingleFactVariable, Path> combinedFact2Path = new HashMap<SingleFactVariable, Path>(fact2Path);
			combinedFact2Path.putAll(existentialFact2Path);

			for (final EquivalenceClass equiv : equivalenceClassToPathLeaf.keySet()) {
				equivalenceClassToPathLeaf.computeIfAbsent(equiv, e -> e.getPathLeaf(combinedFact2Path));
			}

			// Only existential Paths without Variables
			final Set<Path> existentialPaths = new HashSet<>(existentialFact2Path.values());

			// Generate PathFilters from CE (recurse)
			final List<PathFilterList> filters =
					new NoORsPFC(initialFactTemplate, initialFactAndPathMap.getLeft(), combinedFact2Path,
							pathToJoinedWith, equivalenceClassToPathLeaf, false).collect(ce).getPathFilters();

			// Collect all used Paths for every PathFilter
			final Map<PathFilterList, HashSet<Path>> filter2Paths =
					filters.stream().collect(
							Collectors.toMap(Function.identity(),
									filter -> PathCollector.newHashSet().collectAll(filter).getPaths()));

			// Split PathFilters into those only using existential Paths and those also using non
			// existential Paths
			final Map<Boolean, LinkedList<PathFilterList>> tmp =
					filters.stream().collect(
							Collectors.partitioningBy(filter -> existentialPaths.containsAll(filter2Paths.get(filter)),
									toCollection(LinkedList::new)));
			final LinkedList<PathFilterList> pureExistentialFilters = tmp.get(Boolean.TRUE);
			final LinkedList<PathFilterList> nonPureExistentialFilters = tmp.get(Boolean.FALSE);

			// Add all pureExistentialFilters to result List because they don't have to be combined
			// or ordered
			final List<PathFilterList> resultFilters = new ArrayList<>(pureExistentialFilters);

			if (nonPureExistentialFilters.isEmpty()) {
				// there are only existential filters
				// FIXME satisfy equivalence classes before the closure

				// if there are only existential filters, append one combining them with an initial
				// fact path
				assert null != initialFactPath;
				final ArrayList<Path> paths = new ArrayList<>();
				paths.addAll(existentialPaths);
				paths.add(initialFactPath);
				final PathFilter existentialClosure =
						new PathFilter(isPositive, existentialPaths, new PathFilter.DummyPathFilterElement(toArray(
								paths, Path[]::new)));
				joinPaths(pathToJoinedWith, existentialClosure);
				return Arrays.asList(new PathFilterList.PathFilterExistentialList(resultFilters, existentialClosure));
			}

			// Construct HashMap from Paths to Filters
			final Map<Path, Set<PathFilterList>> path2Filters = new HashMap<>();
			filter2Paths.forEach((pathFilter, paths) -> paths.forEach(path -> path2Filters.computeIfAbsent(path,
					x -> new HashSet<>()).add(pathFilter)));

			// Find connected components of the existential Paths
			final Map<Path, Set<Path>> joinedExistentialPaths = new HashMap<>();
			final Set<Path> processedExistentialPaths = new HashSet<>();
			// While there are unjoined Filters continue
			while (!pureExistentialFilters.isEmpty()) {
				// Take one arbitrary filter
				final LinkedList<PathFilterList> collectedFilters =
						new LinkedList<>(Collections.singletonList(pureExistentialFilters.poll()));
				Set<PathFilterList> newCollectedFilters = new HashSet<>(collectedFilters);
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
							newCollectedPaths.stream().flatMap(path -> path2Filters.get(path).stream())
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
				final List<PathFilterList> collectedFilters =
						new ArrayList<>(Collections.singletonList(nonPureExistentialFilters.poll()));
				Set<PathFilterList> newCollectedFilters = new HashSet<>(collectedFilters);
				final Set<Path> collectedExistentialPaths = new HashSet<>();

				while (!newCollectedFilters.isEmpty()) {
					// search for all existential Paths used by the new Filters
					final Set<Path> newCollectedExistentialPaths =
							newCollectedFilters.stream()
									.flatMap((final PathFilterList f) -> filter2Paths.get(f).stream()).collect(toSet());
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
							newCollectedExistentialPaths.stream().flatMap(path -> path2Filters.get(path).stream())
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
				for (final PathFilterList filterList : collectedFilters) {
					for (final PathFilter filter : filterList) {
						filterElements.addAll(Arrays.asList(filter.getFilterElements()));
					}
				}
				final PathFilter combiningFilter =
						new PathFilter(isPositive, collectedExistentialPaths, toArray(filterElements,
								PathFilterElement[]::new));
				joinPaths(pathToJoinedWith, combiningFilter);
				resultFilters.add(combiningFilter);
				processedExistentialPaths.addAll(collectedExistentialPaths);
			}
			// FIXME satisfy equivalence classes

			{
				// if not all paths within this existential CE have been used in some test, add a
				// dummy filter element to have them joined, too
				final Set<Path> unprocessedExistentialPaths = new HashSet<>(existentialPaths);
				unprocessedExistentialPaths.removeAll(processedExistentialPaths);
				if (!unprocessedExistentialPaths.isEmpty()) {
					final PathFilter dummy =
							new PathFilter(isPositive, unprocessedExistentialPaths,
									new PathFilter.DummyPathFilterElement(toArray(unprocessedExistentialPaths,
											Path[]::new)));
					joinPaths(pathToJoinedWith, dummy);
					return Arrays.asList(new PathFilterList.PathFilterExistentialList(resultFilters, dummy));
				}
			}

			return resultFilters;
		}

		@Override
		public void defaultAction(final ConditionalElement ce) {
			// Just ignore. InitialFactCEs and TemplateCEs already did their job during
			// FactVariable collection
		}

		@Override
		public void visit(final AndFunctionConditionalElement ce) {
			pathFilters =
					ce.getChildren()
							.stream()
							// Process all children CEs
							.map(child -> child.accept(
									new NoORsPFC(initialFactTemplate, initialFactPath, paths, pathToJoinedWith,
											equivalenceClassToPathLeaf, negated)).getPathFilters())
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
					processExistentialCondition(initialFactTemplate, initialFactPath, ce.getChildren().get(0), paths,
							pathToJoinedWith, equivalenceClassToPathLeaf, true);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.pathFilters =
					processExistentialCondition(initialFactTemplate, initialFactPath, ce.getChildren().get(0), paths,
							pathToJoinedWith, equivalenceClassToPathLeaf, false);
		}

		@Override
		public void visit(final NotFunctionConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			// Call a PathFilterCollector for the child of the NotFunctionCE with toggled negated
			// flag.
			this.pathFilters =
					ce.getChildren()
							.get(0)
							.accept(new NoORsPFC(initialFactTemplate, initialFactPath, paths, pathToJoinedWith,
									equivalenceClassToPathLeaf, !negated)).getPathFilters();
		}

		@Override
		public void visit(final SharedConditionalElementWrapper ce) {
			// use the wrapper for the inner shared instances
			this.pathFilters =
					Collections.singletonList(ce.getWrapper().newSharedElement(
							ce.getCe()
									.accept(new NoORsPFC(initialFactTemplate, initialFactPath, paths, pathToJoinedWith,
											equivalenceClassToPathLeaf, negated)).getPathFilters()));
		}

		@Override
		public void visit(final TestConditionalElement ce) {
			final PredicateWithArguments<SymbolLeaf> pwa = ce.getPredicateWithArguments();
			// ignore equals-test, they are covered via the equivalence classes
			if (pwa instanceof PredicateWithArgumentsComposite
					&& ((PredicateWithArgumentsComposite<SymbolLeaf>) pwa).getFunction().inClips()
							.equals(Equals.inClips))
				return;
			final PredicateWithArguments<PathLeaf> predicate =
					SymbolToPathTranslator.translate(FWADeepCopy.copy(pwa), equivalenceClassToPathLeaf);
			final PathFilter pathFilter =
					new PathFilter(new PathFilterElement((negated) ? new PredicateWithArgumentsComposite<>(
							FunctionDictionary.lookupPredicate(Not.inClips, SlotType.BOOLEAN), predicate) : predicate));
			joinPaths(pathToJoinedWith, pathFilter);
			this.pathFilters = Collections.singletonList(pathFilter);
		}

		private static void joinPaths(final Map<Path, Set<Path>> pathToJoinedWith, final PathFilter pathFilter) {
			final Set<Path> joinedPaths =
					PathCollector
							.newHashSet()
							.collectAll(pathFilter)
							.getPaths()
							.stream()
							.flatMap(
									p -> pathToJoinedWith.computeIfAbsent(p, k -> new HashSet<Path>(Arrays.asList(k)))
											.stream()).collect(toSet());
			joinedPaths.forEach(p -> pathToJoinedWith.put(p, joinedPaths));
		}
	}
}