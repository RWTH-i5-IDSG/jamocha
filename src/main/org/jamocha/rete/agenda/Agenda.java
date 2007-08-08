/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
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
import java.util.ArrayList;
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

	public Agenda(Rete engine, ConflictResolutionStrategy strategy) {
		this.engine = engine;
		activations = new ArrayList<Activation>();
		this.strategy=strategy;
	}
	
	public Agenda(Rete engine) {
		this(engine, new FifoConflictResolutionStrategy());
	}
	
	public void setConflictResolutionStrategy(ConflictResolutionStrategy strat) {
		strategy = strat;
	}
	
	public ConflictResolutionStrategy getConflictResolutionStrategy(){
		return strategy;
	}
	
	public void addActivation(Activation a) {
		strategy.addActivation(activations,a);
	}
	
	public void removeActivation(Activation a){
		List<Activation> forDelete = new ArrayList<Activation>();
		for (Activation i : activations) {
			if (a.equals(i)) forDelete.add(i);
		}
		for (Activation del : forDelete)
			strategy.removeActivation(activations,del);
	}
	
	public boolean activationExists(Activation a){
		return activations.contains(a);
	}

	public int fire() throws ExecuteException{
		try {
			int result=0;
			for (Activation activation : activations) {
				activation.fire(engine);
				result++;
			}
			return result;
		} finally {
			activations.clear();
		}
	}
	

}
