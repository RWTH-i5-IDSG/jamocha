/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package woolfel.rulebenchmark;

import java.io.FileWriter;
import java.io.IOException;

import org.jamocha.rete.Constants;


public class GeneratePropogateRules {

	public GeneratePropogateRules() {
		super();
	}

	public void writeDeftemplate1(StringBuffer buf) {
		buf.append("(deftemplate object1" + Constants.LINEBREAK);
		buf.append("  (slot stringfield (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot intfield (type INTEGER) )" + Constants.LINEBREAK);
		buf.append("  (slot longfield (type LONG) )" + Constants.LINEBREAK);
		buf.append("  (slot doublefield (type DOUBLE) )" + Constants.LINEBREAK);
		buf.append("  (slot floatfield (type FLOAT) )" + Constants.LINEBREAK);
		buf.append("  (slot shortfield (type SHORT) )" + Constants.LINEBREAK);
		buf.append(")" + Constants.LINEBREAK);
	}
	
	public void writeDeftemplate2(StringBuffer buf) {
		buf.append("(deftemplate object2" + Constants.LINEBREAK);
		buf.append("  (slot stringfield (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot intfield (type INTEGER) )" + Constants.LINEBREAK);
		buf.append("  (slot longfield (type LONG) )" + Constants.LINEBREAK);
		buf.append("  (slot doublefield (type DOUBLE) )" + Constants.LINEBREAK);
		buf.append("  (slot floatfield (type FLOAT) )" + Constants.LINEBREAK);
		buf.append("  (slot shortfield (type SHORT) )" + Constants.LINEBREAK);
		buf.append(")" + Constants.LINEBREAK);
	}

	public void writeRightActivateFacts(int count, StringBuffer buf) {
		buf.append("(assert (object1 " + "(stringfield \"1" +
				"\")(intfield 1)(longfield 1)" +
				"(doublefield 100.00)(floatfield 100)" +
				"(shortfield 1)" +
				") )" + Constants.LINEBREAK);
		for (int idx=1; idx <= count; idx++) {
			buf.append("(assert (object2 " + "(stringfield \"" +
					idx + "\")(intfield " + idx + ")(longfield " +
					idx + ")(doublefield 100.00)(floatfield 100)" +
					"(shortfield " + idx + ")" +
					") )" + Constants.LINEBREAK);
		}
	}
	
	public void writeLeftActivateFacts(int count, StringBuffer buf) {
		buf.append("(assert (object2 " + "(stringfield \"1\")" +
				"(intfield 1)(longfield 1)" +
				"(doublefield 100.00)(floatfield 100)" +
				"(shortfield 1)" +
				") )" + Constants.LINEBREAK);
		for (int idx=1; idx <= count; idx++) {
			buf.append("(assert (object1 " + "(stringfield \"1" +
					"\")(intfield 1)(longfield 1)" +
					"(doublefield 100.00)(floatfield 100)" +
					"(shortfield " + idx + ")" +
					") )" + Constants.LINEBREAK);
		}
	}

	public void writeZeroJoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			buf.append("(defrule zerojrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (stringfield \"" + idx + "\")" +
					Constants.LINEBREAK);
			buf.append("    (intfield " + idx + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + idx + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (stringfield ?sfd)" + Constants.LINEBREAK);
			buf.append("    (doublefield ?dbfd)" + Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"zerojrule" + idx + " fired " +
					"\" ?sfd \" \" ?dbfd crlf)" + Constants.LINEBREAK);
			buf.append(")" + Constants.LINEBREAK);
		}
	}
	
	public void writeProfile(StringBuffer buf) {
		buf.append("(profile all)" + Constants.LINEBREAK);
	}
	
	public void writeFire(StringBuffer buf) {
		buf.append("(fire)" + Constants.LINEBREAK);
	}
	
	public void writePrintProfile(StringBuffer buf) {
		buf.append("(print-profile)" + Constants.LINEBREAK);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String outfile = null;
		int rules = 5;
		int data = 50000;
		boolean right = true;
		if (args != null && args.length > 0) {
			if (args[0] != null) {
				outfile = args[0];
			}
			if (args[1] != null) {
				rules = Integer.parseInt(args[1]);
			}
			if (args[2] != null) {
				data = Integer.parseInt(args[2]);
			}
			if (args[3] != null && args[3].equals("left")) {
				right = false;
			}
			
			GeneratePropogateRules gen = new GeneratePropogateRules();
			StringBuffer buf = new StringBuffer();
			gen.writeDeftemplate1(buf);
			gen.writeDeftemplate2(buf);
			gen.writeZeroJoinRule(rules,buf);
			gen.writeProfile(buf);
			if (right) {
				gen.writeRightActivateFacts(data,buf);
			} else {
				gen.writeLeftActivateFacts(data,buf);
			}
			gen.writeFire(buf);
			gen.writePrintProfile(buf);
			try {
				FileWriter writer = new FileWriter(outfile);
				writer.write(buf.toString());
				writer.close();
				System.out.println("rules generated");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Required parameters");
			System.out.println("     filename");
			System.out.println("     number of rules");
			System.out.println("     number of facts");
			System.out.println("     right|left");
		}
	}

}
