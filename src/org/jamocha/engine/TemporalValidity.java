package org.jamocha.engine;

public interface TemporalValidity {

	public class EventPoint {
		
		public enum Type {START, STOP};
		
		private long timestamp;
		
		private Type type;
		
		public EventPoint(Type type, long timestamp) {
			this.type = type;
			this.timestamp = timestamp;
		}
		
		public long getTimestamp() {
			return timestamp;
		}
		
		public Type getType() {
			return type;
		}
		
		public String toString() {
			return "[EventPoint; Timestamp="+timestamp+"; Type="+type+"]";
		}
		
	}
	
	EventPoint getNextEvent(long from);
	
}
