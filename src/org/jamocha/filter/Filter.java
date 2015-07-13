package org.jamocha.filter;

import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public abstract class Filter<L extends ExchangeableLeaf<L>> {
	protected final PredicateWithArguments<L> function;
}