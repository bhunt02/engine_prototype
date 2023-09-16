package classes.data_structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentSet<E> extends ConcurrentCollection<E> {
    ArrayList<E> set = super.list;

    /** use this to lock for write operations like add/remove */
    private final Lock readLock;
    /** use this to lock for read operations like get/iterator/contains.. */
    private final Lock writeLock;

    {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    @Override
    public boolean add(E e) {
        writeLock.lock();
        try {
            if (!this.set.contains(e)) {
                return set.add(e);
            } else { return false;}
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> e) {
        writeLock.lock();
        boolean success = true;
        try {
            for (E el : e) {
                if (!this.set.contains(el)) {
                    set.add(el);
                } else { success = false;}
            }
            return success;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public <E> E[] toArray(E[] e) {
        readLock.lock();
        try {
            return set.toArray(e);
        } finally {
            readLock.unlock();
        }
    }
}
