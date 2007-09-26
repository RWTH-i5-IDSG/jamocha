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

import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.modules.Module;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 */
public class Agendas {

	protected Map<Module, Agenda> agendas;

	protected Rete engine;

	/**
	 * Flag if Activations should be watched. If set to true an engine message
	 * will be send each time an activation is added or removed.
	 */
	protected boolean watchActivations = false;

	public Agendas(Rete engine) {
		this.engine = engine;
		agendas = new HashMap<Module, Agenda>();
	}

	public Agenda getAgenda(Module module) {
		Agenda a = agendas.get(module);
		if (a == null) {
			a = new Agenda(engine);
			if (watchActivations)
				a.setWatch(watchActivations);
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
			agenda.setWatch(watch);
		}
	}

	public void setProfileAdd(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setProfileRemove(boolean b) {
		// TODO Auto-generated method stub

	}

}
