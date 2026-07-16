package io.github.filant.filtersmith.trie;

import java.util.HashMap;
import java.util.Map;

public final class ReverseDomainTrie {
  private final Node root = new Node();

  public boolean hasAncestor(String domain) {
    Node node = root;
    int end = domain.length();
    while (end > 0) {
      int start = previousLabelStart(domain, end);
      String label = domain.substring(start, end);
      node = node.children.get(label);
      if (node == null) {
        return false;
      }
      if (node.terminal && start > 0) {
        return true;
      }
      end = start - 1;
    }
    return false;
  }

  public void insert(String domain) {
    Node node = root;
    int end = domain.length();
    while (end > 0) {
      int start = previousLabelStart(domain, end);
      String label = domain.substring(start, end);
      node = node.children.computeIfAbsent(label, ignored -> new Node());
      end = start - 1;
    }
    node.terminal = true;
  }

  private int previousLabelStart(String domain, int end) {
    int start = end - 1;
    while (start >= 0 && domain.charAt(start) != '.') {
      start--;
    }
    return start + 1;
  }

  private static final class Node {
    private final Map<String, Node> children = new HashMap<>();
    private boolean terminal;
  }
}

