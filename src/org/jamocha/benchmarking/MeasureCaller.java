package org.jamocha.benchmarking;

import java.lang.reflect.Constructor;

public class MeasureCaller {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String strat = args[0];
		String time = args[1];
		String step = args[2];
		String size = args[3];
		String measurement = args[4];
		String outputfile = args[5];
		
		try {
			Class<KnowledgebaseProvider> measureClass = (Class<KnowledgebaseProvider>) Class.forName("org.jamocha.benchmarking."+measurement);
			KnowledgebaseProvider provider = measureClass.newInstance();
			int iSize = Integer.parseInt(size);
			int iTime = Integer.parseInt(time);
			int iStep = Integer.parseInt(step);
			Benchmark b = new Benchmark(provider, iSize, iTime, iStep, strat);
			b.measure(outputfile);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
		
		
	}

}
