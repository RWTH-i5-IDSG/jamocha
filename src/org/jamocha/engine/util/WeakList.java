package org.jamocha.engine.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class WeakList<T> {
	private final List<WeakReference<T>> inputs;
	private final ReferenceQueue<T> referenceQueue = new ReferenceQueue<>();

	/**
	 * Constructs a new WeakList using an ArrayList with its default ctor
	 */
	public WeakList() {
		this(new ArrayList<WeakReference<T>>());
	}

	private WeakList(final List<WeakReference<T>> emptyList) {
		this.inputs = emptyList;
	}

	public static <T> WeakList<T> weakArrayList() {
		return new WeakList<T>(new ArrayList<WeakReference<T>>());
	}

	public static <T> WeakList<T> weakArrayList(final int initialCapacity) {
		return new WeakList<T>(new ArrayList<WeakReference<T>>(initialCapacity));
	}

	public static <T> WeakList<T> weakVector() {
		return new WeakList<T>(new Vector<WeakReference<T>>());
	}

	public static <T> WeakList<T> weakVector(final int initialCapacity) {
		return new WeakList<T>(new Vector<WeakReference<T>>(initialCapacity));
	}

	public static <T> WeakList<T> weakStack() {
		return new WeakList<T>(new Stack<WeakReference<T>>());
	}

	public static <T> WeakList<T> weakLinkedList() {
		return new WeakList<T>(new LinkedList<WeakReference<T>>());
	}

	public List<WeakReference<T>> get() {
		Reference<? extends T> reference = this.referenceQueue.poll();
		while (null != reference) {
			this.inputs.remove(reference);
			reference = this.referenceQueue.poll();
		}
		return this.inputs;
	}

	public void append(final WeakReference<T> nodeInput) {
		this.inputs.add(new WeakReference<>(nodeInput.get(),
				this.referenceQueue));
	}
}