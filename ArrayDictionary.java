package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;
    private int size;

    // You may add extra fields or helper methods though!

    public ArrayDictionary() {
        this.pairs = new Pair[10];
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     * <p>
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
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
