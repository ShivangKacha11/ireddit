package org.dev.entity;

public class redditPostResponse {

    private String kind;
    private redditPostData data;

    // Getters and Setters
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public redditPostData getData() {
        return data;
    }

    public void setData(redditPostData data) {
        this.data = data;
    }

//    @Override
//    public String toString() {
//        return "RedditPostResponse{" +
//                "kind='" + kind + '\'' +
//                ", data=" + data +
//                '}';
//    }
}
