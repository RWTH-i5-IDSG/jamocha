package woolfel.examples.model;

public class GenerateJoinRule {

	public static final String LINEBREAK = System.getProperty("line.separator");
	
	public GenerateJoinRule() {
		super();
	}
	
	public void writeTemplates(StringBuffer buf) {
		buf.append("(deftemplate transaction" + LINEBREAK);
		buf.append("  (slot accountId (type STRING))" + LINEBREAK);
		buf.append("  (slot buyPrice (type DOUBLE))" + LINEBREAK);
		buf.append("  (slot countryCode (type STRING))" + LINEBREAK);
		buf.append("  (slot currentPrice (type DOUBLE))" + LINEBREAK);
		buf.append("  (slot cusip (type INTEGER))" + LINEBREAK);
		buf.append("  (slot exchange (type STRING))" + LINEBREAK);
		buf.append("  (slot industryGroupID (type INTEGER))" + LINEBREAK);
		buf.append("  (slot industryID (type INTEGER))" + LINEBREAK);
		buf.append("  (slot issuer (type STRING))" + LINEBREAK);
		buf.append("  (slot lastPrice (type DOUBLE))" + LINEBREAK);
		buf.append("  (slot purchaseDate (type STRING))" + LINEBREAK);
		buf.append("  (slot sectorID (type INTEGER))" + LINEBREAK);
		buf.append("  (slot shares (type DOUBLE))" + LINEBREAK);
		buf.append("  (slot subIndustryID (type INTEGER))" + LINEBREAK);
		buf.append("  (slot total (type DOUBLE))" + LINEBREAK);
		buf.append(")" + LINEBREAK);
		buf.append("(deftemplate account" + LINEBREAK);
		buf.append("  (slot accountId (type STRING))" + LINEBREAK);
		buf.append("  (slot cash (type DOUBLE))" + LINEBREAK);
		buf.append("  (slot fixedIncome (type DOUBLE))" + LINEBREAK);
		buf.append("  (slot stocks (type DOUBLE))" + LINEBREAK);
		buf.append("  (slot countryCode (type STRING))" + LINEBREAK);
		buf.append(")");		
	}

	public void writeTransactions(StringBuffer buf) {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
