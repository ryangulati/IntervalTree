package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv); 
		}
		
		
		ArrayList<Interval> intervalsLeft = intervals;
		
		
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		
		ArrayList<Integer> sortedEndPoints = 
	    getSortedEndPoints(intervalsLeft, intervalsRight);
	    root = buildTreeNodes(sortedEndPoints);	
		mapIntervalsToTree(intervalsLeft, intervalsRight);
		
	
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		// COMPLETE THIS METHOD
		for (int i = 1; i < intervals.size(); i++) {
			for (int j = i; j > 0; j--) {
				
				Interval i1 = intervals.get(j);
				Interval i2 = intervals.get(j-1);
				
				if (endpoint(i1, lr) < endpoint(i2, lr)) {
					intervals.set(j, i2);
					intervals.set(j-1, i1);
				}
			}
		}
	}
	private static int endpoint(Interval in, char lr) {
		if (lr == 'l') return in.leftEndPoint;
		if (lr == 'r') return in.rightEndPoint;
		else {
			System.out.println("Invalid endpoint token: " + lr);
			return 0;
		}
	}
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		ArrayList<Integer> endPoints = new ArrayList<Integer>();
		
		for (Interval X : leftSortedIntervals){
			endPoints.add(X.leftEndPoint);
			System.out.println("endpoint left add " + endPoints);
		}
		
		for (int i=0; i<endPoints.size()-2; i++){
			while (endPoints.get(i) == endPoints.get(i+1)){
				endPoints.remove(i+1);
				System.out.println("endpoint delete left dupes " + endPoints);
			}
		}
		
		int pos = -1;
		boolean canAdd = true;
		for (int i=0; i<rightSortedIntervals.size()-1; i++){
			for (int j=0; j<endPoints.size(); j++){
				if (endPoints.get(j) < rightSortedIntervals.get(i).rightEndPoint){
					pos = j;
					canAdd = true;
				}
				else if (endPoints.get(j) == rightSortedIntervals.get(i).rightEndPoint){
					canAdd = false;
					break;
				}
			}
			if (canAdd){
				endPoints.add(pos+1, rightSortedIntervals.get(i).rightEndPoint);
			}
			System.out.println("endpoint right add " + endPoints);
		}
		
		return endPoints;
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */

	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		
		Queue<IntervalTreeNode> tree = new Queue<IntervalTreeNode>();
		float value;
		
		for(int i = 0; i < endPoints.size(); i ++){
			
			value = endPoints.get(i);
				
			IntervalTreeNode treeNode = new IntervalTreeNode(value,value,value);
			treeNode.leftIntervals = new ArrayList<Interval>();
			treeNode.rightIntervals = new ArrayList<Interval>();

			tree.enqueue(treeNode);

			
		}
		
		
		IntervalTreeNode result = null;
		
		int TSize = tree.size;
		
		while(TSize > 0){
			
			if(TSize == 1){
				
				result = tree.dequeue();
				return result;
				
			}
			else{
				int tempSize = TSize;
				while(tempSize > 1){
					IntervalTreeNode t1 = tree.dequeue();
					IntervalTreeNode t2 = tree.dequeue();
					float v1 = t1.maxSplitValue;
					float v2 = t2.minSplitValue;
					float x = (v1+v2)/(2);
					IntervalTreeNode N = new IntervalTreeNode(x, t1.minSplitValue, t2.maxSplitValue);
					N.leftIntervals = new ArrayList<Interval>();
					N.rightIntervals = new ArrayList<Interval>();
					N.leftChild = t1;
					N.rightChild = t2;
					tree.enqueue(N);
					tempSize -=2;
					
					
				}
				if (tempSize == 1){
					IntervalTreeNode single = tree.dequeue();
					tree.enqueue(single);
				}
				TSize = tree.size;
			}
				
			
		}
		
		result = tree.dequeue();
		return result;
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	private IntervalTreeNode find(float minValue, float maxValue)
	{
     
		return this.find(minValue, maxValue, root);
	}
	
	private IntervalTreeNode find(float min, float max, IntervalTreeNode head)
	{
		float splitValue = (min+max)/2;
		
     
		if (min <= head.splitValue && max >= head.splitValue)
		{
			return head;
		}
		
		if (splitValue > head.splitValue)
		{
			if (head.rightChild == null)
				return null;
			else
				return this.find(min, max, head.rightChild);
		}
		else
		{
			if (head.leftChild == null)
				return null;
			else
				return this.find(min, max, head.leftChild);
		}
	}
	
	private ArrayList<Interval> addToIntervals(ArrayList<Interval> node, Interval interval)
	{
      
		if (node == null)
		{
			node = new ArrayList<Interval>();
		}
		
      
		node.add(interval);
		
		return node;
	}
	
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		
      for (int i = 0; i < leftSortedIntervals.size(); i++)
		{
			if (leftSortedIntervals.get(i) == null)
				break;
			
			IntervalTreeNode nodeToAddAt = this.find(leftSortedIntervals.get(i).leftEndPoint, leftSortedIntervals.get(i).rightEndPoint);
			nodeToAddAt.leftIntervals = this.addToIntervals(nodeToAddAt.leftIntervals, leftSortedIntervals.get(i));
		}
		
      
		for (int i = 0; i < rightSortedIntervals.size(); i++)
		{
			if (rightSortedIntervals.get(i) == null)
				break;
			
			IntervalTreeNode nodeToAddAt = this.find(rightSortedIntervals.get(i).leftEndPoint, rightSortedIntervals.get(i).rightEndPoint);
			nodeToAddAt.rightIntervals = this.addToIntervals(nodeToAddAt.rightIntervals, rightSortedIntervals.get(i));
		}

  
	}

	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
public ArrayList<Interval> findIntersectingIntervals(Interval q) {
	
	return getIntersections(root, q);
}

private ArrayList<Interval> getIntersections(IntervalTreeNode node, Interval interval){
	ArrayList<Interval> intersection = new ArrayList<Interval>();

	if (node == null){
		return intersection;
	}

	float nodeSplitVal = node.splitValue;
	
	ArrayList<Interval> rightIntervals = node.rightIntervals;
	ArrayList<Interval> leftIntervals = node.leftIntervals;
	IntervalTreeNode leftChild = node.leftChild;
	IntervalTreeNode rightChild = node.rightChild;

	if (interval.contains(nodeSplitVal)){
		
		for (Interval i : leftIntervals){
			intersection.add(i);
		}
		
		intersection.addAll(getIntersections(rightChild, interval));
		
		intersection.addAll(getIntersections(leftChild, interval));
		
	}

	else if (nodeSplitVal < interval.leftEndPoint){
		
		int i = rightIntervals.size()-1;
		
		while (i >= 0 && (rightIntervals.get(i).intersects(interval))){
			intersection.add(rightIntervals.get(i));
			i--;
		}

		intersection.addAll(getIntersections(rightChild, interval));
	}

	else if (nodeSplitVal > interval.rightEndPoint){
		
		int j = 0;
		
		while ((j < leftIntervals.size()) && (leftIntervals.get(j).intersects(interval))){
			
			intersection.add(leftIntervals.get(j));						
			j++;
		}
		
		intersection.addAll(getIntersections(leftChild, interval));
	}

	return intersection;
}

}



