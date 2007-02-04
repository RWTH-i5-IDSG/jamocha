/*
 * Copyright 2002-2007 Peter Lin
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

import java.nio.BufferUnderflowException;
import java.util.NoSuchElementException;

import org.jamocha.rete.Activation;
import org.jamocha.rete.QueueActivation;
import org.jamocha.rete.Strategy;

/**
 * This priorityqueue is based on the BinaryHeap from commons collections.
 * I've changed it to be specific to activations. It's not a general
 * purpose queue. It is built specifically for activation lists to be used
 * by the agenda.
 * @author pete
 *
 */
public class PriorityQueue {

    private final static int DEFAULT_CAPACITY = 13;
    protected int size;
    protected Activation[] elements;
    protected boolean minHeap;
    protected Strategy strategy;

    public PriorityQueue() {
        this(DEFAULT_CAPACITY, true);
    }

    public PriorityQueue(int capacity) {
        this(capacity, true);
    }

    public PriorityQueue(boolean isMinHeap) {
        this(DEFAULT_CAPACITY, isMinHeap);
    }

    public PriorityQueue(boolean isMinHeap, Strategy strat) {
        this(isMinHeap);
        strategy = strat;
    }

    public PriorityQueue(int capacity, boolean minHeap) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity");
        }
        this.minHeap = minHeap;

        elements = new Activation[capacity + 1];
    }

    /**
     * Clears all elements from queue.
     */
    public void clear() {
        elements = new Activation[elements.length];  // for gc
        size = 0;
    }

    /**
     * return true if empty
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * return true if it's full and we need to resize it
     * @return
     */
    public boolean isFull() {
        return elements.length == size + 1;
    }

    public void insert(Activation element) {
        if (isFull()) {
            grow();
        }
        if (minHeap) {
            percolateUpMinHeap(element);
        } else {
            percolateUpMaxHeap(element);
        }
    }

    public Activation peek(){
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return elements[1];
        }
    }

    public Activation pop() throws NoSuchElementException {
        final Activation result = peek();
        elements[1] = elements[size--];

        elements[size + 1] = null;

        if (size != 0) {
            // percolate top element to it's place in tree
            if (minHeap) {
                percolateDownMinHeap(1);
            } else {
                percolateDownMaxHeap(1);
            }
        }

        return result;
    }

    protected void percolateDownMinHeap(final int index) {
        final Activation element = elements[index];
        int hole = index;

        while ((hole * 2) <= size) {
            int child = hole * 2;

            // if we have a right child and that child can not be percolated
            // up then move onto other child
            if (child != size && compare(elements[child + 1], elements[child]) < 0) {
                child++;
            }

            // if we found resting place of bubble then terminate search
            if (compare(elements[child], element) >= 0) {
                break;
            }

            elements[hole] = elements[child];
            hole = child;
        }

        elements[hole] = element;
    }

    /**
     * Percolates element down heap from the position given by the index.
     * <p>
     * Assumes it is a maximum heap.
     *
     * @param index the index of the element
     */
    protected void percolateDownMaxHeap(final int index) {
        final Activation element = elements[index];
        int hole = index;

        while ((hole * 2) <= size) {
            int child = hole * 2;

            // if we have a right child and that child can not be percolated
            // up then move onto other child
            if (child != size && compare(elements[child + 1], elements[child]) > 0) {
                child++;
            }

            // if we found resting place of bubble then terminate search
            if (compare(elements[child], element) <= 0) {
                break;
            }

            elements[hole] = elements[child];
            hole = child;
        }

        elements[hole] = element;
    }

    /**
     * Percolates element up heap from the position given by the index.
     * <p>
     * Assumes it is a minimum heap.
     *
     * @param index the index of the element to be percolated up
     */
    protected void percolateUpMinHeap(final int index) {
        int hole = index;
        Activation element = elements[hole];
        while (hole > 1 && compare(element, elements[hole / 2]) < 0) {
            // save element that is being pushed down
            // as the element "bubble" is percolated up
            final int next = hole / 2;
            elements[hole] = elements[next];
            hole = next;
        }
        elements[hole] = element;
    }

    /**
     * Percolates a new element up heap from the bottom.
     * <p>
     * Assumes it is a minimum heap.
     *
     * @param element the element
     */
    protected void percolateUpMinHeap(final Activation element) {
        elements[++size] = element;
        if (element instanceof QueueActivation) {
            ((QueueActivation)element).setQueueIndex(size);
        }
        percolateUpMinHeap(size);
    }

    /**
     * Percolates element up heap from from the position given by the index.
     * <p>
     * Assume it is a maximum heap.
     *
     * @param index the index of the element to be percolated up
     */
    protected void percolateUpMaxHeap(final int index) {
        int hole = index;
        Activation element = elements[hole];
        
        while (hole > 1 && compare(element, elements[hole / 2]) > 0) {
            // save element that is being pushed down
            // as the element "bubble" is percolated up
            final int next = hole / 2;
            elements[hole] = elements[next];
            hole = next;
        }

        elements[hole] = element;
    }
    
    /**
     * Percolates a new element up heap from the bottom.
     * <p>
     * Assume it is a maximum heap.
     *
     * @param element the element
     */
    protected void percolateUpMaxHeap(final Activation element) {
        elements[++size] = element;
        if (element instanceof QueueActivation) {
            ((QueueActivation)element).setQueueIndex(size);
        }
        percolateUpMaxHeap(size);
    }
    
    /**
     * Compares two objects using the comparator if specified, or the
     * natural order otherwise.
     * 
     * @param left  the first object
     * @param right  the second object
     * @return -ve if a less than b, 0 if they are equal, +ve if a greater than b
     */
    private int compare(Activation left, Activation right) {
        return strategy.compare(left, right);
    }

    /**
     * Increases the size of the heap to support additional elements
     */
    protected void grow() {
        final Activation[] elements = new Activation[this.elements.length * 2];
        System.arraycopy(elements, 0, elements, 0, elements.length);
        this.elements = elements;
    }

    /**
     * Returns a string representation of this heap.  The returned string
     * is similar to those produced by standard JDK collections.
     *
     * @return a string representation of this heap
     */
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        sb.append("[ ");

        for (int i = 1; i < size + 1; i++) {
            if (i != 1) {
                sb.append(", ");
            }
            sb.append(elements[i]);
        }

        sb.append(" ]");

        return sb.toString();
    }


    /**
     * Returns an iterator over this heap's elements.
     *
     * @return an iterator over this heap's elements
     */
    public Iterator iterator() {
        return new Iterator() {

            private int index = 1;
            private int lastReturnedIndex = -1;

            public boolean hasNext() {
                return index <= size;
            }

            public Activation next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturnedIndex = index;
                index++;
                return elements[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                elements[ lastReturnedIndex ] = elements[ size ];
                elements[ size ] = null;
                size--;  
                if( size != 0 && lastReturnedIndex <= size) {
                    int compareToParent = 0;
                    if (lastReturnedIndex > 1) {
                        compareToParent = compare(elements[lastReturnedIndex], 
                            elements[lastReturnedIndex / 2]);  
                    }
                    if (minHeap) {
                        if (lastReturnedIndex > 1 && compareToParent < 0) {
                            percolateUpMinHeap(lastReturnedIndex); 
                        } else {
                            percolateDownMinHeap(lastReturnedIndex);
                        }
                    } else {  // max heap
                        if (lastReturnedIndex > 1 && compareToParent > 0) {
                            percolateUpMaxHeap(lastReturnedIndex); 
                        } else {
                            percolateDownMaxHeap(lastReturnedIndex);
                        }
                    }          
                }
                index--;
                lastReturnedIndex = -1; 
            }

        };
    }


    /**
     * Adds an object to this heap.
     * @param object  the object to add
     * @return true, always
     */
    public boolean add(Activation object) {
        insert(object);
        return true;
    }

    /**
     * Returns the priority element.
     * @return the priority element
     * @throws BufferUnderflowException if this heap is empty
     */
    public Activation get() {
        try {
            return peek();
        } catch (NoSuchElementException e) {
            throw new BufferUnderflowException();
        }
    }

    /**
     * Removes the priority element. Same as {@link #pop()}.
     *
     * @return the removed priority element
     * @throws BufferUnderflowException if this heap is empty
     */
    public Activation remove() {
        try {
            return pop();
        } catch (NoSuchElementException e) {
            throw new BufferUnderflowException();
        }
    }
    
    public Activation remove(int index) {
        return null;
    }
    
    /**
     * Returns the number of elements in this heap.
     *
     * @return the number of elements in this heap
     */
    public int size() {
        return size;
    }

}

