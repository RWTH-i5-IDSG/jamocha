/*
 * Created on Dec 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package woolfel.rulebenchmark;

import java.io.FileWriter;
import java.util.Random;

/**
 * @author Peter Lin
 *
 * comment
 */
public class SimpleRuleGenerator2 {

    public static final String LINEBREAK = System.getProperty("line.separator");

    public String fileName = null;
    private FileWriter wtr = null;
    public int ruleType = 0;

    private Random ran = new Random();
    
    /**
	 * 
	 */
	public SimpleRuleGenerator2() {
		super();
	}

    public void setFilename(String name) {
        this.fileName = name;
    }
    
    public void generateRules(int count) {
        try {
            this.wtr = new FileWriter(this.fileName);
            wtr.write(getDefTemplate());
            if (ruleType == 0) {
                generateSimpleRule(count);
            } else if (ruleType == 1) {
                this.generateSimpleRuleIncreaseOrder(count);
            } else if (ruleType == 2) {
                this.generateSimpleRuleRandomOrder(count);
            } else if (ruleType == 3) {
                this.generateSimpleRuleDecreaseOrder(count);
            } else if (ruleType == 4) {
                generateSequentialRule(count);
            }
            wtr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getDefTemplate() {
        StringBuffer buf = new StringBuffer();
        buf.append("(deftemplate transaction" + LINEBREAK);
        buf.append("  (slot accountId)" + LINEBREAK);
        buf.append("  (slot buyPrice)" + LINEBREAK);
        buf.append("  (slot countryCode)" + LINEBREAK);
        buf.append("  (slot currentPrice)" + LINEBREAK);
        buf.append("  (slot cusip)" + LINEBREAK);
        buf.append("  (slot exchange)" + LINEBREAK);
        buf.append("  (slot fitchLongRating)" + LINEBREAK);
        buf.append("  (slot fitchShortRating)" + LINEBREAK);
        buf.append("  (slot gaurantor)" + LINEBREAK);
        buf.append("  (slot industryGroupID)" + LINEBREAK);
        buf.append("  (slot industryID)" + LINEBREAK);
        buf.append("  (slot issuer)" + LINEBREAK);
        buf.append("  (slot lastPrice)" + LINEBREAK);
        buf.append("  (slot purchaseDate)" + LINEBREAK);
        buf.append("  (slot sectorID)" + LINEBREAK);
        buf.append("  (slot shares)" + LINEBREAK);
        buf.append("  (slot spLongRating)" + LINEBREAK);
        buf.append("  (slot spShortRating)" + LINEBREAK);
        buf.append("  (slot subIndustryID)" + LINEBREAK);
        buf.append(")" + LINEBREAK);
        return buf.toString();
    }
    
    public String getDefTemplate2() {
        StringBuffer buf = new StringBuffer();
        buf.append("(deftemplate transaction");
        buf.append("  (slot accountId (type STRING))");
        buf.append("  (slot buyPrice (type DOUBLE))");
        buf.append("  (slot countryCode (type STRING))");
        buf.append("  (slot currentPrice (type DOUBLE))");
        buf.append("  (slot cusip (type INTEGER))");
        buf.append("  (slot exchange (type STRING))");
        buf.append("  (slot industryGroupID (type INTEGER))");
        buf.append("  (slot industryID (type INTEGER))");
        buf.append("  (slot issuer (type STRING))");
        buf.append("  (slot lastPrice (type DOUBLE))");
        buf.append("  (slot purchaseDate (type STRING))");
        buf.append("  (slot sectorID (type INTEGER))");
        buf.append("  (slot shares (type DOUBLE))");
        buf.append("  (slot subIndustryID (type INTEGER))");
        buf.append("  (slot total (type DOUBLE))");
        buf.append(")");
        buf.append("(deftemplate account");
        buf.append("  (slot accountId (type STRING))");
        buf.append("  (slot cash (type DOUBLE))");
        buf.append("  (slot fixedIncome (type DOUBLE))");
        buf.append("  (slot stocks (type DOUBLE))");
        buf.append("  (slot countryCode (type STRING))");
        buf.append(")");
        return buf.toString();
    }
    
    public void generateSimpleRule(int count) {
        for (int idx=0; idx < count; idx++) {
            StringBuffer buf = new StringBuffer();
            buf.append("(defrule rule" + idx + LINEBREAK);
            buf.append(" (transaction" + LINEBREAK);
            buf.append("    (accountId ?accid)" + LINEBREAK);
            buf.append("    (buyPrice ?bp)" + LINEBREAK);
            buf.append("    (countryCode \"US\")" + LINEBREAK);
            buf.append("    (exchange \"NYSE\")" + LINEBREAK);
            buf.append("    (subIndustryID 25501010)" + LINEBREAK);
            buf.append("    (issuer \"" + SimpleDataGenerator2.issuers[idx] +
                    "\")" + LINEBREAK);
            buf.append("  )" + LINEBREAK);
            buf.append("=>" + LINEBREAK);
            buf.append("  (printout t \"rule" + idx + " was fired\" )" + LINEBREAK);
            buf.append(")" + LINEBREAK);
            try {
                wtr.write(buf.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * generates the rules such that the slots are order in increasing
     * order so that slots with fewer possible values are first.
     * @param count
     */
    public void generateSimpleRuleIncreaseOrder(int count) {
        for (int idx=0; idx < count; idx++) {
            StringBuffer buf = new StringBuffer();
            buf.append("(defrule rule" + idx + LINEBREAK);
            buf.append(" (transaction" + LINEBREAK);
            buf.append("    (accountId ?accid)" + LINEBREAK);
            buf.append("    (buyPrice ?bp)" + LINEBREAK);
            buf.append("    (exchange \"" + SimpleDataGenerator2.exchange[ran.nextInt(4)] +
                    "\")" + LINEBREAK);
            buf.append("    (countryCode \"" + SimpleDataGenerator2.countries[ran.nextInt(9)] +
                    "\")" + LINEBREAK);
            buf.append("    (subIndustryID " + SimpleDataGenerator2.gics[ran.nextInt(19)] +
                    ")" + LINEBREAK);
            buf.append("    (issuer \"" + SimpleDataGenerator2.issuers[ran.nextInt(24)] +
                    "\")" + LINEBREAK);
            // buf.append("    (cusip " + 
            //        SimpleDataGenerator2.cusips[ran.nextInt(SimpleDataGenerator2.cusips.length)] +
            //        ")" + LINEBREAK);
            buf.append("  )" + LINEBREAK);
            buf.append("=>" + LINEBREAK);
            buf.append("  (printout t \"rule" + idx + " was fired\" )" + LINEBREAK);
            buf.append(")" + LINEBREAK);
            try {
                wtr.write(buf.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void generateSimpleRuleRandomOrder(int count) {
        for (int idx=0; idx < count; idx++) {
            StringBuffer buf = new StringBuffer();
            buf.append("(defrule rule" + idx + LINEBREAK);
            buf.append(" (transaction" + LINEBREAK);
            buf.append("    (accountId ?accid)" + LINEBREAK);
            buf.append("    (buyPrice ?bp)" + LINEBREAK);
            buf.append("    (countryCode \"" + SimpleDataGenerator2.countries[ran.nextInt(9)] +
                    "\")" + LINEBREAK);
            buf.append("    (subIndustryID " + SimpleDataGenerator2.gics[ran.nextInt(19)] +
                    ")" + LINEBREAK);
            buf.append("    (exchange \"" + SimpleDataGenerator2.exchange[ran.nextInt(4)] +
                    "\")" + LINEBREAK);
            buf.append("    (issuer \"" + SimpleDataGenerator2.issuers[ran.nextInt(23)] +
                    "\")" + LINEBREAK);
            // buf.append("    (cusip " + 
            //        SimpleDataGenerator2.cusips[ran.nextInt(SimpleDataGenerator2.cusips.length)] +
            //        ")" + LINEBREAK);
            buf.append("  )" + LINEBREAK);
            buf.append("=>" + LINEBREAK);
            buf.append("  (printout t \"rule" + idx + " was fired\" )" + LINEBREAK);
            buf.append(")" + LINEBREAK);
            try {
                wtr.write(buf.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * generates the rule such that the slots are arranged in decreasing
     * order of unique values. In other words, countryCode has 10 possible
     * values, subIndustry has 9 possible values, etc.
     * @param count
     */
    public void generateSimpleRuleDecreaseOrder(int count) {
        for (int idx=0; idx < count; idx++) {
            StringBuffer buf = new StringBuffer();
            buf.append("(defrule rule" + idx + LINEBREAK);
            buf.append(" (transaction" + LINEBREAK);
            buf.append("    (accountId ?accid)" + LINEBREAK);
            buf.append("    (buyPrice ?bp)" + LINEBREAK);
            // buf.append("    (cusip " + 
            //        SimpleDataGenerator2.cusips[ran.nextInt(SimpleDataGenerator2.cusips.length)] +
            //        ")" + LINEBREAK);
            buf.append("    (issuer \"" + SimpleDataGenerator2.issuers[ran.nextInt(23)] +
                    "\")" + LINEBREAK);
            buf.append("    (subIndustryID " + SimpleDataGenerator2.gics[ran.nextInt(19)] +
                    ")" + LINEBREAK);
            buf.append("    (countryCode \"" + SimpleDataGenerator2.countries[ran.nextInt(9)] +
                    "\")" + LINEBREAK);
            buf.append("    (exchange \"" + SimpleDataGenerator2.exchange[ran.nextInt(4)] +
                    "\")" + LINEBREAK);
            buf.append("  )" + LINEBREAK);
            buf.append("=>" + LINEBREAK);
            buf.append("  (printout t \"rule" + idx + " was fired\" )" + LINEBREAK);
            buf.append(")" + LINEBREAK);
            try {
                wtr.write(buf.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void generateSequentialRule(int count) {
        for (int idx=0; idx < count; idx++) {
            StringBuffer buf = new StringBuffer();
            buf.append("(defrule rule" + idx + LINEBREAK);
            buf.append(" (transaction" + LINEBREAK);
            buf.append("    (accountId \"" + idx + "\")" + LINEBREAK);
            buf.append("  )" + LINEBREAK);
            buf.append("=>" + LINEBREAK);
            buf.append("  (printout t \"rule" + idx + " was fired\" )" + LINEBREAK);
            buf.append(")" + LINEBREAK);
            try {
                wtr.write(buf.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            SimpleRuleGenerator2 gen = new SimpleRuleGenerator2();
            int count = Integer.parseInt(args[0]);
            int type = Integer.parseInt(args[2]);
            gen.ruleType = type;
            gen.setFilename(args[1]);
            gen.generateRules(count);
            System.out.println("done!!");
        }
    }
}
