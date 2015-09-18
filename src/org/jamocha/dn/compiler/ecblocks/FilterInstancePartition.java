package org.jamocha.dn.compiler.ecblocks;

import static org.jamocha.util.Lambdas.newHashSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
class FilterInstancePartition extends Partition<FilterInstance, FilterInstancePartition.FilterInstanceSubSet> {
	@Getter
	static class FilterInstanceSubSet extends Partition.SubSet<FilterInstance> {
		final Filter filter;

		public FilterInstanceSubSet(final IdentityHashMap<Either<Rule, ExistentialProxy>, FilterInstance> elements) {
			super(elements);
			this.filter = elements.values().iterator().next().getFilter();
		}

		public FilterInstanceSubSet(final Map<Either<Rule, ExistentialProxy>, ? extends FilterInstance> elements) {
			this(new IdentityHashMap<>(elements));
		}

		public boolean contains(final FilterInstancePartition.FilterInstanceSubSet other) {
			return this.elements.entrySet().containsAll(other.elements.entrySet());
		}
	}

	final IdentityHashMap<Filter, Set<FilterInstancePartition.FilterInstanceSubSet>> filterLookup =
			new IdentityHashMap<>();

	public FilterInstancePartition(final FilterInstancePartition copy) {
		super(copy);
		this.filterLookup.putAll(copy.filterLookup);
	}

	@Override
	public void add(final FilterInstancePartition.FilterInstanceSubSet newSubSet) {
		super.add(newSubSet);
		this.filterLookup.computeIfAbsent(newSubSet.filter, newHashSet()).add(newSubSet);
	}

	public Set<FilterInstancePartition.FilterInstanceSubSet> lookupByFilter(final Filter filter) {
		return this.filterLookup.get(filter);
	}

	@Override
	public boolean remove(final FilterInstanceSubSet s) {
		final boolean removed = super.remove(s);
		if (removed)
			filterLookup.get(s.filter).remove(s);
		return removed;
	}
}