package org.dev.entity;

public class redditChildren {

    private redditChildrenData data;

    // Getters and Setters
    public redditChildrenData getData() {
        return data;
    }
    public void setData(redditChildrenData data) {
        this.data = data;
    }
    @Override
    public String toString() {
        return "RedditChildren{" +
                "data=" + data +
                '}';
    }
}
