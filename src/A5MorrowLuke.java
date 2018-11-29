import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


/***********************************************************************************
 ***********************************************************************************
 * class A5Solution
 *
 *   Comparing BSTs and 2-3 trees.
 *
 *   DO NOT CHANGE ANYTHING IN THIS CLASS
 ***********************************************************************************
 ************************************************************************************/

public class A5MorrowLuke {

    public static void main(String[] args) {

        System.out.println("\n\nComp 2140 Assignment 5 "
                + "BSTs and 2-3 Trees, Winter 2018\n");

        getAndProcessFile();

        System.out.println("\nProgram ended normally.\n");

    } // end main

    private static void getAndProcessFile() {
        Scanner keyboard; // To read in the name of the input file
        String fileName; // The name of the input file typed in by the user
        BufferedReader in;  // Read text from a character-input stream.

        try {
            // Retrieve the file to be read using keyboard (console) input.

            keyboard = new Scanner(System.in);
            System.out.println("\nEnter the input file name (.txt files only): ");
            fileName = keyboard.nextLine();
            in = new BufferedReader(new FileReader(fileName));

            // Now process the sequences in the input file.

            processSequences(in);

            // Finally, close the input file.

            in.close();

        } catch (IOException ex) {
            System.out.println("\n\n***I/O error: " + ex.getMessage() + "\n\n");
        }

    } // getAndProcessFile

    private static void processSequences(BufferedReader in) {
        int numSequences = 0; // number of sequences
        int numInSequence = 0; // number of ints in the current sequence
        int[] data; // A sequence
        String inLine;   // A line of input from BufferedReader in.

        try {
            // Get the number of sequences.

            inLine = in.readLine();
            numSequences = Integer.parseInt(inLine);

            // Read in and perform the four steps with each sequence.

            for (int seqNumber = 0; seqNumber < numSequences; seqNumber++) {
                // Get the number of integers in this sequence.

                inLine = in.readLine();
                numInSequence = Integer.parseInt(inLine);

                // Read the sequence into an array so it can be
                // re-used multiple time.

                data = new int[numInSequence];

                for (int i = 0; i < numInSequence; i++) {
                    inLine = in.readLine();
                    data[i] = Integer.parseInt(inLine);
                }

                // Test the BST class with the sequence

                testBSTOnSequence(data);

                // Test the TwoThreeTree class with the sequence

                test23TreeOnSequence(data);

            } // end for seqNumber

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }

    } // end processSequences

    private static void testBSTOnSequence(int[] data) {
        long start, stop, elapsed; // used in timing
        boolean allFound; // search() returns true if the item is found; false otherwise.

        // Create a new, empty BST

        BST tBST = new BST();

        // Time the BST insertions.

        start = System.nanoTime();
        for (int i = 0; i < data.length; i++) {
            tBST.insert(data[i]);
        }
        stop = System.nanoTime();
        elapsed = stop - start;

        System.out.println("\nInserting " + data.length
                + " integers into a BST: " + elapsed + " nanoseconds.");

        // Time the BST searches.

        allFound = true;
        start = System.nanoTime();
        for (int i = 0; i < data.length; i++) {
            allFound = allFound && tBST.search(data[i]);
        }
        stop = System.nanoTime();
        elapsed = stop - start;

        System.out.println("\nSearching for the " + data.length
                + " integers in the BST: " + elapsed + " nanoseconds.");
        System.out.println("All items inserted were "
                + (allFound ? "" : "not ") + "found.");

        // Print out the first 20 integers in the BST

        System.out.println("\nThe smallest 20 integers in the BST:");
        tBST.printTree();

    } // end testBSTOnSequence


