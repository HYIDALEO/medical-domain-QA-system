package com.appleyk.answer.Graph;

import java.util.HashSet;
import java.util.Set;

import com.appleyk.node.KbEntity;



public class ShortestDistance implements Comparable<ShortestDistance>{
    public int distance = 0;
    public int unreachable_count = 0;
    public Set<KbEntity> nodes = new HashSet<>();

    @Override
    public int compareTo(ShortestDistance o) {
        if (unreachable_count < o.unreachable_count) {
            return -1;
        } else if (unreachable_count > o.unreachable_count) {
            return 1;
        } else {
            return Integer.compare(distance, o.distance);
        }
    }
}
