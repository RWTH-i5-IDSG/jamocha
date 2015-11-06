/*
 * Copyright 2002-2015 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.compiler.ecblocks;

import static org.jamocha.util.Lambdas.newHashSet;
import static org.jamocha.util.Lambdas.newTreeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.atlassian.fugue.Either;

@Getter
@RequiredArgsConstructor
public class ECBlockSet {
	final HashSet<Block> blocks = new HashSet<>();
	final TreeMap<Integer, HashSet<Block>> ruleCountToBlocks = new TreeMap<>();
	final TreeMap<Integer, HashSet<Block>> filterCountToBlocks = new TreeMap<>();
	final HashMap<Either<Rule, ExistentialProxy>, HashSet<Block>> ruleInstanceToBlocks = new HashMap<>();
	final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> ruleCountToFilterCountToBlocks = new TreeMap<>();
	final TreeMap<Integer, TreeMap<Integer, HashSet<Block>>> filterCountToRuleCountToBlocks = new TreeMap<>();

	public ECBlockSet(final ECBlockSet copy) {
		this.blocks.addAll(copy.blocks);
		copy.ruleCountToBlocks.forEach((i, s) -> this.ruleCountToBlocks.put(i, new HashSet<>(s)));
		copy.filterCountToBlocks.forEach((i, s) -> this.filterCountToBlocks.put(i, new HashSet<>(s)));
		copy.ruleInstanceToBlocks.forEach((r, s) -> this.ruleInstanceToBlocks.put(r, new HashSet<>(s)));
		for (final Entry<Integer, TreeMap<Integer, HashSet<Block>>> entry : copy.ruleCountToFilterCountToBlocks
				.entrySet()) {
			final TreeMap<Integer, HashSet<Block>> map = new TreeMap<>();
			entry.getValue().forEach((i, s) -> map.put(i, new HashSet<>(s)));
			this.ruleCountToFilterCountToBlocks.put(entry.getKey(), map);
		}
		for (final Entry<Integer, TreeMap<Integer, HashSet<Block>>> entry : copy.filterCountToRuleCountToBlocks
				.entrySet()) {
			final TreeMap<Integer, HashSet<Block>> map = new TreeMap<>();
			entry.getValue().forEach((i, s) -> map.put(i, new HashSet<>(s)));
			this.filterCountToRuleCountToBlocks.put(entry.getKey(), map);
		}
	}

	private static int getRowCount(final Block block) {
		return block.getNumberOfRows();
	}

	private static int getFilterCount(final Block block) {
		return block.getNumberOfColumns();
	}

	boolean addDuringHorizontalRecursion(final Block block) {
		final Integer rowCount = getRowCount(block);
		final Integer filterCount = getFilterCount(block);
		// first check if there is a block of the same height with more filter instances
		{
			final NavigableMap<Integer, HashSet<Block>> fixedRuleCountFilters =
					this.ruleCountToFilterCountToBlocks.computeIfAbsent(rowCount, newTreeMap()).tailMap(filterCount,
							true);
			for (final Set<Block> fixedFilterCountRule : fixedRuleCountFilters.values()) {
				for (final Block candidate : fixedFilterCountRule) {
					if (block.containedIn(candidate)) {
						return false;
					}
				}
			}
		}
		// then check if there is a block of the same width with more rules
		{
			final NavigableMap<Integer, HashSet<Block>> fixedFilterCountRules =
					this.filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap()).tailMap(rowCount,
							true);
			for (final Set<Block> fixedFilterCountRule : fixedFilterCountRules.values()) {
				for (final Block candidate : fixedFilterCountRule) {
					if (block.containedIn(candidate)) {
						return false;
					}
				}
			}
		}
		// finally check if there is a block larger in both dimensions
		{
			for (final TreeMap<Integer, HashSet<Block>> fixedFilterCountMap : this.filterCountToRuleCountToBlocks
					.tailMap(filterCount, false).values()) {
				for (final HashSet<Block> candidates : fixedFilterCountMap.tailMap(rowCount, false).values()) {
					for (final Block candidate : candidates) {
						if (block.containedIn(candidate)) {
							return false;
						}
					}
				}
			}
		}
		actuallyInsertBlockIntoAllCaches(block);
		return true;
	}

	private void removeContainedBlocks(final Block block) {
		final Integer rowCount = getRowCount(block);
		final Integer filterCount = getFilterCount(block);

		final List<Block> toRemove = new ArrayList<>();

		final Collection<TreeMap<Integer, HashSet<Block>>> filterCountToBlocksRuleCountHead =
				this.ruleCountToFilterCountToBlocks.headMap(rowCount, true).values();
		for (final TreeMap<Integer, HashSet<Block>> filterCountToBlocksFixedRuleCount : filterCountToBlocksRuleCountHead) {
			final Collection<HashSet<Block>> blocksFixedRuleCountFilterCountHead =
					filterCountToBlocksFixedRuleCount.headMap(filterCount, true).values();
			for (final HashSet<Block> blocksFixedRuleCountFixedFilterCount : blocksFixedRuleCountFilterCountHead) {
				for (final Block candidate : blocksFixedRuleCountFixedFilterCount) {
					if (candidate != block && candidate.containedIn(block)) {
						// can't remove right now since we are iterating over a collection that
						// would be changed
						toRemove.add(candidate);
					}
				}
			}
		}
		for (final Block remove : toRemove) {
			remove(remove);
		}
	}

	private void actuallyInsertBlockIntoAllCaches(final Block block) {
		this.blocks.add(block);
		final Integer ruleCount = getRowCount(block);
		final Integer filterCount = getFilterCount(block);
		this.ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).add(block);
		this.filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).add(block);
		this.ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
				.computeIfAbsent(filterCount, newHashSet()).add(block);
		this.filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
				.computeIfAbsent(ruleCount, newHashSet()).add(block);
		block.getRows().forEach(
				r -> this.ruleInstanceToBlocks.computeIfAbsent(r.getRuleOrProxy(), newHashSet()).add(block));
		removeContainedBlocks(block);
	}

	public boolean isContained(final Block block) {
		final Integer ruleCount = getRowCount(block);
		final Integer filterCount = getFilterCount(block);
		for (final TreeMap<Integer, HashSet<Block>> treeMap : this.filterCountToRuleCountToBlocks.tailMap(filterCount)
				.values()) {
			for (final HashSet<Block> blocks : treeMap.tailMap(ruleCount).values()) {
				if (blocks.stream().anyMatch(block::containedIn)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean addDuringConflictResolution(final Block block) {
		if (isContained(block)) {
			return false;
		}
		actuallyInsertBlockIntoAllCaches(block);
		return true;
	}

	public boolean remove(final Block block) {
		if (!this.blocks.remove(block))
			return false;
		final Integer ruleCount = getRowCount(block);
		final Integer filterCount = getFilterCount(block);
		this.ruleCountToBlocks.computeIfAbsent(ruleCount, newHashSet()).remove(block);
		this.filterCountToBlocks.computeIfAbsent(filterCount, newHashSet()).remove(block);
		this.ruleCountToFilterCountToBlocks.computeIfAbsent(ruleCount, newTreeMap())
				.computeIfAbsent(filterCount, newHashSet()).remove(block);
		this.filterCountToRuleCountToBlocks.computeIfAbsent(filterCount, newTreeMap())
				.computeIfAbsent(ruleCount, newHashSet()).remove(block);
		block.getRows().forEach(
				r -> this.ruleInstanceToBlocks.computeIfAbsent(r.getRuleOrProxy(), newHashSet()).remove(block));
		return true;
	}
}