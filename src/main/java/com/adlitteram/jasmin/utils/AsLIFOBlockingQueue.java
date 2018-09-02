
package com.adlitteram.jasmin.utils;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

class AsLIFOBlockingQueue extends AbstractQueue implements BlockingQueue, java.io.Serializable {
    private final BlockingDeque q;

    AsLIFOBlockingQueue(BlockingDeque q) {
        this.q = q;
    }

    @Override
    public boolean offer(Object o) {
        return q.offerFirst(o);
    }

    @Override
    public Object poll() {
        return q.pollFirst();
    }

    @Override
    public Object remove() {
        return q.removeFirst();
    }

    @Override
    public Object peek() {
        return q.peekFirst();
    }

    @Override
    public Object element() {
        return q.getFirst();
    }

    @Override
    public int size() {
        return q.size();
    }

    @Override
    public boolean isEmpty() {
        return q.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return q.contains(o);
    }

    @Override
    public Iterator iterator() {
        return q.iterator();
    }

    @Override
    public Object[] toArray() {
        return q.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return q.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return q.offerFirst(o);
    }

    @Override
    public boolean remove(Object o) {
        return q.remove(o);
    }

    @Override
    public void clear() {
        q.clear();
    }

    @Override
    public int remainingCapacity() {
        return q.remainingCapacity();
    }

    @Override
    public int drainTo(Collection c) {
        return q.drainTo(c);
    }

    @Override
    public int drainTo(Collection c, int m) {
        return q.drainTo(c, m);
    }

    @Override
    public void put(Object o) throws InterruptedException {
        q.putFirst(o);
    }

    @Override
    public Object take() throws InterruptedException {
        return q.takeFirst();
    }

    @Override
    public boolean offer(Object o, long timeout, TimeUnit unit) throws InterruptedException {
        return q.offerFirst(o, timeout, unit);
    }

    @Override
    public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
        return q.pollFirst(timeout, unit);
    }
}
