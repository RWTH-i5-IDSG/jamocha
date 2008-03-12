package org.jamocha.jsr94;

import org.jamocha.rete.Rete;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaTransactionBasedSession {

	private Rete engine;
	
	private Rete backup;
	
	public JamochaTransactionBasedSession() {
		engine = new Rete();
		commit();
	}
	
	public void commit() {
		// TODO implement me or a clone method in the engine
		//backup = engine.clone();
	}
	
	public void rollback() {
		// TODO implement me or a clone method in the engine
		//engine = backup.clone();
	}
	
	public Rete getEngine() {
		return engine;
	}

	public void release() {
		engine.close();	engine = null;
		if (backup!=null) {
			backup.close();
			backup = null;
		}
	}

	
}
