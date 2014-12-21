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
import java.util.Map.Entry;
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
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
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
			final Set<VariableSymbol> symbolsInLeafs = SymbolInSymbolLeafsCollector.collect(ce);
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
			// restore the SlotVariable - equivalence class mapping
			symbolToECbackup.forEach((vs, ec) -> vs.getEqual().getEqualSlotVariables()
					.forEach(sv -> sv.getEqual().add(ec)));
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
				for (final EquivalenceClass equiv : equivalenceClasses) {
					createEquivalenceClassTests(pathToJoinedWith, pathMap, equivalenceClassToPathLeaf, pathFilters,
							neqAlreadyDone, equiv);
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

		private static final Predicate not = FunctionDictionary.lookupPredicate(Not.inClips, SlotType.BOOLEAN);

		private static void createEquivalenceClassTests(final Map<Path, Set<Path>> pathToJoinedWith,
				final Map<SingleFactVariable, Path> pathMap,
				final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf,
				final List<PathFilterList> pathFilters, final Set<EquivalenceClass> neqAlreadyDone,
				final EquivalenceClass equiv) {
			// TODO improve the test generation by doing something smart
			if (!neqAlreadyDone.add(equiv))
				return;
			final FunctionWithArguments<PathLeaf> element = equivalenceClassToPathLeaf.get(equiv);
			if (null == element)
				return;

			if (!equiv.getEqualSlotVariables().isEmpty()) {
				final Set<FunctionWithArguments<PathLeaf>> equalPathLeafs =
						equiv.getEqualSlotVariables().stream().map(sv -> sv.getPathLeaf(pathMap)).collect(toSet());
				equiv.getFactVariable().map(fv -> fv.getPathLeaf(pathMap)).ifPresent(equalPathLeafs::add);
				if (equalPathLeafs.size() > 1) {
					equalPathLeafs.remove(element);
					for (final FunctionWithArguments<PathLeaf> other : equalPathLeafs) {
						final PathFilter eqTest =
								new PathFilter(new PathFilterElement(
										GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips, element,
												other)));
						joinPaths(pathToJoinedWith, eqTest);
						pathFilters.add(eqTest);
					}
				}
			}
			if (!equiv.getEqualFWAs().isEmpty()) {
				for (final FunctionWithArguments<SymbolLeaf> other : equiv.getEqualFWAs()) {
					final PathFilter eqTest =
							new PathFilter(new PathFilterElement(GenericWithArgumentsComposite.newPredicateInstance(
									Equals.inClips, element,
									SymbolToPathTranslator.translate(other, equivalenceClassToPathLeaf))));
					joinPaths(pathToJoinedWith, eqTest);
					pathFilters.add(eqTest);
				}
			}
			if (!equiv.getUnequalEquivalenceClasses().isEmpty()) {
				for (final EquivalenceClass nequiv : equiv.getUnequalEquivalenceClasses()) {
					if (!neqAlreadyDone.contains(nequiv)) {
						final PathFilter neqTest =
								new PathFilter(new PathFilterElement(new PredicateWithArgumentsComposite<PathLeaf>(not,
										GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips, element,
												equivalenceClassToPathLeaf.get(nequiv)))));
						joinPaths(pathToJoinedWith, neqTest);
						pathFilters.add(neqTest);
					}
				}
			}
		}

		private <T extends ConditionalElement> NoORsPFC collect(final T ce) {
			return ce.accept(this);
		}

		private static Map<SingleSlotVariable, SingleSlotVariable> getMatchingSVs(final SingleFactVariable fv,
				final Set<SingleSlotVariable> targets) {
			final Map<EquivalenceClass, SingleSlotVariable> targetMap =
					targets.stream().collect(toMap(sv -> sv.getEqual().iterator().next(), Function.identity()));
			final Map<SingleSlotVariable, SingleSlotVariable> fvToTarget = new HashMap<>();
			for (final SingleSlotVariable sv : fv.getSlotVariables()) {
				final EquivalenceClass ec = sv.getEqual().iterator().next();
				if (targetMap.keySet().contains(ec)) {
					fvToTarget.put(sv, targetMap.get(ec));
				}
			}
			return fvToTarget;
		}

		static List<PathFilterList> processExistentialCondition(final Template initialFactTemplate,
				final Path initialFactPath, final ConditionalElement ce, final Map<SingleFactVariable, Path> fact2Path,
				final Map<Path, Set<Path>> pathToJoinedWith,
				final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf, final boolean isPositive) {
			// Collect the existential FactVariables and corresponding paths from the existentialCE
			final Pair<Path, Map<SingleFactVariable, Path>> initialFactAndPathMap =
					ShallowFactVariableCollector.generatePaths(initialFactTemplate, ce);
			final Map<SingleFactVariable, Path> shallowExistentialFact2Path = initialFactAndPathMap.getRight();

			// combine existential FactVariables and Paths with non existential ones for PathFilter
			// generation
			fact2Path.putAll(shallowExistentialFact2Path);

			final List<SingleFactVariable> deepExistentialFactVariables = DeepFactVariableCollector.collect(ce);

			for (final EquivalenceClass equiv : equivalenceClassToPathLeaf.keySet()) {
				equivalenceClassToPathLeaf.computeIfAbsent(equiv, e -> e.getPathLeaf(fact2Path));
			}

			// Only existential Paths without Variables
			final Set<Path> shallowExistentialPaths = new HashSet<>(shallowExistentialFact2Path.values());

			// Generate PathFilters from CE (recurse)
			final List<PathFilterList> filters =
					new NoORsPFC(initialFactTemplate, initialFactAndPathMap.getLeft(), fact2Path, pathToJoinedWith,
							equivalenceClassToPathLeaf, false).collect(ce).getPathFilters();

			final Map<SingleFactVariable, Path> deepExistentialFact2Path =
					deepExistentialFactVariables.stream().collect(toMap(Function.identity(), fv -> fact2Path.get(fv)));
			final HashSet<Path> deepExistentialPaths = new HashSet<>(deepExistentialFact2Path.values());

			{
				final Set<SingleFactVariable> shallowExistentialFVs = shallowExistentialFact2Path.keySet();
				for (final SingleFactVariable exFV : shallowExistentialFVs) {
					final Set<SingleSlotVariable> slotVariables = new HashSet<>(exFV.getSlotVariables());
					slotLoop: while (!slotVariables.isEmpty()) {
						final SingleSlotVariable slotVariable = slotVariables.iterator().next();
						final Set<EquivalenceClass> eqSet = slotVariable.getEqual();
						assert eqSet.size() == 1;
						final EquivalenceClass equal = eqSet.iterator().next();
						final Set<SingleSlotVariable> equalSlotVariables = new HashSet<>(equal.getEqualSlotVariables());
						final ArrayList<FunctionWithArguments<SymbolLeaf>> equalFWAs = equal.getEqualFWAs();
						// remove the slot variable from the equivalence class as it will not be
						// accessible outside of the existential part
						equalSlotVariables.remove(slotVariable);
						if (equalSlotVariables.size() < 1 && equalFWAs.isEmpty()) {
							// nothing to do
							slotVariables.remove(slotVariable);
							continue;
						}
						// we have to insert an equality test

						// just checking equality regarding FWAs breaks if we allow FWAs to return
						// different values for different calls with the same arguments such as
						// (random 0 1).
						// without knowing anything about the equal FWAs we would have to:
						// if there are equalFWAs: join everything at once
						// if there are no equalFWAs: join at least one of the FVs of the equal SVs

						if (!equalFWAs.isEmpty()) {
							// to make use of the fact that the slot would easily be covered by a
							// ConstantLeaf, we inspect the equalFWAs
							final Optional<FunctionWithArguments<SymbolLeaf>> optConstantLeaf =
									equalFWAs.stream().filter(fwa -> fwa instanceof ConstantLeaf).findAny();
							if (optConstantLeaf.isPresent()) {
								// no path merging needed
								filters.add(new PathFilter(new PathFilterElement(GenericWithArgumentsComposite
										.newPredicateInstance(Equals.inClips, equivalenceClassToPathLeaf.get(equal),
												SymbolToPathTranslator.translate(optConstantLeaf.get(),
														equivalenceClassToPathLeaf)))));
								slotVariables.remove(slotVariable);
								equal.getEqualSlotVariables().remove(slotVariable);
								continue slotLoop;
							} else {
								// the hard way: join everything
								// FIXME THE HARD WAY
								System.err.println(" THE HARD WAY ");
								throw new Error("not yet implemented");
								// continue slotLoop;
							}
						} else {
							// no equal FWAs: join at least one of the FVs of the equal SVs
							final Optional<Map<SingleSlotVariable, SingleSlotVariable>> optBestCandidate =
									slotVariables
											.stream()
											.flatMap(
													sv -> sv.getEqual().iterator().next().getEqualSlotVariables()
															.stream().map(SingleSlotVariable::getFactVariable))
											.distinct().filter(fv -> !fv.equals(exFV) && fact2Path.containsKey(fv))
											.map(fv -> getMatchingSVs(fv, slotVariables))
											.max((a, b) -> Integer.compare(a.size(), b.size()));
							if (!optBestCandidate.isPresent())
								throw new Error("What just happened?");
							final Map<SingleSlotVariable, SingleSlotVariable> merged = optBestCandidate.get();
							for (final Entry<SingleSlotVariable, SingleSlotVariable> entry : merged.entrySet()) {
								final PathFilter filter =
										new PathFilter(new PathFilterElement(
												GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips,
														entry.getKey().getPathLeaf(fact2Path), entry.getValue()
																.getPathLeaf(fact2Path))));
								joinPaths(pathToJoinedWith, filter);
								filters.add(filter);
							}
							for (final SingleSlotVariable sv : merged.values()) {
								sv.getEqual().iterator().next().getEqualSlotVariables().remove(sv);
							}
							slotVariables.removeAll(merged.values());
							continue slotLoop;
						}
					}
				}
			}

			// Collect all used Paths for every PathFilter
			final Map<PathFilterList, HashSet<Path>> filter2Paths =
					filters.stream().collect(
							Collectors.toMap(Function.identity(),
									filter -> PathCollector.newHashSet().collectAll(filter).getPaths()));

			// Split PathFilters into those only using existential Paths and those also using non
			// existential Paths
			final LinkedList<PathFilterList> pureExistentialFilters, nonPureExistentialFilters;
			{
				final Map<Boolean, LinkedList<PathFilterList>> tmp =
						filters.stream().collect(
								Collectors.partitioningBy(
										filter -> deepExistentialPaths.containsAll(filter2Paths.get(filter)),
										toCollection(LinkedList::new)));
				pureExistentialFilters = tmp.get(Boolean.TRUE);
				nonPureExistentialFilters = tmp.get(Boolean.FALSE);
			}

			// Add all pureExistentialFilters to result List because they don't have to be combined
			// or ordered
			final List<PathFilterList> resultFilters = new ArrayList<>(pureExistentialFilters);

			if (nonPureExistentialFilters.isEmpty()) {
				// if there are only existential filters, append one combining them with an initial
				// fact path
				assert null != initialFactPath;
				final ArrayList<Path> paths = new ArrayList<>();
				paths.addAll(shallowExistentialPaths);
				paths.add(initialFactPath);
				final PathFilter existentialClosure =
						new PathFilter(isPositive, shallowExistentialPaths, new PathFilter.DummyPathFilterElement(
								toArray(paths, Path[]::new)));
				joinPaths(pathToJoinedWith, existentialClosure);
				return Arrays.asList(new PathFilterList.PathFilterExistentialList(resultFilters, existentialClosure));
			}

			// Construct HashMap from Paths to Filters
			final Map<Path, Set<PathFilterList>> path2Filters = new HashMap<>();
			filter2Paths.forEach((pathFilter, paths) -> paths.forEach(path -> path2Filters.computeIfAbsent(path,
					x -> new HashSet<>()).add(pathFilter)));

			// Find connected components of the existential Paths
			final Map<Path, Set<Path>> joinedExistentialPaths =
					deepExistentialPaths.stream().collect(
							toMap(Function.identity(), p -> new HashSet<>(Arrays.asList(p))));
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
					newCollectedExistentialPaths.retainAll(shallowExistentialPaths);
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

			{
				// if not all paths within this existential CE have been used in some test, add a
				// dummy filter element to have them joined, too
				final Set<Path> unprocessedExistentialPaths = new HashSet<>(shallowExistentialPaths);
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