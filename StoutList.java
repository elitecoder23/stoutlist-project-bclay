package edu.iastate.cs228.hw3;

import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This class implements a list interface using linked nodes that can store multiple items per node.
 * Rules for adding and removing elements are designed to maintain that each node (except possibly the last one) is at least half full.
 */

public class StoutList<E extends Comparable<? super E>> extends AbstractSequentialList<E> {
	/**
	 * Default number of elements that may be stored in each node.
	 */
	private static final int DEFAULT_NODESIZE = 4;

	/**
	 * Number of elements that can be stored in each node.
	 */
	private final int nodeSize;

	/**
	 * Dummy node for head. It should be private but set to public here only for
	 * grading purpose. In practice, you should always make the head of a linked
	 * list a private instance variable.
	 */
	public Node head;

	/**
	 * Dummy node for tail.
	 */
	private Node tail;

	/**
	 * Number of elements in the list.
	 */
	private int size;

	/**
	 * Constructs an empty list with the default node size.
	 */
	public StoutList() {
		this(DEFAULT_NODESIZE);
	}

	/**
	 * Constructs an empty list with a specified node size.
	 * 
	 * @param nodeSize The number of elements that may be stored in each node; it must be an even number.
	 */

	public StoutList(int nodeSize) {
		if (nodeSize <= 0 || nodeSize % 2 != 0)
			throw new IllegalArgumentException();

		// dummy nodes
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		this.nodeSize = nodeSize;
	}

	/**
	 * Constructor for grading purposes. Fully implemented.
	 * 
	 * @param head The head of the data structure.
	 * @param tail The tail of the data structure.
	 * @param nodeSize The maximum size of a node.
	 * @param size The current size of the data structure.
	 */

	public StoutList(Node head, Node tail, int nodeSize, int size) {
		this.head = head;
		this.tail = tail;
		this.nodeSize = nodeSize;
		this.size = size;
	}

	/**
	 * @return the number of elements in the list
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Adds an item to the end of the list.
	 *
	 * @param item The item to add to the list.
	 * @return 'true' if the item was added successfully, 'false' if the item already exists in the list.
	 * @throws NullPointerException If 'item' is null.
	 */
	@Override
	public boolean add(E item) {
	    // Check for null item and throw an exception.
	    if (item == null) {
	        throw new NullPointerException();
	    }

	    // If the item already exists in the list, return 'false.'
	    if (contains(item)) {
	        return false;
	    }

	    // If the list is empty, create a new node and add the item to it.
	    if (size == 0) {
	        Node length = new Node();
	        length.addItem(item);
	        head.next = length;
	        length.previous = head;
	        length.next = tail;
	        tail.previous = length;
	    } else {
	        // If the last node is not full, add the item to it.
	        if (tail.previous.count < nodeSize) {
	            tail.previous.addItem(item);
	        }
	        // If the last node is full, create another node at the end and add the item.
	        else {
	            Node length = new Node();
	            length.addItem(item);
	            Node temporary = tail.previous;
	            temporary.next = length;
	            length.previous = temporary;
	            length.next = tail;
	            tail.previous = length;
	        }
	    }
	    
	    // Increase the size of the list since the item has been added.
	    size++;
	    return true;
	}


	/**
	 * Checks if the list contains a specific element by searching through the list for duplicates.
	 *
	 * @param element The element to search for.
	 * @return 'true' if the list contains the element, 'false' otherwise.
	 */

	public boolean contains(E element) {
		if(size < 1)
			return false;
		Node temporary = head.next;
		while(temporary != tail) {
			for(int i=0;i<temporary.count;i++) {
				if(temporary.data[i].equals(element))
					return true;
				temporary = temporary.next;
			}
		}
		return false;
	}

