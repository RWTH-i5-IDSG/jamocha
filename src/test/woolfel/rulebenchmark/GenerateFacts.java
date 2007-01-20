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
public class GenerateFacts {

    public static final String LINEBREAK = System.getProperty("line.separator");
	/**
	 * 
	 */
	public GenerateFacts() {
		super();
	}

    public String generateOrderedFact(int count) {
        return "(assert (fact nsh" + count + ") )" + LINEBREAK;
    }
    
    /**
     * generate asserts for objectOne deffact
     * @param count
     * @return
     */
    public String generateFact(int count) {
        return "(assert (object1 (attr1 \"" + count + "\") )  )" + LINEBREAK;
    }
    
    public String generateFactWithRun(int count) {
        return "(assert (object1 (attr1 \"" + count + "\") )  ) (run)" + LINEBREAK;
    }

    public String generateFactUnique(int count) {
        return "(assert (object" + count + " (attr1 \"" + count + "\") )  )" + LINEBREAK;
    }
    
    public String generateRetract(int count) {
        return "(retract " + (count + 1)+ ")" + LINEBREAK;
    }
    
    public static void main(String[] args) {
        String output = "data.clp";
        String rfile = "data-retract.clp";
        int count = 1000;
        boolean fire = false;
        boolean retract = false;
        boolean ordered = false;
        boolean unique = false;
        GenerateFacts gen = new GenerateFacts();
        if (args != null && args.length > 0) {
            if (args[0] != null) {
                count = Integer.parseInt(args[0]);
            }
            if (args.length >= 2 && args[1] != null) {
                output = args[1];
            }
            if (args.length >= 3 && args[2].equals("true")) {
                fire = true;
            }
            if (args.length >= 4 && args[3].equals("true")) {
                retract = true;
                rfile = output.substring(0,output.length()-4) + "-retract.clp";
            }
            if (args.length >=5 && args[4].equals("true")) {
                ordered = true;
            }
            if (args.length >=6 && args[5].equals("true")) {
                unique = true;
            }
            try {
                FileWriter writer = new FileWriter(output);
                FileWriter rwriter = null;
                if (retract) {
                    rwriter = new FileWriter(rfile);
                }
                for (int idx=0; idx < count; idx++) {
                    if (fire) {
                        writer.write(gen.generateFactWithRun(idx));
                    } else if (ordered) {
                        writer.write(gen.generateOrderedFact(idx));
                    } else if (unique) {
                        writer.write(gen.generateFactUnique(idx));
                    } else {
                        writer.write(gen.generateFact(idx));
                    }
                    if (retract) {
                        rwriter.write(gen.generateRetract(idx));
                    }
                }
                writer.close();
                if (retract) {
                    rwriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Finished generating " + count + " facts.");
            System.out.println("The data saved to " + output);
        } else {
            System.out.println("The utility takes the following parameters");
            System.out.println(" ");
            System.out.println("java woolfel.rulebenchmark.GenerateFact 1000 output.clp(optional) true(optional) true(optional)");
            System.out.println("first parameter - number of facts");
            System.out.println("second parameter - output file");
            System.out.println("third parameter - run after each assert");
            System.out.println("forth parameter - generate retract for facts");
            System.out.println("fifth parameter - use ordered fact");
            System.out.println("sixth parameter - use unique deffact");
        }
	}
}
