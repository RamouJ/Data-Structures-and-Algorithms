package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.InvalidElementException;

/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int size;
    private IDictionary<T, Integer> map;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        this.size = 0;
        this.heap = makeArrayOfT(10);
        this.map = new ChainedHashDictionary();
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     * <p>
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * upwards from a given index, if necessary.
     */
    private void percolateUp(int index) {
        while (index > 0 && heap[index / NUM_CHILDREN] != null && heap[index / 4].compareTo(heap[index]) > 0) {
            swap(index, index / NUM_CHILDREN);
            map.put(heap[index], index);
            map.put(heap[index / NUM_CHILDREN], index / NUM_CHILDREN);
            percolateUp(index / NUM_CHILDREN);
        }
    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * downwards from a given index, if necessary.
     */

    private void percolateDown(int index) {
        boolean needToPercolate = true;
        if (size == 1) {
            return;
        }
        if (heap[index * NUM_CHILDREN + 1] == null) {
            return;
        }
        T smallestChild = heap[index];
        int smallestIndex = 0;
        for (int i = 1; i <= NUM_CHILDREN; i++) {
            if (NUM_CHILDREN * index + i < size) {
                if (heap[NUM_CHILDREN * index + i].compareTo(smallestChild) < 0) {
                    smallestChild = heap[NUM_CHILDREN * index + i];
                    smallestIndex = NUM_CHILDREN * index + i;
                }
            }
        }
        swap(index, smallestIndex);
        map.put(heap[index], index);
        map.put(heap[smallestIndex], smallestIndex);

        index = smallestIndex;
        for (int i = 1; i <= NUM_CHILDREN; i++) {
            if (NUM_CHILDREN * index + i < size) {
                if (heap[NUM_CHILDREN * index + i].compareTo(smallestChild) < 0) {
                    needToPercolate = true;
                    break;
                }
            }
            needToPercolate = false;
        }
        if (smallestIndex * NUM_CHILDREN + 1 <= size && needToPercolate) {
            percolateDown(smallestIndex);
        }
    }


    /**
     * A method stub that you may replace with a helper method for determining
     * which direction an index needs to percolate and percolating accordingly.
     */
    private void percolate(int index) {
        if (index == 0) {
            percolateDown(index);
        } else {
            int parentIndex = index / 2;
            if (heap[index].compareTo(heap[parentIndex]) < 0) {
                percolateUp(index);
            } else if (heap[index].compareTo(heap[parentIndex]) > 0) {
                percolateDown(index);
            }
        }
    }


    /**
     * A method stub that you may replace with a helper method for swapping
     * the elements at two indices in the 'heap' array.
     */
    private void swap(int a, int b) {
        T temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;

    }

    @Override
    public T removeMin() {
        if (heap.length == 0 || size == 0) {
            throw new EmptyContainerException();
        }
        T itemToReturn = heap[0];
        map.remove(heap[0]);
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        percolateDown(0);
        return itemToReturn;
    }

    @Override
    public T peekMin() {
        if (size >= 1) {
            return heap[0];
        }
        throw new EmptyContainerException();
    }

    @Override
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (map.containsKey(item)) {
            throw new InvalidElementException();
        }
        if (size + 1 > heap.length) {
            T[] tempArray = makeArrayOfT(heap.length * 2);
            for (int i = 0; i < heap.length; i++) {
                tempArray[i] = heap[i];
            }
            heap = tempArray;
        }
        int index = size;
        size++;
        heap[index] = item;
        map.put(item, index);
        percolateUp(index);
    }

    @Override
    public boolean contains(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (map.containsKey(item)) {
            return true;
        }
        return false;
    }

    @Override
    public void remove(T item) {
        int index = 0;
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (!map.containsKey(item)) {
            throw new InvalidElementException();
        }

        index = map.remove(item);

        if (index == size - 1) {
            heap[index] = null;
            size--;
        } else {
            swap(index, size - 1);
            map.put(heap[index], index);
            heap[size-1] = null;
            size--;
            percolate(index);
        }

    }


    @Override
    public void replace(T oldItem, T newItem) {
        int index = 0;
        if (newItem == null) {
            throw new IllegalArgumentException();
        }
        if (!map.containsKey(oldItem) || map.containsKey(newItem)) {
            throw new InvalidElementException();
        }

        index = map.remove(oldItem);
        heap[index] = newItem;
        map.put(newItem, index);
        percolate(index);
    }


    @Override
    public int size() {
        return size;
    }
}