	/**
	 * Adds an element to a specific position in the data structure.
	 *
	 * @param pos The position where the element should be added.
	 * @param element The element to add to the list.
	 * @throws IndexOutOfBoundsException If 'pos' is out of bounds.
	 */
	@Override
	public void add(int pos, E element) {
	    // Check if 'pos' is within bounds; otherwise, throw an exception.
	    if (pos < 0 || pos > size)
	        throw new IndexOutOfBoundsException();

	    // If the list is empty, create a new node and add 'element' to offset 0.
	    if (head.next == tail)
	        add(element);

	    NodeInfo nodeInfo = find(pos);
	    Node temporary = nodeInfo.node;
	    int offset = nodeInfo.offset;

	    // If 'offset' is 0 and one of the following cases occurs:
	    if (offset == 0) {
	        // If 'temporary' has a predecessor with fewer than 'nodeSize' elements (and is not the 'head'),
	        // add 'element' to the predecessor.
	        if (temporary.previous.count < nodeSize && temporary.previous != head) {
	            temporary.previous.addItem(element);
	            size++;
	            return;
	        }
	        // If 'temporary' is the 'tail' node and its predecessor has 'nodeSize' elements, create a new node
	        // and add 'element' at offset 0.
	        else if (temporary == tail) {
	            add(element);
	            size++;
	            return;
	        }
	    }
	    // If there is space in node 'temporary', add 'element' at 'offset', shifting array elements as necessary.
	    if (temporary.count < nodeSize) {
	        temporary.addItem(offset, element);
	    }
	    // Otherwise, perform a split operation:
	    else {
	        Node newSuccessor = new Node();
	        int halfPoint = nodeSize / 2;
	        int count = 0;
	        while (count < halfPoint) {
	            newSuccessor.addItem(temporary.data[halfPoint]);
	            temporary.removeItem(halfPoint);
	            count++;
	        }

	        Node oldSuccessor = temporary.next;

	        temporary.next = newSuccessor;
	        newSuccessor.previous = temporary;
	        newSuccessor.next = oldSuccessor;
	        oldSuccessor.previous = newSuccessor;

	        // If 'offset' is less than or equal to 'nodeSize / 2', add 'element' to node 'temporary' at 'offset'.
	        if (offset <= nodeSize / 2) {
	            temporary.addItem(offset, element);
	        }
	        // If 'offset' is greater than 'nodeSize / 2', add 'element' to node 'newSuccessor' at 'offset - (nodeSize / 2)'.
	        if (offset > nodeSize / 2) {
	            newSuccessor.addItem(offset - (nodeSize / 2), element);
	        }
	    }
	    // Increase the size of the list since an element has been added.
	    size++;
	}


	/**
	 * Removes an element at the specified position 'position' in the data structure.
	 * If 'position' is out of bounds, an 'IndexOutOfBoundsException' is thrown.
	 * 
	 * The removal process follows these rules:
	 * 1. If the node containing the element is the last node and has only one element, it is deleted.
	 * 2. If the node is the last node with two or more elements or has more than half of the node size, it removes the element and shifts other elements as necessary.
	 * 3. Otherwise, if the node has at most half of the node size, it merges with its successor node.
	 * 
	 * - If the successor node has more than half of the node size, a 'mini-merge' occurs, moving the first element from the successor to the current node.
	 * - If the successor node has half or fewer elements, a 'full merge' occurs, moving all elements from the successor to the current node and deleting the successor node.
	 * 
	 * After the removal, the size of the data structure is decreased by one, and the removed element is returned.
	 *
	 * @param position The position of the element to be removed.
	 * @return The removed element.
	 * @throws IndexOutOfBoundsException If 'position' is out of bounds.
	 */
	@Override
	public E remove(int position) {
		
		if (position < 0 || position > size)
			throw new IndexOutOfBoundsException();
		NodeInfo nodeInfo = find(position);
		Node temporary = nodeInfo.node;
		int offset = nodeInfo.offset;
		E nodeValue = temporary.data[offset];

		
		if (temporary.next == tail && temporary.count == 1) {
			Node predecessor = temporary.previous;
			predecessor.next = temporary.next;
			temporary.next.previous = predecessor;
			temporary = null;
		}
		
		else if (temporary.next == tail || temporary.count > nodeSize / 2) {
			temporary.removeItem(offset);
		}
		
		else {
			temporary.removeItem(offset);
			Node succesor = temporary.next;
			
			
			if (succesor.count > nodeSize / 2) {
				temporary.addItem(succesor.data[0]);
				succesor.removeItem(0);
			}
			
			else if (succesor.count <= nodeSize / 2) {
				for (int i = 0; i < succesor.count; i++) {
					temporary.addItem(succesor.data[i]);
				}
				temporary.next = succesor.next;
				succesor.next.previous = temporary;
				succesor = null;
			}
		}
		// decrease the size of list, since item has been removed
		size--;
		return nodeValue;
	}

