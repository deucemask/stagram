package com.codepath.gangstagram;

import java.util.Date;

/**
 * Created by dmaskev on 10/11/15.
 */
public class FeedItem {

    public static class User {
        public String name;
        public String avatar;

        public User(String name, String avatar) {
            this.name = name;
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }

    public String imageUrl;
//    public String imageHeight;
    public String caption;
    public Long likesCnt;
    public User user;
    public Date date;

    public long getRelDate() {
        return System.currentTimeMillis() - this.date.getTime();
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "imageUrl='" + imageUrl + '\'' +
                ", caption='" + caption + '\'' +
                ", userName='" + user + '\'' +
                ", likesCnt='" + likesCnt + '\'' +
                ", createdAt=" + date +
                '}';
    }
}
