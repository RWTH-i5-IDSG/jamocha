/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class RegexMatcher extends BaseMatcher<String> {
	private final String regex;

	public RegexMatcher(final String regex) {
		this.regex = regex;
	}

	@Override
	public boolean matches(final Object o) {
		if (!(o instanceof String))
			return false;
		return ((String) o).matches(regex);
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("matches regex=").appendText(regex);
	}

	public static RegexMatcher matches(final String regex) {
		return new RegexMatcher(regex);
	}
}
