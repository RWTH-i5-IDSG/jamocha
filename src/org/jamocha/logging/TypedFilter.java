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

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class TypedFilter extends AbstractFilter {
	private static final long serialVersionUID = -5595124971046769329L;
	// boolean in pair: true if watched => collection is exclusion list
	// false if not watched => collection is inclusion list
	final EnumMap<MarkerType, Pair<Boolean, Collection<Marker>>> markerToInExClusionList = new EnumMap<>(
			MarkerType.class);
	@Getter
	@Setter
	boolean keepItemsWithoutMarkers;

	public TypedFilter(final boolean keepItemsWithoutMarkers) {
		this.keepItemsWithoutMarkers = keepItemsWithoutMarkers;
		unwatchAll();
	}

	public void watchAll() {
		EnumSet.allOf(MarkerType.class).forEach(
				mt -> this.markerToInExClusionList.put(mt, Pair.of(Boolean.TRUE, new ArrayList<>())));
	}

	public void watch(final MarkerType markerType, final Marker... markers) {
		if (0 == markers.length) {
			this.markerToInExClusionList.put(markerType, Pair.of(Boolean.TRUE, new ArrayList<>()));
			return;
		}
		final Pair<Boolean, Collection<Marker>> pair = this.markerToInExClusionList.get(markerType);
		// if not already generally watched then add else ignore
		if (!pair.getLeft()) {
			pair.getRight().addAll(Arrays.asList(markers));
		}
	}

	public void unwatch(final MarkerType markerType, final Marker... markers) {
		if (0 == markers.length) {
			this.markerToInExClusionList.put(markerType, Pair.of(Boolean.FALSE, new ArrayList<>()));
			return;
		}
		final Pair<Boolean, Collection<Marker>> pair = this.markerToInExClusionList.get(markerType);
		// if generally watched then add else ignore
		if (pair.getLeft()) {
			pair.getRight().addAll(Arrays.asList(markers));
		}
	}

	public void unwatchAll() {
		EnumSet.allOf(MarkerType.class).forEach(
				mt -> this.markerToInExClusionList.put(mt, Pair.of(Boolean.FALSE, new ArrayList<>())));
	}

	private boolean check(final Marker marker) {
		if (null == marker)
			return this.keepItemsWithoutMarkers;
		for (final Entry<MarkerType, Pair<Boolean, Collection<Marker>>> e : this.markerToInExClusionList.entrySet()) {
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
	public Result filter(final LogEvent event) {
		return decide(event.getMarker());
	}

	@Override
	public Result filter(final Logger logger, final Level level, final Marker marker, final Message msg,
			final Throwable t) {
		return decide(marker);
	}

	@Override
	public Result filter(final Logger logger, final Level level, final Marker marker, final Object msg,
			final Throwable t) {
		return decide(marker);
	}

	@Override
	public Result filter(final Logger logger, final Level level, final Marker marker, final String msg,
			final Object... params) {
		return decide(marker);
	}

	public boolean isRelevant(final MarkerType markerType) {
		final Pair<Boolean, Collection<Marker>> pair = this.markerToInExClusionList.get(markerType);
		if (pair.getLeft() || !pair.getRight().isEmpty())
			return true;
		return false;
	}
}