    private static void test23TreeOnSequence(int[] data) {
        long start, stop, elapsed; // used in timing
        boolean allFound; // search() returns true if the key is found; otherwise false

        // Create a new, empty 2-3 tree

        TwoThreeTree t23 = new TwoThreeTree();

        // Time the 2-3 tree insertions.

        start = System.nanoTime();
        for (int i = 0; i < data.length; i++) {
            t23.insert(data[i]);
        }
        stop = System.nanoTime();
        elapsed = stop - start;

        System.out.println("\nInserting " + data.length
                + " integers into a 2-3 tree: " + elapsed + " nanoseconds.");

        // Time the 2-3 tree searches.

        allFound = true;
        start = System.nanoTime();
        for (int i = 0; i < data.length; i++) {
            allFound = allFound && t23.search(data[i]);
        }
        stop = System.nanoTime();
        elapsed = stop - start;

        System.out.println("\nSearching for the " + data.length
                + " integers in the 2-3 tree: " + elapsed + " nanoseconds.\n");
        System.out.println("All items inserted were "
                + (allFound ? "" : "not ") + "found.\n");
        System.out.println("The 2-3 tree "
                + ((t23.treeOK()) ? "passes " : "does NOT pass ") + "all sanity checks.");

        // Print out the first 20 integers in the 2-3 tree.

        System.out.println("\nThe smallest 20 integers in the 2-3 tree:");
        t23.printTree();

    } // end test23TreeOnSequence

} // end class A5Solution


/***********************************************************************************
 ***********************************************************************************
 * class TwoThreeTree - implements search, insert and print in a leaf-based 2-3 tree
 ***********************************************************************************
 ************************************************************************************/

class TwoThreeTree {

    /*************************************************************************
     *************************************************************************
     * class TwoThreeNode
     *
     *   A node in a leaf-based 2-3 tree:
     *   - could be either a leaf (no children) or an 
     *     interior node (with 2 or 3 children)
     *   - data are stored only in leaves
     *   - interior nodes contain only index values to guide 
     *     searches to the correct leaf.
     *
     *   Leaf:
     *   - contains exactly one data item and no children
     *   - therefore, the key array is of size 1 and the child array is not
     *     allocated.
     *
     *   Interior node:
     *   - contains 1 or 2 index values (which do NOT count as data items,
     *     searches must always go to the leaves!) and 2 or 3 children.
     *   - contains numIndexValues index values and numIndexValues+1 children
     *   - key array is always size 2 and child array is always size 3.
     *   - if the interior node has only 2 children, then:
     *       - it contains only 1 index value, which is stored in key[0]
     *       - child[0] is its left child
     *       - child[1] is its right child
     *   - if the interior node has 3 children, then:
     *       - it contains 2 index values, and the smaller index value is
     *         key[0] and the larger index value is key[1] --- that is,
     *         the key array is kept in sorted order.
     *       - child[0] is the leftmost child (values < key[0])
     *         child[1] is the middle child (values >= key[0] and < key[1])
     *         child[2] is the rightmost child (value >= key[1])
     *
     *************************************************************************
     *************************************************************************/

    private class TwoThreeNode {

        public TwoThreeNode parent; // A pointer to this node's parent
        public int numIndexValues; // The number of index values stored in this node.
        // An interior node has numIndexValues+1 children.
        public int[] key; // The data value in a leaf, or the index values(s)
        // in an interior node.
        public TwoThreeNode[] child; // The children of an interior node (null if leaf)

        /***********************************************************************
         * Constructor (version 1)
         *
         * Creates a new leaf for key k with parent p.
         *
         * Notice that the child array is not allocated (a leaf has no children)
         * and the key array has size 1 because we will only store ONE data item
         * in a leaf.  (A leaf NEVER becomes an interior node, so this is OK.)
         *
         ************************************************************************/
        public TwoThreeNode(int k, TwoThreeNode p) {
            key = new int[1];
            key[0] = k;
            numIndexValues = 1;
            child = null;
            parent = p;
        }

        /***********************************************************************
         * Constructor (version 2)
         *
         *  Create a new interior node to contain one index value indexValue
         *  with parent p and two children left and right.
         *
         *  Notice that because a new interior node always has only two children,
         *  this node is currently using:
         *  - key[0], but not key[1]
         *  - child[0] (its left child) and child[1] (its right child),
         *    but child[2] is unused.
         *
         *  An interior node could gain another index value and child in
         *  a split-and-push-up in an insertion, so we always allocate
         *  big enough key[] and child[] arrays to allow for that.
         *
         ************************************************************************/
        public TwoThreeNode(int indexValue, TwoThreeNode p,
                            TwoThreeNode left, TwoThreeNode right) {
            key = new int[2];
            key[0] = indexValue;
            numIndexValues = 1;
            child = new TwoThreeNode[3];
            child[0] = left;
            child[1] = right;
            child[2] = null;
            parent = p;
        }

