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

/**
 * Contains {@link org.jamocha.filter.NodeFilterSet filters} and their components.
 *
 * A Filter contains so-called {@link org.jamocha.filter.Filter filter elements} representing atomic {@link
 * org.jamocha.function.Predicate predicates} specifying the tests to be performed on data according to the condition
 * part of a rule. Predicates are and may contain {@link org.jamocha.function.Function functions}. Implementations of
 * {@link org.jamocha.function.Function functions} are found in the package {@link org.jamocha.filter.impls}.<br /> In a
 * {@link org.jamocha.filter.Filter filter element}, these predicates are stored as {@link
 * org.jamocha.function.fwa.FunctionWithArguments functions with arguments}. {@link
 * org.jamocha.function.fwa.FunctionWithArguments FunctionWithArguments} is an interface for a {@link
 * org.jamocha.function.Function} bundled with its Arguments. A Filter is constructed using the following classes
 * implementing this interface: {@link org.jamocha.function.fwa.GenericWithArgumentsComposite
 * GenericWithArgumentsComposite}, {@link org.jamocha.function.fwa.ConstantLeaf ConstantLeaf} , {@link
 * org.jamocha.function.fwa.PathLeaf PathLeaf}. In doing so, we combine Functions, Constants and Paths. After all {@link
 * org.jamocha.filter.Path paths} used have been mapped to their corresponding addresses, we can transform ( {@link
 * org.jamocha.filter.PathNodeFilterSetToAddressNodeFilterSetTranslator#translate(PathFilter,
 * org.jamocha.dn.memory.CounterColumnMatcher)} ) the filter to contain only {@link
 * org.jamocha.function.fwa.GenericWithArgumentsComposite GenericWithArgumentsComposite}, {@link
 * org.jamocha.function.fwa.ConstantLeaf ConstantLeaf} , {@link org.jamocha.function.fwa.ParameterLeaf ParameterLeaf}.
 * During this step, the {@link org.jamocha.filter.NodeFilterSet.PathFilter PathFilterElements} are translated into
 * {@link org.jamocha.filter.AddressNodeFilterSet.AddressFilter AddressFilterElements} storing the addresses formerly
 * stored in the {@link org.jamocha.filter.Path paths}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.filter.NodeFilterSet
 * @see org.jamocha.filter.Filter
 * @see org.jamocha.function.Function
 * @see org.jamocha.function.fwa.FunctionWithArguments
 * @see org.jamocha.filter.Path
 * @see org.jamocha.filter.impls
 */
package org.jamocha.filter;

