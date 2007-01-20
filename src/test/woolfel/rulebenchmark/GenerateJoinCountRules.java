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


public class GenerateJoinCountRules {

	public GenerateJoinCountRules() {
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
		buf.append("  (slot stringfield2 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield3 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield4 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield5 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield6 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield7 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield8 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield9 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield10 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield11 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield12 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield13 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield14 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield15 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield16 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield17 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield18 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield19 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield20 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield21 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield22 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield23 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield24 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield25 (type STRING) )" + Constants.LINEBREAK);
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
		buf.append("  (slot stringfield2 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield3 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield4 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield5 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield6 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield7 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield8 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield9 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield10 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield11 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield12 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield13 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield14 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield15 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield16 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield17 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield18 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield19 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield20 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield21 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield22 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield23 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield24 (type STRING) )" + Constants.LINEBREAK);
		buf.append("  (slot stringfield25 (type STRING) )" + Constants.LINEBREAK);
		buf.append(")" + Constants.LINEBREAK);
	}

	public void writeRightActivateFacts(int count, StringBuffer buf) {
		buf.append("(assert (object1 " + "(stringfield \"1" +
				"\")(intfield 1)(longfield 1)" +
				"(doublefield 100.00)(floatfield 100)" +
				"(shortfield 1)" +
				"(stringfield2 \"1\")(stringfield3 \"1\")" +
				"(stringfield4 \"1\")(stringfield5 \"1\")" +
				"(stringfield6 \"1\")(stringfield6 \"1\")" +
				"(stringfield7 \"1\")(stringfield8 \"1\")" +
				"(stringfield9 \"1\")(stringfield10 \"1\")" +
				"(stringfield11 \"1\")(stringfield12 \"1\")" +
				") )" + Constants.LINEBREAK);
		for (int idx=1; idx <= count; idx++) {
			buf.append("(assert (object2 " + "(stringfield \"" +
					idx + "\")(intfield 1)(longfield 1" +
					")(doublefield 100.00)(floatfield 100)" +
					"(shortfield 1)" +
					"(stringfield2 \"1\")(stringfield3 \"1\")" +
					"(stringfield4 \"1\")(stringfield5 \"1\")" +
					"(stringfield6 \"1\")(stringfield6 \"1\")" +
					"(stringfield7 \"1\")(stringfield8 \"1\")" +
					"(stringfield9 \"1\")(stringfield10 \"1\")" +
					"(stringfield11 \"1\")(stringfield12 \"1\")" +
					") )" + Constants.LINEBREAK);
		}
	}
	
	public void writeLeftActivateFacts(int count, StringBuffer buf) {
		buf.append("(assert (object2 " + "(stringfield \"1\")" +
				"(intfield 1)(longfield 1)" +
				"(doublefield 100.00)(floatfield 100)" +
				"(shortfield 1)" +
				"(stringfield2 \"1\")(stringfield3 \"1\")" +
				"(stringfield4 \"1\")(stringfield5 \"1\")" +
				"(stringfield6 \"1\")(stringfield6 \"1\")" +
				"(stringfield7 \"1\")(stringfield8 \"1\")" +
				"(stringfield9 \"1\")(stringfield10 \"1\")" +
				"(stringfield11 \"1\")(stringfield12 \"1\")" +
				") )" + Constants.LINEBREAK);
		for (int idx=1; idx <= count; idx++) {
			buf.append("(assert (object1 " + "(stringfield \"1" +
					"\")(intfield 1)(longfield 1)" +
					"(doublefield 100.00)(floatfield 100)" +
					"(shortfield " + idx + ")" +
					"(stringfield2 \"1\")(stringfield3 \"1\")" +
					"(stringfield4 \"1\")(stringfield5 \"1\")" +
					"(stringfield6 \"1\")(stringfield6 \"1\")" +
					"(stringfield7 \"1\")(stringfield8 \"1\")" +
					"(stringfield9 \"1\")(stringfield10 \"1\")" +
					"(stringfield11 \"1\")(stringfield12 \"1\")" +
					") )" + Constants.LINEBREAK);
		}
	}

