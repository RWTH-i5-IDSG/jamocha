/*
 * Copyright 2002-2007 Peter Lin
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
package org.jamocha.rete;

import java.io.Serializable;

/**
 * @author Peter Lin
 *
 * ActivationList defines the basic operations for an activation list. This
 * makes it easier to experiment with different ways of implementing an
 * activation list. The potential methods are queue, priorityQueue, stack
 * and linkedlist.
 * Since I haven't decided on the approach, using an interface will allow me
 * to replace the implementation later on. Rather than guess, my plan is to
 * implement different versions and benchmark them. This way, I can use the
 * one that works the better.
 */
public interface ActivationList extends Serializable {
    /**
     * Depending on whether lazy is set or not, the activation list may
     * assume the activations are ordered by priority and should just
     * return the first or last activation. in the case where the agenda
     * is lazy, it will need to compare the evaluations.
     * @return
     */
    Activation nextActivation();

    /**
     * Add a new activation to the list
     * @param act
     */
    void addActivation(Activation act);

    /**
     * Remove a given activation from the list
     * @param act
     * @return
     */
    Activation removeActivation(Activation act);

    /**
     * In order for strategies to prioritize the activations, we have
     * to expose the underlying list.
     * @return
     */
    void clear();

    /**
     * Every module will have a strategy. when the module creates an
     * activation list, it should set the strategy.
     * @param strat
     */
    void setStrategy(Strategy strat);

    /**
     * If an activation list is lazy, it will delay the compare until
     * nextActivation is called.
     * @param lazy
     */
    void setLazy(boolean lazy);

    /**
     * In some cases, if most of the activations will be removed, it makes
     * sense to do lazy comparison. This means that any strategy could
     * potentially work lazily
     * @return
     */
    boolean isLazy();
    
    /**
     * number of activation in the list
     * @return
     */
    int size();
    
    /**
     * sometimes we need to clone the list, so that users can see what is
     * in the activation list or print it out.
     * @return
     */
    ActivationList clone();
}