	/**
	 * Sorts all elements in the "stout" list in non-decreasing order. To achieve this, follow these steps:
	 * 1. Traverse the list and copy its elements into an array while deleting every visited node along the way.
	 * 2. Sort the array using the "insertionSort()" method. Please note that sorting efficiency is not a critical concern for this project.
	 * 3. Copy all elements from the sorted array back to the "stout" list, creating new nodes for storage. After sorting, all nodes except possibly the last one will be full of elements.
	 * 
	 * To use this method, you must have implemented a "Comparator<E>" for calling "insertionSort()".
	 */

	public void sort() {
		
		E[] sortDataList = (E[]) new Comparable[size];

		int tempIndex = 0;
		Node temporary = head.next;
		while (temporary != tail) {
			for (int i = 0; i < temporary.count; i++) {
				sortDataList[tempIndex] = temporary.data[i];
				tempIndex++;
			}
			temporary = temporary.next;
		}

		head.next = tail;
		tail.previous = head;

		insertionSort(sortDataList, new ElementComparator());
		size = 0;
		for (int i = 0; i < sortDataList.length; i++) {
			add(sortDataList[i]);
		}

	}

	/**
	 * Sorts all elements in the StoutList in non-increasing order by calling the 'bubbleSort()' method. After sorting, all but the last nodes are expected to be filled with elements.
	 * 
	 * Note: The 'bubbleSort()' method can only be called if 'Comparable<? super E>' is implemented.
	 */

	public void sortReverse() {
		
		E[] rsortDataList = (E[]) new Comparable[size];

		int tempIndex = 0;
		Node temporary = head.next;
		while (temporary != tail) {
			for (int i = 0; i < temporary.count; i++) {
				rsortDataList[tempIndex] = temporary.data[i];
				tempIndex++;
			}
			temporary = temporary.next;
		}

		head.next = tail;
		tail.previous = head;

		bubbleSort(rsortDataList);
		size = 0;
		for (int i = 0; i < rsortDataList.length; i++) {
			add(rsortDataList[i]);
		}
	}

	@Override
	public Iterator<E> iterator() {
		
		return new StoutListIterator();
	}

	@Override
	public ListIterator<E> listIterator() {
		
		return new StoutListIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		
		return new StoutListIterator(index);
	}

	/**
	 * Generates a string representation of this list, displaying the internal structure of the nodes.
	 */

	public String toStringInternal() {
		return toStringInternal(null);
	}

	/**
	 * Generates a string representation of this list, displaying the internal structure of the nodes and the position of the specified iterator.
	 *
	 * @param iterator An iterator used for this list.
	 */

