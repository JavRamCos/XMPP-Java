package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DistanceVector {
    List<List<String>> table_info;
    public DistanceVector() {
        this.table_info = new ArrayList<>();
    }

    public void initialStep(String node_name, List<List<String>> topology) {
        this.table_info.add(Arrays.asList(node_name, "0", node_name));
        // DESTINATION NODE, DISTANCE, HOP (NODE)
        for(List<String> relation : topology) {
            String l_node = relation.get(0);
            String r_node = relation.get(2);
            List<String> stored_nodes = getTableIndex(0);
            // CHECK IF RELATION IS VALID)
            if(l_node.equals(r_node)) continue;
            // ADD ELEMENT
            if(l_node.equals(node_name) || r_node.equals(node_name)) {
                // RELATION HAS (node_name)
                String node = l_node.equals(node_name) ? r_node : l_node;
                if(stored_nodes.contains(node)) {
                    // REMOVE OLD ENTRY
                    List<String> old_entry = getElement(node);
                    this.table_info.remove(old_entry);
                }
                // ADD NEW ENTRY
                this.table_info.add(Arrays.asList(node, relation.get(1), node));
            } else {
                // NODES ARE NOT (node_name)
                // 1ST NODE
                if(!stored_nodes.contains(l_node)) this.table_info.add(Arrays.asList(l_node, "-1", "-"));
                // 2ND NODE
                if(!stored_nodes.contains(r_node)) this.table_info.add(Arrays.asList(r_node, "-1", "-"));
            }
        }
    }

    public void updateTable(String node_name, Message msg) {
        // CHECK IF NODE NAME IS VALID
        List<String> entry = getElement(msg.from);
        if(entry == null) {
            System.out.println(msg.from + " is not recognized");
            return;
        }
        // UPDATE DISTANCES
        int self_dist = Integer.parseInt(entry.get(1));
        for(List<String> temp : msg.getTable_info()) {
            if(temp.get(0).equals(node_name)) continue;
            // CHECK NODE DISTANCE (-1 == INFINITE)
            int node_dist = Integer.parseInt(temp.get(1));
            if(node_dist < 0) continue;
            // CHECK IF NODE IS VALID
            List<String> self_node = getElement(temp.get(0));
            if(self_node == null)  {
                System.out.println(temp.get(0) + " is not recognized");
                return;
            }
            // COMPARE SELF & NODE DISTANCES
            if(self_dist + node_dist == Integer.parseInt(self_node.get(1))) {
                if(self_node.get(2).contains(msg.from)) continue;
                this.table_info.remove(self_node);
                self_node.set(2, self_node.get(2)+","+msg.from);
                this.table_info.add(self_node);
            } else if(self_dist + node_dist < Integer.parseInt(self_node.get(1))) {
                this.table_info.remove(self_node);
                this.table_info.add(Arrays.asList(self_node.get(0), Integer.toString(self_dist+node_dist),
                        temp.get(0)));
            }
        }
    }

    public List<String> getElement(String name) {
        // OBTAIN ENTRY IN RESULT TABLE (DESTINATION, DISTANCE, HOP)
        for(List<String> temp : this.table_info)
            if(temp.get(0).equals(name)) return temp;
        return null;
    }

    public List<String> getTableIndex(int index) {
        // OBTAIN ALL (index) ELEMENTS IN (source) LIST
        List<String> result = new ArrayList<>();
        for(List<String> temp : this.table_info)
            result.add(temp.get(index));
        return result;
    }

    public void showCurrentTable() {
        // DISPLAY CURRENT TABLE INFORMATION
        System.out.println("======= DISTANCE VECTOR =======");
        for(List<String> temp : this.table_info) {
            System.out.println("Node: "+temp.get(0)+", Distance: "+temp.get(1)+", Hop: "+temp.get(2));
        }
        System.out.println("");
    }
}
