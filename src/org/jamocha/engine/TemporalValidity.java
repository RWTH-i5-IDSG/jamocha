package org.jamocha.engine;

public interface TemporalValidity {

	public class EventPoint {
		
		public enum Type {START, STOP};
		
		long timestamp;
		
		Type type;
		
	}
	
	EventPoint getNextEvent(long from);
	
}
