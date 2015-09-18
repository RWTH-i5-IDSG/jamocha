package org.jamocha.dn.compiler.ecblocks;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.languages.common.SingleFactVariable;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@ToString(of = { "rule", "filters" })
public class ExistentialProxy {
	final Rule rule;
	final ECExistentialSet existential;
	final Set<Filter> filters = new HashSet<>();
	final Set<SingleFactVariable> factvariables;
	final Either<Rule, ExistentialProxy> either;

	public ExistentialProxy(final Rule rule, final ECExistentialSet existential) {
		this.rule = rule;
		this.existential = existential;
		this.factvariables = existential.getExistentialFactVariables();
		this.either = Either.right(this);
	}

	public Filter.FilterInstance getExistentialClosure() {
		return this.rule.getExistentialProxies().inverse().get(this);
	}
}