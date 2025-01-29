package org.dev.entity;

import java.util.List;

public class redditPostData {

    private List<redditChildren> children;
    private String after;
    private int dist;
    private String modhash;

    // Getters and Setters
    public List<redditChildren> getChildren() {
        return children;
    }
    public void setChildren(List<redditChildren> children) {
        this.children = children;
    }
    public String getAfter() {
        return after;
    }
    public void setAfter(String after) {
        this.after = after;
    }
    public int getDist() {
        return dist;
    }
    public void setDist(int dist) {
        this.dist = dist;
    }
    public String getModhash() {
        return modhash;
    }
    public void setModhash(String modhash) {
        this.modhash = modhash;
    }
    @Override
    public String toString() {
        return "RedditPostData{" +
                "children=" + children +
                ", after='" + after + '\'' +
                ", dist=" + dist +
                ", modhash='" + modhash + '\'' +
                '}';
    }
}
