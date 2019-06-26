package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    private final double lambda;

    // You MUST use this field to store the contents of your dictionary.
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int size;
    private int tableSize;

    // You're encouraged to add extra fields (and helper methods) though!

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

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     * <p>
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int arraySize) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
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

    /**
     * Hints:
     * <p>
     * 1. You should add extra fields to keep track of your iteration
     * state. You can add as many fields as you want. If it helps,
     * our reference implementation uses three (including the one we
     * gave you).
     * <p>
     * 2. Before you try and write code, try designing an algorithm
     * using pencil and paper and run through a few examples by hand.
     * <p>
     * We STRONGLY recommend you spend some time doing this before
     * coding. Getting the invariants correct can be tricky, and
     * running through your proposed algorithm using pencil and
     * paper is a good way of helping you iron them out.
     * <p>
     * 3. Think about what exactly your *invariants* are. As a
     * reminder, an *invariant* is something that must *always* be
     * true once the constructor is done setting up the class AND
     * must *always* be true both before and after you call any
     * method in your class.
     * <p>
     * Once you've decided, write them down in a comment somewhere to
     * help you remember.
     * <p>
     * You may also find it useful to write a helper method that checks
     * your invariants and throws an exception if they're violated.
     * You can then call this helper method at the start and end of each
     * method if you're running into issues while debugging.
     * <p>
     * (Be sure to delete this method once your iterator is fully working.)
     * <p>
     * Implementation restrictions:
     * <p>
     * 1. You **MAY NOT** create any new data structures. Iterators
     * are meant to be lightweight and so should not be copying
     * the data contained in your dictionary to some other data
     * structure.
     * <p>
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     * instance inside your 'chains' array, however.
     */
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

            // if (itr == null) {
            //     throw new NoSuchElementException();
            // }
            // while (chains[i] == null) {
            //     i++;
            // }
            // this.itr = chains[i].iterator();
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
