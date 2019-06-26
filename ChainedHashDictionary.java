package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    private final double lambda;
    private IDictionary<K, V>[] chains;
    private int size;
    private int tableSize;

    public ChainedHashDictionary() {
        this.size = 0;
        this.tableSize = 10;
        this.chains = makeArrayOfChains(tableSize);
        this.lambda = 0.5;
    }

    public ChainedHashDictionary(double lambda) {
        this.lambda = lambda;
        this.size = 0;
        this.tableSize = 10;
        this.chains = makeArrayOfChains(tableSize);

    }

    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int arraySize) {
        return (IDictionary<K, V>[]) new IDictionary[arraySize];
    }

    @Override
    public V get(K key) {
        int keyHash;
        if (key == null) {
            keyHash = 0;
        } else {
            keyHash = key.hashCode();
        }
        if (keyHash < 0) {
            keyHash = -keyHash;
        }
        int modSize = keyHash % tableSize;
        if (chains[modSize] == null) {
            throw new NoSuchKeyException();
        }
        IDictionary<K, V> dict = chains[modSize];
        return dict.get(key);
    }

    @Override
    public void put(K key, V value) {
        int keyHash;
        if (size/tableSize >= lambda) {
            tableSize = tableSize * 2 + 1;
            size = 0;
            IDictionary<K, V>[] newChains = makeArrayOfChains(tableSize);
            for (int i = 0; i < chains.length; i++) {
                if (chains[i] != null) {
                    for (KVPair<K, V> pair : chains[i]) {
                        K newKey = pair.getKey();
                        V newValue = pair.getValue();
                        keyHash = newKey.hashCode();
                        if (keyHash < 0) {
                            keyHash *= -1;
                        }
                        int modSize = keyHash % tableSize;
                        if (newChains[modSize] == null) {
                            newChains[modSize] = new ArrayDictionary<>();
                        }
                        newChains[modSize].put(newKey, newValue);
                        size++;
                    }
                }
            }
            chains = newChains;
        }
        if (key == null) {
            keyHash = 0;
        } else {
            keyHash = key.hashCode();
        }
        if (keyHash < 0) {
            keyHash *= -1;
        }
        int modSize = keyHash % tableSize;
        if (chains[modSize] == null) {
            chains[modSize] = new ArrayDictionary<>();
        }
        if (chains[modSize].containsKey(key)) {
            chains[modSize].put(key, value);
        } else {
            chains[modSize].put(key, value);
            size++;
        }
    }

    @Override
    public V remove(K key) {
        int keyHash;
        if (key == null) {
            keyHash = 0;
        } else {
            keyHash = key.hashCode();
        }
        if (keyHash < 0) {
            keyHash = -keyHash;
        }
        int modSize = keyHash % tableSize;
        if (chains[modSize] == null) {
            throw new NoSuchKeyException();
        }
        IDictionary<K, V> newDict = chains[modSize];
        if (newDict.containsKey(key)) {
            V valueToReturn = newDict.get(key);
            newDict.remove(key);
            chains[modSize] = newDict;
            size--;
            return valueToReturn;
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        int keyHash;
        if (key == null) {
            keyHash = 0;
        } else {
            keyHash = key.hashCode();
        }
        if (keyHash < 0) {
            keyHash = -keyHash;
        }
        int modSize = keyHash % tableSize;
        if (chains[modSize] == null) {
            return false;
        }
        IDictionary<K, V> newDict = chains[modSize];
        if (newDict.containsKey(key)) {
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        int size;
        int i;
        Iterator<KVPair<K, V>> itr;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.size = chains.length;
            this.i = 0;
            this.itr = null;

            for (int j = 0; j < size - 1; j++) {
                if (chains[j] != null) {
                    i = j;
                    itr = chains[i].iterator();
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            if (itr == null) {
                return false;
            }
            if (itr.hasNext()) {
                return true;
            }
            for (int x = i + 1; x < size; x++) {
                if (chains[x] != null && chains[x].size() != 0) {
                    i = x;
                    itr = chains[i].iterator();
                    return true;
                }
            }

            return false;
        }

        @Override
        public KVPair<K, V> next() {
            if (itr == null) {
                throw new NoSuchElementException();
            }
            if (hasNext()) {
                return itr.next();
            }
            throw new NoSuchElementException();
        }
    }
}
