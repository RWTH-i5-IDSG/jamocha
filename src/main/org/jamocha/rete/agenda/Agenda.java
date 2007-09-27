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
import org.jamocha.rete.util.ProfileStats;

/**
 * @author Josef Alexander Hahn
 */

public class Agenda implements Serializable {

	private static final int INITIAL_CAPACITY = 10;

	private static final long serialVersionUID = 1L;

	protected Rete engine;

	protected ConflictResolutionStrategy strategy = null;

	protected Queue<Activation> activations;

	protected boolean watchActivations = false;

	protected boolean profileFire = false;

	protected boolean profileAddActivation = false;

	protected boolean profileRemoveActivation = false;

	public Agenda(Rete engine, ConflictResolutionStrategy strategy, boolean watch, boolean profileFire, boolean profileAddActivation, boolean profileRemoveActivation) {
		super();
		this.engine = engine;
		this.strategy = strategy;
		this.watchActivations = watch;
		this.profileFire = profileFire;
		this.profileAddActivation = profileAddActivation;
		this.profileRemoveActivation = profileRemoveActivation;
		this.activations = new PriorityQueue<Activation>(INITIAL_CAPACITY, strategy);
	}

	public void setConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		if (strat != null) {
			strategy = strat;
			// we need to recompute the activation list
			Queue<Activation> oldList = activations;
			int oldListSize = oldList.size();
			activations = new PriorityQueue<Activation>((oldListSize > 0) ? oldListSize : INITIAL_CAPACITY, strategy);
			if (oldListSize > 0)
				activations.addAll(oldList);
		}
	}

	public ConflictResolutionStrategy getConflictResolutionStrategy() {
		return strategy;
	}

	public void addActivation(Activation a) {
		//Watch?
		if (watchActivations) {
			engine.writeMessage("==> Activation: " + a.toString());
		}
		//profile?
		if (profileAddActivation) {
			ProfileStats.startAddActivation();
			activations.offer(a);
			ProfileStats.endAddActivation();
		} else {
			activations.offer(a);
		}
	}

	public void removeActivation(Activation a) {
		//wath?
		if (watchActivations) {
			engine.writeMessage("<== Activation: " + a.toString());
		}
		//profile?
		if (profileAddActivation) {
			ProfileStats.startRemoveActivation();
			activations.remove(a);
			ProfileStats.endRemoveActivation();
		} else {
			activations.remove(a);
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
		if (profileFire) {
			ProfileStats.startFire();
		}
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
		if (profileFire) {
			ProfileStats.endFire();
		}
		return fireCount;
	}

	public void setWatchActivations(boolean watch) {
		this.watchActivations = watch;
	}

	public void setProfileAddActivation(boolean profileAddActivation) {
		this.profileAddActivation = profileAddActivation;
	}

	public void setProfileFire(boolean profileFire) {
		this.profileFire = profileFire;
	}

	public void setProfileRemoveActivation(boolean profileRemoveActivation) {
		this.profileRemoveActivation = profileRemoveActivation;
	}

}
