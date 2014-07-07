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
package org.jamocha.languages.clips.parser;

import org.jamocha.languages.clips.parser.generated.SFPAndFunction;
import org.jamocha.languages.clips.parser.generated.SFPNotFunction;
import org.jamocha.languages.clips.parser.generated.SFPParserTreeConstants;
import org.jamocha.languages.clips.parser.generated.SimpleNode;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ForallTransformer {

	public static void transform(final SimpleNode node) {
		for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
			final SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			if (SFPParserTreeConstants.JJTFORALLCE == child.getId()) {
				final SimpleNode outerNot =
						new SFPNotFunction(SFPParserTreeConstants.JJTNOTFUNCTION);
				node.jjtAddChild(outerNot, i);
				final SimpleNode outerAnd =
						new SFPAndFunction(SFPParserTreeConstants.JJTANDFUNCTION);
				outerNot.jjtAddChild(outerAnd, 0);
				outerAnd.jjtAddChild(child.jjtGetChild(0), 0);
				final SimpleNode innerNot =
						new SFPNotFunction(SFPParserTreeConstants.JJTNOTFUNCTION);
				outerAnd.jjtAddChild(innerNot, 1);
				if (child.jjtGetNumChildren() > 2) {
					final SimpleNode innerAnd =
							new SFPAndFunction(SFPParserTreeConstants.JJTANDFUNCTION);
					innerNot.jjtAddChild(innerAnd, 0);
					for (int j = 1; j < child.jjtGetNumChildren(); ++j) {
						innerAnd.jjtAddChild(child.jjtGetChild(j), j - 1);
					}
				} else {
					innerNot.jjtAddChild(child.jjtGetChild(1), 0);
				}
				transform(outerNot);
			} else {
				transform(child);
			}
		}
	}
}
