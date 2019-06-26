package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainedHashSet<T> implements ISet<T> {
    private IDictionary<T, Boolean> map;

    public ChainedHashSet() {
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
        private Iterator<KVPair<T, Boolean>> iter;

        public SetIterator(Iterator<KVPair<T, Boolean>> iter) {
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
