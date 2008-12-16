package org.jamocha.benchmarking;

import org.jamocha.engine.Engine;

public class ManyTemporalRules implements KnowledgebaseProvider {

	
	public Engine getProblemInstance(int size, String tempStrat) {
		Engine engine = new Engine(tempStrat);
		engine.eval("(deftemplate tmpl1 (slot a) (slot b) )");
		for (int i=0; i<size; i++) {
		engine.eval("(defrule rule"+i+" (declare (auto-focus true) (temporal-validity(duration 5) (millisecond */10) ) )"+
				"(tmpl1 (a ?a) (b ?b) )"+
				"=>"+
				"(less ?a ?b)"+
				")"
				);
		}
		engine.eval("(assert (tmpl1  (a 1) (b 2) ) )");		
		return engine;
	}
	
}