        /***********************************************************************
         * The usual accessors and mutators, which you are NOT required to use.
         ***********************************************************************/
        public int getNumIndexValues() {
            return numIndexValues;
        }

        public int getKey(int index) {
            return key[index];
        }

        public void setKey(int index, int newValue) {
            key[index] = newValue;
        }

        public TwoThreeNode getParent() {
            return parent;
        }

        public void setParent(TwoThreeNode newParent) {
            parent = newParent;
        }

        public TwoThreeNode getChild(int index) {
            return child[index];
        }

        public void setChild(int index, TwoThreeNode newChild) {
            child[index] = newChild;
        }

        /************************************************************
         *  isLeaf
         *
         *    Return true if the TwoThreeNode is a leaf; false
         *    otherwise.
         *
         *    A TwoThreeNode is a leaf if it has no children
         *    and if it has no children, then child is null;
         *    and child is null ONLY when there are no children.
         *
         **************************************************************/
        public boolean isLeaf() {
            return (child == null);
        }

        /************************************************************
         *  isInteriorNode
         *
         *    Return true if the TwoThreeNode is an interior node; false
         *    otherwise.
         *
         *    A TwoThreeNode is an interior if it has children
         *    and if it has children, then child is not null.
         *    (Child is null ONLY when there are no children.)
         *
         **************************************************************/
        public boolean isInteriorNode() {
            return (child != null);
        }

        /************************************************************
         *  parentChildPointersOK
         *
         *    A debugging method that helps sanity-check the 2-3 tree.
         *
         *    Returns true if, for "this" and all its descendants,
         *    each parent's child points back at its parent with its
         *    parent pointer; returns false only if a problem is detected.
         *
         *    The method does a traversal of the 2-3 tree starting
         *    at "this".  It checks if the children of "this" point
         *    at "this" with their parent pointers, and does the
         *    same check recursively at each child.  The base case
         *    of the recursion: if we're at a leaf, do nothing (return
         *    true) because they have no children, so no problem can
         *    happen at a leaf.
         *
         **************************************************************/
        public boolean parentChildPointersOK() {
            boolean pointersOK = true;
            if (isInteriorNode()) {
                for (int i = 0; i < this.numIndexValues + 1 && pointersOK; i++) {
                    pointersOK = pointersOK && (child[i].parent == this);
                    if (!pointersOK) {
                        System.out.print("Parent / child pointer problem: ");
                        System.out.print("parent contains ");
                        for (int j = 0; j < numIndexValues; j++)
                            System.out.print(key[j] + " ");
                        System.out.print("\n    " + i + "th child contains ");
                        for (int j = 0; j < child[i].numIndexValues; j++)
                            System.out.print(child[i].key[j] + " ");
                        System.out.println();
                    }
                    pointersOK = pointersOK && (child[i].parentChildPointersOK());
                } // end for i
            } // end if child != null
            return pointersOK;
        } // end parentChildPointersOK

