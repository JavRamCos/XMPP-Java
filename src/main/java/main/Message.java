package main;

import java.util.List;

public class Message {
    String type;
    String from;
    String to;
    int hop_count;
    String message;
    List<List<String>> table_info;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getHop_count() {
        return hop_count;
    }

    public void setHop_count(int hop_count) {
        this.hop_count = hop_count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<List<String>> getTable_info() {
        return table_info;
    }

    public void setTable_info(List<List<String>> table_info) {
        this.table_info = table_info;
    }
}
