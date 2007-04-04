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
package org.jamocha.rete.functions;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalParameterException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;

/**
 * @author Josef Alexander Hahn
 * 
 */
public class JDBClink implements Function, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String JDBCLINK = "jdbclink";

	/**
	 * 
	 */
	public JDBClink() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.Function#getReturnType()
	 */
	public JamochaType getReturnType() {
		return JamochaType.BOOLEAN;
	}

	public JamochaValue executeFunction(Rete engine, Parameter[] params)
			throws EvaluationException {

		if (params != null) {
			if (params.length == 3) {
				
				long jdbclink = params[0].getValue(engine).getFactIdValue();
				String action = params[1].getValue(engine).getStringValue();
				JamochaValue thirdParam = params[2].getValue(engine);
				
                                // Get configuration fact
				Fact configFact = engine.getFactById(jdbclink);
				
                String jdbc_driver = configFact.getSlotValue("JDBCdriver").getStringValue();
				String jdbc_url    = configFact.getSlotValue("JDBCurl").getStringValue();
				String username    = configFact.getSlotValue("Username").getStringValue();
				String password    = configFact.getSlotValue("Password").getStringValue();
				String tmplt       = configFact.getSlotValue("TemplateName").getStringValue();
				String table       = configFact.getSlotValue("TableName").getStringValue();
				
                                // Load JDBC database-specific driver
				try {
                                    // Call newInstance() that gets wasted and collected by GC as recommended in
                                    // http://java.sun.com/developer/onlineTraining/Database/JDBC20Intro/JDBC20.html
                                    Class.forName(jdbc_driver).newInstance();
				} catch (Exception e){
					System.out.println(e.toString());
				}
				
				Deftemplate template = (Deftemplate) engine.findTemplate(tmplt);
				Slot[] slots = template.getAllSlots();
				Connection conn = null;
				try{
					conn = DriverManager.getConnection(jdbc_url,username,password);
					Statement s = conn.createStatement();
					
					if (action.equals("import")) {
						String sqlStatement = "SELECT "+slots[0].getName();
						for( int i=1 ; i<slots.length ; i++ ){
							sqlStatement += "," + slots[i].getName();
						}
						sqlStatement += " FROM " + table;
						ResultSet rs = s.executeQuery(sqlStatement);
						while (rs.next()) {
							
							Slot[] rowValues = new Slot[slots.length];
							for( int i=0 ; i<slots.length ; i++ ){
								// TODO: Typechecking?!
								// TODO: Does it work with Datetime?
								// TODO: Why cant Orys' parser assert facts wrt the jdbclink-template?
								Object o = rs.getObject(slots[i].getName());
								JamochaValue val = new JamochaValue(o);
								rowValues[i] = new Slot( slots[i].getName() , val);
							}
							// TODO: What is meant by the filter-fact (see feature request)
							Deffact rowFact = new Deffact(template,null,rowValues,engine.nextFactId());
							engine.assertFact(rowFact);
						}
						return JamochaValue.newBoolean(true);
						
					} else if (action.equals("export")) {
						// TODO: For now, we assume the third parameter to be a csv-list of facts. dirty thing :(
						
						// get primary keys from our table
						DatabaseMetaData meta = conn.getMetaData();
					    ResultSet rs = meta.getPrimaryKeys(null, null, table);
					    List<String> keys = new ArrayList<String>();
					    while (rs.next()) {
					    	keys.add( rs.getString("COLUMN_NAME") );
					    }
					    	
					    // generate some prepared statements
						String insertStatement = "INSERT INTO " + table + " (" + slots[0].getName();
						for( int i=1 ; i<slots.length ; i++){
							insertStatement += "," + slots[i].getName();
						}
						insertStatement += ") VALUES (?";
						for( int i=1 ; i<slots.length ; i++){
							insertStatement += ",?";
						}
						insertStatement += ")";
						
						String updateStatement = "UPDATE " + table + " SET " + slots[0].getName() + "=?";
						for( int i=1 ; i<slots.length ; i++){
							updateStatement += "," + slots[i].getName() + "=?";
						}
						updateStatement += " WHERE ";
						boolean firstKey = true;
						for (String key : keys) {
							if (!firstKey) {
								updateStatement += " AND ";
							}
							firstKey = false;
							updateStatement += key + "=?";
						} 
						String lookupStatement = "SELECT * FROM " + table + " WHERE ";
						firstKey = true;
						for ( String key : keys ) {
							if (!firstKey) {
								lookupStatement += " AND ";
							}
							firstKey = false;
							lookupStatement += key + "=?";
						}
						PreparedStatement inserter = conn.prepareStatement(insertStatement);
						PreparedStatement updater  = conn.prepareStatement(updateStatement);
						PreparedStatement lookuper = conn.prepareStatement(lookupStatement); // any better name ;) ?
					
						// iterate over our facts
						for( int i=0 ; i < thirdParam.getListCount() ; i++ ) {
							Fact actFact = (Fact) engine.getFactById( (thirdParam.getListValue(i).getFactIdValue()));
							
							// check whether to update or to insert
							boolean insert = true;
							if ( keys.size() > 0 ) {
								int keyindex=1;
								for( String key : keys ) {
									lookuper.setObject(keyindex++, actFact.getSlotValue(key).getObjectValue());
								}
								insert = !lookuper.executeQuery().next();
							}
							
							PreparedStatement actor = insert ? inserter : updater;
							
							// TODO: Check for the right deftemplate
							for( int j=1 ; j<=slots.length ; j++ ){
								// TODO: Typechecking?!
								actor.setObject(j, actFact.getSlotValue(slots[j-1].getName()).getObjectValue());
							}
							if (!insert) {
								int j = slots.length + 1;
								for( String key : keys ) {
									actor.setObject(j++, actFact.getSlotValue(key).getObjectValue());
								}
							}
							actor.execute();
						}
						return JamochaValue.newBoolean(true);
						
					} else {
						throw new EvaluationException("Unknown action '"+action+"'");
					}
					
					
					
				
				} catch(SQLException e) {
					//TODO: better handling than print stack trace to stderr
					e.printStackTrace();
					return JamochaValue.newBoolean(false);
				} finally {
					if (conn != null) {
						/* yippie, exception hell ;) */
						try {conn.close();} catch (SQLException e) {}
					}
				}
			}
		}
		throw new IllegalParameterException(3, false);
	}

	public String getName() {
		return JDBCLINK;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length == 3) {
			StringBuffer buf = new StringBuffer();
			buf.append("(jdbclink");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" " + params[idx].getExpressionString());

			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(jdbclink <fact-id> (\"import\"|\"export\")) (<fact-id>|<binding>)\n" 
					+ "Function description:\n"
					+ "\t first parameter is a jdbclink-fact\n"
					+ "\t second parameter specified whether to export to or to import from the given jdbclink\n"
					+ "\t third parameter [later]\n"
					+ "\t it returns true in the case of success.\n";
		}
	}
}
