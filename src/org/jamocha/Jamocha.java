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
package org.jamocha;

import java.io.InputStream;
import java.util.Objects;
import java.util.Queue;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.Network;
import org.jamocha.dn.PlainScheduler;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.Warning;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
@Log4j2
public class Jamocha {
	
	@Getter
	final private Network network;

	SFPParser parser;
	final SFPVisitorImpl visitor;

	public Jamocha() {
		network = new Network(Integer.MAX_VALUE, new PlainScheduler());
		visitor = new SFPVisitorImpl(network, network);
	}
	
	public void loadParser(InputStream inputStream) {
		parser = new SFPParser(inputStream);
	}

	public Pair<Queue<Warning>, String> parse() throws ParseException {
		final SFPStart n = parser.Start();
		if (null == n) return null;
		final String value = Objects.toString(n.jjtAccept(visitor, null));
		return Pair.of(visitor.getWarnings(), value);
	}

	public void shutdown() {
		network.shutdown();
	}
	
	public static void main(final String[] args) {
		Jamocha jamocha = new Jamocha();
		jamocha.loadParser(System.in);

		while (true) {
			try {
				System.out.print("SFP> ");
				final Pair<Queue<Warning>,String> parserReturn = jamocha.parse();
				if (null == parserReturn)
					System.exit(0);
				final String expression = parserReturn.getRight();
				if (!(null == expression || "null".equals(expression))) {
					log.info(expression);
				}
				final Queue<Warning> warnings = parserReturn.getLeft();
				warnings.forEach(w -> log.warn("Warning: " + w.getMessage()));
			} catch (final ParseException e) {
				log.catching(e);
			} catch (final Throwable e) {
				log.catching(e);
			}
		}
	}

}
