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

import java.util.Collection;
// import java.util.Iterator;
import java.util.Set;

/**
 * @author Peter Lin
 *
 */
public abstract class AbstractMap implements Map {

	static final int MAX_CAPACITY = 1 << 30;

	protected int size;

	protected int threshold;

	protected float loadFactor;

	protected ObjectComparator comparator;

	protected Entry[] table;

	private EntryIterator eIterator;
	
	/**
	 * 
	 */
	public AbstractMap(final int capacity, final float factor) {
		super();
        this.loadFactor = factor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];
        this.comparator = EqualityEquals.getInstance();
	}

	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}
	
	public abstract boolean containsKey(Object key);

	public abstract Object get(Object key);

	public abstract Object put(Object key, Object value);

	public abstract Object remove(Object key);

	/**
	 * clear aggressively clears the table and nulls the
	 * references.
	 */
	public void clear() {
		for (int idx=0; idx < this.table.length; idx++) {
			if (this.table[idx] != null) {
				// we clear the table
				Entry e = this.table[idx];
				e.clear();
			}
		}
		this.table = null;
		this.eIterator.reset();
	}

	public Iterator keyIterator() {
		if (this.eIterator == null) {
			this.eIterator = new EntryIterator(this);
		}
		this.eIterator.reset();
		return this.eIterator;
	}

    protected int indexOf(final int hashCode, final int dataSize) {
		return hashCode & (dataSize - 1);
	}

    protected void resize(final int newCapacity) {
        final Entry[] oldTable = this.table;
        final int oldCapacity = oldTable.length;
        if ( oldCapacity == AbstractMap.MAX_CAPACITY ) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }

        final Entry[] newTable = new Entry[newCapacity];

        for ( int i = 0; i < this.table.length; i++ ) {
            Entry entry = this.table[i];
            if ( entry == null ) {
                continue;
            }
            this.table[i] = null;
            Entry next = null;
            while ( entry != null ) {
                next = entry.getNext();

                final int index = indexOf( entry.hashCode(),
                                           newTable.length );
                entry.setNext( newTable[index] );
                newTable[index] = entry;

                entry = next;
            }
        }

        this.table = newTable;
        this.threshold = (int) (newCapacity * this.loadFactor);
    }
    
	/**
	 * Internal interface for comparing objects
	 * @author pete
	 *
	 */
	public interface ObjectComparator {
		public int hashCodeOf(Object object);

		public int rehash(int hashCode);

		public boolean equal(Object object1, Object object2);
	}

	public static class InstanceEquals implements ObjectComparator {
		public static ObjectComparator INSTANCE = new InstanceEquals();

		public static ObjectComparator getInstance() {
			return InstanceEquals.INSTANCE;
		}

		public int hashCodeOf(final Object key) {
			return rehash(key.hashCode());
		}

		public int rehash(int h) {
			h += ~(h << 9);
			h ^= (h >>> 14);
			h += (h << 4);
			h ^= (h >>> 10);
			return h;
		}

		public boolean equal(final Object object1, final Object object2) {
			return object1 == object2;
		}
	}

	public static class EqualityEquals implements ObjectComparator {
		public static ObjectComparator INSTANCE = new EqualityEquals();

		public static ObjectComparator getInstance() {
			return EqualityEquals.INSTANCE;
		}

		public int hashCodeOf(final Object key) {
			return rehash(key.hashCode());
		}

		public int rehash(int h) {
			h += ~(h << 9);
			h ^= (h >>> 14);
			h += (h << 4);
			h ^= (h >>> 10);
			return h;
		}

		public boolean equal(final Object object1, final Object object2) {
			return object1.equals(object2);
		}
	}

	public static class EntryIterator implements Iterator {

		private AbstractMap hashMap;

		private Entry[] table;

		private int row;

		private int length;

		private Entry entry;
		
		private Entry next;

		public EntryIterator(final AbstractMap map) {
			this.hashMap = map;
		}

		public Object next() {
            if ( this.entry == null ) {
                // keep skipping rows until we come to the end, or find one that is populated
                while ( this.entry == null ) {
                    this.row++;
                    if ( this.row == this.length ) {
                        return null;
                    }
                    this.entry = this.table[this.row];
                }
            } else {
                this.entry = this.entry.getNext();
                if ( this.entry == null ) {
                    this.entry = (Entry)next();
                }
            }

            return this.entry;
		}

		public void remove() {
			hashMap.remove(this.entry);
		}

		public void reset() {
			this.table = this.hashMap.table;
			this.length = this.table.length;
			this.row = -1;
			this.entry = null;
			this.next = null;
		}
	}
}
