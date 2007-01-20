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

/**
 * @author Peter Lin
 *
 * Feel free to use this code as you wish, consider it public domain.
 */
public class GenerateRules {

    public static final String LINEBREAK = System.getProperty("line.separator");
    
	/**
	 * 
	 */
	public GenerateRules() {
		super();
    }

    /**
     * The method generates a rule of the following format
     * (defrule rule0 (fact nsh00) => (printout t "rule0 was fired" crlf))
     * @param count
     * @return
     */
    public String generateSimpleRule(int count) {
        StringBuffer buf = new StringBuffer();
        buf.append("(defrule rule" + count);
        buf.append(" (fact nsh" + count + ") => (printout t \"rule" + count);
        buf.append(" was fired\" ))" + LINEBREAK);
        return buf.toString();
    }
    
    /**
     * (defrule ruleXX
     *   (objectXX
     *     (attr1 "true")
     *   )
     * =>
     *   (printout t "ruleXX fired" )
     * )
     * @param count
     * @return
     */
    public String generateDefRule(int count) {
        StringBuffer buf = new StringBuffer();
        buf.append("(defrule rule" + count + LINEBREAK);
        buf.append("  (object1" + LINEBREAK);
        buf.append("    (attr1 \"" + count + "\")" + LINEBREAK);
        buf.append("  )" + LINEBREAK);
        buf.append("=>" + LINEBREAK);
        buf.append("  (printout t \"rule" + count + " fired\")" + LINEBREAK);
        buf.append(")" + LINEBREAK);
        return buf.toString();
    }
    
    public String generateUniqueDeftRule(int count) {
        StringBuffer buf = new StringBuffer();
        buf.append("(defrule rule" + count + LINEBREAK);
        buf.append("  (object" + count + LINEBREAK);
        buf.append("    (attr1 \"" + count + "\")" + LINEBREAK);
        buf.append("  )" + LINEBREAK);
        buf.append("=>" + LINEBREAK);
        buf.append("  (printout t \"rule" + count + " fired\")" + LINEBREAK);
        buf.append(")" + LINEBREAK);
        return buf.toString();
    }

    /**
     * returns the string declaring the deffact.
     * @return
     */
    public String getDeffact() {
        StringBuffer buf = new StringBuffer();
        buf.append("(deftemplate object1" + LINEBREAK);
        buf.append("  (slot attr1)" + LINEBREAK);
        buf.append("  (slot attr2)" + LINEBREAK);
        buf.append("  (slot attr3)" + LINEBREAK);
        buf.append("  (slot attr4)" + LINEBREAK);
        buf.append("  (slot attr5)" + LINEBREAK);
        buf.append("  (slot attr6)" + LINEBREAK);
        buf.append("  (slot attr7)" + LINEBREAK);
        buf.append("  (slot attr8)" + LINEBREAK);
        buf.append("  (slot attr9)" + LINEBREAK);
        buf.append("  (slot attr10)" + LINEBREAK);
        buf.append(")" + LINEBREAK);
        return buf.toString();
    }
    
    /**
     * returns the string declaring the deffact.
     * @return
     */
    public String getDeffact(int count) {
        StringBuffer buf = new StringBuffer();
        buf.append("(deftemplate object" + count + LINEBREAK);
        buf.append("  (slot attr1)" + LINEBREAK);
        buf.append("  (slot attr2)" + LINEBREAK);
        buf.append("  (slot attr3)" + LINEBREAK);
        buf.append("  (slot attr4)" + LINEBREAK);
        buf.append("  (slot attr5)" + LINEBREAK);
        buf.append("  (slot attr6)" + LINEBREAK);
        buf.append("  (slot attr7)" + LINEBREAK);
        buf.append("  (slot attr8)" + LINEBREAK);
        buf.append("  (slot attr9)" + LINEBREAK);
        buf.append("  (slot attr10)" + LINEBREAK);
        buf.append(")" + LINEBREAK);
        return buf.toString();
    }

    public static void main(String[] args) {
        String output = "rules.clp";
        int count = 1000;
        boolean deffact = false;
        boolean unique = false;
        GenerateRules gen = new GenerateRules();
        if (args != null && args.length > 0) {
            if (args[0] != null) {
                count = Integer.parseInt(args[0]);
            }
            if (args.length >= 2 && args[1] != null) {
                output = args[1];
            }
            if (args.length >= 3 && args[2].equals("true")) {
                deffact = true;
            }
            if (args.length >= 4 && args[3].equals("true")) {
                unique = true;
            }
            try {
                FileWriter writer = new FileWriter(output);
                if (unique) {
                    for (int idx=0; idx < count; idx++) {
                        writer.write(gen.getDeffact(idx));
                    }
                } else {
                    writer.write(gen.getDeffact());
                }
                for (int idx=0; idx < count; idx++) {
                    if (deffact && !unique) {
                        writer.write(gen.generateDefRule(idx));
                    } else if (deffact && unique) {
                        writer.write(gen.generateUniqueDeftRule(idx));
                    } else {
                        writer.write(gen.generateSimpleRule(idx));
                    }
                }
                writer.close();
                System.out.println("Finished generating " + count + " rules.");
                System.out.println("The rules were saved to " + output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("The utility takes the following parameters");
            System.out.println(" ");
            System.out.println("java woolfel.rulebenchmark.GenerateRules 1000 output.clp(optional) true");
            System.out.println("first parameter - number of rules");
            System.out.println("second parameter - output file");
            System.out.println("third parameter - use deffact instead of ordered facts");
            System.out.println("forth parameter - generate n deffact declaration");
        }
	}
}
