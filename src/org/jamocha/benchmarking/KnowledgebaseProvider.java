package org.jamocha.benchmarking;

import org.jamocha.engine.Engine;

public interface KnowledgebaseProvider {
	
	public Engine getProblemInstance(int size, String tempStrat);

}
