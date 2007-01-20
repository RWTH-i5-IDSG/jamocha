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
package woolfel.examples.model;

/**
 * @author Peter Lin
 *
 * A test interface for Account object
 */
public interface IAccount {
    String getTitle();
    void setTitle(String val);
    String getFirst();
    void setFirst(String val);
    String getLast();
    void setLast(String val);
    String getMiddle();
    void setMiddle(String val);
    String getOfficeCode();
    void setOfficeCode(String val);
    String getRegionCode();
    void setRegionCode(String val);
    String getStatus();
    void setStatus(String val);
    String getAccountId();
    void setAccountId(String val);
    String getAccountType();
    void setAccountType(String val);
    String getUsername();
    void setUsername(String val);
    String getAreaCode();
    void setAreaCode(String val);
    String getExchange();
    void setExchange(String val);
    String getNumber();
    void setNumber(String val);
    String getExt();
    void setExt(String val);
}
