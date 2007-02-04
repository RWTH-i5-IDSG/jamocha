/**
 * 
 */
package org.jamocha.rete;

import java.util.List;

import org.jamocha.rete.util.PriorityQueue;

/**
 * @author pete
 *
 */
public class QueueActivationList extends PriorityQueue implements
        ActivationList {

    protected Strategy theStrategy = null;

    protected boolean lazy = false;

    /**
     * 
     */
    public QueueActivationList(Strategy strategy) {
        super();
        this.theStrategy = strategy;
    }

    public void addActivation(Activation act) {
        this.add(act);
    }

    public List getList() {
        return null;
    }

    public boolean isAscendingOrder() {
        return true;
    }

    public boolean isLazy() {
        return false;
    }

    public Activation nextActivation() {
        return this.pop();
    }

    public Activation removeActivation(Activation act) {
        return null;
    }

    public void setLazy(boolean lazy) {
    }

    public void setStrategy(Strategy strat) {
        this.theStrategy = strat;
    }

    public ActivationList clone() {
        return null;
    }
}
