package org.jamocha.dn.compiler.ecblocks;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@ToString(of = { "subSets" })
class Partition<T, S extends Partition.SubSet<T>> {
	@RequiredArgsConstructor
	@Getter
	@ToString
	static class SubSet<T> {
		final IdentityHashMap<Either<Rule, ExistentialProxy>, T> elements;

		public SubSet(final Map<Either<Rule, ExistentialProxy>, ? extends T> elements) {
			this(new IdentityHashMap<>(elements));
		}

		public T get(final Either<Rule, ExistentialProxy> rule) {
			return this.elements.get(rule);
		}
	}

	final Set<S> subSets = new HashSet<>();
	final IdentityHashMap<T, S> lookup = new IdentityHashMap<>();

	public Partition(final Partition<T, S> copy) {
		this.subSets.addAll(copy.subSets);
		this.lookup.putAll(copy.lookup);
	}

	public void add(final S newSubSet) {
		assert this.subSets.stream().allMatch(ss -> ss.getElements().keySet().equals(newSubSet.elements.keySet()));
		assert this.subSets.stream().allMatch(
				ss -> Collections.disjoint(ss.getElements().values(), newSubSet.elements.values()));
		this.subSets.add(newSubSet);
		for (final T newElement : newSubSet.elements.values()) {
			this.lookup.put(newElement, newSubSet);
		}
	}

	public void extend(final Either<Rule, ExistentialProxy> rule, final IdentityHashMap<S, T> extension) {
		for (final S subset : this.subSets) {
			subset.elements.put(rule, extension.get(subset));
		}
	}

	public S lookup(final T element) {
		return this.lookup.get(element);
	}

	public void remove(final Either<Rule, ExistentialProxy> rule) {
		for (final S s : subSets) {
			s.elements.remove(rule);
		}
	}

	public boolean remove(final S s) {
		s.elements.keySet().forEach(lookup::remove);
		return subSets.remove(s);
	}
}