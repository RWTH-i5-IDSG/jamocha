package org.jamocha.benchmarking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jamocha.engine.Engine;
import org.jamocha.engine.ExecuteException;

public class Benchmark {

	/*
	 * in all our benchmarks, we measure the lag over the time and the knowledgebase-size
	 * 
	 * LAG
	 *  |
	 *  |   SIZE
	 *  |   /
	 *  |  /
	 *  | /
	 *  |/
	 *  *--------------------------- TIME
	 * 
	 */
	
	protected class MeasureThread extends Thread {
		
		protected int maxtime,step;
		
		protected Engine problem;
		
		protected int[] lag;
	
		protected boolean finished;
		
		public boolean isFinished() {
			return finished;
		}

		public MeasureThread(Engine problem, int maxtime, int step) {
			this.problem=problem;
			this.maxtime=maxtime;
			this.step=step;
			this.lag=new int[maxtime/step +1];
			this.finished=false;
			this.setName("Measure");
		}
		
		public void run() {
			
			try {
				problem.fire();
			} catch (ExecuteException e1) {
				e1.printStackTrace();
			}
			
			for(int i=0; i <= (maxtime/step); i++) {
				lag[i] = problem.getLag();
				try {this.sleep(step);} catch (InterruptedException e) {}
			}
			
			finished = true;
			problem.dispose();
		
			
		}

		public int[] getValues() {
			return lag;
		}
		
	}
	
	
	private KnowledgebaseProvider provider;
	
	private int maxsize,maxtime,step; // maxtime and step in milliseconds
	
	private String tempstrat;
	
	public Benchmark(KnowledgebaseProvider provider, int size, int maxtime, int step, String strategy) {
		this.provider=provider;
		this.maxsize=size;
		this.maxtime=maxtime;
		this.step=step;
		this.tempstrat = strategy;
	}
	
	public int measure(String outputFile) throws IOException {
		File f = new File(outputFile);
		FileWriter ow = new FileWriter(f);
		int s=maxsize;
		Engine problem = provider.getProblemInstance(s, tempstrat);
		MeasureThread measure = new MeasureThread(problem,maxtime,step);
		measure.start();
		while (!measure.isFinished())try {Thread.sleep(500);} catch (InterruptedException e) {}
		int[] values = measure.getValues();
		int max=0;
		for(int i=0; i< values.length; i++) {
			max = (values[i] > max) ? values[i] : max;
			//ow.write(String.format("   <value size=\"%d\" strategy=\"%s\" time=\"%d\" lag=\"%d\" />\n", s, tempstrat, i*step,values[i]));
			ow.write(i*step+" "+s+" "+values[i]+"\n");
		}
		ow.close();
		return max;
	}
	
	
	
	
}
