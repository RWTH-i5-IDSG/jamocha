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
package org.jamocha.logging;

import java.io.Serializable;
import java.util.logging.Logger;



/**
 * @author Peter Lin
 * 
 * A quick and simple logger. To make it easier for other classes to
 * log and not have to import log4j stuff. 
 */
public class DefaultLogger implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected Logger log = null;
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public DefaultLogger(Class theclazz) {
		super();
		log = Logger.getLogger(theclazz.getCanonicalName());
		//TODO re-introduce log configuration
//		PropertyConfigurator.configure("log4j.properties");
	}

	public void debug(String msg) {
		log.log(java.util.logging.Level.FINE, msg);
	}
	
	public void debug(Exception exc) {
		log.log(java.util.logging.Level.FINE, exc.getMessage() , exc);
	}
	
	public void fatal(String msg) {
		log.log(java.util.logging.Level.SEVERE, msg);
	}
	
	public void fatal(Exception exc) {
		log.log(java.util.logging.Level.SEVERE, exc.getMessage() , exc);
	}
	
	public void info(String msg) {
		log.log(java.util.logging.Level.INFO, msg);
	}
	
	public void info(Exception exc) {
		log.log(java.util.logging.Level.INFO, exc.getMessage() , exc);
	}
	
	public void warn(String msg) {
		log.log(java.util.logging.Level.WARNING, msg);
	}
	
	public void warn(Exception exc) {
		log.log(java.util.logging.Level.WARNING, exc.getMessage() , exc);
	}
}
