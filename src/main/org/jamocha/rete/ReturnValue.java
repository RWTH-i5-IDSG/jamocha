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
package org.jamocha.rete;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Peter Lin
 *
 * ReturnValue defines the base methods to get the value and type of
 * the return value. Since users will be able to use CLIPS syntax
 * to define functions, we provide this functionality.
 */
public interface ReturnValue extends Serializable {
    int getValueType();
    
    Object getValue();
    
    String getStringValue();
    
    boolean getBooleanValue() throws ClassCastException;
    
    int getIntValue() throws NumberFormatException;
    
    short getShortValue() throws NumberFormatException;
    
    long getLongValue() throws NumberFormatException;
    
    float getFloatValue() throws NumberFormatException;
    
    double getDoubleValue() throws NumberFormatException;

    BigInteger getBigIntegerValue() throws NumberFormatException;
    
    BigDecimal getBigDecimalValue() throws NumberFormatException;
}