        /************************************************************
         *  valuesOK
         *
         *    A debugging method that helps sanity-check the 2-3 tree.
         *
         *    Returns true if, for "this" and all its descendants,
         *    each index value in an interior node (could be 1 or 2)
         *    is > the largest value stored in the child to its left
         *    and <= the smallest value stored in the child to its right,
         *    and is also > the largest data value stored in any leaf
         *    descendant of the child to its left and <= the smallest
         *    data value stored in any leaf descendant of its child to
         *    its right.
         *    Returns false only if a problem is detected.
         *
         *    The method does a traversal of the 2-3 tree starting
         *    at "this".  It checks each index value in "this" against
         *
         *    at "this" with their parent pointers, and does the
         *    same check recursively at each child.  The base case
         *    of the recursion: if we're at a leaf, do nothing (return
         *    true) because they have no children, so no problem can
         *    happen at a leaf.
         *
         **************************************************************/
        public boolean valuesOK() {
            boolean valuesWork = true;
            if (isInteriorNode()) {
                if (child[0].getMaxDataValue() < key[0]
                        && key[0] <= child[1].getMinDataValue()) {
                    if (child[0].key[child[0].numIndexValues - 1] < key[0]
                            && key[0] <= child[1].key[0]) {
                        valuesWork = child[0].valuesOK() && child[1].valuesOK();
                        if (numIndexValues == 2) {
                            if (key[0] < key[1]) {
                                if (child[1].getMaxDataValue() < key[1]
                                        && key[1] <= child[2].getMinDataValue()) {
                                    if (child[1].key[child[1].numIndexValues - 1] < key[1]
                                            && key[1] <= child[2].key[0]) {
                                        valuesWork = child[2].valuesOK();
                                    } else {
                                        System.out.println("2nd index value " + key[1]
                                                + " is wrong with "
                                                + " left child index value "
                                                + child[1].key[child[1].numIndexValues - 1]
                                                + " or right child index value "
                                                + child[2].key[0]);
                                        valuesWork = false;
                                    }
                                } else {
                                    System.out.println("2nd index value " + key[1]
                                            + " is wrong with "
                                            + "max left leaf descendant "
                                            + child[1].getMaxDataValue()
                                            + " or min right leaf descendant "
                                            + child[2].getMinDataValue());
                                    valuesWork = false;
                                }
                            } else {
                                System.out.println("Index values are not in sorted order: key[0] = " + key[0]
                                        + " key[1] = " + key[1]);
                                valuesWork = false;
                            }
                        } // end if numIndexValue == 2
                    } else {
                        System.out.println("1st index value " + key[0] + " is wrong with "
                                + " left child index value "
                                + child[0].key[child[0].numIndexValues - 1]
                                + " or right child index value "
                                + child[1].key[0]);
                        valuesWork = false;
                    }
                } else {
                    System.out.println("1st index value " + key[0] + " is wrong with "
                            + "max left descendant " + child[0].getMaxDataValue()
                            + " or min right descendant "
                            + child[1].getMinDataValue());
                    valuesWork = false;
                }
            }
            return valuesWork;
        } // end valuesOK

        /************************************************************
         *  getMinDataValue
         *
         *    Return the smallest data item stored in any leaf
         *    descendant of "this".
         *
         *    Goes to the leftmost child until it reaches a leaf,
         *    and returns key[0] stored in the leaf.
         *
         **************************************************************/
        private int getMinDataValue() {
            TwoThreeNode curr = this;

            while (curr.isInteriorNode()) {
                curr = curr.child[0];
            }
            return curr.key[0];
        } // end getMinDataValue

        /************************************************************
         *  getMaxDataValue
         *
         *    Return the largest data item stored in any leaf
         *    descendant of "this".
         *
         *    Goes to the rightmost child until it reaches a leaf,
         *    and returns key[0] stored in the leaf.
         *
         **************************************************************/
        private int getMaxDataValue() {
            TwoThreeNode curr = this;

            while (curr.isInteriorNode()) {
                curr = curr.child[curr.numIndexValues];
            }
            return curr.key[0];
        } // end getMaxDataValue

        /* chooses which child matches or directs to the key and returns it. */
        public TwoThreeNode moveToChild(int searchKey) {
            int index = numIndexValues - 1;
            while (index >= 0 && searchKey < key[index]) {
                index--;
            }
            return child[index + 1];
        }

    } // end class TwoThreeNode

    /**************************************************************
     **************************************************************
     * Back to class TwoThreeTree
     **************************************************************
     **************************************************************/

    private TwoThreeNode root;
    public int numToPrint = 20;// the number of prints that are allowed in a 2-3 tree

    /**************************************************************
     * Constructor
     *
     *    Creates an empty leaf-based 2-3 tree.
     **************************************************************/
    public TwoThreeTree() {
        root = null;
    }

