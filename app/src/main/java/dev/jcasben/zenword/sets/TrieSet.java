package dev.jcasben.zenword.sets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class TrieSet {

    private class Node {
        private HashMap<Character, Node> childs;
        private boolean isFinalKey;
    }
    private Node root;

    public TrieSet() {
        root = null;
    }

    public boolean isEmpty() { return root == null; }

    public boolean contains(String key) {
        Node current = root;
        for (int i = 0; i < key.length(); i++) {
            Node node = current.childs.get(key.charAt(i));
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isFinalKey;
    }

    public boolean add(String key) {
        if (root == null) {
            root = new Node();
            root.childs = new HashMap<>();
        }
        Node current = root;
        boolean found;
        for (Character c : key.toCharArray()) {
            Node node = current.childs.get(c);
            if (node == null) {
                node = new Node();
                current.childs.put(c, node);
            }
            current = node;
        }

        found = current.isFinalKey;
        current.isFinalKey = true;
        return !found;
    }

    public UnsortedLinkedListSet<String> display() {
        char [] str = new char[7];
        UnsortedLinkedListSet<String> words = new UnsortedLinkedListSet<>();
        displayR(root, str, 0, words);
        return words;
    }

    private void displayR(Node node, char[] str, int level, UnsortedLinkedListSet words) {
        if (node.isFinalKey) {
            for (int i = level; i < str.length; i++) {
                str[i] = 0;
                words.add(Arrays.toString(str));
            }
        }

        Iterator<Character> iterator = node.childs.keySet().iterator();
        while (iterator.hasNext()) {
            char letter = iterator.next();
            str[level] = letter;
            displayR(node.childs.get(letter), str, level + 1, words);
        }
    }

}

