/**
 * Contains {@link org.jamocha.filter.Filter filters} and their components.
 * 
 * A Filter contains so-called {@link org.jamocha.filter.Filter.FilterElement filter elements}
 * representing atomic {@link org.jamocha.function.Predicate predicates} specifying the tests to be
 * performed on data according to the condition part of a rule. Predicates are and may contain
 * {@link org.jamocha.function.Function functions}. Implementations of
 * {@link org.jamocha.function.Function functions} are found in the package
 * {@link org.jamocha.filter.impls}.<br />
 * In a {@link org.jamocha.filter.Filter.FilterElement filter element}, these predicates are stored
 * as {@link org.jamocha.function.fwa.FunctionWithArguments functions with arguments}.
 * {@link org.jamocha.function.fwa.FunctionWithArguments FunctionWithArguments} is an interface for a
 * {@link org.jamocha.function.Function} bundled with its Arguments. A Filter is constructed using the
 * following classes implementing this interface:
 * {@link org.jamocha.function.fwa.GenericWithArgumentsComposite GenericWithArgumentsComposite},
 * {@link org.jamocha.function.fwa.ConstantLeaf ConstantLeaf} ,
 * {@link org.jamocha.function.fwa.PathLeaf PathLeaf}. In doing so, we combine Functions, Constants
 * and Paths. After all {@link org.jamocha.filter.Path paths} used have been mapped to their
 * corresponding addresses, we can transform (
 * {@link org.jamocha.filter.PathFilterToAddressFilterTranslator#translate(PathFilter, org.jamocha.dn.memory.CounterColumnMatcher)}
 * ) the filter to contain only {@link org.jamocha.function.fwa.GenericWithArgumentsComposite
 * GenericWithArgumentsComposite}, {@link org.jamocha.function.fwa.ConstantLeaf ConstantLeaf} ,
 * {@link org.jamocha.function.fwa.PathLeaf.ParameterLeaf ParameterLeaf}. During this step, the
 * {@link org.jamocha.filter.Filter.PathFilterElement PathFilterElements} are translated into
 * {@link org.jamocha.filter.AddressFilter.AddressFilterElement AddressFilterElements} storing the
 * addresses formerly stored in the {@link org.jamocha.filter.Path paths}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.filter.Filter
 * @see org.jamocha.filter.Filter.FilterElement
 * @see org.jamocha.function.Function
 * @see org.jamocha.function.fwa.FunctionWithArguments
 * @see org.jamocha.filter.Path
 * @see org.jamocha.filter.impls
 */
package org.jamocha.filter;

