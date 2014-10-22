/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha;

import java.io.InputStream;
import java.util.Queue;

import org.jamocha.dn.Network;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.Warning;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class Jamocha {
	
	Network network;
	
	public Jamocha() {
		network = new Network();
	}
	
	public Queue<Warning> parse(InputStream inputStream) throws ParseException {
		SFPParser parser = new SFPParser(inputStream);
		SFPVisitorImpl visitor = new SFPVisitorImpl(network, network);
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

}
