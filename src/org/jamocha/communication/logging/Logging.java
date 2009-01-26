/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.communication.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Peter Lin
 * @author Josef Alexander Hahn
 * 
 * A quick and simple logger.
 */
public class Logging {

	public static class JamochaLogger {

		private final Logger log;

		
//		File f;
//		FileWriter fw;
//		private JamochaLogger(final Logger log) {
//			this.log = null;
//			f = new File("/home/free-radical/jam.log"+log.getName());
//		}
//		public void write(String msg) throws IOException {
//			FileWriter fw = new FileWriter(f,true);
//			fw.write(msg+"\n");
//			fw.close();
//		}
//		public void debug(String msg)  {
//			try {		write(msg);	} catch (IOException e) {				e.printStackTrace();			}
//		}
//		public void info(String msg)  {
//			try {		write(msg);	} catch (IOException e) {				e.printStackTrace();			}
//		}
//		public void fatal(String msg)  {
//			try {		write(msg);	} catch (IOException e) {				e.printStackTrace();			}
//		}
//		public void warn(String msg)  {
//			try {		write(msg);	} catch (IOException e) {				e.printStackTrace();			}
//		}
//		public void debug(Exception e)  {
//			debug(e.getMessage());
//		}
//		public void info(Exception e)  {
//			debug(e.getMessage());
//		}
//		public void fatal(Exception e)  {
//			debug(e.getMessage());
//		}
//		public void warn(Exception e)  {
//			debug(e.getMessage());
//		}

		
		
		
		
		
		private JamochaLogger(final Logger log) {
			this.log = log;
		}

		public void debug(final String msg) {
			log.log(java.util.logging.Level.FINE, msg);
		}

		public void debug(final Exception exc) {
			log.log(java.util.logging.Level.FINE, exc.getMessage(), exc);
			exc.printStackTrace();
		}

		public void info(final String msg) {
			log.log(java.util.logging.Level.INFO, msg);
		}

		public void info(final Exception exc) {
			log.log(java.util.logging.Level.INFO, exc.getMessage(), exc);
			exc.printStackTrace();
		}

		public void warn(final String msg) {
			log.log(java.util.logging.Level.WARNING, msg);
		}

		public void warn(final Exception exc) {
			log.log(java.util.logging.Level.WARNING, exc.getMessage(), exc);
			exc.printStackTrace();
		}

		public void fatal(final String msg) {
			log.log(java.util.logging.Level.SEVERE, msg);
		}

		public void fatal(final Exception exc) {
			log.log(java.util.logging.Level.SEVERE, exc.getMessage(), exc);
			exc.printStackTrace();
		}

	}

	private static Map<String, JamochaLogger> loggers = new HashMap<String, JamochaLogger>();

	/**
	 * returns a logger for the given class as logger name
	 * 
	 * @param clz
	 * @return
	 */
	public static JamochaLogger logger(final Class<? extends Object> clz) {
		return logger(clz.getCanonicalName());
	}

	/**
	 * returns a logger for the given name. this name should be the fully
	 * qualified class name from the class, which accesses the logger
	 * 
	 * @param className
	 * @return
	 */
	public static JamochaLogger logger(final String className) {
		JamochaLogger res = loggers.get(className);
		if (res == null) {
			final Logger julLogger = Logger.getLogger(className);
			res = new JamochaLogger(julLogger);
			loggers.put(className, res);
		}
		return res;
	}

}
