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
package org.jamocha.dn;

import org.jamocha.dn.nodes.Node.TokenQueue;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link Scheduler} to process {@linkplain Runnable runnables} in order of arrival in only one
 * {@link Thread thread}. The Scheduler has to be started by calling {@link #run()}.
 *
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class PlainScheduler implements Scheduler, Runnable {

    final LinkedList<TokenQueue> workQueue = new LinkedList<>();

    private interface State {
        void run();
    }

    final private State ACTIVE_STATE = new State() {

        @Override
        public void run() {
            while (!workQueue.isEmpty()) {
                workQueue.poll().run();
            }
        }
    };

    final private State INACTIVE_STATE = new State() {

        @Override
        public void run() {

        }
    };

    private State state = ACTIVE_STATE;

    @Override
    public void enqueue(final TokenQueue runnable) {
        this.workQueue.add(runnable);
    }

    @Override
    public void activate() {
        state = ACTIVE_STATE;
    }

    @Override
    public void deactivate() {
        state = INACTIVE_STATE;
    }

    /**
     * Processes all enqueued {@link Runnable Runnables} in order of arrival and return when queue
     * is empty.
     */
    @Override
    public void run() {
        state.run();
    }

    /**
     * If there is a {@link Runnable} in the queue, process the first and return true, otherwise
     * return false.
     *
     * @return true iff a {@link Runnable} was processed
     */
    public boolean runOneJob() {
        if (this.workQueue.isEmpty())
            return false;
        this.workQueue.poll().run();
        return true;
    }

    @Override
    public void waitForNoUnfinishedJobs() {
        run();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public List<TokenQueue> shutdownNow() {
        return workQueue;
    }
}