    /************************************************************
     * searchToLeaf
     *
     * PURPOSE: To return the leaf where a search for key searchKey
     *          ends in the 2-3 tree.
     *          If the tree is completely empty, return null.
     *
     **************************************************************/
    private TwoThreeNode searchToLeaf(int searchKey) {
        TwoThreeNode curr = root;
        if (root != null) {
            while (curr.child != null) {
                curr.moveToChild(searchKey);
            }
        }
        return curr;

    } // end searchToLeaf

    /************************************************************
     * search
     *
     *    Search for key searchKey in the 2-3 tree, returning
     *    true if searchKey is found and 
     *    false otherwise.
     *
     **************************************************************/
    public boolean search(int searchKey) {

        boolean found;
        TwoThreeNode curr = searchToLeaf(searchKey);
        found = searchKey == curr.key[0];
        return found;

    } // end search


    /************************************************************
     * insert
     *
     *    Insert new key newKey into the tree.
     *     - First, search for newKey.
     *     - We end up at a leaf.
     *     - If the leaf contains newKey, simply return (no duplicates!)
     *     - Otherwise, handle the insertion (including any splitting and
     *       pushing up required).
     *
     **************************************************************/
    public void insert(int newKey) {
        TwoThreeNode curr = searchToLeaf(newKey);
        if (curr != null) {//parent is found
            addChild(curr, new TwoThreeNode(newKey, curr.parent));
            pushToParent(curr.parent);
        } else {//root is empty
            root = new TwoThreeNode(newKey, null);
        }
    } // end insert

    public void swap(TwoThreeNode[] a, int posA, int posB) {
        TwoThreeNode temp = a[posA];
        a[posA] = a[posB];
        a[posB] = temp;
    }
    public void swap(int[] a, int posA, int posB) {
        int temp = a[posA];
        a[posA] = a[posB];
        a[posB] = temp;
    }

    public void addChild(TwoThreeNode matchingPosition, TwoThreeNode newItem) {
        int index = matchingPosition.parent.numIndexValues - 1;
        TwoThreeNode parent = matchingPosition.parent;
        while (index >= 0 && newItem.key[0] < parent.key[index]) {
            swap(parent.child,index,index+1);
            index--;
        }
        matchingPosition.parent.child[index + 1]=newItem;
        matchingPosition.parent.numIndexValues++;
    }

    public void pushToParent(TwoThreeNode parent) {
        if(parent.numIndexValues<3) {//overfull parent
            TwoThreeNode grandParent = parent.parent;
            swap(grandParent.key,grandParent.numIndexValues-1,grandParent.numIndexValues);
            grandParent.key[grandParent.numIndexValues-1]=parent.key[parent.numIndexValues-1];
        }
    }

    /************************************************************
     *  printTree
     *
     *    Print an appropriate message if the tree is empty;
     *    otherwise, call a recursive method to print the
     *    first twenty keys in an inorder traversal.
     *    NOTE: index values are NOT printed, only real data values
     *          which are stored exclusively in the leaves are printed.
     *
     **************************************************************/
    public void printTree() {
        if (root != null) {
            printTree(root);
        } else {
            System.out.println("Tree is empty");
        }
        if (numToPrint <= 0) {
            System.out.print("...\n");
        }
        numToPrint = 20;// reset the print counter
    } // end printTree

    public void printTree(TwoThreeNode x) {
        if (numToPrint > 0) {
            if (x.isInteriorNode()) {//x is an interior node
                for (TwoThreeNode i : x.child) {
                    printTree(i);
                }
            } else {//x is a leaf
                System.out.println(x.key[0] + " ");
                numToPrint--;
            }
        }
    }

