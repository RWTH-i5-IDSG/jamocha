package org.jamocha.dn.compiler.ecblocks;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule.ECSetRule;
import org.jamocha.languages.common.SingleFactVariable;

import com.atlassian.fugue.Either;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class Rule {
	final ECSetRule original;
	final Set<Filter> filters = new HashSet<>();
	final Set<SingleFactVariable> factvariables;
	final BiMap<Filter.FilterInstance, ExistentialProxy> existentialProxies = HashBiMap.create();
	final Either<Rule, ExistentialProxy> either;

	public Rule(final ECSetRule original) {
		this.original = original;
		this.factvariables = original.getFactVariables();
		this.either = Either.left(this);
	}

	@Override
	public String toString() {
		return this.original.getParent().getName() + "@" + Integer.toHexString(System.identityHashCode(this));
	}
}