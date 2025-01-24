package com.adlitteram.jasmin.utils;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

class AsLIFOBlockingQueue<T> extends AbstractQueue<T> implements BlockingQueue<T>, java.io.Serializable {

    private final BlockingDeque<T> q;

    AsLIFOBlockingQueue(BlockingDeque<T> q) {
        this.q = q;
    }

    @Override
    public boolean offer(T o) {
        return q.offerFirst(o);
    }

    @Override
    public T poll() {
        return q.pollFirst();
    }

    @Override
    public T remove() {
        return q.removeFirst();
    }

    @Override
    public T peek() {
        return q.peekFirst();
    }

    @Override
    public T element() {
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
    public Iterator<T> iterator() {
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
    public boolean add(T o) {
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
    public int drainTo(Collection<? super T> c) {
        return q.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super T> c, int m) {
        return q.drainTo(c, m);
    }

    @Override
    public void put(T o) throws InterruptedException {
        q.putFirst(o);
    }

    @Override
    public T take() throws InterruptedException {
        return q.takeFirst();
    }

    @Override
    public boolean offer(T o, long timeout, TimeUnit unit) throws InterruptedException {
        return q.offerFirst(o, timeout, unit);
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return q.pollFirst(timeout, unit);
    }
}
