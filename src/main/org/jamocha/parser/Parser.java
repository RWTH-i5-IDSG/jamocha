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
package org.jamocha.parser;

/**
 * @author Peter Lin
 *
 * Parser is a simple interface defining the basic parser operation
 */
public interface Parser {
    /**
     * Method is meant for cases where the system is parsing an
     * external file.
     * @return
     */
    Object parse();
    /**
     * Method is meant to parse a discrete amount of input. The
     * method takes Object input. It is up to the implementing
     * class to determine if the input is raw text, inputstream or
     * some other wrapper.
     * @param input
     * @return
     */
    Object parse(Object input);
}
