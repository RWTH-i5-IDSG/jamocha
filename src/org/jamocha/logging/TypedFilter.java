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
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class TypedFilter extends Filter<ILoggingEvent> {
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
				Arrays.stream(markerNames).map(MarkerFactory::getMarker).toArray(Marker[]::new));
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
				Arrays.stream(markerNames).map(MarkerFactory::getMarker).toArray(Marker[]::new));
	}

	public void unwatch(final MarkerType markerType, final Marker... markers) {
		final Pair<Boolean, Collection<Marker>> pair = this.markerToInExClusionList.get(markerType);
		// if generally watched then add else ignore
		if (pair.getLeft()) {
			pair.getRight().addAll(Arrays.asList(markers));
		}
	}

	private boolean check(final ILoggingEvent event) {
		if (null == event)
			return false;
		final Marker marker = event.getMarker();
		if (null == marker)
			return this.keepItemsWithoutMarkers;
		for (final Entry<MarkerType, Pair<Boolean, Collection<Marker>>> e : this.markerToInExClusionList
				.entrySet()) {
			if (!marker.contains(e.getKey().commonMarker)) {
				continue;
			}
			// type matched
			final Pair<Boolean, Collection<Marker>> pair = e.getValue();
			if (pair.getLeft()) {
				// type watched, check for exclusion
				for (final Marker ex : pair.getRight()) {
					if (marker.contains(ex)) {
						return false;
					}
				}
				// not excluded
				return true;
			}
			// type not watched, marker included?
			for (final Marker in : pair.getRight()) {
				if (marker.contains(in)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public FilterReply decide(final ILoggingEvent event) {
		return check(event) ? FilterReply.NEUTRAL : FilterReply.DENY;
	}
}