	public String toStringInternal(ListIterator<E> iterator) {
		int count = 0;
		int position = -1;
		if (iterator != null) {
			position = iterator.nextIndex();
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		Node current = head.next;
		while (current != tail) {
			sb.append('(');
			E data = current.data[0];
			if (data == null) {
				sb.append("-");
			} else {
				if (position == count) {
					sb.append("| ");
					position = -1;
				}
				sb.append(data.toString());
				++count;
			}

			for (int i = 1; i < nodeSize; ++i) {
				sb.append(", ");
				data = current.data[i];
				if (data == null) {
					sb.append("-");
				} else {
					if (position == count) {
						sb.append("| ");
						position = -1;
					}
					sb.append(data.toString());
					++count;

					// iterator at end
					if (position == size && count == size) {
						sb.append(" |");
						position = -1;
					}
				}
			}
			sb.append(')');
			current = current.next;
			if (current != tail)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Represents the node type used in this list. Each node can hold a maximum of 'nodeSize' elements in an array, and empty slots in the array are represented as null.
	 */

	private class Node {
		/**
		 * Array of actual data elements.
		 */
		// Unchecked warning unavoidable.
		public E[] data = (E[]) new Comparable[nodeSize];

		/**
		 * Link to next node.
		 */
		public Node next;

		/**
		 * A reference to the previous node in the linked structure.

		 */
		public Node previous;

		/**
		 * Represents the index of the next available offset in this node, which is also equal to the number of elements currently in this node.
 

		 */
		public int count;

		/**
 * Inserts an item into this node at the first available offset.
 * Precondition: The current count must be less than the nodeSize.
 *
 * @param item The element to be added.
 */

		void addItem(E item) {
			if (count >= nodeSize) {
				return;
			}
			data[count++] = item;
		}

		/**
		 * Inserts an item into this node at the specified offset, shifting elements to the right as needed.
		 *
		 * Precondition: 'count' must be less than 'nodeSize'.
		 *
		 * @param offset The array index at which to place the new element.
		 * @param item   The element to be added.
		 */

		void addItem(int offset, E item) {
			if (count >= nodeSize) {
				return;
			}
			for (int i = count - 1; i >= offset; --i) {
				data[i + 1] = data[i];
			}
			++count;
			data[offset] = item;
		}

		/**
		 * Removes an element from this node at the specified offset and shifts elements to the left if necessary.
		 * Precondition: The offset must be in the range of 0 (inclusive) to 'count' (exclusive).
		 *
		 * @param offset The offset at which the element should be deleted.
		 */
		void removeItem(int offset) {
			E item = data[offset];
			for (int i = offset + 1; i < nodeSize; ++i) {
				data[i - 1] = data[i];
			}
			data[count - 1] = null;
			--count;
		}
	}

	/**
	 * A helper class that represents a specific position within the list.
	 */
	private class NodeInfo {
		public Node node;
		public int offset;

		public NodeInfo(Node node, int offset) {
			this.node = node;
			this.offset = offset;
		}
	}

	/**
	 * A helper method used to find a specific item in the list.
	 *
	 * @param position The position of the item for which information is needed.
	 * @return The NodeInfo of the item at the specified position in the list.
	 */

	private NodeInfo find(int pos) {
		Node temporary = head.next;
		int currPos = 0;
		while (temporary != tail) {
			if (currPos + temporary.count <= pos) {
				currPos += temporary.count;
				temporary = temporary.next;
				continue;
			}

			NodeInfo nodeInfo = new NodeInfo(temporary, pos - currPos);
			return nodeInfo;

		}
		return null;
	}

	/**
	 * Custom Iterator for StoutList
	 */
	private class StoutListIterator implements ListIterator<E> {

		final int LAST_ACTION_PREV = 0;
		final int LAST_ACTION_NEXT = 1;

		/**
		 * Pointer of iterator
		 */
		int currentPosition;
		
		/**
		 * Represents the data structure of the iterator in an array format.
		 */

		public E[] dataList;
		
		/**
		 * Keeps track of the last action performed by the program, primarily used for the 'remove()' and 'set()' methods to identify the item to remove or modify.
		 */

		 
		int lastAction;

		/**
		 * Default constructor that sets the iterator's pointer to the beginning of the list.
		 */

		public StoutListIterator() {
			currentPosition = 0;
			lastAction = -1;
			setup();
		}

		/**
		 * Constructs an iterator positioned at a specific index within the list.
		 * Sets the iterator's pointer to the specified 'position' in the list.
		 *
		 * @param position The index to set the iterator's position to.
		 */

		public StoutListIterator(int position) {
			
			currentPosition = position;
			lastAction = -1;
			setup();
		}

		/**
		 * Converts the data from the StoutList into an array format and stores it in 'dataList'.
		 */

		private void setup() {
			dataList = (E[]) new Comparable[size];

			int tempIndex = 0;
			Node temporary = head.next;
			while (temporary != tail) {
				for (int i = 0; i < temporary.count; i++) {
					dataList[tempIndex] = temporary.data[i];
					tempIndex++;
				}
				temporary = temporary.next;
			}
		}

		/**
		 * @return whether iterator has next available value or not
		 */
		@Override
		public boolean hasNext() {
			if (currentPosition >= size)
				return false;
			else
				return true;
		}

		/**
		 * Retrieves the next available value and advances the pointer by one position.
		 *
		 * @return The next available value from the iterator.
		 */

		@Override
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			lastAction = LAST_ACTION_NEXT;
			return dataList[currentPosition++];
		}

		/**
		 * Removes the last element returned by the 'next()' or 'previous()' method from the list.
		 * This method can only be called once per call of 'next()' or 'previous()'.
		 * It also removes the element from the StoutList.
		 */

		@Override
		public void remove() {
			
			if (lastAction == LAST_ACTION_NEXT) {
				StoutList.this.remove(currentPosition - 1);
				setup();
				lastAction = -1;
				currentPosition--;
				if (currentPosition < 0)
					currentPosition = 0;
			} else if (lastAction == LAST_ACTION_PREV) {
				StoutList.this.remove(currentPosition);
				setup();
				lastAction = -1;
			} else {
				throw new IllegalStateException();
			}
		}

		/**
		 * Checks if the iterator has a previous available value.
		 *
		 * @return true if the iterator has a previous available value, false otherwise.

		 */
		@Override
		public boolean hasPrevious() {
			
			if (currentPosition <= 0)
				return false;
			else
				return true;
		}

		/**
		 * Returns the index of the next available element.
		 *
		 * @return The index of the next available element.

		 */
		@Override
		public int nextIndex() {
			
			return currentPosition;
		}
		
		/**
		 * Retrieves the previous available element and moves the pointer back by one position.
		 *
		 * @return The previous available element.
		 */

		@Override
		public E previous() {
			
			if (!hasPrevious())
				throw new NoSuchElementException();
			lastAction = LAST_ACTION_PREV;
			currentPosition--;
			return dataList[currentPosition];
		}

		/**
		 * @return index of previous element
		 */
		@Override
		public int previousIndex() {
			
			return currentPosition - 1;
		}

		/**
		 * Replaces the element currently pointed to with the specified element.
		 *
		 * @param elementToReplace The element that will replace the current element.
		 */

		@Override
		public void set(E elementToReplace) {
			
			if (lastAction == LAST_ACTION_NEXT) {
				NodeInfo nodeInfo = find(currentPosition - 1);
				nodeInfo.node.data[nodeInfo.offset] = elementToReplace;
				dataList[currentPosition - 1] = elementToReplace;
			} else if (lastAction == LAST_ACTION_PREV) {
				NodeInfo nodeInfo = find(currentPosition);
				nodeInfo.node.data[nodeInfo.offset] = elementToReplace;
				dataList[currentPosition] = elementToReplace;
			} else {
				throw new IllegalStateException();
			}

		}

		/**
		 * Appends the given element to the end of the list.
		 *
		 * @param elementToAdd The element to be added to the list.
		 */

		@Override
		public void add(E elementToAdd) {
			
			if (elementToAdd == null)
				throw new NullPointerException();

			StoutList.this.add(currentPosition, elementToAdd);
			currentPosition++;
			setup();
			lastAction = -1;

		}


	}

	/**
	 * Sorts an array of elements in non-decreasing order using the insertion sort algorithm.
	 *
	 * @param elements   An array containing the elements to be sorted.
	 * @param comparator The comparator used for sorting.
	 */

	private void insertionSort(E[] array, Comparator<? super E> comparator) {
		for (int i = 1; i < array.length; i++) {
			E key = array[i];
			int j = i - 1;

			while (j >= 0 && comparator.compare(array[j], key) > 0) {
				array[j + 1] = array[j];
				j--;
			}
			array[j + 1] = key;
		}
	}

	/**
	 * Sorts the array using the bubble sort algorithm in non-increasing order.
	 * This method relies on the 'compareTo()' method provided by a class implementing the Comparable interface with type 'E' or '? super E'.
	 *
	 * @param array An array holding elements to be sorted.
	 */

	private void bubbleSort(E[] array) {
		int length = array.length;
		for (int i = 0; i < length - 1; i++) {
			for (int j = 0; j < length - i - 1; j++) {
				if (array[j].compareTo(array[j + 1]) < 0) {
					E temporary = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temporary;
				}
			}
		}

	}

	/**
	 * This is a custom Comparator that is intended to be used with insertion sort.
	 */

	class ElementComparator<E extends Comparable<E>> implements Comparator<E> {
		@Override
		public int compare(E elementToAdd, E arg1) {
			
			return elementToAdd.compareTo(arg1);
		}

	}

}
