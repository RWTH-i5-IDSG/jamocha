package org.jamocha.dn.compiler;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import org.jamocha.filter.PathFilter.DummyPathFilterElement;
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
import org.jamocha.function.impls.predicates.And;
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
		private final Map<EquivalenceClass, Path> ec2Path;
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
				// check whether the fact variables are bound via a TPCE
				for (final Iterator<SingleFactVariable> iterator = ec.getFactVariables().iterator(); iterator.hasNext();) {
					final SingleFactVariable fv = iterator.next();
					if (!occurringFactVariables.contains(fv)) {
						// the fact variable is not contained in the CE, remove it
						iterator.remove();
					}
				}
				// for every slot variable, check whether the corresponding fact variable is
				// contained in the CE
				for (final Iterator<SingleSlotVariable> iterator = ec.getEqualSlotVariables().iterator(); iterator
						.hasNext();) {
					final SingleSlotVariable sv = iterator.next();
					if (!occurringFactVariables.contains(sv.getFactVariable())) {
						// not contained, remove the SV and its reference to this EC
						iterator.remove();
						sv.getEqualSet().remove(ec);
					}
				}
				// for every FWA, check whether the FWA occurs in the CE
				ec.getEqualFWAs().removeIf(fwa -> !occurringFWAs.contains(fwa));
			}

			/* merge (bind)s and overlapping SlotVariables */
			for (final VariableSymbol vs : symbols) {
				final EquivalenceClass ec = vs.getEqual();
				for (final SingleSlotVariable sv : ec.getEqualSlotVariables()) {
					if (sv.getEqualSet().size() <= 1)
						continue;
					final Iterator<EquivalenceClass> ecIter = sv.getEqualSet().iterator();
					final EquivalenceClass first = ecIter.next();
					while (ecIter.hasNext()) {
						final EquivalenceClass other = ecIter.next();
						first.merge(other);
						replaceEC(symbols, Collections.singletonMap(other, first));
					}
				}
			}

			/* merge fact variables within equivalence classes */
			symbols.forEach(vs -> vs.getEqual().mergeEquivalenceClassesOfFactVariables());

			/* check that all variables are bound */
			final Set<VariableSymbol> symbolsInLeafs = SymbolInSymbolLeafsCollector.collect(ce);
			for (final VariableSymbol vs : symbols) {
				final EquivalenceClass ec = vs.getEqual();
				if (ec.getFactVariables().isEmpty() && ec.getEqualSlotVariables().isEmpty()) {
					if (!ec.getUnequalEquivalenceClasses().isEmpty() || symbolsInLeafs.contains(vs))
						// vs is not bound
						throw new VariableNotDeclaredError(vs.getImage());
				}
			}

			/* merge equals test conditional elements arguments */
			ce.accept(new CEEquivalenceClassBuilder(symbols, false));

			final TranslatedPath result =
					consolidateOnCopiedEquivalenceClasses(initialFactTemplate, rule, pathToJoinedWith, ce, symbols
							.stream().map(VariableSymbol::getEqual).collect(toSet()), Specificity.calculate(ce),
							oldToNew);

			// reset the symbol - equivalence class mapping
			symbolToECbackup.forEach((vs, ec) -> vs.setEqual(ec));
			replaceEC(symbols, oldToNew.inverse());
			// restore the SlotVariable - equivalence class mapping
			symbolToECbackup.forEach((vs, ec) -> vs.getEqual().getEqualSlotVariables()
					.forEach(sv -> sv.getEqualSet().add(ec)));
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
				oldEC.getFactVariables().forEach(fv -> fv.setEqual(newEC));
				// SingleSlotVariable.equal (Set)
				for (final SingleSlotVariable sv : oldEC.getEqualSlotVariables()) {
					final Set<EquivalenceClass> equalSet = sv.getEqualSet();
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
								left.merge(right);
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
				final Set<EquivalenceClass> equivalenceClasses, final int specificity,
				final Map<EquivalenceClass, EquivalenceClass> oldToNew) {
			final Pair<Path, Map<EquivalenceClass, Path>> initialFactAndEc2Path =
					ShallowFactVariableCollector.generatePaths(initialFactTemplate, ce);
			final Map<EquivalenceClass, Path> ec2Path = initialFactAndEc2Path.getRight();
			final Set<Path> allPaths = new HashSet<>(ec2Path.values());

			final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf = new HashMap<>();
			for (final EquivalenceClass equiv : equivalenceClasses) {
				equivalenceClassToPathLeaf.put(equiv, equiv.getPathLeaf(ec2Path));
			}

			final NoORsPFC instance =
					new NoORsPFC(initialFactTemplate, initialFactAndEc2Path.getLeft(), ec2Path, pathToJoinedWith,
							equivalenceClassToPathLeaf, false).collect(ce);
			final List<PathFilterList> pathFilters = instance.getPathFilters();

			// add the tests for the equivalence classes
			{
				final Set<EquivalenceClass> neqAlreadyDone = new HashSet<>();
				for (final EquivalenceClass equiv : equivalenceClasses) {
					createEquivalenceClassTests(pathToJoinedWith, ec2Path, equivalenceClassToPathLeaf, pathFilters,
							neqAlreadyDone, equiv);
				}
			}

			mergeMissingPathsViaDummy(allPaths, pathFilters, pathToJoinedWith, initialFactAndEc2Path.getLeft());
			final Map<EquivalenceClass, PathLeaf> originalEC2PathLeaf = new HashMap<>();
			oldToNew.forEach((k, v) -> originalEC2PathLeaf.put(k, equivalenceClassToPathLeaf.get(v)));
			return rule.newTranslated(new PathFilterSharedListWrapper().newSharedElement(pathFilters),
					originalEC2PathLeaf, specificity);
		}

		private static void mergeMissingPathsViaDummy(final Set<Path> allPaths, final List<PathFilterList> pathFilters,
				final Map<Path, Set<Path>> pathToJoinedWith, final Path initialFactPath) {
			final Set<Path> collectedPaths =
					(pathFilters.isEmpty() ? Collections.<Path> emptySet() : PathCollector.newHashSet()
							.collectOnlyInFilterElements(pathFilters.get(pathFilters.size() - 1)).getPaths().stream()
							.flatMap((p) -> pathToJoinedWith.get(p).stream()).collect(toSet()));
			{
				final PathCollector<HashSet<Path>> pc = PathCollector.newHashSet();
				pathFilters.forEach(pc::collectAll);
				if (pc.getPaths().contains(initialFactPath)) {
					allPaths.add(initialFactPath);
				}
			}
			if (collectedPaths.containsAll(allPaths)) {
				return;
			}
			allPaths.addAll(collectedPaths);
			final PathFilter dummy =
					new PathFilter(new PathFilter.DummyPathFilterElement(toArray(allPaths, Path[]::new)));
			joinPaths(pathToJoinedWith, dummy);
			pathFilters.add(dummy);
			return;
		}

		private static final Predicate not = FunctionDictionary.lookupPredicate(Not.inClips, SlotType.BOOLEAN);

		private static void createEquivalenceClassTests(final Map<Path, Set<Path>> pathToJoinedWith,
				final Map<EquivalenceClass, Path> ec2Path,
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
				createEqualSlotsAndFactsTests(equiv, pathFilters, pathToJoinedWith, ec2Path, (x) -> true,
						(x) -> element);
			}
			if (!equiv.getEqualFWAs().isEmpty()) {
				for (final FunctionWithArguments<SymbolLeaf> other : equiv.getEqualFWAs()) {
					addEqualityTestTo(pathFilters, pathToJoinedWith, element,
							SymbolToPathTranslator.translate(other, equivalenceClassToPathLeaf), true);
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

		private static void createEqualSlotsAndFactsTests(
				final EquivalenceClass equiv,
				final List<PathFilterList> pathFilters,
				final Map<Path, Set<Path>> pathToJoinedWith,
				final Map<EquivalenceClass, Path> ec2Path,
				final java.util.function.Predicate<? super SingleFactVariable> pred,
				final Function<? super Set<FunctionWithArguments<PathLeaf>>, ? extends FunctionWithArguments<PathLeaf>> elementChooser) {
			final Set<FunctionWithArguments<PathLeaf>> equalPathLeafs =
					equiv.getEqualSlotVariables().stream().filter(sv -> pred.test(sv.getFactVariable()))
							.map(sv -> sv.getPathLeaf(ec2Path)).collect(toSet());
			equiv.getFactVariables().stream().filter(pred).map(fv -> fv.getPathLeaf(ec2Path))
					.forEach(equalPathLeafs::add);
			if (equalPathLeafs.size() <= 1) {
				return;
			}
			final FunctionWithArguments<PathLeaf> element = elementChooser.apply(equalPathLeafs);
			equalPathLeafs.remove(element);
			for (Iterator<FunctionWithArguments<PathLeaf>> iterator = equalPathLeafs.iterator(); iterator.hasNext();) {
				final FunctionWithArguments<PathLeaf> other = iterator.next();
				final PathFilter eqTest =
						new PathFilter(new PathFilterElement(GenericWithArgumentsComposite.newPredicateInstance(
								Equals.inClips, element, other)));
				joinPaths(pathToJoinedWith, eqTest);
				pathFilters.add(eqTest);
			}
			// remove all used single slot variables and the fact variable except the for the
			// _element_ chosen (which is not part of equalPathLeafs)
			equiv.getEqualSlotVariables().removeIf(sv -> equalPathLeafs.contains(sv.getPathLeaf(ec2Path)));
			equiv.getFactVariables().removeIf(fv -> equalPathLeafs.contains(fv.getPathLeaf(ec2Path)));
		}

		private <T extends ConditionalElement> NoORsPFC collect(final T ce) {
			return ce.accept(this);
		}

		private static Map<EquivalenceClass, PathLeaf> getMatchingFVsAndPathLeafs(final SingleFactVariable fv,
				final Set<EquivalenceClass> targets, final Map<EquivalenceClass, Path> ec2Path) {
			final Map<EquivalenceClass, PathLeaf> ecToTarget = new HashMap<>();
			for (final SingleSlotVariable sv : fv.getSlotVariables()) {
				final EquivalenceClass ec = sv.getEqual();
				if (targets.contains(ec)) {
					ecToTarget.put(ec, sv.getPathLeaf(ec2Path));
				}
			}
			if (targets.contains(fv.getEqual())) {
				ecToTarget.put(fv.getEqual(), fv.getPathLeaf(ec2Path));
			}
			return ecToTarget;
		}

		// merge the fact variables given and apply all tests between them
		private static void mergeFVs(final Set<SingleFactVariable> shallowExistentialFVs,
				final Map<Path, Set<Path>> pathToJoinedWith, final Map<EquivalenceClass, Path> ec2Path,
				final List<PathFilterList> pathFilters) {
			if (shallowExistentialFVs.size() == 1)
				return;
			final Set<EquivalenceClass> done = new HashSet<>();
			for (final SingleFactVariable factVariable : shallowExistentialFVs) {
				for (final SingleSlotVariable slotVariable : factVariable.getSlotVariables()) {
					final EquivalenceClass equiv = slotVariable.getEqual();
					if (!done.add(equiv))
						continue;
					createEqualSlotsAndFactsTests(equiv, pathFilters, pathToJoinedWith, ec2Path,
							shallowExistentialFVs::contains, set -> set.iterator().next());
				}
			}
		}

		static List<PathFilterList> processExistentialCondition(final Template initialFactTemplate,
				final Path initialFactPath, final ConditionalElement ce, final Map<EquivalenceClass, Path> ec2Path,
				final Map<Path, Set<Path>> pathToJoinedWith,
				final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf, final boolean isPositive) {
			// Collect the existential FactVariables and corresponding paths from the existentialCE
			final Pair<Path, Map<EquivalenceClass, Path>> initialFactAndPathMap =
					ShallowFactVariableCollector.generatePaths(initialFactTemplate, ce);
			final Map<EquivalenceClass, Path> shallowExistentialEc2Path = initialFactAndPathMap.getRight();

			// combine existential FactVariables and Paths with non existential ones for PathFilter
			// generation
			ec2Path.putAll(shallowExistentialEc2Path);

			for (final EquivalenceClass equiv : equivalenceClassToPathLeaf.keySet()) {
				equivalenceClassToPathLeaf.computeIfAbsent(equiv, e -> e.getPathLeaf(ec2Path));
			}

			// Only existential Paths
			final Set<Path> shallowExistentialPaths = new HashSet<>(shallowExistentialEc2Path.values());

			// Generate PathFilters from CE (recurse)
			final List<PathFilterList> filters =
					new NoORsPFC(initialFactTemplate, initialFactAndPathMap.getLeft(), ec2Path, pathToJoinedWith,
							equivalenceClassToPathLeaf, false).collect(ce).getPathFilters();

			{
				final Set<SingleFactVariable> shallowExistentialFVs =
						new HashSet<>(ShallowFactVariableCollector.collect(ce));

				// if we have a fact variable that is bound here and elsewhere, throw
				shallowExistentialEc2Path
						.keySet()
						.stream()
						.forEach(
								ec -> {
									if (!shallowExistentialFVs.containsAll(ec.getMerged()))
										throw new RuntimeException(
												"Shared Fact Variable binding inside and outside of Existential CE found, unsupported!");
								});

				// if there is more than just one TPCE (i.e. there is more than one FactVariable),
				// we have to join those in an own node first (thus they all come in via the same
				// edge) to be able to access all relevant information in the test of the
				// "existential CE"
				// equal slot variables within the existential-CE always have to be included in the
				// test, so add them too
				mergeFVs(shallowExistentialFVs, pathToJoinedWith, ec2Path, filters);
				// after the merge, the mapping between Slot Variables of the existential FVs and
				// their equivalence classes should be 1:1 if you ignore the SlotVariables that are
				// no longer contained in their equivalence class
				final Map<EquivalenceClass, SingleSlotVariable> ecToSVs =
						shallowExistentialFVs
								.stream()
								.flatMap(exFV -> exFV.getSlotVariables().stream())
								.collect(
										toMap(sv -> sv.getEqual(), Function.identity(), (a, b) -> (a.getEqual()
												.getEqualSlotVariables().contains(a) ? a : b)));

				/*
				 * if there are no symbol occurrences outside (which means we have at most one equal
				 * slot variable left), work off the CE now
				 */
				final Map<Boolean, Set<EquivalenceClass>> partitionedEquivalenceClasses =
						ecToSVs.keySet()
								.stream()
								.collect(
										partitioningBy(
												ec -> ec.getEqualSlotVariables().size() <= 1
														&& !Optional.ofNullable(ec.getFactVariables().peekFirst())
																.filter(fv -> !shallowExistentialFVs.contains(fv))
																.isPresent(), toSet()));
				shallowExistentialEc2Path
						.keySet()
						.stream()
						.filter(ec -> !ecToSVs.keySet().contains(ec) && !ec.getEqualSlotVariables().isEmpty())
						.forEach(
								ec -> partitionedEquivalenceClasses.get(
										!ec.getEqualSlotVariables().stream()
												.filter(sv -> shallowExistentialEc2Path.containsKey(sv.getEqual()))
												.findAny().isPresent()).add(ec));

				final Set<EquivalenceClass> localEquivalenceClasses = partitionedEquivalenceClasses.get(Boolean.TRUE);
				for (final EquivalenceClass localEquivalenceClass : localEquivalenceClasses) {
					final FunctionWithArguments<PathLeaf> element =
							equivalenceClassToPathLeaf.get(localEquivalenceClass);
					createEqualSlotsAndFactsTests(localEquivalenceClass, filters, pathToJoinedWith, ec2Path,
							(x) -> true, (x) -> element);
					for (final FunctionWithArguments<SymbolLeaf> fwa : localEquivalenceClass.getEqualFWAs()) {
						addEqualityTestTo(filters, pathToJoinedWith, element,
								SymbolToPathTranslator.translate(fwa, equivalenceClassToPathLeaf), true);
					}
					for (final EquivalenceClass unequal : localEquivalenceClass.getUnequalEquivalenceClasses()) {
						final PathLeaf other = equivalenceClassToPathLeaf.get(unequal);
						addEqualityTestTo(filters, pathToJoinedWith, element, other, false);
						localEquivalenceClass.removeNegatedEdge(unequal);
					}
				}

				final Set<EquivalenceClass> nonLocalEquivalenceClasses =
						partitionedEquivalenceClasses.get(Boolean.FALSE);
				if (!nonLocalEquivalenceClasses.isEmpty()) {
					/*
					 * the equal FWAs have to be scanned for existential symbols - those containing
					 * any need to be joined here in every case
					 */
					for (final EquivalenceClass nonLocalEquivalenceClass : nonLocalEquivalenceClasses) {
						final LinkedList<FunctionWithArguments<SymbolLeaf>> equalFWAs =
								nonLocalEquivalenceClass.getEqualFWAs();
						final FunctionWithArguments<PathLeaf> element =
								equivalenceClassToPathLeaf.get(nonLocalEquivalenceClass);
						final Set<FunctionWithArguments<SymbolLeaf>> equalFWAsContainingCurrentExistentialSymbols =
								equalFWAs
										.stream()
										.filter(fwa -> SymbolInSymbolLeafsCollector.collect(fwa).stream()
												.map(vs -> vs.getEqual()).filter(localEquivalenceClasses::contains)
												.findAny().isPresent()).collect(toSet());
						for (final FunctionWithArguments<SymbolLeaf> other : equalFWAsContainingCurrentExistentialSymbols) {
							addEqualityTestTo(filters, pathToJoinedWith, element,
									SymbolToPathTranslator.translate(other, equivalenceClassToPathLeaf), true);
							equalFWAs.remove(other);
						}
					}

					/*
					 * just checking equality regarding FWAs breaks if we allow FWAs to return
					 * different values for different calls with the same arguments such as (random
					 * 0 1). without knowing anything about the equal FWAs we would have to: if
					 * there are equalFWAs: join everything at once if there are no equalFWAs: join
					 * at least one of the FVs of the equal SVs
					 */

					/*
					 * now, the only thing left to do should be to find non-existential fact
					 * variables or constants which we can use to create a final test establishing
					 * the ties to the non-existential parts of the non-local equivalence classes
					 * after which all existential parts are converted into a number in its counter
					 * column
					 */
					// use constants where possible
					final Set<Pair<EquivalenceClass, FunctionWithArguments<SymbolLeaf>>> ecToConstant =
							nonLocalEquivalenceClasses
									.stream()
									.map(ec -> ec.getEqualFWAs().stream().filter(fwa -> fwa instanceof ConstantLeaf)
											.findAny().map(c -> Pair.of(ec, c)).orElse(null)).filter(Objects::nonNull)
									.collect(toSet());
					for (final Pair<EquivalenceClass, FunctionWithArguments<SymbolLeaf>> ecAndConstant : ecToConstant) {
						final EquivalenceClass equal = ecAndConstant.getLeft();
						final PathLeaf element = equivalenceClassToPathLeaf.get(equal);
						final FunctionWithArguments<PathLeaf> other =
								SymbolToPathTranslator.translate(ecAndConstant.getRight(), equivalenceClassToPathLeaf);
						// create the filter
						final PathFilter filter =
								new PathFilter(new PathFilterElement(
										GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips, element,
												other)));
						joinPaths(pathToJoinedWith, filter);
						filters.add(filter);
						// mark as done
						nonLocalEquivalenceClasses.remove(equal);
					}
					// no constants found for the following ones, join non-existential fact
					// variables
					while (!nonLocalEquivalenceClasses.isEmpty()) {
						final Optional<Map<EquivalenceClass, PathLeaf>> optBestCandidate =
								nonLocalEquivalenceClasses
										.stream()
										.flatMap(
												ec -> ec.getEqualSlotVariables().stream()
														.map(SingleSlotVariable::getFactVariable))
										.distinct()
										.filter(fv -> !shallowExistentialFVs.contains(fv)
												&& ec2Path.containsKey(fv.getEqual()))
										.map(fv -> getMatchingFVsAndPathLeafs(fv, nonLocalEquivalenceClasses, ec2Path))
										.max((a, b) -> Integer.compare(a.size(), b.size()));
						if (optBestCandidate.isPresent()) {
							final Map<EquivalenceClass, PathLeaf> merged = optBestCandidate.get();
							for (final Entry<EquivalenceClass, PathLeaf> entry : merged.entrySet()) {
								final PathLeaf target = entry.getValue();
								final EquivalenceClass ec = entry.getKey();
								final SingleSlotVariable svDone = ecToSVs.get(ec);
								final PathLeaf source;
								if (svDone != null) {
									// remove the SV
									ec.getEqualSlotVariables().remove(svDone);
									source = svDone.getPathLeaf(ec2Path);
								} else {
									final SingleFactVariable exFV = ec.getFactVariables().pollFirst();
									assert shallowExistentialFVs.contains(exFV);
									source = exFV.getPathLeaf(ec2Path);
								}
								// create the filter
								final PathFilter filter =
										new PathFilter(new PathFilterElement(
												GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips,
														source, target)));
								joinPaths(pathToJoinedWith, filter);
								filters.add(filter);
							}
							nonLocalEquivalenceClasses.removeAll(merged.keySet());
						} else {
							// only non-existential fact variables left
							nonLocalEquivalenceClasses.stream().forEach(ec -> {
								final PathLeaf source = ec.getFactVariables().pollFirst().getPathLeaf(ec2Path);
								final PathLeaf target = ec.getEqualSlotVariables().peekFirst().getPathLeaf(ec2Path);
								// create the filter
									final PathFilter filter =
											new PathFilter(new PathFilterElement(GenericWithArgumentsComposite
													.newPredicateInstance(Equals.inClips, source, target)));
									joinPaths(pathToJoinedWith, filter);
									filters.add(filter);
								});
							nonLocalEquivalenceClasses.clear();
						}
					}
				}
			}

			final List<SingleFactVariable> deepExistentialFactVariables = DeepFactVariableCollector.collect(ce);

			final Set<Path> deepExistentialPaths =
					deepExistentialFactVariables.stream().map(fv -> ec2Path.get(fv.getEqual())).collect(toSet());

			// Collect all used Paths for every PathFilter
			final Map<PathFilterList, HashSet<Path>> filter2Paths =
					filters.stream().collect(
							Collectors.toMap(Function.identity(),
									filter -> PathCollector.newHashSet().collectAll(filter).getPaths()));

			// Split PathFilters into those only using existential Paths and those also using non
			// existential Paths
			final LinkedList<PathFilterList> pureExistentialFilters, mixedExistentialFilters, nonExistentialFilters;
			{
				final Map<Boolean, LinkedList<PathFilterList>> tmp =
						filters.stream().collect(
								Collectors.partitioningBy(
										filter -> deepExistentialPaths.containsAll(filter2Paths.get(filter)),
										toCollection(LinkedList::new)));
				pureExistentialFilters = tmp.get(Boolean.TRUE);
				final Map<Boolean, LinkedList<PathFilterList>> tmp2 =
						tmp.get(Boolean.FALSE)
								.stream()
								.collect(
										partitioningBy(
												filter -> Collections.disjoint(deepExistentialPaths,
														filter2Paths.get(filter)), toCollection(LinkedList::new)));
				mixedExistentialFilters = tmp2.get(Boolean.FALSE);
				nonExistentialFilters = tmp2.get(Boolean.TRUE);
			}

			// Add all nonExistentialFilters to the result list because they don't have to be
			// combined or ordered
			final List<PathFilterList> resultFilters = new ArrayList<>(nonExistentialFilters);

			// Construct HashMap from Paths to Filters
			final Map<Path, Set<PathFilterList>> path2Filters = new HashMap<>();
			filter2Paths.forEach((pathFilter, paths) -> paths.forEach(path -> path2Filters.computeIfAbsent(path,
					x -> new HashSet<>()).add(pathFilter)));

			// Find connected components of the existential Paths
			final Map<Path, Set<Path>> joinedExistentialPaths =
					deepExistentialPaths.stream().collect(
							toMap(Function.identity(), p -> new HashSet<>(Arrays.asList(p))));
			final Map<Set<Path>, List<PathFilterList>> joinedExistentialFilters = new HashMap<>();
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
				// save the corresponding filters
				joinedExistentialFilters.put(collectedPaths, collectedFilters);
			}

			// existential paths already marked as existential in some filter
			final Set<Path> processedExistentialPaths = new HashSet<>();
			// Combine mixedExistentialFilters if necessary and add them to result list
			while (!mixedExistentialFilters.isEmpty()) {
				final List<PathFilterList> collectedFilters =
						new ArrayList<>(Collections.singletonList(mixedExistentialFilters.poll()));
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
					newCollectedFilters.retainAll(mixedExistentialFilters);
					// remove already known filters
					newCollectedFilters.removeAll(collectedFilters);
					// add them all to the collect set
					collectedFilters.addAll(newCollectedFilters);
					// remove them from the set of unassigned filters
					mixedExistentialFilters.removeAll(newCollectedFilters);
				}

				final List<PathFilterList> correspondingPureExistentialFilters =
						collectedExistentialPaths
								.stream()
								.map(joinedExistentialPaths::get)
								.distinct()
								.flatMap(
										set -> joinedExistentialFilters.getOrDefault(set, Collections.emptyList())
												.stream()).collect(toList());
				final List<PredicateWithArguments<PathLeaf>> predicates =
						collectedFilters.stream().flatMap(fl -> StreamSupport.stream(fl.spliterator(), false))
								.flatMap(f -> Arrays.stream(f.getFilterElements())).map(PathFilterElement::getFunction)
								.collect(toList());
				final PathCollector<HashSet<Path>> pathCollector = PathCollector.newHashSet();
				predicates.forEach(pathCollector::collect);
				final HashSet<Path> missingPaths = pathCollector.getPaths();
				missingPaths.removeAll(deepExistentialPaths);

				final boolean missing = !missingPaths.isEmpty();
				final PathFilterElement[] pfes = new PathFilterElement[missing ? 2 : 1];
				if (missing) {
					pfes[0] = new DummyPathFilterElement(toArray(missingPaths, Path[]::new));
				}
				pfes[pfes.length - 1] =
						new PathFilterElement(
								predicates.size() > 1 ? GenericWithArgumentsComposite.newPredicateInstance(
										And.inClips,
										(FunctionWithArguments<PathLeaf>[]) toArray(predicates,
												PredicateWithArguments[]::new)) : predicates.get(0));
				final PathFilter combiningFilter = new PathFilter(isPositive, collectedExistentialPaths, pfes);
				joinPaths(pathToJoinedWith, combiningFilter);
				resultFilters.add(new PathFilterList.PathFilterExistentialList(correspondingPureExistentialFilters,
						combiningFilter));
				processedExistentialPaths.addAll(collectedExistentialPaths);
			}

			{
				// if not all paths within this existential CE have been processed and marked as
				// existential, add a dummy filter element for each existential component to have
				// them joined, too
				final Set<Path> unprocessedExistentialPaths = new HashSet<>(shallowExistentialPaths);
				unprocessedExistentialPaths.removeAll(processedExistentialPaths);
				if (!unprocessedExistentialPaths.isEmpty()) {
					// create dummy FEs for the existential path connected components
					final ArrayList<DummyPathFilterElement> fes = new ArrayList<>();
					// gather the corresponding filters
					final List<PathFilterList> unprocessedExistentialFilters = new ArrayList<>();
					unprocessedExistentialPaths
							.stream()
							.map(joinedExistentialPaths::get)
							.distinct()
							.forEach(
									(set) -> {
										unprocessedExistentialFilters.addAll(joinedExistentialFilters.getOrDefault(set,
												Collections.emptyList()));
										set.add(initialFactPath);
										fes.add(new DummyPathFilterElement(toArray(set, Path[]::new)));
									});
					final PathFilter dummy =
							new PathFilter(isPositive, unprocessedExistentialPaths, toArray(fes,
									DummyPathFilterElement[]::new));
					joinPaths(pathToJoinedWith, dummy);
					resultFilters
							.add(new PathFilterList.PathFilterExistentialList(unprocessedExistentialFilters, dummy));
				}
			}

			return resultFilters;
		}

		private static void addEqualityTestTo(final List<PathFilterList> filters,
				final Map<Path, Set<Path>> pathToJoinedWith, final FunctionWithArguments<PathLeaf> element,
				final FunctionWithArguments<PathLeaf> other, final boolean isPositive) {
			final PathFilter eqTest =
					new PathFilter(new PathFilterElement(GenericWithArgumentsComposite.newPredicateInstance(isPositive,
							Equals.inClips, element, other)));
			joinPaths(pathToJoinedWith, eqTest);
			filters.add(eqTest);
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
									new NoORsPFC(initialFactTemplate, initialFactPath, ec2Path, pathToJoinedWith,
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
					processExistentialCondition(initialFactTemplate, initialFactPath, ce.getChildren().get(0), ec2Path,
							pathToJoinedWith, equivalenceClassToPathLeaf, true);
		}

		@Override
		public void visit(final NegatedExistentialConditionalElement ce) {
			assert ce.getChildren().size() == 1;
			this.pathFilters =
					processExistentialCondition(initialFactTemplate, initialFactPath, ce.getChildren().get(0), ec2Path,
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
							.accept(new NoORsPFC(initialFactTemplate, initialFactPath, ec2Path, pathToJoinedWith,
									equivalenceClassToPathLeaf, !negated)).getPathFilters();
		}

		@Override
		public void visit(final SharedConditionalElementWrapper ce) {
			// use the wrapper for the inner shared instances
			this.pathFilters =
					Collections.singletonList(ce.getWrapper().newSharedElement(
							ce.getCe()
									.accept(new NoORsPFC(initialFactTemplate, initialFactPath, ec2Path,
											pathToJoinedWith, equivalenceClassToPathLeaf, negated)).getPathFilters()));
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