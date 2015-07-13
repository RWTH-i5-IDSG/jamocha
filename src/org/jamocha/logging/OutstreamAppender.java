/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.logging;

import java.io.OutputStream;
import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;

import lombok.Value;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class OutstreamAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
	private static final long serialVersionUID = 6337016117146419220L;
	static int instanceCount = 0;

	private OutstreamAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter,
			final OutputStreamManager manager, final boolean ignoreExceptions) {
		super(name, layout, filter, ignoreExceptions, true, manager);
	}

	public static OutstreamAppender newInstance(final OutputStream outputStream,
			final Layout<? extends Serializable> layout, final Filter filter) {
		return newInstance(outputStream, layout, filter, false);
	}

	public static OutstreamAppender newInstance(final OutputStream outputStream,
			final Layout<? extends Serializable> layout, final Filter filter, final boolean follow) {
		final String name = "OutstreamAppender" + ++instanceCount;
		final OutstreamAppender outstreamAppender =
				new OutstreamAppender(name, layout, filter, getManager(follow, name, outputStream, layout), true);
		outstreamAppender.start();
		return outstreamAppender;
	}

	@Value
	private static class FactoryData {
		private final OutputStream os;
		private final String type;
		private final Layout<? extends Serializable> layout;
	}

	private static class MyOutputStreamManager extends OutputStreamManager {
		protected MyOutputStreamManager(final OutputStream os, final String streamName, final Layout<?> layout) {
			super(os, streamName, layout);
		}
	}

	private static ManagerFactory<OutputStreamManager, FactoryData> factory =
			(final String name, final FactoryData data) -> new MyOutputStreamManager(data.os, data.type, data.layout);

	private static OutputStreamManager getManager(final boolean follow, final String name,
			final OutputStream outputStream, final Layout<? extends Serializable> layout) {
		return OutputStreamManager.getManager(name + '.' + follow, new FactoryData(outputStream, name, layout),
				factory);
	}
}
