/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.util;

/**
 * @author Peter Lin
 * 
 * A basic HashMap implementation inspired by Mark's HashTable for
 * Drools3. The main difference is this HashMap tries to be compatable
 * with java.util.HashMap. This means it's not as stripped down as
 * the super optimized HashTable mark wrote for drools. Hopefully the
 * extra stuff doesn't make too big of a difference.
 */
public class HashMap extends AbstractMap {

	/**
	 * @param capacity
	 * @param factor
	 */
	public HashMap() {
		super(101, 0.75f);
	}

	public boolean containsKey(Object key) {
        return get(key) != null;
	}

	public Object get(final Object key) {
		final int hashCode = this.comparator.hashCodeOf(key);
		final int index = indexOf(hashCode, this.table.length);

		ObjectEntry current = (ObjectEntry) this.table[index];
		while (current != null) {
			if (hashCode == current.hashCode
					&& this.comparator.equal(key, current.key)) {
				return current.value;
			}
			current = (ObjectEntry) current.getNext();
		}
		return null;
	}

	/**
	 * 
	 */
	public Object put(Object key, Object value) {
		return put(key,value,false);
	}

	public Object put(final Object key, final Object value, boolean checkExists) {
		final int hashCode = this.comparator.hashCodeOf(key);
		final int index = indexOf(hashCode, this.table.length);

		// scan the linked entries to see if it exists
		if (checkExists) {
			Entry current = this.table[index];
			while (current != null) {
				if (hashCode == current.hashCode()
						&& key.equals(current.getKey())) {
					final Object oldValue = current.getValue();
					current.setValue(value);
					return oldValue;
				}
				current = (ObjectEntry) current.getNext();
			}
		}

		// create a new ObjectEntry
		final ObjectEntry entry = new ObjectEntry(key, value, hashCode);
		// in case there is already an entry with the same hashcode,
		// set it as the next entry for the new one. this means the older
		// entries are pushed down the bucket
		entry.next = this.table[index];
		this.table[index] = entry;

		if (this.size++ >= this.threshold) {
			resize(2 * this.table.length);
		}
		return null;
	}

	public Object remove(Object key) {
        final int hashCode = this.comparator.hashCodeOf( key );
        final int index = indexOf( hashCode,
                             this.table.length );

        ObjectEntry previous = (ObjectEntry) this.table[index];
        ObjectEntry current = previous;
        while ( current != null ) {
            final ObjectEntry next = (ObjectEntry) current.getNext();
            if ( hashCode == current.hashCode && this.comparator.equal( key,
                                                                   current.key ) ) {
                if ( previous == current ) {
                    this.table[index] = next;
                } else {
                    previous.setNext( next );
                }
                current.setNext( null );
                this.size--;
                return current.value;
            }
            previous = current;
            current = next;
        }
        return null;
	}

    public static class ObjectEntry implements Entry {
		private Object key;

		private Object value;

		private int hashCode;

		private Entry next;

		public ObjectEntry(final Object key, final Object value,
				final int hashCode) {
			this.key = key;
			this.value = value;
			this.hashCode = hashCode;
		}

		public Object getValue() {
			return this.value;
		}

		public void setValue(Object val) {
			this.value = val;
		}
		
		public Object getKey() {
			return this.key;
		}

		public Entry getNext() {
			return this.next;
		}

		public void setNext(final Entry next) {
			this.next = next;
		}

		public void clear() {
			this.key = null;
			this.value = null;
			this.next.clear();
		}
		
        public int hashCode() {
            return this.hashCode;
        }

		public boolean equals(final Object object) {
			if (object == this) {
				return true;
			}
			final ObjectEntry other = (ObjectEntry) object;
			return this.key.equals(other.key) && this.value.equals(other.value);
		}
	}
}
