package org.jamocha.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;

@Getter
@RequiredArgsConstructor
@ToString
public abstract class Filter<L extends ExchangeableLeaf<L>> {
	protected final PredicateWithArguments<L> function;
}