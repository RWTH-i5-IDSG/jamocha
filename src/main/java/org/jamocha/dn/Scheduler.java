/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.dn;

import org.jamocha.dn.nodes.Node.TokenQueue;

import java.util.List;

/**
 * This interface declares a scheduler usable by any {@link Network network}. It should take {@link Runnable Runnables}
 * and {@link Runnable#run() run} them (optional in separate threads).
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see Runnable
 */
public interface Scheduler {

    /**
     * Add a {@link Runnable} to be processed by the scheduler.
     *
     * @param runnable
     *         the {@link Runnable} to add to the schedulers queue
     */
    void enqueue(final TokenQueue runnable);

    /**
     * Activate scheduler. In active state the scheduler may process {@link TokenQueue TokenQueues}.
     */
    void activate();

    /**
     * Deactivate scheduler. In inactive state the scheduler may not process any {@link TokenQueue TokenQueues). Running
     * TokenQueues are finished before method returns.
     */
    void deactivate();

    /**
     * Methods returns only if the queue of {@link TokenQueue}s is empty.
     */
    void waitForNoUnfinishedJobs();

    /**
     * Initiate shutdown of the scheduler. All enqueued jobs can still be processed. No new jobs are enqueued.
     */
    void shutdown();

    /**
     * Forces shutdown of the scheduler. Only running jobs are finished.
     *
     * @return the current {@link TokenQueue} queue.
     */
    List<TokenQueue> shutdownNow();
}
