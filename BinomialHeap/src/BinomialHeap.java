/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeap {
	public int size;
	public HeapNode last;
	public HeapNode min;
	public int numberOfTrees;


	public void updateHeap(int size, HeapNode last, HeapNode min, int numberOfTrees) {
		this.size = size;
		this.last = last;
		this.min = min;
		this.numberOfTrees = numberOfTrees;
	}


	/**
	 * pre: key > 0
	 * <p>
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 */
	public HeapItem insert(int key, String info) {
		HeapItem item = new HeapItem(key, info);
		BinomialHeap temp = new BinomialHeap();
		temp.updateHeap(1, item.node, item.node, 1);
		this.meld(temp);
		this.size++;
		return item;
	}



	public HeapNode getNewMin(HeapNode node){
		HeapNode currMin = node;
		int currMinNum = node.getKey();
		HeapNode current = node.next;
		while (current != node){
			if (current.getKey() < currMinNum){
				currMin = current;
				currMinNum = current.getKey();
			}
			current = current.next;
		}
		return currMin;
	}


	/**
	 * Delete the minimal item
	 */
	public void deleteMin() {
		HeapNode min2 = this.getNewMin(min.child);
		BinomialHeap heap2 = new BinomialHeap();
		heap2.updateHeap((int)Math.pow(2, min.rank)-1, min.child, min2, min.rank);
		resetParent(min.child);

		if (this.numTrees() == 1){
			this.updateHeap(0, null, null, 0);
			this.meld(heap2);
			return;
		}

		HeapNode prev = min.getBro();
		if (last == min){
			last = prev;
		}

		prev.next = min.next;
		min.next = null;
		min.child = null;
		min = this.getNewMin(last);
		this.meld(heap2);
	}



	public static void resetParent(HeapNode node){
		while (node.parent != null){
			node.parent = null;
			node = node.next;
		}
	}

	/**
	 * Return the minimal HeapItem
	 */


	public HeapItem findMin() {
		return this.min.item;
	}



	public void heapifyUp(HeapItem pointer) {
		HeapNode temp;
		HeapNode parent = pointer.node.parent;
		while (parent != null && pointer.key < parent.getKey()) {
			pointer.node.item = parent.item;
			parent.item.node = pointer.node;
			parent.item = pointer;
			pointer.node = parent;
			parent = parent.parent;
		}
	}

	/**
	 * pre: 0 < diff < item.key
	 * <p>
	 * Decrease the key of item by diff and fix the heap.
	 */
	public void decreaseKey(HeapItem item, int diff) {
		item.key = item.key - diff;
		this.heapifyUp(item);
		if (item.key < min.getKey()){
			min = item.node;
		}
	}

	/**
	 * Delete the item from the heap.
	 */
	public void delete(HeapItem item) {
		int decrease = this.findMin().key - 1;
		this.decreaseKey(item, item.key - decrease);
		this.deleteMin();
	}

	/**
	 * Meld the heap with heap2
	 */

	public void makeForest(BinomialHeap heap2) {
		HeapNode node1 = this.last;
		HeapNode node2 = heap2.last;
		int counter1 = 0;
		int counter2 = 0;
		HeapNode next2;
		while (counter1 < this.numTrees() && counter2 <= heap2.numTrees()) {
			if (node1.next.rank < node2.rank) { //find the correct place in this heap to insert the current tree of heap2
				node1 = node1.next;
				counter1++;
			} else { //if node1.next.rank >= rank2, insert the current node2 before.
				next2 = node2.next; //temp
				node2.next = node1.next;
				node1.next = node2;
				node2 = next2;
				node1 = node1.next;
				counter2++;
			}
		}
		if (counter1 == this.numTrees()) { //if we went through all of heap1 but more trees remain in heap2, connect them to the forest
			HeapNode first = node1.next;
			node1.next = node2;
			this.last = heap2.last;
			this.last.next = first;
		}
	}


	public void meld(BinomialHeap heap2) {
		int numOfBothTrees = this.numTrees() + heap2.numTrees();
		int countOfLinks = 0;

		if (numOfBothTrees == 0 || heap2.empty()) { //if both trees are empty or heap2 is empty, we won!
			return;
		}
		if (this.empty()) { //if this heap is empty we will turn it to heap2
			this.updateHeap(heap2.size(), heap2.last, heap2.findMin().node, heap2.numTrees());
			return;
		}
		//if we reached here there is at least one tree per heap, so we will join them into a FOREST.
		this.makeForest(heap2);
		HeapNode prev = this.last;
		HeapNode curr = this.last.next;
		HeapNode next = curr.next;
		HeapNode cont, newNode, newLast;

		while (curr.rank <= next.rank && prev != next) { //prev == next if there are only 2 trees left
			if (curr.rank != next.rank || curr.rank == next.next.rank) {
				prev = prev.next;
				curr = curr.next;
				next = next.next; //lol
			} else {
				cont = next.next;
				newNode = HeapNode.link(curr, next);
				countOfLinks++;
				prev.next = newNode;
				newNode.next = cont;
				curr = newNode;
				next = cont;
			}
		}
		if (prev == next) { //only 2 trees left
			if (curr.rank == next.rank) {
				newNode = HeapNode.link(curr, next);
				newNode.next = newNode;
				countOfLinks++;
				this.updateHeap(this.size() + heap2.size(), newNode, newNode, 1);
				return;
			} else {
				if (curr.rank > next.rank) {
					newLast = curr;
				} else {
					newLast = next;
				}
				this.updateHeap(this.size() + heap2.size(), newLast, HeapNode.minNode(this.min, heap2.min), 2);
			}
		}
		this.updateHeap(this.size() + heap2.size(), curr, HeapNode.minNode(this.min, heap2.min), numOfBothTrees - countOfLinks);
	}

	/**
	 * Return the number of elements in the heap
	 */
	public int size() {
		return this.size;
	}

	/**
	 * The method returns true if and only if the heap
	 * is empty.
	 */
	public boolean empty() {
		if (this.size() == 0) {
			return true;
		}
		return false; // should be replaced by student code
	}

	/**
	 * Return the number of trees in the heap.
	 */
	public int numTrees() {
		return this.numberOfTrees;
	}

	/**
	 * Class implementing a node in a Binomial Heap.
	 *
	 */
	public class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;

		public int getKey(){
			return this.item.key;
		}

		public static HeapNode minNode(HeapNode min1, HeapNode min2){
			if (min1.getKey() < min2.getKey() || min2.parent != null){ //if min1=min2 and they have the same rank, one might be the child of the other
				return min1;
			} else{

				return min2;
			}
		}

		public static HeapNode link(HeapNode node1, HeapNode node2) {
			if (node1.getKey() > node2.getKey()) { //suppose node1 =< node2
				HeapNode temp = node1;
				node1 = node2;
				node2 = temp;
			}
			if (node1.child == null) {
				node2.next = node2;
			} else {
				node2.next = node1.child.next;
				node1.child.next = node2;
			}
			node1.child = node2;
			node2.parent = node1;
			return node1;
		}

		public HeapNode getBro(){
			HeapNode curr = this.next;
			while (curr != this){
				curr = curr.next;
			}
			return curr;
		}
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 *
	 */
	public class HeapItem{
		public HeapNode node;
		public int key;
		public String info;

		public HeapItem(int key, String info) {
			this.key = key;
			this.info = info;
			this.node = new HeapNode();
			this.node.item = this;
		}
	}

}
