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
 * @author pete
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleDataGenerator2 {

    public static final String LINEBREAK = System.getProperty("line.separator");

    public String fileName = null;
    private FileWriter wtr = null;
    
    // 5 exchanges
    public static final String[] exchange = {"NYSE","NSDQ","LNSE","TKYO",
            "TWSE"};

    // 50 country codes
    public static final String[] countries = {"ad","ae","af","ag","ai","al","am","an","ao","aq",
            "ar","as","at","au","aw","az","ba","bb","bd","be",
            "bf","bg","bh","bi","bj","bm","bn","bo","br","bs",
            "bt","bv","bw","by","bz","ca","cc","cf","cd","cg",
            "ch","ci","ck","cl","cm","cn","co","cr","cs","cu"};

    // 50 gics codes
    public static final String[] gics = {
            "10101010","10101020",
            "10101030","10101040",
            "10151010","10151020",
            "10151030","10151040",
            "10201010","10201020",
            "10201030","10201040",
            "10251010","10251020",
            "10251030","10251040",
            "10301010","10301020",
            "10301030","10301040",
            "10351010","10351020",
            "10351030","10351040",
            "15101010","15101020",
            "15101030","15101040",
            "15101050","15101060",
            "20101010","20101020",
            "20101030","20101040",
            "20201010","20201020",
            "20202010","20202020",
            "20203010","20203020",
            "20301010","25201010",
            "25201020","25201030",
            "25201040","25201050",
            "25301010","25301020",
            "25301030","25301040"
            };
    
    // 72 issuers
    public static final String[] issuers = {"AAA","BBB","CCC","DDD",
            "EEE","FFF","GGG","HHH",
            "III","JJJ","KKK","LLL",
            "MMM","NNN","OOO","PPP",
            "QQQ","RRR","SSS","TTT",
            "UUU","VVV","WWW","XXX",
            "ABA","ABB","ACC","ADD",
            "AEE","AFF","AGG","AHH",
            "AII","AJJ","AKK","ALL",
            "AMM","ANN","AOO","APP",
            "AQQ","ARR","ASS","ATT",
            "AUU","AVV","AWW","AXX",
            "BAA","BAB","BCC","BDD",
            "BEE","BFF","BGG","BHH",
            "BII","BJJ","BKK","BLL",
            "BMM","BNN","BOO","BPP",
            "BQQ","BRR","BSS","BTT",
            "BUU","BVV","BWW","BXX"
            };
    
    
    // 200 cusips
    public static final String[] cusips = {
            "847737565","584420736","776465086","280242230","334158152",
            "240867935","865474029","712660351","561035530","885735172",
            "344390487","726734369","662451788","620752883","175954168",
            "275954292","433047801","735586843","553510103","199051430",
            "627054774","507581511","166646659","146242070","205556553",
            "858200420","567390188","837309795","544327373","196415543",
            "376167430","709124904","142320670","793376081","319274555",
            "598219400","312682706","452645228","862002954","781373494",
            "329456994","494484219","412667199","378783313","121983709",
            "516617409","759266383","390964563","189568170","299204213",
            "516882651","434218430","533656092","164337122","674085353",
            "805617402","161380471","526810808","246381058","543770169",
            "711156257","892582569","126332571","625084288","129234989",
            "298767420","800008121","326697747","430943176","699324322",
            "457534625","255201847","832079721","235670198","172932416",
            "716019948","818895879","554087729","252053341","325203505",
            "238020594","687847258","573268222","601057503","662345181",
            "873231211","266740054","373598371","876107238","346368955",
            "308242432","492744005","863453561","778001345","415452716",
            "440854222","362538853","306015545","538338324","226109937",
            "291109859","465698045","275037983","231414280","657154953",
            "436542044","623976668","160958597","373890311","799096553",
            "264441576","848359254","290321677","619705057","425313404",
            "651303643","125055052","230578439","433271871","756549107",
            "550753853","275255867","217673701","624940673","464978577",
            "761006871","843979044","581790865","181347331","276563989",
            "737710072","598011565","234915977","805612660","738288049",
            "692081442","797446761","613907542","775595207","506734023",
            "189336199","834664189","699580158","503890441","575276947",
            "498699382","591222097","211717749","845478011","196549951",
            "368319329","397034350","546967177","427870745","797040185",
            "873689215","693406914","132401530","551968693","826752040",
            "265624282","820677108","159350949","122391661","239356499",
            "732955767","280177720","261149820","524868552","281696493",
            "449214275","838265786","314942299","743073356","190634823",
            "828036249","563688551","253314976","145533821","256624960",
            "372972821","577759195","564621623","392200752","887317176",
            "393577061","146082440","438948311","211472361","672761685",
            "495769041","529334375","893767035","864211473","877467440",
            "254167483","352560040","485389540","226205919","426306245"
            };

    private Random ran = new Random();

    /**
	 * 
	 */
	public SimpleDataGenerator2() {
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
            SimpleDataGenerator2 gen = new SimpleDataGenerator2();
            gen.setFilename(file);
            gen.generate(count);
            System.out.println("done!!");
        }
    }
}
