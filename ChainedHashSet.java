package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see ISet for more details on what each method is supposed to do.
 */
public class ChainedHashSet<T> implements ISet<T> {
    // This should be the only field you need
    private IDictionary<T, Boolean> map;

    public ChainedHashSet() {
        // No need to change this method
        this.map = new ChainedHashDictionary<>();
    }

    @Override
    public void add(T item) {
        map.put(item, true);
    }

    @Override
    public void remove(T item) {
        if (map.containsKey(item)) {
            map.remove(item);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean contains(T item) {
        if (map.containsKey(item)) {
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new SetIterator<>(this.map.iterator());
    }

    private static class SetIterator<T> implements Iterator<T> {
        // This should be the only field you need
        private Iterator<KVPair<T, Boolean>> iter;

        public SetIterator(Iterator<KVPair<T, Boolean>> iter) {
            // No need to change this method.
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            if (iter.hasNext()) {
                return true;
            }
            return false;
        }

        @Override
        public T next() {
            if (hasNext()) {
                return iter.next().getKey();
            }
            throw new NoSuchElementException();
        }
    }
}
