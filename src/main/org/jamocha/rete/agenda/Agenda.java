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
import java.util.List;

import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;

/**
 * @author Josef Alexander Hahn
 */

public class Agenda implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Rete engine;

	protected ConflictResolutionStrategy strategy = null;

	protected List<Activation> activations;

	protected boolean watch = false;

	public Agenda(Rete engine, ConflictResolutionStrategy strategy) {
		this.engine = engine;
		activations = strategy.getEmptyActivationList(0);
		this.strategy = strategy;
	}

	public Agenda(Rete engine) {
		this(engine, new FirstComeFirstServeStrategy());
	}

	public void setConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		if (strat != null) {
			strategy = strat;
			// we need to recompute the activation list
			List<Activation> oldList = activations;
			activations = strat.getEmptyActivationList(oldList.size());
			for (Activation a : oldList)
				strategy.addActivation(activations, a);
		}
	}

	public ConflictResolutionStrategy getConflictResolutionStrategy() {
		return strategy;
	}

	public void addActivation(Activation a) {
		strategy.addActivation(activations, a);
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

	public List<Activation> getActivations() {
		return activations;
	}

	public boolean activationExists(Activation a) {
		return activations.contains(a);
	}

	public int fire(int maxFire) throws ExecuteException {
		int fireCount = 0;
		if (maxFire == -1) {
			while (activations.size() > 0) {
				Activation act = activations.remove(0);
				act.fire(engine);
				fireCount++;
			}
		} else {
			while (activations.size() > 0 && fireCount < maxFire) {
				Activation act = activations.remove(0);
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
