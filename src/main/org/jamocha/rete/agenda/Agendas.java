package org.jamocha.rete.agenda;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.Module;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.ExecuteException;

/**
 * @author Josef Alexander Hahn
 */
public class Agendas {
	
	protected Map<Module,Agenda> agendas;

	protected Rete engine;
	
	public Agendas(Rete engine) {
		this.engine = engine;
		agendas = new HashMap<Module,Agenda>();
	}
	
	public Agenda getAgenda(Module module) {
		Agenda a = agendas.get(module);
		if (a == null) {
			a = new Agenda(engine);
			agendas.put(module,a);
		}
		return a;
	}
	
	public void fireFocus() throws ExecuteException {
		Module focus = engine.getCurrentFocus();
		Agenda agendaFocus = getAgenda(focus);
		agendaFocus.fire();
	}
	
	
}
