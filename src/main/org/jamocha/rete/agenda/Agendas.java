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

	/**
	 * Flag if Activations should be watched. If set to true an engine message
	 * will be send each time an activation is added or removed.
	 */
	protected boolean watch = false;

	public Agendas(Rete engine) {
		this.engine = engine;
		agendas = new HashMap<Module, Agenda>();
	}

	public Agenda getAgenda(Module module) {
		Agenda a = agendas.get(module);
		if (a == null) {
			a = new Agenda(engine);
			if (watch)
				a.setWatch(watch);
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
		this.watch = watch;
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
