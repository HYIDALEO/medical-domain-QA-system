package com.appleyk.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class TopologyGraph<N, E> {
    protected Table<N, N, Set<E>> graph = HashBasedTable.create();
    protected Set<N> isolatedNodes = new HashSet<>();

    //N sn  a
    //N en  b
    //E e   a相关b
    void putEdge(N sn, N en, E e) {
        Preconditions.checkNotNull(sn);
        Preconditions.checkNotNull(en);
        Preconditions.checkNotNull(e);
        if (!this.graph.contains(sn, en)) {
            this.graph.put(sn, en, new HashSet<>());
        }
        
        this.graph.get(sn, en).add(e);
        
        //如果node是sn或en，则不再是孤立点。
        this.isolatedNodes.removeIf(node -> node.equals(sn) || node.equals(en));
    }

    void putNode(N n) {
        Preconditions.checkNotNull(n);
        this.isolatedNodes.add(n);
    }

    public Set<N> getNodes() {
        Set<N> sets = new HashSet<>();
        sets.addAll(this.graph.columnKeySet());
        sets.addAll(this.graph.rowKeySet());
        sets.addAll(isolatedNodes);
        return sets;
    }

    boolean hasNode(N n) {
        return this.graph.containsRow(n) || this.graph.containsColumn(n) || this.isolatedNodes.contains(n);
    }

    boolean hasEdge(N sn, N en) {
        return this.graph.contains(sn, en);
    }

    boolean isConnected(N sn, N en) {
        return this.hasEdge(sn, en);
    }

    boolean isConnected(N sn, N en, E e) {
        return this.graph.contains(sn, en) && this.graph.get(sn, en).contains(e);
    }

    int nodeSize() {
        Set<N> nodes = new HashSet<>();
        nodes.addAll(this.isolatedNodes);
        nodes.addAll(this.graph.rowKeySet());
        nodes.addAll(this.graph.columnKeySet());
        return nodes.size();
    }

    Set<E> getEdges(N sn, N en) {
        return this.hasEdge(sn, en) ? this.graph.get(sn, en) : new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    List<Path> findPaths(N sn, N en) {
        Preconditions.checkNotNull(sn);
        Preconditions.checkNotNull(en);
        Preconditions.checkArgument(this.hasNode(sn), "%s节点不存在", sn);
        Preconditions.checkArgument(this.hasNode(en), "%s节点不存在", en);

        List<Path> paths = new ArrayList<>();
        Stack<N> stack = new Stack<>();
        Set<N> isVisited = new HashSet<>();
        stack.push(sn);
        isVisited.add(sn);
        while (!stack.isEmpty()) {
            N n = stack.peek();
            if (n.equals(en)) {
                List<N> nList = new ArrayList<>();
                nList.addAll(stack);
                //由于两个节点之间可能存在不止一条边，所以这一条路径可能是多条路径
                List<Path> temp = new ArrayList<>();
                temp.add(new Path());
                for (int i = 0; i < nList.size() - 1; i++) {
                    N n1 = nList.get(i);
                    N n2 = nList.get(i + 1);
                    Set<E> edges = this.getEdges(n1, n2);
                    List<Path> ttemp = new ArrayList<>();
                    for (Path path : temp) {
                        //将这条路径复制成多条
                        edges.forEach((e) -> {
                            try {
                                Path nPath = (Path) path.clone();
                                nPath.addEdge(n1, n2, e);
                                ttemp.add(nPath);
                            } catch (CloneNotSupportedException e1) {
                            }
                        });
                    }
                    temp = ttemp;
                }
                paths.addAll(temp);
            }
            Set<N> ns = this.graph.row(n).keySet();
            Iterator<N> it = ns.iterator();
            while (it.hasNext()) {
                N nn = it.next();
                if (!isVisited.contains(nn)) {
                    stack.push(nn);
                    isVisited.add(nn);
                    break;
                }
            }
            if (stack.peek().equals(n)) {
                stack.pop();
            }
        }
        return paths;
    }

    static class Path<N, E> {

        List<N> nodes = new ArrayList<>();
        List<E> edges = new ArrayList<>();

        void addEdge(N sn, N en, E e) {
            if (this.nodes.isEmpty()) {
                this.nodes.add(sn);
            }
            this.nodes.add(en);
            this.edges.add(e);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            super.clone();
            Path<N, E> path = new Path<>();
            path.edges.addAll(this.edges);
            path.nodes.addAll(this.nodes);
            return path;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("");
            if (!this.nodes.isEmpty()) {
                for (int i = 0; i < this.nodes.size() - 1; i++) {
                    sb.append(this.nodes.get(i).toString());
                    sb.append("--");
                    sb.append(this.edges.get(i).toString());
                    sb.append("-->");
                }
                sb.append(this.nodes.get(this.nodes.size() - 1).toString());
            }
            return sb.toString();
        }
    }
}
