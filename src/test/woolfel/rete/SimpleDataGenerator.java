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
package woolfel.rete;

import java.io.FileWriter;
import java.util.Random;


/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleDataGenerator {

    public static final String LINEBREAK = System.getProperty("line.separator");

    public String fileName = null;
    private FileWriter wtr = null;
    
    // 10 country codes
    public static final String[] countries = {"US","BR","FR","NZ","CA","MX",
      "CH","TW","NU","IT"};

    // 9 gics codes
    public static final String[] gics = {"25201010","25201020","25201030",
            "25201040","25201050","25301010","25301020",
            "25301030","25301040"};
    
    // 8 issuers
    public static final String[] issuers = {"AAA","BBB","CCC","DDD",
            "EEE","FFF","GGG","HHH"};
    
    // 7 exchanges
    public static final String[] exchange = {"NYSE","NSDQ","LNSE","TKYO",
            "TWSE","PSEX","RMSE"};
    
    // 6 cusips
    public static final String[] cusips = {
            "576335338",
            "847737565",
            "584420736",
            "776465086",
            "280242230",
            "334158152"};

    private Random ran = new Random();

    /**
	 * 
	 */
	public SimpleDataGenerator() {
		super();
	}
    
    public void setFilename(String name) {
        this.fileName = name;
    }

    public void generate(int count) {
        try {
            this.wtr = new FileWriter(this.fileName);
            createDeffacts(count);
            wtr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createDeffacts(int count) {
        for (int idx=0; idx < count; idx++) {
            String country = countries[ran.nextInt(countries.length)];
            String gicsCode = gics[ran.nextInt(gics.length -1)];
            String iss = issuers[ran.nextInt(issuers.length -1)];
            String ex = exchange[ran.nextInt(exchange.length -1)];
            String csip = cusips[ran.nextInt(cusips.length -1)];
            StringBuffer buf = new StringBuffer();
            buf.append("(assert (transaction");
            buf.append(" (accountId \"" + idx + "id\")");
            buf.append(" (buyPrice 55.23)");
            buf.append(" (countryCode \"" + country + "\")");
            buf.append(" (currentPrice 58.95)");
            buf.append(" (cusip " + csip + ")");
            buf.append(" (exchange \"" + ex + "\")");
            buf.append(" (industryGroupID "  + gicsCode.substring(0,4) + ")");
            buf.append(" (industryID "  + gicsCode.substring(0,6) + ")");
            buf.append(" (issuer \"" + iss + "\")");
            buf.append(" (lastPrice 50.12)");
            buf.append(" (sectorID " + gicsCode.substring(0,2) + ")");
            buf.append(" (shares 100)");
            buf.append(" (subIndustryID " + gicsCode + ")");
            buf.append(" ) )" + LINEBREAK);
            try {
                wtr.write(buf.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            int count = Integer.parseInt(args[0]);
            String file = args[1];
            SimpleDataGenerator gen = new SimpleDataGenerator();
            gen.setFilename(file);
            gen.generate(count);
            System.out.println("done!!");
        }
    }
}
