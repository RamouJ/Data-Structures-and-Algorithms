package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    private Pair<K, V>[] pairs;
    private int size;

    public ArrayDictionary() {
        this.pairs = new Pair[10];
        this.size = 0;
    }


    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        if (size == 0) {
            throw new NoSuchKeyException();
        } else {
            for (int i = 0; i < size; i++) {
                if (pairs[i].key != null && key != null) {
                    if (pairs[i].key.equals(key)) {
                        return pairs[i].value;
                    }
                } else {
                    if (pairs[i].key == key) {
                        return pairs[i].value;
                    }
                }
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public void put(K key, V value) {

        if (size >= pairs.length) {
            Pair<K, V>[] tempArray = new Pair[size * 2];
            for (int i = 0; i < pairs.length; i++) {
                tempArray[i] = pairs[i];
            }
            pairs = tempArray;

        }
        if (size == 0) {
            pairs[size] = new Pair<>(key, value);
            size++;
        } else {
            for (int i = 0; i < size; i++) {
                if (key == null || pairs[i].key == null) {
                    if (pairs[i].key == key) {
                        pairs[i].value = value;
                        return;
                    }
                } else {
                    if (pairs[i].key.equals(key)) {
                        pairs[i].value = value;
                        return;
                    }
                }

            }
            pairs[size] = new Pair<>(key, value);
            size++;
        }

    }

    @Override
    public V remove(K key) {
        if (this.size == 0) {
            throw new NoSuchKeyException();
        }

        for (int i = 0; i < size; i++) {
            if (pairs[i].key != null && key != null) {
                if (pairs[i].key.equals(key)) {
                    V value = pairs[i].value;
                    pairs[i] = pairs[size - 1];
                    //setting current pair index to be the next index pair
                    pairs[size - 1] = null;
                    size--;
                    return value;
                }
            } else {
                if (pairs[i].key == key) {
                    V value = pairs[i].value;
                    pairs[i] = pairs[size - 1];
                    //setting current pair index to be the next index pair
                    pairs[size - 1] = null;
                    size--;
                    return value;
                }
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < size; i++) {
            if (pairs[i].key != null && key != null) {
                if (pairs[i].key.equals(key)) {
                    return true;
                }
            } else {
                if (pairs[i].key == key) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
