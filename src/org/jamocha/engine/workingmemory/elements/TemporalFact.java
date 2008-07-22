/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.workingmemory.elements;

/**
 * TemporalFact is an extension which supports the concepts mentioned by karl.
 * The extension adds the following concepts. 1. expire - expiration time 2.
 * source - URL 3. service type - method used to obtain the fact 4. validity -
 * probability of the facts validity These ideas are useful for semantic web,
 * agents, temporal systems and services.
 * 
 * @author Peter Lin
 * 
 */
public interface TemporalFact extends Fact {
	public static final String EXPIRATION = "expiration-time";
	public static final String SOURCE = "source";
	public static final String SERVICE_TYPE = "service-type";
	public static final String VALIDITY = "validity";

	void setExpirationTime(long time);

	long getExpirationTime();

	void setSource(String url);

	String getSource();

	void setServiceType(String type);

	String getServiceType();

	void setValidity(int valid);

	int getValidity();
}
