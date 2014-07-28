/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class TypedFilter extends AbstractFilter {
	// boolean in pair: true if watched => collection is exclusion list
	// false if not watched => collection is inclusion list
	final EnumMap<MarkerType, Pair<Boolean, Collection<Marker>>> markerToInExClusionList =
			new EnumMap<>(MarkerType.class);
	final boolean keepItemsWithoutMarkers;

	public TypedFilter(final boolean keepItemsWithoutMarkers) {
		this.keepItemsWithoutMarkers = keepItemsWithoutMarkers;
		EnumSet.allOf(MarkerType.class).forEach(
				mt -> this.markerToInExClusionList.put(mt,
						Pair.of(Boolean.FALSE, new ArrayList<>())));
	}

	public void watch(final MarkerType markerType) {
		this.markerToInExClusionList.put(markerType, Pair.of(Boolean.TRUE, new ArrayList<>()));
	}

	public void watch(final MarkerType markerType, final String... markerNames) {
		watch(markerType,
				Arrays.stream(markerNames).map(MarkerManager::getMarker).toArray(Marker[]::new));
	}

	public void watch(final MarkerType markerType, final Marker... markers) {
		final Pair<Boolean, Collection<Marker>> pair = this.markerToInExClusionList.get(markerType);
		// if not already generally watched then add else ignore
		if (!pair.getLeft()) {
			pair.getRight().addAll(Arrays.asList(markers));
		}
	}

	public void unwatch(final MarkerType markerType) {
		this.markerToInExClusionList.put(markerType, Pair.of(Boolean.FALSE, new ArrayList<>()));
	}

	public void unwatch(final MarkerType markerType, final String... markerNames) {
		unwatch(markerType,
				Arrays.stream(markerNames).map(MarkerManager::getMarker).toArray(Marker[]::new));
	}

	public void unwatch(final MarkerType markerType, final Marker... markers) {
		final Pair<Boolean, Collection<Marker>> pair = this.markerToInExClusionList.get(markerType);
		// if generally watched then add else ignore
		if (pair.getLeft()) {
			pair.getRight().addAll(Arrays.asList(markers));
		}
	}

	private boolean check(final Marker marker) {
		if (null == marker)
			return this.keepItemsWithoutMarkers;
		for (final Entry<MarkerType, Pair<Boolean, Collection<Marker>>> e : this.markerToInExClusionList
				.entrySet()) {
			if (!marker.isInstanceOf(e.getKey().commonMarker)) {
				continue;
			}
			// type matched
			final Pair<Boolean, Collection<Marker>> pair = e.getValue();
			if (pair.getLeft()) {
				// type watched, check for exclusion
				for (final Marker ex : pair.getRight()) {
					if (marker.isInstanceOf(ex)) {
						return false;
					}
				}
				// not excluded
				return true;
			}
			// type not watched, marker included?
			for (final Marker in : pair.getRight()) {
				if (marker.isInstanceOf(in)) {
					return true;
				}
			}
		}
		return false;
	}
	public Result decide(final Marker marker) {
		return check(marker) ? Result.NEUTRAL : Result.DENY;
	}

	@Override
	public Result filter(LogEvent event) {
		return decide(event.getMarker());
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker,
			Message msg, Throwable t) {
		return decide(marker);
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker,
			Object msg, Throwable t) {
		return decide(marker);
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker,
			String msg, Object... params) {
		return decide(marker);
	}
}
