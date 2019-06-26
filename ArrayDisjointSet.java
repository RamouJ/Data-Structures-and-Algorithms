package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;

/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private int index;
    private IDictionary<T, Integer> dict;

    // However, feel free to add more fields and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        this.index = 0;
        this.dict = new ChainedHashDictionary<>();
        this.pointers = new int[10];
    }

    @Override
    public void makeSet(T item) {
        if (dict.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        if (index == pointers.length - 1) {
            int[] tempArray = new int[pointers.length * 2];
            for (int i = 0; i < pointers.length; i++) {
                tempArray[i] = pointers[i];
            }
            pointers = tempArray;
        }
        pointers[index] = -1;
        dict.put(item, index);
        index++;
    }

    @Override
    public int findSet(T item) {
        if (!dict.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        IList<Integer> list = new DoubleLinkedList<>();

        int i = dict.get(item);

        while (pointers[i] >= 0) {
            list.add(i);
            i = pointers[i];
        }
        if (!list.isEmpty()) {
            compression(list, i);
        }
        return i;
    }

    private void compression(IList<Integer> list, int root) {
        for (int i : list) {
            pointers[i] = root;
        }
    }

    @Override
    public void union(T item1, T item2) {
        if (!dict.containsKey(item1) || !dict.containsKey(item2)) {
            throw new IllegalArgumentException();
        }

        int root1 = findSet(item1);
        int root2 = findSet(item2);

        if (root1 == root2) {
            return;
        }

        if (pointers[root1] == pointers[root2]) {
            pointers[root1] = pointers[root1] - 1;
            pointers[root2] = root1;
        } else if (pointers[root1] < pointers[root2]) {
            pointers[root2] = root1;
        } else {
            pointers[root1] = root2;
        }
    }
}
