/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Dag {
    private HashMap nodes = new HashMap();

    public boolean hasPredecessors(Object data) {
        Node from = this.findNode(data);
        return from != null && from.numPreds != 0;
    }

    public boolean hasSuccessors(Object data) {
        Node to = this.findNode(data);
        return to != null && !to.succs.isEmpty();
    }

    public boolean canFollow(Object query, Object base) {
        Node queryNode = this.findNode(query);
        Node baseNode = this.findNode(base);
        if (baseNode == null || queryNode == null) {
            return !base.equals(query);
        }
        return this.canFollow(queryNode, baseNode);
    }

    public boolean addEdge(Object srcData, Object dstData) {
        if (!this.canFollow(dstData, srcData)) {
            return false;
        }
        Node src = this.createNode(srcData);
        Node dst = this.createNode(dstData);
        if (src.succs.add(dst)) {
            ++dst.numPreds;
        }
        return true;
    }

    public boolean removeEdge(Object srcData, Object dstData) {
        Node src = this.findNode(srcData);
        Node dst = this.findNode(dstData);
        if (src == null || dst == null) {
            return false;
        }
        if (!src.succs.remove(dst)) {
            return false;
        }
        --dst.numPreds;
        if (dst.numPreds == 0 && dst.succs.isEmpty()) {
            this.nodes.remove(dstData);
        }
        if (src.numPreds == 0 && src.succs.isEmpty()) {
            this.nodes.remove(srcData);
        }
        return true;
    }

    public void removeNode(Object data) {
        Node n = this.findNode(data);
        if (n == null) {
            return;
        }
        Iterator it = n.succs.iterator();
        while (it.hasNext()) {
            Node succ = (Node)it.next();
            --succ.numPreds;
            if (succ.numPreds != 0 || !succ.succs.isEmpty()) continue;
            it.remove();
        }
        if (n.numPreds > 0) {
            it = this.nodes.values().iterator();
            while (it.hasNext()) {
                Node q = (Node)it.next();
                if (!q.succs.remove(n) || q.numPreds != 0 || !q.succs.isEmpty()) continue;
                it.remove();
            }
        }
    }

    private Node findNode(Object data) {
        if (data == null) {
            return null;
        }
        return (Node)this.nodes.get(data);
    }

    private Node createNode(Object data) {
        Node ret = this.findNode(data);
        if (ret != null) {
            return ret;
        }
        if (data == null) {
            return null;
        }
        ret = new Node(data);
        this.nodes.put(data, ret);
        return ret;
    }

    private boolean canFollow(Node query, Node base) {
        if (base == query) {
            return false;
        }
        for (Node n2 : this.nodes.values()) {
            n2.mark = false;
        }
        LinkedList<Node> fringe = new LinkedList<Node>();
        fringe.add(query);
        while (!fringe.isEmpty()) {
            Node n2;
            n2 = (Node)fringe.removeFirst();
            for (Node next : n2.succs) {
                if (next.mark) continue;
                if (next == base) {
                    return false;
                }
                next.mark = true;
                fringe.addLast(next);
            }
        }
        return true;
    }

    private static class Node {
        Object data;
        HashSet succs = new HashSet();
        int numPreds = 0;
        boolean mark;

        Node(Object data) {
            this.data = data;
        }
    }

}

