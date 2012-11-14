package org.jamocha.engine.nodes;

public interface Memory {
	// Speichert Fakttupelmengen
	// Verfügt ggf. über verschiedene Indizes <- Entscheidung bei Netzkonstruktion
	// Nutzt Indizes bei entsprechenden Anfragen
	
	public void flush();
}
