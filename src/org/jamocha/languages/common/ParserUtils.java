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

package org.jamocha.languages.common;

import java.util.Calendar;
import java.util.Date;

public class ParserUtils {

	public static String getStringLiteral(final String text) {
		if (text != null && text.length() > 1) {
			if (text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"') {
				final StringBuffer buf = new StringBuffer();
				final int len = text.length() - 1;
				boolean escaping = false;
				for (int i = 1; i < len; i++) {
					final char ch = text.charAt(i);
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

	public static String escapeStringLiteral(final String text) {
		final StringBuilder buffer = new StringBuilder();
		for (final char chr : text.toCharArray()) {
			if (chr == '"' || chr == '\\') {
				buffer.append('\\');
			}
			buffer.append(chr);
		}
		return buffer.toString();
	}

	public static String dateToString(final Date date) {
		final StringBuilder res = new StringBuilder();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		res.append(cal.get(Calendar.YEAR)).append("-");
		final int month = cal.get(Calendar.MONTH) + 1;
		res.append((month < 10) ? "0" + month : month).append("-");
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		res.append((day < 10) ? "0" + day : day).append(" ");
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		res.append((hour < 10) ? "0" + hour : hour).append(":");
		final int minute = cal.get(Calendar.MINUTE);
		res.append((minute < 10) ? "0" + minute : minute).append(":");
		final int second = cal.get(Calendar.SECOND);
		res.append((second < 10) ? "0" + second : second);
		res.append(cal.get(Calendar.ZONE_OFFSET));
		return res.toString();
	}

	public static long dateToLong(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTimeInMillis();
	}

}