    /************************************************************
     *  treeOK
     *
     *    A debugging method that sanity-checks the 2-3 tree.
     *
     *    It checks that each child of a parent points to its parent.
     *    (A recursive helper method does this check in a 
     *    traversal of the tree.)
     *
     *    It checks that each index value correctly differentiates
     *    between the two children on either side of it.
     *    (Another recursive helper method does this check in
     *    another traversal of the tree.)
     *
     *    It returns true only if NO problems are found; otherwise,
     *    if any problem is found, it returns false.
     *
     **************************************************************/
    public boolean treeOK() {
        boolean allOK = true; // An empty tree has NO problems!

        if (root != null) {
            if (root.parent == null) {
                if (root.parentChildPointersOK()) {
                    allOK = root.valuesOK();
                    if (!allOK)
                        System.out.println("Error: something wrong with values");
                } else {
                    allOK = false;
                    System.out.println("ERROR: something wrong with parent/child pointers");
                }
            } else {
                allOK = false;
                System.out.println("ERROR: root's parent pointer is NOT null!");
            }
        }
        return allOK;
    } // end treeOK

} // end class TwoThreeTree


/***********************************************************************************
 ***********************************************************************************
 * class BST - implements search, insert and print in a binary search tree (BST)
 ***********************************************************************************
 ************************************************************************************/

class BST {


    /**************************************************************
     **************************************************************
     * class BSTNode - implements a binary search tree node
     **************************************************************
     **************************************************************/

    private class BSTNode {
        public int item;
        public BSTNode left;
        public BSTNode right;

        /**************************************************************
         * Constructor
         *
         *    Creates a leaf containing the new item.
         *
         **************************************************************/
        public BSTNode(int newItem) {
            item = newItem;
            left = null;
            right = null;
        }

        /* You don't need the following methods, but you can use them if you want to. */
        public int getItem() {
            return item;
        }

        public BSTNode getLeft() {
            return left;
        }

        public BSTNode getRight() {
            return right;
        }

        public void setLeft(BSTNode l) {
            left = l;
        }

        public void setRight(BSTNode r) {
            right = r;
        }
        // No, you can't have setItem(). (You really shouldn't use it in this application.)

    } // end class BSTNode


    /**************************************************************
     **************************************************************
     * Back to class BST
     **************************************************************
     **************************************************************/

    private BSTNode root;
    private int numToPrint = 20;

    /************************************************************
     *  Constructor
     *
     *    Create an empty BST.
     *
     **************************************************************/
    public BST() {

        root = null;

    }

    /************************************************************
     *  insert
     *
     *    Insert newKey into the BST, if newKey is not already
     *    in the tree. (No duplicates!)
     *
     **************************************************************/
    public void insert(int newKey) {

        BSTNode curr = root;
        BSTNode prev = null;

        while (curr != null && curr.item != newKey) {
            prev = curr;
            if (curr.item > newKey) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
        }//either curr==null or newKey is a duplicate
        if (prev != null) {//normal insertion
            if (prev.item > newKey) {
                prev.left = new BSTNode(newKey);
            } else {
                prev.right = new BSTNode(newKey);
            }
        } else if (curr == null) {//tree is empty
            root = new BSTNode(newKey);
        }//do nothing if newKey is a duplicate
    } // end insert

    /************************************************************
     *  search
     *
     *    Search for searchKey in a BST.
     *
     *    Return true if searchKey is found; false otherwise.
     *
     **************************************************************/
    public boolean search(int searchKey) {
        BSTNode curr = root;
        boolean found = false;
        while (curr != null && curr.item != searchKey) {
            if (curr.item > searchKey) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
        }//curr is either null xor the desired item
        if (curr != null) {//not null, there for the desired item
            found = true;
        }
        return found;//is null, therefore not the desired item
    } // end search

    /************************************************************
     *  printTree
     *
     *    Print an appropriate message if the tree is empty;
     *    otherwise, call a recursive method to print the
     *    first twenty keys in an inorder traversal.
     *
     **************************************************************/
    public void printTree() {
        if (root != null) {
            printTree(root);
        } else {
            System.out.println("Tree is empty");
        }
        if (numToPrint <= 0) {
            System.out.println("...\n");
        }
        numToPrint = 20;// reset the print counter
    } // end printTree

    public void printTree(BSTNode x) {
        if (numToPrint > 0) {
            printTree(x.left);
            System.out.println(x.item + " ");
            numToPrint--;
            printTree(x.right);
        }
    }

} // end class BST

