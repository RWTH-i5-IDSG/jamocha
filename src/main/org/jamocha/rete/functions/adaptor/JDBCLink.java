/*
 * Copyright 2007 Josef Alexander Hahn, Alexander Wilden
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
package org.jamocha.rete.functions.adaptor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.FunctionDescription;

/**
 * @author Josef Alexander Hahn
 * 
 * Exports or imports facts to a database via a jdbc link. Returns true in case
 * of success.
 */
public class JDBCLink implements Function, Serializable {
	
	

	private static final class Description implements FunctionDescription {

		public String getDescription() {
			return "The jdbclink function transfers data from a DB-table line by line to and from Jamocha. For each line of the DB-table a fact of a corresponding Jamocha template is created. Therefore, the Jamocha template will be defined before the data import. The facts are exported by the jdbclink function from Jamocha back to the DB-table from a fact list. For each fact in the list, there will be a new record inserted in the DB or an existing record will become updated (according to the contents of the primary key) The facts are again based on the well defined template. Exporting wrt an incomplete template (not all columns from the table) is dangerous!";
		}

		public int getParameterCount() {
			return 3;
		}

		public String getParameterDescription(int parameter) {
			switch (parameter) {
			case 0:
				return "jdbclink fact describing the connection to use.";
			case 1:
				return "Operation is either import or export.";
			case 2:
				return "for export: list of facts to export. for import: list of jdbccondition-facts for filtering";
			}
			return "";
		}

		public String getParameterName(int parameter) {
			switch (parameter) {
			case 0:
				return "jdbclink";
			case 1:
				return "operation";
			case 2:
				return "facts";
			}
			return "";
		}

		public JamochaType[] getParameterTypes(int parameter) {
			switch (parameter) {
			case 0:
				return JamochaType.FACT_IDS;
			case 1:
				return JamochaType.STRINGS;
			case 2:
				return JamochaType.LISTS;
			}
			return JamochaType.NONE;
		}

		public JamochaType[] getReturnType() {
			return JamochaType.BOOLEANS;
		}

		public boolean isParameterCountFixed() {
			return true;
		}

		public boolean isParameterOptional(int parameter) {
			return false;
		}
	}

	private static final FunctionDescription DESCRIPTION = new Description();

	static final long serialVersionUID = 0xDeadBeafCafeBabeL;

	public static final String NAME = "jdbclink";

	public FunctionDescription getDescription() {
		return DESCRIPTION;
	}

