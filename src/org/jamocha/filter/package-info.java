/**
 * Contains {@link org.jamocha.filter.Filter filters} and their components.
 * 
 * A Filter contains so-called {@link org.jamocha.filter.Filter.FilterElement
 * filter elements} representing atomic {@link org.jamocha.filter.Predicate
 * predicates} specifying the tests to be performed on data according to the
 * condition part of a rule. Predicates are and may contain
 * {@link org.jamocha.filter.Function functions}. Implementations of
 * {@link org.jamocha.filter.Function functions} are found in the package
 * {@link org.jamocha.filter.impls}.<br />
 * In a {@link org.jamocha.filter.Filter.FilterElement filter element}, these
 * predicates are stored as {@link org.jamocha.filter.FunctionWithArguments
 * functions with arguments}. {@link org.jamocha.filter.FunctionWithArguments}
 * is an interface for a {@link org.jamocha.filter.Function} bundled with its
 * Arguments. A Filter is constructed using the following classes implementing
 * this interface: {@link org.jamocha.filter.FunctionWithArgumentsComposite},
 * {@link org.jamocha.filter.ConstantLeaf}, {@link org.jamocha.filter.PathLeaf}.
 * In doing so, we combine Functions, Constants and Paths. After all Paths used
 * have been mapped to their corresponding addresses (see
 * {@link org.jamocha.filter.PathTransformation}), we can transform (
 * {@link org.jamocha.filter.Filter#translatePath()}) the filter to contain only
 * {@link org.jamocha.filter.FunctionWithArgumentsComposite},
 * {@link org.jamocha.filter.ConstantLeaf},
 * {@link org.jamocha.filter.PathLeaf.ParameterLeaf}. During this step, the
 * {@link org.jamocha.filter.Filter.FilterElement FilterElements} get their
 * {@link org.jamocha.filter.Filter.FilterElement#addressesInTarget}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.filter.Filter
 * @see org.jamocha.filter.Filter.FilterElement
 * @see org.jamocha.filter.Function
 * @see org.jamocha.filter.FunctionWithArguments
 * @see org.jamocha.filter.Path
 * @see org.jamocha.filter.PathTransformation
 * @see org.jamocha.filter.impls
 */
package org.jamocha.filter;

