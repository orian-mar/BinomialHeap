// username:
// name1: Orian Marmelstein
// id1: 207860487
// name2: Lilach Chay
// id2: 322270513

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

	/**
	 * Update thisHeap fields
	 *
	 * Time Complexity: O(1)
	 */
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
	 *
	 * Time Complexity: O(log n) w.c
	 */
	public HeapItem insert(int key, String info) {
		HeapItem item = new HeapItem(key, info);
		HeapNode realLast = this.last;
		this.addTree(item.node); //connect the node to the heap (as last tree)

		if (realLast != null) {
			this.last = realLast; //the inserted node.rank is 0, so we want it to be first
		}
		if (this.numTrees() == 1) { //if the heap was empty before, we can finish
			return item;
		}

		HeapNode prev = this.last;
		HeapNode curr = this.last.next; //our inserted tree
		HeapNode next = curr.next;
		HeapNode cont, newNode;

		//check if linking are needed after adding the tree
		while(curr.rank == next.rank && this.numTrees() != 2) {
			cont = next.next;
			newNode = HeapNode.link(curr, next);
			this.numberOfTrees--;
			prev.next = newNode;
			newNode.next = cont;
			curr = newNode;
			next = cont;
		}

		if(this.numTrees() == 2) {
			if (curr.rank == next.rank) {
				newNode = HeapNode.link(curr, next);
				newNode.next = newNode;
				this.updateHeap(this.size(), newNode, newNode, 1);
			}
		}
		this.min = HeapNode.getMinRoot(this.min);
		return item;
	}


	/**
	 * Return the min HeapNode in the level of this node's brothers (in current tree)
	 * Used in deleteMin() for finding the new min of this heap and
	 * the min of children heap before melding them
	 *
	 * Time Complexity: O(log n)
	 */
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
	 *
	 * Detach the node and his brothers from their parent
	 * Used in deleteMin() for detaching the children heap
	 *
	 * Time Complexity: O(log n)
	 */
	public static void resetParent(HeapNode node){
		while (node.parent != null){
			node.parent = null;
			node = node.next;
		}
	}


	/**
	 * Delete the minimal item
	 *
	 * Time Complexity: O(log n)
	 */
	public void deleteMin() {

		if (this.empty()) {
			return;
		}

		HeapNode prev = min.getBro();
		if (last == min){
			last = prev; //if minNode was last - save the newLast before detaching the min
		}

		//check if minNode.rank is 0 (meld is not needed)
		if (min.child == null) {
			if (this.numTrees() == 1) { //if min is the only node - empty the whole heap and break
				this.updateHeap(0, null, null, 0);
				return;
			}
			//detach min and update the heap
			prev.next = min.next;
			min.next = null;
			this.updateHeap(this.size()-1, last, this.getNewMin(last), this.numTrees()-1);
			return;
		}
		//create children heap and detach them from min
		HeapNode min2 = this.getNewMin(min.child);
		BinomialHeap heap2 = new BinomialHeap();
		heap2.updateHeap((int)Math.pow(2, min.rank)-1, min.child, min2, min.rank);
		resetParent(min.child);

		//if min.tree was alone in the heap - the children heap will replace the original
		if (this.numTrees() == 1){
			this.updateHeap(heap2.size(), heap2.last, heap2.findMin().node, heap2.numTrees());
			return;
		}

		//detach children heap and update this.heap
		this.size = this.size - heap2.size - 1;
		this.numberOfTrees--;
		prev.next = this.min.next;
		this.min.next = null;
		this.min.child = null;
		this.min = this.getNewMin(last);

		//meld heap2 into this.heap
		this.meld(heap2);
	}


	/**
	 * Return the minimal HeapItem
	 *
	 * Time Complexity: O(1)
	 */
	public HeapItem findMin() {
		if (this.min == null) {
			return null;
		}
		return this.min.item;
	}


	/**
	 * Move up the item to the correct level in the tree according to its key
	 * Used in decreaseKey()
	 *
	 * Time Complexity: O(log n)
	 */
	public void heapifyUp(HeapItem pointer) {
		HeapNode parent = pointer.node.parent;

		while (parent != null && pointer.key < parent.getKey()) {
			//the HeapNode represent the place in the tree, so we replace only the items
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
	 *
	 * Time Complexity: O(log n)
	 */
	public void decreaseKey(HeapItem item, int diff) {
		item.key = item.key - diff;
		this.heapifyUp(item);
		if (item.key < min.getKey()){
			min = item.node;
		}
	}

	/**
	 * Delete the item from the heap
	 *
	 * Time Complexity: O(log n)
	 */
	public void delete(HeapItem item) {
		//Change the item into being the new minimum (and then deleteMin)
		int decrease = this.findMin().key - 1;
		this.decreaseKey(item, item.key - decrease);
		deleteMin();
	}


	/**
	 * Add HeapNode tree to the end of this heap
	 * Used in makeForest and insert
	 *
	 * Time Complexity: O(1)
	 */
	public void addTree(HeapNode node)
	{
		if (this.empty()) //this node will be the only tree in the heap
		{
			this.updateHeap((int)Math.pow(2, node.rank), node, node,1);
			node.next = node;
		}
		else
		{
			HeapNode newMin = this.min;
			if (node.getKey() < this.min.getKey()) //check if min should be updated
				newMin = node;
			node.next = this.last.next; //connect the node
			this.last.next = node;
			this.updateHeap(this.size +(int)Math.pow(2, node.rank), node, newMin, this.numberOfTrees + 1);
		}
	}


	/**
	 * Merge heap 2 into this heap while keeping the rank order
	 * Used in Meld
	 *
	 * Time Complexity: O(log n)
	 */
	public void makeForest(BinomialHeap heap2) {
		BinomialHeap forest = new BinomialHeap();
		HeapNode node1 = this.last.next;
		HeapNode node2 = heap2.last.next;
		int counter1 = 0;
		int counter2 = 0;
		HeapNode next1, next2;

		while (counter1 < this.numTrees() && counter2 < heap2.numTrees()) {
			if (node1.rank < node2.rank) { //add the tree with the smaller rank first
				next1 = node1.next;
				forest.addTree(node1);
				node1 = next1;
				counter1++;
			} else { //if node1.next.rank >= rank2
				next2 = node2.next;
				forest.addTree(node2);
				node2 = next2;
				counter2++;
			}
		}
		//if we went through all of heap2 but more trees remain in heap1, add them to the forest
		while (counter1 < this.numTrees()) {
			next1 = node1.next;
			forest.addTree(node1);
			node1 = next1;
			counter1++;
		}
		//if we went through all of heap1 but more trees remain in heap2, add them to the forest
		while (counter2 < heap2.numTrees()) {
			next2 = node2.next;
			forest.addTree(node2);
			node2 = next2;
			counter2++;
		}
		//this.heap becomes the forest
		this.updateHeap(forest.size, forest.last, forest.min, forest.numberOfTrees);
	}


	/**
	 * Meld the heap with heap2
	 *
	 * Time Complexity: O(log n)
	 */
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

		while (prev != next && next != this.last) { //prev == next if there are only 2 trees left
			if (curr.rank < next.rank || curr.rank == next.next.rank) {
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
				this.updateHeap(this.size(), newNode, newNode, 1);
			} else {
				if (curr.rank > next.rank) {
					newLast = curr;
				} else {
					newLast = next;
				}
				this.updateHeap(this.size(), newLast, HeapNode.getMinRoot(this.min), 2);
			}
			return;
		}
		if (curr.rank != next.rank) {
			this.updateHeap(this.size(), last, HeapNode.getMinRoot(this.min), numOfBothTrees - countOfLinks);
			return;
		}
		HeapNode first = next.next;
		HeapNode lastNode = HeapNode.link(curr, next);
		prev.next = lastNode;
		lastNode.next = first;
		countOfLinks++;
		this.updateHeap(this.size(), lastNode, HeapNode.getMinRoot(this.min), numOfBothTrees - countOfLinks);
	}


	/**
	 * Return the number of elements in the heap
	 *
	 * Time Complexity: O(1)
	 */
	public int size() {
		return this.size;
	}

	/**
	 * The method returns true if and only if the heap
	 * is empty.
	 *
	 * Time Complexity: O(1)
	 */
	public boolean empty() {
		return this.size() == 0;
	}

	/**
	 * Return the number of trees in the heap.
	 *
	 * Time Complexity: O(1)
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

		/**
		 * Return the key of the node
		 *
		 * Time Complexity: O(1)
		 *
		 */
		public int getKey(){
			return this.item.key;
		}

		/**
		 * Go to the root of the node (get the minimum root)
		 * Used in Meld and Insert
		 *
		 * Time Complexity: O(1)
		 */
		public static HeapNode getMinRoot(HeapNode min) {
			while(min.parent != null)
				min = min.parent;
			return min;
		}


		/**
		 * Link node1 and node2 into one tree, in order
		 * Used in Meld and Insert
		 *
		 * Time Complexity: O(1)
		 */
		public static HeapNode link(HeapNode node1, HeapNode node2) {
			//suppose node1 =< node2
			if (node1.getKey() > node2.getKey()) {
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
			node1.rank++;
			return node1;
		}

		/**
		 * Return the previous brother of the node
		 * (the one we do not have a pointer to)
		 *
		 * Time Complexity: O(log n)
		 */
		public HeapNode getBro(){
			HeapNode curr = this.next;
			while (curr.next != this){
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
