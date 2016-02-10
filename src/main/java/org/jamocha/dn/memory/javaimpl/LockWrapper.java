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
package org.jamocha.dn.memory.javaimpl;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import lombok.experimental.UtilityClass;
import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
public final class LockWrapper {
    static final long TRY_LOCK_TIMEOUT = 1L;
    static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private static boolean readLock(final ReadWriteLock readWriteLock) {
        try {
            return readWriteLock.readLock().tryLock(TRY_LOCK_TIMEOUT, TIME_UNIT);
        } catch (final InterruptedException e) {
            return false;
        }
    }

    public static final class ReadLockWrapper implements AutoCloseable {
        final ReadWriteLock lock;

        private ReadLockWrapper(final ReadWriteLock lock) {
            this.lock = lock;
        }

        public static ReadLockWrapper withTimeout(final ReadWriteLock lock) throws CouldNotAcquireLockException {
            if (!readLock(lock)) {
                throw new CouldNotAcquireLockException();
            }
            return new ReadLockWrapper(lock);
        }

        public static ReadLockWrapper withoutTimeout(final ReadWriteLock lock) {
            lock.readLock().lock();
            return new ReadLockWrapper(lock);
        }

        @Override
        public void close() {
            this.lock.readLock().unlock();
        }
    }

    public static class WriteLockWrapper implements AutoCloseable {
        final ReadWriteLock lock;

        public WriteLockWrapper(final ReadWriteLock lock) {
            this.lock = lock;
            lock.writeLock().lock();
        }

        @Override
        public void close() {
            this.lock.writeLock().unlock();
        }
    }

    public static class MultipleReadLockWrapper implements AutoCloseable {
        final List<ReadWriteLock> locks;

        public MultipleReadLockWrapper(final Edge originEdge) throws CouldNotAcquireLockException {
            this(Arrays.stream(originEdge.getTargetNode().getIncomingEdges()).filter(e -> e != originEdge)
                    .map(e -> ((MemoryHandlerMain) e.getSourceNode().getMemory()).lock).collect(toList()));
        }

        public MultipleReadLockWrapper(final List<ReadWriteLock> locks) throws CouldNotAcquireLockException {
            this.locks = locks;
            // acquire all locks
            final ListIterator<ReadWriteLock> iter = locks.listIterator();
            while (iter.hasNext()) {
                // if one of the locks can not be acquired
                if (!readLock(iter.next())) {
                    // go backwards skipping the current element
                    iter.previous();
                    // release all locks already acquired
                    while (iter.hasPrevious()) {
                        iter.previous().readLock().unlock();
                    }
                    throw new CouldNotAcquireLockException();
                }
            }
        }

        @Override
        public void close() {
            for (final ReadWriteLock lock : this.locks) {
                lock.readLock().unlock();
            }
        }
    }
}
