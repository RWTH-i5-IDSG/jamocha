package org.jamocha.rete.agenda;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.modules.Module;

/**
 * @author Josef Alexander Hahn
 */
public class Agendas {

	protected Map<Module, Agenda> agendas;

	protected Rete engine;

	public Agendas(Rete engine) {
		this.engine = engine;
		agendas = new HashMap<Module, Agenda>();
	}

	public Agenda getAgenda(Module module) {
		Agenda a = agendas.get(module);
		if (a == null) {
			a = new Agenda(engine);
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

	public void setWatch(boolean watch) {
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
