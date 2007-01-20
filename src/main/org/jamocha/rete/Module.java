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
import java.util.Collection;

import org.jamocha.rule.Rule;


/**
 * @author Peter Lin
 *
 * Interface defining a module. A module may contain templates and
 * rules. It is responsible for keeping track of the Activations
 * and adding the activation to the list.
 */
public interface Module extends Serializable {
    /**
     * Add a new activation. Classes implementing the Agenda should
     * check to make sure the activation is new. If it isn't new,
     * don't add it.
     * @param actv
     */
    void addActivation(Activation actv);
    /**
     * Add a new rule to the module. Implementing classes may want
     * to check the status of the rule engine before adding new
     * rules. In the case where rules are added dynamically at
     * runtime, it's a good idea to check the rule engine isn't
     * busy. A important note about this method is the rule
     * should already be compiled to RETE nodes. If the rule isn't
     * compiled, it will not get evaluated.
     * @param rl
     */
    void addRule(Rule rl);
    /**
     * Add a new template to the module
     * @param temp
     */
    void addTemplate(Template temp, Rete engine, WorkingMemory mem);
    /**
     * Clear will remove all the rules, activations, and templates
     * from the module.
     */
    void clear();
    /**
     * Implementing classes need to keep a list of the rules, so
     * that when new rules are added, the module can check to see
     * if a rule with the same name already exists.
     * @param rl
     * @return
     */
    boolean containsRule(Rule rl);
    /**
     * Implementing classes need to keep a list of rules, so that
     * when a new template is declared, the module can check to see
     * if the module already exists.
     * @param key
     * @return
     */
    boolean containsTemplate(Object key);
    /**
     * fireActivations will execute the Activations in the activation
     * list. Implementing classes should make the method synchronized.
     */
    int getActivationCount();
    /**
     * Return a list of all the activation
     * @return
     */
    Collection getAllActivations();
    /**
     * Return the name of the module. The interface doesn't provide
     * any guidelines for the format, but it is a good idea to restrict
     * names without punction marks.
     * @return
     */
    String getModuleName();
    /**
     * Look up the template
     * @param key
     * @return
     */
    Template getTemplate(Defclass key);
    /**
     * look up the template using a string template name
     * @param key
     * @return
     */
    Template getTemplate(String key);
    /**
     * Look up the parent template by the template name
     * @param key
     * @return
     */
    Template findParentTemplate(String key);
    /**
     * Return the Deftemplates in a collection
     * @return
     */
    Collection getTemplates();
    /**
     * return the number of actual deftemplates declared
     * using deftemplate or objects
     * @return
     */
    int getTemplateCount();
    /**
     * Return a list of all the rules in this module
     * @return
     */
    Collection getAllRules();
    /**
     * Return the rule count
     * @return
     */
    int getRuleCount();
    /**
     * remove an activation from the activation list.
     * @param actv
     * @return
     */
    Activation removeActivation(Activation actv);
    /**
     * Remove a rule from the module. The method returns void, since
     * the user should have found the rule they want to remove first.
     * @param rl
     */
    void removeRule(Rule rl, Rete engine, WorkingMemory mem);
    /**
     * Remove a template from the module. The method returns void,
     * since the user should have found the template first.
     * @param temp
     */
    void removeTemplate(Template temp, Rete engine, WorkingMemory mem);
    /**
     * In the event we need to find the rule, this method will look
     * up the name of the rule without the module name. Implementing
     * classes may take the module + rulename for the parameter, it
     * is up to the developer.
     * @param name
     * @return
     */
    Rule findRule(String name);
    /**
     * Method will remove the activation from the module and return it
     * to the engine. The method should only be called when the RHS
     * of the rule should be executed.
     * @return
     */
    Activation nextActivation(Rete engine);
    /**
     * To use a lazy agenda, call the method with true. To turn off
     * lazy agenda, call it with false.
     * @param lazy
     */
    void setLazy(boolean lazy);
}
