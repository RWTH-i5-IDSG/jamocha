package org.jamocha.benchmarking;

import org.jamocha.engine.Engine;

public class TestMeasurement implements KnowledgebaseProvider {

	
	public Engine getProblemInstance(int size, String tempStrat) {
		Engine engine = new Engine(tempStrat);

		for (int i=0; i<size; i++) {
			engine.eval("(defrule rulea-"+i+" (declare (auto-focus true)(temporal-validity (millisecond */2) (duration 1) )) (_initialFact) => (printout t \"brezel\" crlf) )");
			engine.eval("(defrule ruleb-"+i+" (declare (auto-focus true)(temporal-validity (millisecond */2) (duration 1) )) (_initialFact) => (printout t \"brezel\" crlf) )");
		}
		
		return engine;
	}
	
}
