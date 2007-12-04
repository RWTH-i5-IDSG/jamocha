/*
 * Copyright 2002-2006 Alexander Hahn, Sebastian Reinartz
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jamocha.parser.EvaluationException;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.modules.Module;
import org.jamocha.settings.JamochaSettings;
import org.jamocha.settings.SettingsChangedListener;
import org.jamocha.settings.SettingsConstants;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 */
public class Agendas implements SettingsChangedListener {

	protected Map<Module, Agenda> agendas;

	protected Rete engine;

	private String[] interestedProperties = { SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_FIRE, SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ADD_ACTIVATION,
			SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_REMOVE_ACTIVATION, SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_ACTIVATIONS, SettingsConstants.ENGINE_STRATEGY_SETTINGS_STRATEGY_MAIN };

	/**
	 * Flag if Activations should be watched. If set to true an engine message
	 * will be send each time an activation is added or removed.
	 */
	protected boolean watchActivations = false;

	protected boolean profileFire = false;

	protected boolean profileAddActivation = false;

	protected boolean profileRemoveActivation = false;

	protected ConflictResolutionStrategy strategy = new BreadthStrategy();

	public Agendas(Rete engine) {
		this.engine = engine;
		agendas = new HashMap<Module, Agenda>();
		JamochaSettings.getInstance().addListener(this, interestedProperties);
	}

	public Agenda getAgenda(Module module) {
		Agenda a = agendas.get(module);
		if (a == null) {
			a = new Agenda(engine, strategy, watchActivations, profileFire, profileAddActivation, profileRemoveActivation);
			if (watchActivations)
				a.setWatchActivations(watchActivations);
			agendas.put(module, a);
		}
		return a;
	}

	public int fireFocus(int maxFire) throws ExecuteException {
		Module focus = engine.getCurrentFocus();
		Agenda agendaFocus = getAgenda(focus);
		return agendaFocus.fire(maxFire);
	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public void setWatchActivations(boolean watch) {
		this.watchActivations = watch;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags) {
			agenda.setWatchActivations(watch);
		}
	}

	public void setProfileAddActivation(boolean b) {
		profileAddActivation = b;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags) {
			agenda.setProfileAddActivation(b);
		}
	}

	public void setProfileRemoveActivation(boolean b) {
		profileRemoveActivation = b;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags) {
			agenda.setProfileRemoveActivation(b);
		}
	}

	public void setProfileFire(boolean b) {
		this.profileFire = b;
		Collection<Agenda> ags = agendas.values();
		for (Agenda agenda : ags) {
			agenda.setProfileFire(b);
		}
	}

	public void setStrategy(String strategyName) throws EvaluationException {
		try {
			// set new strategy:
			strategy = ConflictResolutionStrategy.getStrategy(strategyName);
			Collection<Agenda> ags = agendas.values();
			for (Agenda agenda : ags) {
				agenda.setConflictResolutionStrategy(strategy);
			}

		} catch (InstantiationException e) {
			throw new EvaluationException("Error while setting the strategy to " + strategyName, e);
		} catch (IllegalAccessException e) {
			throw new EvaluationException("Error while setting the strategy to " + strategyName, e);
		}

	}

	public void settingsChanged(String propertyName) {
		JamochaSettings settings = JamochaSettings.getInstance();
		// watch activations
		if (propertyName.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_WATCH_ACTIVATIONS)) {
			setWatchActivations(settings.getBoolean(propertyName));
		}
		// profile:
		else if (propertyName.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_FIRE)) {
			setProfileFire(settings.getBoolean(propertyName));
		} else if (propertyName.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_ADD_ACTIVATION)) {
			setProfileAddActivation(settings.getBoolean(propertyName));
		} else if (propertyName.equals(SettingsConstants.ENGINE_GENERAL_SETTINGS_PROFILE_REMOVE_ACTIVATION)) {
			setProfileRemoveActivation(settings.getBoolean(propertyName));
		}
		// strategie:
		else if (propertyName.equals(SettingsConstants.ENGINE_STRATEGY_SETTINGS_STRATEGY_MAIN)) {
			try {
				setStrategy(settings.getString(propertyName));
			} catch (EvaluationException e) {
				e.printStackTrace();
			}
		}

	}

	public void removeActivations() {
		Collection<Agenda> temp = agendas.values();
		for(Agenda agenda : temp) {
			agenda.removeActivation();
		}
	}

}
