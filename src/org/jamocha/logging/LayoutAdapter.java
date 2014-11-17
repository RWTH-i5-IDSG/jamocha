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
package org.jamocha.logging;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.core.util.Charsets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class LayoutAdapter {
	public static PatternLayout createLayout(final Configuration config, final boolean plain) {
		return PatternLayout.createLayout(plain ? PatternLayout.DEFAULT_CONVERSION_PATTERN
				: PatternLayout.SIMPLE_CONVERSION_PATTERN, config, (RegexReplacement) null, Charsets.UTF_8, true, true,
				"", "");
	}

	public static PatternLayout createLayout(final Configuration config) {
		return createLayout(config, true);
	}
}