	public void writeLeftActivateFacts2(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			buf.append("(assert (object2 " + "(stringfield \"" + idx +
					"\")(intfield 1)(longfield 1)" +
					"(doublefield 100.00)(floatfield 100)" +
					"(shortfield \"1\")" +
					"(stringfield2 \"1\")(stringfield3 \"1\")" +
					"(stringfield4 \"1\")(stringfield5 \"1\")" +
					"(stringfield6 \"1\")(stringfield6 \"1\")" +
					"(stringfield7 \"1\")(stringfield8 \"1\")" +
					"(stringfield9 \"1\")(stringfield10 \"1\")" +
					"(stringfield11 \"1\")(stringfield12 \"1\")" +
					"(stringfield13 \"1\")(stringfield14 \"1\")" +
					"(stringfield15 \"1\")(stringfield16 \"1\")" +
					") )" + Constants.LINEBREAK);
		}
		writeProfile(buf);
		buf.append("(assert (object1 " + "(stringfield \"1\")" +
				"(intfield 1)(longfield 1)" +
				"(doublefield 100.00)(floatfield 100)" +
				"(shortfield 1)" +
				"(stringfield2 \"1\")(stringfield3 \"1\")" +
				"(stringfield4 \"1\")(stringfield5 \"1\")" +
				"(stringfield6 \"1\")(stringfield6 \"1\")" +
				"(stringfield7 \"1\")(stringfield8 \"1\")" +
				"(stringfield9 \"1\")(stringfield10 \"1\")" +
				"(stringfield11 \"1\")(stringfield12 \"1\")" +
				"(stringfield13 \"1\")(stringfield14 \"1\")" +
				"(stringfield15 \"1\")(stringfield16 \"1\")" +
				") )" + Constants.LINEBREAK);
	}

	/**
	 * generate a rule with 1 join between object1 and object2
	 * @param count
	 * @param buf
	 */
	public void write1JoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			String intvar = "?intfld" + idx;
			buf.append("(defrule 1joinrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"1joinrule" + idx + " fired \" " +
					intvar + " crlf)" + Constants.LINEBREAK);
			buf.append(")" + Constants.LINEBREAK);
		}
	}

	/**
	 * Generate a rule with 2 joins
	 * @param count
	 * @param buf
	 */
	public void write2JoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			String intvar = "?intfld" + idx;
			String lngvar = "?lngfld" + idx;
			buf.append("(defrule 2joinrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"2joinrule" + idx + " fired \" " +
					intvar + " crlf)" + Constants.LINEBREAK);
			buf.append(")" + Constants.LINEBREAK);
		}
	}
	
	/**
	 * generate rules with 3 joins between object1 and object2
	 * @param count
	 * @param buf
	 */
	public void write3JoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			String intvar = "?intfld" + idx;
			String lngvar = "?lngfld" + idx;
			String shrtvar = "?shortfld" + idx;
			buf.append("(defrule 3joinrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"3joinrule" + idx + " fired \" " +
					intvar + " crlf)" + Constants.LINEBREAK);
			buf.append(")" + Constants.LINEBREAK);
		}
	}
	
	/**
	 * generate rules with 4 joins between object1 and object2
	 * @param count
	 * @param buf
	 */
	public void write4JoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			String intvar = "?intfld" + idx;
			String lngvar = "?lngfld" + idx;
			String shrtvar = "?shortfld" + idx;
			String dbvar = "?dbfld" + idx;
			buf.append("(defrule 4joinrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"4joinrule" + idx + " fired \" " +
					intvar + " crlf)" + Constants.LINEBREAK);
			buf.append(")" + Constants.LINEBREAK);
		}
	}
	
	/**
	 * generate a rule with 5 joins between object1 and object2
	 * @param count
	 * @param buf
	 */
	public void write5JoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			String intvar = "?intfld" + idx;
			String lngvar = "?lngfld" + idx;
			String shrtvar = "?shortfld" + idx;
			String dbvar = "?dbfld" + idx;
			String strvar = "?strfld2" + idx;
			buf.append("(defrule 5joinrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield2 " + strvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield2 " + strvar + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"5joinrule" + idx + " fired \" " +
					intvar + " crlf)" + Constants.LINEBREAK);
			buf.append(")" + Constants.LINEBREAK);
		}
	}

	/**
	 * generate rules with 10 joins between object1 and object2
	 * @param count
	 * @param buf
	 */
	public void write10JoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			String intvar = "?intfld" + idx;
			String lngvar = "?lngfld" + idx;
			String shrtvar = "?shortfld" + idx;
			String dbvar = "?dbfld" + idx;
			String strvar = "?strfld1" + idx;
			String strvar2 = "?strfld2" + idx;
			String strvar3 = "?strfld3" + idx;
			String strvar4 = "?strfld4" + idx;
			String strvar5 = "?strfld5" + idx;
			String strvar6 = "?strfld6" + idx;
			String strvar7 = "?strfld7" + idx;
			buf.append("(defrule 10joinrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield2 " + strvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield3 " + strvar3 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield4 " + strvar4 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield5 " + strvar5 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield6 " + strvar6 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield7 " + strvar7 + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield2 " + strvar2 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield3 " + strvar3 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield4 " + strvar4 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield5 " + strvar5 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield6 " + strvar6 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield7 " + strvar7 + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"10joinrule" + idx + " fired \" " +
					intvar + " crlf)" + Constants.LINEBREAK);
			buf.append(")" + Constants.LINEBREAK);
		}
	}

	public void write15JoinRule(int count, StringBuffer buf) {
		for (int idx=1; idx <= count; idx++) {
			String intvar = "?intfld" + idx;
			String lngvar = "?lngfld" + idx;
			String shrtvar = "?shortfld" + idx;
			String dbvar = "?dbfld" + idx;
			String strvar = "?strfld1" + idx;
			String strvar2 = "?strfld2" + idx;
			String strvar3 = "?strfld3" + idx;
			String strvar4 = "?strfld4" + idx;
			String strvar5 = "?strfld5" + idx;
			String strvar6 = "?strfld6" + idx;
			String strvar7 = "?strfld7" + idx;
			String strvar8 = "?strfld8" + idx;
			String strvar9 = "?strfld9" + idx;
			String strvar10 = "?strfld10" + idx;
			String strvar11 = "?strfld11" + idx;
			String strvar12 = "?strfld12" + idx;
			buf.append("(defrule 10joinrule" + idx + Constants.LINEBREAK);
			buf.append("  (object1" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield2 " + strvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield3 " + strvar3 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield4 " + strvar4 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield5 " + strvar5 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield6 " + strvar6 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield7 " + strvar7 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield8 " + strvar8 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield9 " + strvar9 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield10 " + strvar10 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield11 " + strvar11 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield12 " + strvar12 + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("  (object2" + Constants.LINEBREAK);
			buf.append("    (intfield " + intvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (longfield " + lngvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (shortfield " + shrtvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (doublefield " + dbvar + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield2 " + strvar2 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield3 " + strvar3 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield4 " + strvar4 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield5 " + strvar5 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield6 " + strvar6 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield7 " + strvar7 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield8 " + strvar8 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield9 " + strvar9 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield10 " + strvar10 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield11 " + strvar11 + ")" +
					Constants.LINEBREAK);
			buf.append("    (stringfield12 " + strvar12 + ")" +
					Constants.LINEBREAK);
			buf.append("  )" + Constants.LINEBREAK);
			buf.append("=>" + Constants.LINEBREAK);
			buf.append("  (printout t \"10joinrule" + idx + " fired \" " +
					intvar + " crlf)" + Constants.LINEBREAK);
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
		int joins = 1;
		boolean right = true;
		boolean profleft = false;
		if (args != null && args.length > 0) {
			if (args[0] != null) {
				outfile = args[0];
			}
			if (args[1] != null) {
				rules = Integer.parseInt(args[1]);
			}
			if (args[2] != null) {
				joins = Integer.parseInt(args[2]);
			}
			if (args[3] != null) {
				data = Integer.parseInt(args[3]);
			}
			if (args[4] != null && args[4].equals("left")) {
				right = false;
			}
			if (args[5] != null && args[5].equals("true")) {
				profleft = true;
			}
			
			GenerateJoinCountRules gen = new GenerateJoinCountRules();
			StringBuffer buf = new StringBuffer();
			gen.writeDeftemplate1(buf);
			gen.writeDeftemplate2(buf);
			if (joins == 1) {
				gen.write1JoinRule(rules,buf);
			} else if (joins == 2) {
				gen.write2JoinRule(rules,buf);
			} else if (joins == 3) {
				gen.write3JoinRule(rules,buf);
			} else if (joins == 4) {
				gen.write4JoinRule(rules,buf);
			} else if (joins == 5) {
				gen.write5JoinRule(rules,buf);
			} else if (joins == 10) {
				gen.write10JoinRule(rules,buf);
			} else if (joins == 15) {
				gen.write15JoinRule(rules, buf);
			}
			if (profleft) {
				if (right) {
					gen.writeRightActivateFacts(data,buf);
				} else {
					gen.writeLeftActivateFacts2(data,buf);
				}
			} else {
				gen.writeProfile(buf);
				if (right) {
					gen.writeRightActivateFacts(data,buf);
				} else {
					gen.writeLeftActivateFacts(data,buf);
				}
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
			System.out.println("     number of joins");
			System.out.println("     number of facts");
			System.out.println("     right|left");
			System.out.println("     profile left true");
		}
	}

}
