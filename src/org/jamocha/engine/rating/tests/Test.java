package org.jamocha.engine.rating.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jamocha.communication.BatchExecutionListener;
import org.jamocha.communication.BatchThread;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.rating.CSVWriter;
import org.jamocha.engine.rating.Converter;
import org.jamocha.engine.rating.exceptions.MissingDataException;
import org.jamocha.engine.rating.inputvalues.InputValuesDictionary;

public class Test {

	static void test() {
		final InputValuesDictionary dict;
		final Engine engine;
		final ReteNet net;
		final BatchThread batchThread;

		dict = new InputValuesDictionary();

		engine = new Engine();
		net = engine.getNet();

		batchThread = new BatchThread(engine);
		batchThread.start();

		final List<String> files = new Vector<String>();
		final File testFile = new File("testFile");
		FileWriter out;
		try {
			out = new FileWriter(testFile);

			final String defTemplatePerson = "(deftemplate Person (slot Name (type STRING))(slot Alter (type LONG)))";
			final String defTemplateBesitz = "(deftemplate Besitz (slot vonPerson (type STRING)) (slot Gegenstand (type STRING)))";
			final String defRulePfannkuchen = "(defrule Pfannkuchenregel (Person (Name ?name))\n"
					+ "	(Besitz (vonPerson ?name) (Gegenstand \"Mehl\") )\n"
					+ "=>\n"
					+ "	(printout t ?name \" hat Mehl fuer Pfannkuchen.\" crlf)\n"
					+ ")";

			out.append(defTemplatePerson);
			out.append(defTemplateBesitz);
			out.append(defRulePfannkuchen);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		files.add(testFile.getName());
		batchThread.processBatchFiles(files);
		batchThread.addBatchExecutionListener(new BatchExecutionListener() {
			@SuppressWarnings("deprecation")
			public void newBatchResults() {
				batchThread.stopThread();

				final Converter converter = new Converter(dict);

				try {
					converter.convert(net);
				} catch (final MissingDataException e) {
					System.err.println(e.toString());
				}

				System.out.println(converter.toString());

				final Map<Thread, StackTraceElement[]> allThreads = Thread
						.getAllStackTraces();
				final long thisThread = Thread.currentThread().getId();
				for (final Thread thread : allThreads.keySet()) {
					if (thisThread != thread.getId()) {
						thread.interrupt();
						thread.stop();
					}
				}
				System.out.println("all threads dead");

			}
		});
	}

	static void testRun() {

		final double rootSize = 10, rootFInsert = 1, rootFDelete = 1, rootTPP = 50;

		try {
			for (int int_v_rootSize = 1; int_v_rootSize <= 100; int_v_rootSize += 1) {
				final double v_rootSize = int_v_rootSize;// Math.floor(Math.exp(int_v_rootSize));
				// {
				// final double v_rootSize = 100;
				// int v_rootSize = 20;
				{
					int v_rootFIns = 1;
					{
						int v_rootFDel = 1;
						{
							final double sel1 = 0.5, sel2 = 0.5, sel3 = 0.5;
							// final double jsfA1A2 = 0.625, jsfLA3 = 0.4;
							final double jsfA1A2 = 0.4, jsfLA3 = 0.625;
							final double jsfA2A3 = 0.5, jsfRA1 = 0.5;
							final RatingTestCase.RatingResult resultJoinThreeAlphasL2R = RTC_JoinThreeAlphasL2R
									.newTest(v_rootSize, v_rootFIns,
											v_rootFDel, rootTPP, sel1, sel2,
											sel3, jsfA1A2, jsfLA3)
									.getRatingResult();
							final RatingTestCase.RatingResult resultJoinThreeAlphasR2L = RTC_JoinThreeAlphasR2L
									.newTest(v_rootSize, v_rootFIns,
											v_rootFDel, rootTPP, sel1, sel2,
											sel3, jsfA2A3, jsfRA1)
									.getRatingResult();
							if (resultJoinThreeAlphasL2R.getMemoryCost() != resultJoinThreeAlphasR2L
									.getMemoryCost()
									|| resultJoinThreeAlphasL2R
											.getRuntimeCost() != resultJoinThreeAlphasR2L
											.getRuntimeCost()) {
								System.out.print(int_v_rootSize
										+ " "
										+ v_rootSize
										+ " memory: ["
										+ resultJoinThreeAlphasL2R
												.getMemoryCost()
										+ " , "
										+ resultJoinThreeAlphasR2L
												.getMemoryCost());
								System.out.print("], runtime: ");
								System.out.print("["
										+ resultJoinThreeAlphasL2R
												.getRuntimeCost()
										+ " , "
										+ resultJoinThreeAlphasR2L
												.getRuntimeCost());
								System.out.println("]");
							}
						}
					}
				}
			}
		} catch (NodeException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	static void plotTest() throws NodeException, IOException {
		final double rootSize = 100, rootFInsert = 10, rootFDelete = 10, rootTPP = 50;
		final double sel1 = 0.8, sel2 = 0.8, sel3 = 0.8;

		final CSVWriter out = new CSVWriter(new File(
				"temp/compareJTA_2Betas_1Multi.dat"), "jsf12", "jsf23",
				"meml2r", "memr2l", "memM", "cpul2r", "cpur2l", "cpuM");

		final int int_jsf123 = 20;
		final double jsf123 = int_jsf123 / 100.0;
		for (int i = int_jsf123; i <= 100; ++i) {
			final double jsf12 = i / 100.0;
			final double jsf23 = jsf123 / jsf12;
			// jsf13 must be 1
			RatingTestCase.RatingResult l2r = RTC_JoinThreeAlphasL2R.newTest(
					rootSize, rootFInsert, rootFDelete, rootTPP, sel1, sel2,
					sel3, jsf12, jsf23).getRatingResult();
			RatingTestCase.RatingResult r2l = RTC_JoinThreeAlphasR2L.newTest(
					rootSize, rootFInsert, rootFDelete, rootTPP, sel1, sel2,
					sel3, jsf23, jsf12).getRatingResult();
			RatingTestCase.RatingResult m = RTC_JoinThreeAlphasMulti.newTest(
					rootSize, rootFInsert, rootFDelete, rootTPP, sel1, sel2,
					sel3, jsf12, 1, jsf23).getRatingResult();
			out.addValues(jsf12, jsf23, l2r.getMemoryCost(),
					r2l.getMemoryCost(), m.getMemoryCost(),
					l2r.getRuntimeCost(), r2l.getRuntimeCost(),
					m.getRuntimeCost());
		}
		out.close();
		System.exit(0);
	}

	public static void main(final String args[]) {
		Logger.getLogger(Engine.class.getCanonicalName())
				.setLevel(Level.SEVERE);
		try {
			plotTest();
		} catch (NodeException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
