/*
 * Copyright 2007 Josef Alexander Hahn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.agenda;

import java.io.Serializable;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;

/**
 * @author Josef Alexander Hahn
 */

public class Agenda implements Serializable {

	private static final int INITIAL_CAPACITY = 10;

	private static final long serialVersionUID = 1L;

	protected Rete engine;

	protected ConflictResolutionStrategy strategy = null;

	protected Queue<Activation> activations;

	protected boolean watch = false;

	public Agenda(Rete engine, ConflictResolutionStrategy strategy) {
		this.engine = engine;
		activations = new PriorityQueue<Activation>(INITIAL_CAPACITY, strategy);
		this.strategy = strategy;
	}

	public Agenda(Rete engine) {
		this(engine, new BreadthStrategy());
	}

	public void setConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		if (strat != null) {
			strategy = strat;
			// we need to recompute the activation list
			Queue<Activation> oldList = activations;
			int oldListSize = oldList.size();
			activations = new PriorityQueue<Activation>(
					(oldListSize > 0) ? oldListSize : INITIAL_CAPACITY,
					strategy);
			if (oldListSize > 0)
				activations.addAll(oldList);
		}
	}

	public ConflictResolutionStrategy getConflictResolutionStrategy() {
		return strategy;
	}

	public void addActivation(Activation a) {
		activations.offer(a);
		if (watch) {
			engine.writeMessage("==> Activation: " + a.toString());
		}
	}

	public void removeActivation(Activation a) {
		activations.remove(a);
		if (watch) {
			engine.writeMessage("<== Activation: " + a.toString());
		}
	}

	public Collection<Activation> getActivations() {
		return activations;
	}

	public boolean activationExists(Activation a) {
		return activations.contains(a);
	}

	public int fire(int maxFire) throws ExecuteException {
		int fireCount = 0;
		if (maxFire < 1) {
			while (activations.size() > 0) {
				Activation act = (Activation) activations.poll();
				// System.out.println(act.getRule().getName());
				act.fire(engine);
				fireCount++;
			}
		} else {
			while (activations.size() > 0 && fireCount < maxFire) {
				Activation act = (Activation) activations.poll();
				act.fire(engine);
				fireCount++;
			}
		}
		return fireCount;
	}

	public boolean isWatch() {
		return watch;
	}

	public void setWatch(boolean watch) {
		this.watch = watch;
	}

}
