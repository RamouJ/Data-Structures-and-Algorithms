package datastructures.concrete;

//import com.sun.java.util.jar.pack.ConstantPool;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 *
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        Node newNode = new Node(back, item, null);
        if (size == 0) {
            front = newNode;
            back = newNode;
        } else {
            back.next = newNode;
            back = back.next;
        }
        size++;
    }

    @Override
    public T remove() {
        Node<T> currentBack = back;
        if (size == 0) {
            throw new EmptyContainerException();
        } else if (size == 1) {
            back = null;
            front = null;
            size--;
            return currentBack.data;
        } else {
            back = back.prev;
            back.next = null;
            size--;
            return currentBack.data;
        }

    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        int positionCount = 0;

        Node<T> current = front;

        if (size == 0) {
            return null;
        } else {
            while (current != null) {
                if (positionCount == index) {
                    return current.data;
                }
                positionCount++;
                current = current.next;
            }
        }
        return null;
    }

    @Override
    public void set(int index, T item) {
        Node newNode = new Node(item);
        Node<T> current = front;
        Node<T> currentBack = back;
        int positionCount = 0;

        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        } else if (size == 1 && index == 0) {
            front = newNode;
            newNode.next = null;
            back = newNode;

            return;
        } else if (index == 0) {
            current.next.prev = newNode;
            newNode.next = current.next;
            front = newNode;
            return;
        } else if (index > 0 && index < size - 1) {
            while (current != null) {
                if (positionCount == index) {
                    current.prev.next = newNode;
                    newNode.next = current.next;

                    newNode.prev = current.prev;
                    current.next.prev = newNode;

                    current.prev = null;
                    current.next = null;

                }
                positionCount++;
                current = current.next;
            }
        } else {
            currentBack.prev.next = newNode;
            newNode.prev = currentBack.prev;
            back = newNode;
        }
    }

    @Override
    public void insert(int index, T item) {

        if (index < 0 || index >= size + 1) {
            throw new IndexOutOfBoundsException();
        }

        int positionCount = 0;
        Node newNode = new Node(item);

        Node<T> current = front;
        Node<T> currentBack = back;

        if (size == 0) {
            front = newNode;
            back = newNode;
            size++;
            return;
        } else if (index == 0) {
            current.prev = newNode;
            newNode.next = current;
            front = newNode;
            size++;
            return;
        } else if ((index > 0) && (index <= size - 1)) {

            int distanceFromBack = size - index;
            int distanceFromFront = index;

            if (distanceFromBack >= distanceFromFront) {
                while (current != null) {
                    if (positionCount == index) {
                        newNode.prev = current.prev;
                        current.prev.next = newNode;
                        current.prev = newNode;
                        newNode.next = current;
                        size++;
                    }
                    positionCount++;
                    current = current.next;
                }
            } else if (distanceFromBack < distanceFromFront) {
                positionCount = size;
                while (currentBack != null) {
                    if (positionCount == index) {
                        newNode.next = currentBack.next;
                        newNode.prev = currentBack;
                        currentBack.next.prev = newNode;
                        currentBack.next = newNode;
                        size++;
                        return;
                    }
                    positionCount--;
                    currentBack = currentBack.prev;
                }
            }
        } else {
            add(item);
        }

    }

    @Override
    public T delete(int index) {
        Node<T> current = front;
        T result;

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        } else if (index == 0 && size != 1) {
            result = front.data;
            front = front.next;
            front.prev = null;
        } else if (index == 0 && size == 1) {
            result = front.data;
            front = null;
            back = null;
        } else if (index == size - 1) {
            result = front.data;
            back = back.prev;
            back.next = null;
        } else {
            int positionCount = 0;
            while (current.next != null) {
                if (positionCount == index) {
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                }
                positionCount++;
                current = current.next;

            }
            result = current.data;
        }

        size--;
        return result;
    }

    @Override
    public int indexOf(T item) {
        Node nodeToCompare = new Node(item);
        int positionCount = 0;
        Node<T> current = front;

        if (current == null) {
            return -1;
        } else {
            while (current != null) {

                if (current.data == nodeToCompare.data && current.data == null) {
                    return positionCount;
                }
                if (current.data.equals(nodeToCompare.data)) {
                    return positionCount;
                }
                positionCount++;
                current = current.next;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(T other) {
        //Node nodeToCompare = new Node(other);
        Node<T> current = front;

        while (current != null) {
            //Passes null element test
            if (current.data == null & other == null) {
                return true;

            }
            //Passes index of and contains correctly compares of item
            if (current.data != null && current.data.equals(other)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            if (current != null) {
                return true;
            }
            return false;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *                                there are no more elements to look at.
         */
        public T next() {
            Node<T> pointer = current;
            if (hasNext()) {
                current = current.next;
                return pointer.data;
            }
            throw new NoSuchElementException();
        }
    }
}
