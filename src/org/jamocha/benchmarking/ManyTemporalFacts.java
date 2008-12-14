package org.jamocha.benchmarking;

import org.jamocha.engine.Engine;

public class ManyTemporalFacts implements KnowledgebaseProvider {

	
	public Engine getProblemInstance(int size, String tempStrat) {
		Engine engine = new Engine(tempStrat);

		for (int i=0; i<size; i++) {
			engine.eval("(deftemplate tmpl1 (slot a) (slot b) )");
			engine.eval("(deftemplate tmpl2 (slot a) (slot b) )");
			engine.eval("(defrule rule1 (declare (auto-focus true) )"+
					"(tmp1 (a ?a) (b ?b) )"+
					"=>"+
					"(assert (tmp2 (a ?a) (b ?b) ) )"
					);
			engine.eval("(defrule rule2 (declare (auto-focus true) )"+
					"?t <- (tmp2 (a ?a) (b ?b) )"+
					"(not (tmp1 (a ?a) (b ?b) )  )"+
					"=>"+
					"(retract ?t )"
					);
		}
		
		return engine;
	}
	
}
