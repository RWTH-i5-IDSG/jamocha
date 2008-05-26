
/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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
package org.jamocha.communication.jsr94;

import org.jamocha.engine.Engine;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class JamochaTransactionBasedSession {

	private Engine engine;

	private Engine backup;

	public JamochaTransactionBasedSession() {
		engine = new Engine();
		commit();
	}

	public void commit() {
		// TODO implement me or a clone method in the engine
		// backup = engine.clone();
	}

	public void rollback() {
		// TODO implement me or a clone method in the engine
		// engine = backup.clone();
	}

	public Engine getEngine() {
		return engine;
	}

	public void release() {
		engine.dispose();
		engine = null;
		if (backup != null) {
			backup.dispose();
			backup = null;
		}
	}

}
