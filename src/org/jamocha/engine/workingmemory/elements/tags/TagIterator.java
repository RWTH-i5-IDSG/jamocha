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