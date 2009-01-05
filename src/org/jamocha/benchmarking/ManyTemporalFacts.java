package org.jamocha.benchmarking;

import org.jamocha.engine.Engine;

public class ManyTemporalFacts implements KnowledgebaseProvider {

	
	public Engine getProblemInstance(int size, String tempStrat) {
		Engine engine = new Engine(tempStrat);

		engine.eval("(deftemplate tmpl1 (slot a) (slot b) )");

		for (int i=0; i<size; i++) {
			engine.eval("(assert (tmpl1 (temporal-validity(duration 5) (millisecond */10) ) (a %d) (b %d) ) )", i, 18);		
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 19);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 12);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 118);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 218);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 318);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 518);		
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 519);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 512);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 5118);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 5218);
//			engine.eval("(assert (tmpl1 (temporal-validity(duration 1) (millisecond */2) ) (a %d) (b %d) ) )", i, 5318);
//
		}
		
		engine.eval("(defrule rule1 (declare (auto-focus true) )"+
				"(tmpl1 (a ?a) (b ?b) )"+
				"=>"+
				"(less ?a ?b)"+
				")"
		);
		
		return engine;
	}
	
}
