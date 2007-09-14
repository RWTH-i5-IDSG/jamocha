/*
 * Copyright 2007 Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package testframework.testcases;

import testframework.AbstractJamochaTest;

/**
 * @author Sebastian Reinartz
 */
public class JTCBierWurstSenf extends AbstractJamochaTest {

	public JTCBierWurstSenf(String arg0) {
		super(arg0);
	}

	private String getTemplates() {
		StringBuilder templates = new StringBuilder();
		templates.append("(deftemplate wurst (slot name (type STRING)) (multislot zutaten) (slot gewicht (type INTEGER)) (slot laenge (type INTEGER) (default 25))"
				+ "(slot hersteller (type STRING)))");

		templates.append(" (deftemplate bier  (slot name ) (slot gewicht ) (slot hersteller))");

		templates.append(" (deftemplate senf (slot gewicht) (slot name))");
		return templates.toString();
	}

	private String getRuleTestNode() {
		StringBuilder rule = new StringBuilder();

		rule.append("(defrule test-node-rule (wurst (gewicht ?x) (name ?y)) (bier (gewicht ?x) (name ?z))(senf (gewicht ?x) (name ?w))(test (> ?x 100))"
				+ "=>(printout t \"Lebensmittel die zusammenpassen. wurst:\" ?y \"zutaten: \" $?v \" Bier: \" ?z  \"Senf: \" ?w \" Gewicht: \" ?x))");

		return rule.toString();
	}

	private String getFacts() {
		StringBuilder facts = new StringBuilder();

		facts.append("(assert (wurst (name \"Fischwurst schwer\") (gewicht 200) (laenge 100) (hersteller \"Nordmann\")))");

		facts.append("(assert (wurst (name \"Fischwurst2\") (gewicht 100) (laenge 100) (hersteller \"Nordmann\")))");

		facts.append("(assert (wurst (name \"miniwurst\") (gewicht 10) (laenge 15) (hersteller \"Nordmann\")))");

		facts.append("(assert (bier  (name \"Bitburger\") (gewicht 100)))");

		facts.append("(assert (bier (name \"Eifel Champus\") (gewicht 200)))");

		facts.append("(assert (senf (name scharf) (gewicht 100)))");

		facts.append("(assert (senf (name auchscharf) (gewicht 200)))");

		facts.append("(assert (senf (name nix) (gewicht 1)))");

		return facts.toString();
	}

	@Override
	public void test() {
		executeTestEquals(getTemplates(), "true");
		executeTestEquals(getFacts(), "f-8");
		executeTestEquals(getRuleTestNode(), "true");
		executeTestEquals("(fire)", "1");
	}

}
