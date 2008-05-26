/*
 * Copyright 2002-2007 Peter Lin
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
package org.jamocha.parser;

import java.util.Calendar;
import java.util.Date;

public class ParserUtils {

	public static String getStringLiteral(String text) {
		if (text != null && text.length() > 1) {
			if (text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"') {
				StringBuffer buf = new StringBuffer();
				int len = text.length() - 1;
				boolean escaping = false;
				for (int i = 1; i < len; i++) {
					char ch = text.charAt(i);
					if (escaping) {
						buf.append(ch);
						escaping = false;
					} else if (ch == '\\') {
						escaping = true;
					} else {
						buf.append(ch);
					}
				}
				return buf.toString();
			}
		}
		return text;
	}

	public static String escapeStringLiteral(String text) {
		StringBuilder buffer = new StringBuilder();
		for (char chr : text.toCharArray()) {
			if (chr == '"' || chr == '\\') {
				buffer.append('\\');
			}
			buffer.append(chr);
		}
		return buffer.toString();
	}

	public static String dateToString(Date date) {
		StringBuilder res = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		res.append(cal.get(Calendar.YEAR)).append("-");
		int month = cal.get(Calendar.MONTH) + 1;
		res.append((month < 10) ? "0" + month : month).append("-");
		int day = cal.get(Calendar.DAY_OF_MONTH);
		res.append((day < 10) ? "0" + day : day).append(" ");
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		res.append((hour < 10) ? "0" + hour : hour).append(":");
		int minute = cal.get(Calendar.MINUTE);
		res.append((minute < 10) ? "0" + minute : minute).append(":");
		int second = cal.get(Calendar.SECOND);
		res.append((second < 10) ? "0" + second : second);
		res.append(cal.get(Calendar.ZONE_OFFSET));
		return res.toString();
	}

	public static long dateToLong(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTimeInMillis();
	}

}
