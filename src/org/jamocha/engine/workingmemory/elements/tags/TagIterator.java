package org.jamocha.engine.workingmemory.elements.tags;

import java.util.Iterator;
import java.util.List;

public class TagIterator implements Iterator<Tag> {

	private Iterator<Tag> itr;
	
	private Tag nextTag;
	
	private Class<? extends Tag> cls;
	
	public TagIterator(Class<? extends Tag> cls, List<Tag> taglist) {
		itr = taglist.iterator();
		this.cls = cls;
		jumpToNextMatch();
	}
	
	private void jumpToNextMatch() {
		if (itr.hasNext()) {
			Tag t = itr.next();
			if (cls.isInstance(t)) {
				nextTag = t;
			} else jumpToNextMatch();
		} else nextTag = null;
	}
	
	public boolean hasNext() {
		return (nextTag != null);
	}

	public Tag next() {
		Tag result = nextTag;
		jumpToNextMatch();
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}