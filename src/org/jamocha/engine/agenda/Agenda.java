/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
 * 
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

package org.jamocha.engine.agenda;

import java.io.Serializable;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.util.ProfileStats;

/**
 * @author Josef Alexander Hahn an agenda is a sorted list (a priority queue) of
 *         activations. we can set the conflict resolution strategy (this is the
 *         sort order of the priorit queue), and add/remove/test-for-existance
 *         activations. furthermore, you can "fire" the agenda, which starts
 *         processing the rule's actions of the activations.
 */
public class Agenda implements Serializable {

	private static final int INITIAL_CAPACITY = 10;
	private static final long serialVersionUID = 1L;
	protected Engine parentEngine;
	protected ConflictResolutionStrategy strategy = null;
	protected Queue<Activation> activations;
	protected boolean watchActivations = false;
	protected boolean profileFire = false;
	protected boolean profileAddActivation = false;
	protected boolean profileRemoveActivation = false;

	public Agenda(Engine engine, ConflictResolutionStrategy strategy,
			boolean watch, boolean profileFire, boolean profileAddActivation,
			boolean profileRemoveActivation) {
		super();
		parentEngine = engine;
		this.strategy = strategy;
		watchActivations = watch;
		this.profileFire = profileFire;
		this.profileAddActivation = profileAddActivation;
		this.profileRemoveActivation = profileRemoveActivation;
		activations = new PriorityQueue<Activation>(INITIAL_CAPACITY, strategy);
	}

	/**
	 * Sets the conflict resolution strategy. It recomputes the activations
	 * order in the sense of the choosen new strategy.
	 * 
	 * @param strat
	 */
	public void setConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		if (strat != null) {
			strategy = strat;
			// we need to recompute the activation list
			Queue<Activation> oldList = activations;
			int oldListSize = oldList.size();
			int newInitialCapacity = oldListSize > 0 ? oldListSize
					: INITIAL_CAPACITY;
			activations = new PriorityQueue<Activation>(newInitialCapacity,
					strategy);
			activations.addAll(oldList);
		} else
			throw new NullPointerException();
	}

	/**
	 * it gets the conflict resolution strategy, this agenda uses at the moment
	 * 
	 * @return
	 */
	public ConflictResolutionStrategy getConflictResolutionStrategy() {
		return strategy;
	}

	/**
	 * add an activation to this agenda
	 * 
	 * @param a
	 */
	public void addActivation(Activation a) {
		// Watch?
		if (watchActivations)
			parentEngine.writeMessage("==> Activation: " + a);

		if (profileAddActivation)
			ProfileStats.startAddActivation();
		activations.offer(a);
		if (profileAddActivation)
			ProfileStats.endAddActivation();
	}

	/**
	 * remove an activation from this agenda
	 * 
	 * @param a
	 */
	public void removeActivation(Activation a) {
		// Watch?
		if (watchActivations)
			parentEngine.writeMessage("<== Activation: " + a);

		if (profileAddActivation)
			ProfileStats.startAddActivation();
		activations.remove(a);
		if (profileAddActivation)
			ProfileStats.endAddActivation();
	}

	/**
	 * returns a collection of activations, which are currently lying in our
	 * agenda
	 * 
	 * @return
	 */
	public Collection<Activation> getActivations() {
		return activations;
	}

	/**
	 * tests, whether an activation exists in the agenda
	 * 
	 * @param a
	 * @return
	 */
	public boolean activationExists(Activation a) {
		return activations.contains(a);
	}

	protected void fireNextActivation() throws ExecuteException {
		activations.poll().fire(parentEngine);
	}

	/**
	 * this fires the agenda's activations. in the order given by the conflict
	 * resolution strategy, the rule's action part will be executed. if the
	 * argument maxFire is given, it fires only that number of actions and stops
	 * after that. after that, it returns the number of executed actions.
	 * 
	 * @param maxFire
	 * @return
	 * @throws org.jamocha.rete.exception.ExecuteException
	 */
	public int fire(int maxFire) throws ExecuteException {
		int fireCount = 0;
		if (profileFire)
			ProfileStats.startFire();
		while (activations.size() > 0 && fireCount < maxFire) {
			fireNextActivation();
			fireCount++;
		}
		if (profileFire)
			ProfileStats.endFire();
		return fireCount;
	}

	/**
	 * this fires the agenda's activations. in the order given by the conflict
	 * resolution strategy, the rule's action part will be executed. if the
	 * argument maxFire is given, it fires only that number of actions and stops
	 * after that. after that, it returns the number of executed actions.
	 * 
	 * @param maxFire
	 * @return
	 * @throws org.jamocha.rete.exception.ExecuteException
	 */
	public int fire() throws ExecuteException {
		int fireCount = 0;
		if (profileFire)
			ProfileStats.startFire();
		while (!activations.isEmpty()) {
			fireNextActivation();
			fireCount++;
		}
		if (profileFire)
			ProfileStats.endFire();
		return fireCount;
	}

	/**
	 * sets whether this agenda should output activation insertions and removals
	 * to the console
	 * 
	 * @param watch
	 */
	public void setWatchActivations(boolean watch) {
		watchActivations = watch;
	}

	/**
	 * sets whether we want to profile activations insertions
	 * 
	 * @param profileAddActivation
	 */
	public void setProfileAddActivation(boolean profileAddActivation) {
		this.profileAddActivation = profileAddActivation;
	}

	/**
	 * sets whether we want to profile "fire"s
	 * 
	 * @param profileFire
	 */
	public void setProfileFire(boolean profileFire) {
		this.profileFire = profileFire;
	}

	/**
	 * sets whether we want to profile activation removals
	 * 
	 * @param profileRemoveActivation
	 */
	public void setProfileRemoveActivation(boolean profileRemoveActivation) {
		this.profileRemoveActivation = profileRemoveActivation;
	}

	/**
	 * remove all activations in the agenda
	 */
	void removeActivation() {
		activations.clear();
	}
}
