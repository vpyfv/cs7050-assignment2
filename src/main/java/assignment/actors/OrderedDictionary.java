package assignment.actors;

import java.util.ArrayList;
import java.util.List;

public class OrderedDictionary implements OrderedDictionaryADT {

    Node root;

    OrderedDictionary() {
        root = null;
    }

    /**
     * Returns the Record object with key k, or it returns null if such a record
     * is not in the dictionary.
     *
     * @param k
     * @return
     * @throws assignment/actors/DictionaryException.java
     */
    @Override
    public ActorRecord find(DataKey k) throws DictionaryException {
        Node current = root;
        int comparison;
        if (root.isEmpty()) {
            throw new DictionaryException("There is no record matches the given key");
        }

        while (true) {
            comparison = current.getData().getDataKey().compareTo(k);
            if (comparison == 0) { // key found
                return current.getData();
            }
            if (comparison == 1) {
                if (current.getLeftChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getLeftChild();
            } else if (comparison == -1) {
                if (current.getRightChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getRightChild();
            }
        }

    }

    /**
     * Inserts r into the ordered dictionary. It throws a DictionaryException if
     * a record with the same key as r is already in the dictionary.
     *
     * @param r
     * @throws DictionaryException
     */
    @Override
    public void insert(ActorRecord r) throws DictionaryException {
        root = insertNode(root, r);
    }

    private Node insertNode(Node current, ActorRecord newRecord) throws DictionaryException {
        if (current == null) {
            return new Node(newRecord);
        }
        //if key is less than current node then insert left side
        if (newRecord.getDataKey().compareTo(current.getData().getDataKey()) < 0) {
            current.setLeftChild(insertNode(current.getLeftChild(), newRecord));
            //if key is greater than current node then insert right side
        } else if (newRecord.getDataKey().compareTo(current.getData().getDataKey()) > 0) {
            current.setRightChild(insertNode(current.getRightChild(), newRecord));
        } else {
            //if key exists throw exception
            throw new DictionaryException("A record with the same key already exists.");
        }
        return current;
    }

    /**
     * Removes the record with Key k from the dictionary. It throws a
     * DictionaryException if the record is not in the dictionary.
     *
     * @param k
     * @throws DictionaryException
     */
    @Override
    public void remove(DataKey k) throws DictionaryException {
        root = removeNode(root, k);
    }

    private Node removeNode(Node root, DataKey k) throws DictionaryException {
        if (root == null) {
            return null;
        }
        int comparison = k.compareTo(root.getData().getDataKey());
        //if key is less than root make left child as root and root as null
        if (comparison < 0) {
            root.setLeftChild(removeNode(root.getLeftChild(), k));
            //if key is greater than root make the right as root and root as null
        } else if (comparison > 0) {
            root.setRightChild(removeNode(root.getRightChild(), k));
        } else {
            //this is the node to be deleted

            //Node with only one child or none
            if (!root.hasLeftChild()) {
                return root.getRightChild();
            } else if (!root.hasRightChild()) {
                return root.getLeftChild();
            }

            //Node with two children : get next successor and make that as root
            ActorRecord successor = successor(k);
            root.setData(successor);

            //remove the successor
            root.setRightChild(removeNode(root.getRightChild(), successor.getDataKey()));

        }
        return root;
    }

    /**
     * Returns the successor of k (the record from the ordered dictionary with
     * smallest key larger than k); it returns null if the given key has no
     * successor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws DictionaryException
     */
    @Override
    public ActorRecord successor(DataKey k) throws DictionaryException {
        Node current = root;
        Node successor = null;
        if (root == null) {
            throw new DictionaryException("database is empty");
        }
        while (current != null) {
            int comparison = current.getData().getDataKey().compareTo(k);
            if (comparison <= 0) {
                current = current.getRightChild();
            } else {
                successor = current;
                current = current.getLeftChild();
            }
        }
        if (successor == null) {
            throw new DictionaryException("There is the last record");
        }
        return successor.getData();
    }


    /**
     * Returns the predecessor of k (the record from the ordered dictionary with
     * largest key smaller than k; it returns null if the given key has no
     * predecessor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws DictionaryException
     */
    @Override
    public ActorRecord predecessor(DataKey k) throws DictionaryException {
        Node current = root;
        Node predecessor = null;
        if (root == null) {
            throw new DictionaryException("database is empty");
        }
        while (current != null) {
            int comparison = current.getData().getDataKey().compareTo(k);
            if (comparison < 0) {
                predecessor = current;
                current = current.getRightChild();
            } else {
                current = current.getLeftChild();
            }
        }
        if (predecessor == null) {
            throw new DictionaryException("There is the first record");
        }
        return predecessor.getData();
    }

    /**
     * Returns the record with smallest key in the ordered dictionary. Returns
     * null if the dictionary is empty.
     *
     * @return
     */
    @Override
    public ActorRecord smallest() throws DictionaryException {
        if (root.isEmpty()) {
            throw new DictionaryException("database is empty");
        }
        Node smallestNode = root;
        while (smallestNode.hasLeftChild()) {
            smallestNode = smallestNode.getLeftChild();
        }
        return smallestNode.getData();
    }

    /*
     * Returns the record with largest key in the ordered dictionary. Returns
     * null if the dictionary is empty.
     */
    @Override
    public ActorRecord largest() throws DictionaryException {
        if (root.isEmpty()) {
            throw new DictionaryException("database is empty");
        }
        Node largetNode = root;
        while (largetNode.hasRightChild()) {
            largetNode = largetNode.getRightChild();
        }
        return largetNode.getData();
    }

    /* Returns true if the dictionary is empty, and true otherwise. */
    @Override
    public boolean isEmpty() {
        return root.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        inOrderTraversal(root, builder);
        return builder.toString();
    }

    private void inOrderTraversal(Node node, StringBuilder builder) {
        if (node == null) {
            return;
        }
        // Traverse the left subtree
        inOrderTraversal(node.getLeftChild(), builder);
        // Visit the node
        if (!node.isEmpty()) { // Check if the node is not empty
            builder.append(node.getData().toString());
            builder.append(", "); // Add a separator between nodes
        }
        // Traverse the right subtree
        inOrderTraversal(node.getRightChild(), builder);
    }

    public List<ActorRecord> contains(DataKey key) throws DictionaryException {
        List<ActorRecord> matches = new ArrayList<>();
        contains(root, key, matches);
        return matches;
    }

    private void contains(Node node, DataKey key, List<ActorRecord> matches) {
        if (node == null || node.isEmpty()) {
            return;
        }

        // Check if the current node's name contains the name and generation should be equal
        if (node.getData().getDataKey().getGeneration() == key.getGeneration() && node.getData().getDataKey().getName().toLowerCase().contains(key.getName().toLowerCase())) {
            matches.add(node.getData()); // Add match to the list
        }

        // Recursively search in left and right subtrees
        contains(node.getLeftChild(), key, matches);
        contains(node.getRightChild(), key, matches);
    }
}
