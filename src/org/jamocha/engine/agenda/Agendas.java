/*
 * Copyright 2002-2008 The Jamocha Team
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;
import org.jamocha.engine.modules.Module;
import org.jamocha.parser.EvaluationException;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz in Jamocha, one module has
 *         exactly one agenda. this class is the map between them, which also
 *         handles some other things, like profiling settings
 */
public class Agendas {

	private class AgendasSettingsChangedListener implements
			SettingsChangedListener {
		public void settingsChanged(String propertyName) {
			JamochaSettings settings = JamochaSettings.getInstance();
			// watch activations
			if (propertyName
					.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_ACTIVATIONS))
				setWatchActivations(settings.getBoolean(propertyName));
			else if (propertyName
					.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_FIRE))
				setProfileFire(settings.getBoolean(propertyName));
			else if (propertyName
					.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ADD_ACTIVATION))
				setProfileAddActivation(settings.getBoolean(propertyName));
			else if (propertyName
					.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_REMOVE_ACTIVATION))
				setProfileRemoveActivation(settings.getBoolean(propertyName));
			else if (propertyName
					.equals(SettingsConstants.ENGINE_STRATEGY_SETTINGS_STRATEGY_MAIN))
				try {
					setStrategy(settings.getString(propertyName));
				} catch (EvaluationException e) {
					Logging.logger(this.getClass()).warn(e);
				}
		}
	}

	protected Map<Module, Agenda> agendas;
	protected Engine parentEngine;
	private final String[] interestedProperties = {
			SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_FIRE,
			SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ADD_ACTIVATION,
			SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_REMOVE_ACTIVATION,
			SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_ACTIVATIONS,
			SettingsConstants.ENGINE_STRATEGY_SETTINGS_STRATEGY_MAIN };
	/**
	 * Flag if Activations should be watched. If set to true an engine message
	 * will be send each time an activation is added or removed.
	 */
	protected boolean watchActivations = false;
	protected boolean profileFire = false;
	protected boolean profileAddActivation = false;
	protected boolean profileRemoveActivation = false;
	protected ConflictResolutionStrategy strategy = new BreadthStrategy();

	public Agendas(Engine engine) {
		parentEngine = engine;
		agendas = new HashMap<Module, Agenda>();
		JamochaSettings.getInstance().addListener(
				new AgendasSettingsChangedListener(), interestedProperties);
	}

	/**
	 * returns the agenda for the given module. it internally creates a new
	 * blank agenda for first-time querying a specific module's agenda.
	 * 
	 * @param module
	 * @return
	 */
	public Agenda getAgenda(Module module) {
		Agenda a = agendas.get(module);
		if (a == null) {
			a = new Agenda(parentEngine, strategy, watchActivations,
					profileFire, profileAddActivation, profileRemoveActivation);
			agendas.put(module, a);
		}
		return a;
	}

	/**
	 * it fires on the agenda, which is currenty the active one. when maxFire is
	 * given, it fires at most this number of activations. it returns the number
	 * of fires activations.
	 * 
	 * @param maxFire
	 * @return
	 * @throws org.jamocha.rete.exception.ExecuteException
	 */
	public int fireFocus(int maxFire) throws ExecuteException {
		Module focus = parentEngine.getCurrentFocus();
		Agenda agendaFocus = getAgenda(focus);
		return agendaFocus.fire(maxFire);
	}

	/**
	 * it fires on the agenda, which is currenty the active one. when maxFire is
	 * given, it fires at most this number of activations. it returns the number
	 * of fires activations.
	 * 
	 * @param maxFire
	 * @return
	 * @throws org.jamocha.rete.exception.ExecuteException
	 */
	public int fireFocus() throws ExecuteException {
		Module focus = parentEngine.getCurrentFocus();
		Agenda agendaFocus = getAgenda(focus);
		return agendaFocus.fire();
	}

	/**
	 * it globally sets, whether we want to get watch-messages for activation
	 * insertions and removals on the engines console
	 * 
	 * @param watch
	 */
	public void setWatchActivations(boolean watch) {
		watchActivations = watch;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags)
			agenda.setWatchActivations(watch);
	}

	/**
	 * this globally sets, whether we want to profile activation insertions
	 * 
	 * @param b
	 */
	public void setProfileAddActivation(boolean b) {
		profileAddActivation = b;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags)
			agenda.setProfileAddActivation(b);
	}

	/**
	 * this globally sets, whether we want to profile activation removals
	 * 
	 * @param b
	 */
	public void setProfileRemoveActivation(boolean b) {
		profileRemoveActivation = b;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags)
			agenda.setProfileRemoveActivation(b);
	}

	/**
	 * this globally sets, whether we want to profile fire
	 * 
	 * @param b
	 */
	public void setProfileFire(boolean b) {
		profileFire = b;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags)
			agenda.setProfileFire(b);
	}

	/**
	 * this globally sets the conflict resolution strategy by a string
	 * 
	 * @param strategyName
	 * @throws org.jamocha.parser.EvaluationException
	 */
	public void setStrategy(String strategyName) throws EvaluationException {
		try {
			// set new strategy:
			strategy = ConflictResolutionStrategy.getStrategy(strategyName);
			Collection<Agenda> ags = agendas.values();
			for (Agenda agenda : ags)
				agenda.setConflictResolutionStrategy(strategy);

		} catch (InstantiationException e) {
			throw new EvaluationException(
					"Error while setting the strategy to " + strategyName, e);
		} catch (IllegalAccessException e) {
			throw new EvaluationException(
					"Error while setting the strategy to " + strategyName, e);
		}

	}

	/**
	 * this removes all activations from all agendas.
	 */
	public void removeActivations() {
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags)
			agenda.removeActivation();
	}
}
