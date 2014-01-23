/*
 * Copyright 2002-2013 The Jamocha Team
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
package org.jamocha.dn.memory;

/**
 * Enum holding all types this system supports.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public enum SlotType {
	/**
	 * Enum value for integer types.
	 */
	LONG(Long.class),
	/**
	 * Enum value for floating point types.
	 */
	DOUBLE(Double.class),
	/**
	 * Enum value for string types.
	 */
	STRING(String.class),
	/**
	 * Enum value for boolean types.
	 */
	BOOLEAN(Boolean.class);

	final private Class<?> javaClass;

	private SlotType(final Class<?> javaClazz) {
		this.javaClass = javaClazz;
	}

	public Class<?> getJavaClass() {
		return this.javaClass;
	}

	/**
	 * Static instance of an empty array of types. Can e.g. be used by functions without parameters
	 * to specify the parameter types.
	 */
	final public static SlotType[] empty = new SlotType[] {};

	@Override
	public String toString() {
		return this.name();
	}
}