	public String getName() {
		return NAME;
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

				String jdbc_driver = configFact.getSlotValue("JDBCdriver")
						.getStringValue();
				String jdbc_url = configFact.getSlotValue("JDBCurl")
						.getStringValue();
				String username = configFact.getSlotValue("Username")
						.getStringValue();
				String password = configFact.getSlotValue("Password")
						.getStringValue();
				String tmplt = configFact.getSlotValue("TemplateName")
						.getStringValue();
				String table = configFact.getSlotValue("TableName")
						.getStringValue();

				// Load JDBC database-specific driver
				try {
					// Call newInstance() that gets wasted and collected by GC
					// as recommended in
					// http://java.sun.com/developer/onlineTraining/Database/JDBC20Intro/JDBC20.html
					Class.forName(jdbc_driver).newInstance();
				} catch (Exception e) {
					System.out.println(e.toString());
				}

				Deftemplate template = (Deftemplate) engine.findTemplate(tmplt);
				Slot[] slots = template.getAllSlots();
				Connection conn = null;
				try {
					conn = DriverManager.getConnection(jdbc_url, username,
							password);

					if (action.equals("import")) {
						return method_import(engine, thirdParam, table, template, slots, conn);
					} else if (action.equals("export")) {
						return method_export(engine, thirdParam, table, slots, conn);
					} else {
						throw new EvaluationException("Unknown action '"
								+ action + "'");
					}

				} catch (SQLException e) {
					// TODO: better handling than print stack trace to stderr
					e.printStackTrace();
					return JamochaValue.newBoolean(false);
				} finally {
					if (conn != null) {
						/* yippie, exception hell ;) */
						try {
							conn.close();
						} catch (SQLException e) {
						}
					}
				}
			}
		}
		throw new IllegalParameterException(3, false);
	}

	private JamochaValue method_export(Rete engine, JamochaValue thirdParam, String table, Slot[] slots, Connection conn) throws SQLException {
		// get primary keys from our table
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rs = meta.getPrimaryKeys(null, null, table);
		List<String> keys = new ArrayList<String>();
		while (rs.next()) {
			keys.add(rs.getString("COLUMN_NAME"));
		}

		// generate some prepared statements
		String insertStatement = "INSERT INTO " + table + " ("
				+ slots[0].getName();
		for (int i = 1; i < slots.length; i++) {
			insertStatement += "," + slots[i].getName();
		}
		insertStatement += ") VALUES (?";
		for (int i = 1; i < slots.length; i++) {
			insertStatement += ",?";
		}
		insertStatement += ")";

		String updateStatement = "UPDATE " + table + " SET "
				+ slots[0].getName() + "=?";
		for (int i = 1; i < slots.length; i++) {
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
		String lookupStatement = "SELECT * FROM " + table
				+ " WHERE ";
		firstKey = true;
		for (String key : keys) {
			if (!firstKey) {
				lookupStatement += " AND ";
			}
			firstKey = false;
			lookupStatement += key + "=?";
		}
		PreparedStatement inserter = conn
				.prepareStatement(insertStatement);
		PreparedStatement updater = conn
				.prepareStatement(updateStatement);
		PreparedStatement lookuper = conn
				.prepareStatement(lookupStatement); // any
		// better
		// name ;) ?

		// iterate over our facts
		for (int i = 0; i < thirdParam.getListCount(); i++) {
			Fact actFact = (Fact) engine
					.getFactById((thirdParam.getListValue(i)
							.getFactIdValue()));

			// check whether to update or to insert
			boolean insert = true;
			if (keys.size() > 0) {
				int keyindex = 1;
				for (String key : keys) {
					lookuper
							.setObject(keyindex++, actFact
									.getSlotValue(key)
									.getObjectValue());
				}
				insert = !lookuper.executeQuery().next();
			}

			PreparedStatement actor = insert ? inserter
					: updater;

			// TODO: Check for the right deftemplate
			for (int j = 1; j <= slots.length; j++) {
				// TODO: Typechecking?!
				Object o = actFact.getSlotValue(
						slots[j - 1].getName())
						.getObjectValue();
				
				// we needs to convert a GregorianCalendar to a Date
				if (o instanceof GregorianCalendar) {
						o = ((GregorianCalendar)o).getTime();
				}
				actor.setObject(j, o);
			}
			if (!insert) {
				int j = slots.length + 1;
				for (String key : keys) {
					Object o = actFact.getSlotValue(key).getObjectValue();
					
					// we needs to convert a GregorianCalendar to a Date
					if (o instanceof GregorianCalendar) {
						o = ((GregorianCalendar)o).getTime();
					}
					
					actor.setObject(j++, o);
				}
			}
			actor.execute();
		}
		return JamochaValue.newBoolean(true);
	}

	private JamochaValue method_import(Rete engine, JamochaValue conditions, String table, Deftemplate template, Slot[] slots, Connection conn) throws SQLException, AssertException {
		StringBuffer statementString = new StringBuffer();
		statementString.append("SELECT ");
		statementString.append(slots[0].getName());
		for (int i = 1; i < slots.length; i++) {
			statementString.append(",");
			statementString.append(slots[i].getName());
		}
		statementString.append(" FROM ");
		statementString.append(table);
		
		for (int i = 0 ; i < conditions.getListCount() ; i++ ) {
			Fact actCondition = (Fact) engine
			.getFactById((conditions.getListValue(i)
					.getFactIdValue()));
			String slotname = actCondition.getSlotValue("SlotName").getStringValue();
			String operator = actCondition.getSlotValue("BooleanOperator").getStringValue();
			if (i == 0) {
				statementString.append(" WHERE (");
			} else {
				statementString.append(" AND (");	
			}
			statementString.append(slotname);
			statementString.append(operator);
			statementString.append("?) ");
		}
		
		PreparedStatement stmt = conn.prepareStatement(statementString.toString());

		for (int i = 0 ; i < conditions.getListCount() ; i++ ) {
			Fact actCondition = (Fact) engine
			.getFactById((conditions.getListValue(i)
					.getFactIdValue()));
			Object value = actCondition.getSlotValue("Value").getObjectValue();
			
			if (value instanceof GregorianCalendar)  {
				GregorianCalendar gregval=((GregorianCalendar)value);
				value = new java.sql.Date( gregval.getTimeInMillis() + gregval.get(gregval.ZONE_OFFSET) );
			}
			stmt.setObject(i+1, value);
		}
		


		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {

			Slot[] rowValues = new Slot[slots.length];
			for (int i = 0; i < slots.length; i++) {
				// TODO: Typechecking?!
				Object o = rs.getObject(slots[i].getName());
				
				// when getting DATETIME-values, we get a Date-object here
				// but JamochaValue detects a given object as DATETIME, iff it is
				// a GregorianCalendar-object. =>
				// if we got a Date, we have to convert it to a GregorianCalendar
				// before putting it into a JamochaValue
				
				if (o instanceof Date){
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime((Date)o);
					o = cal;
				}
				
				JamochaValue val = new JamochaValue(o);
				rowValues[i] = new Slot(slots[i].getName(), val);
			}

			Deffact rowFact = new Deffact(template, null,
					rowValues, -1);
			engine.assertFact(rowFact);
		}
		return JamochaValue.newBoolean(true);
	}
}
