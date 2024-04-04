package com.example.assignment4.entity;

public class Review {

    String comment;
    Float rating;
    String email;

    public Review(String comment, Float rating, String email) {
        this.comment = comment;
        this.rating = rating;
        this.email = email;
    }

    public Review(){
        // Required empty public constructor
    }

    @Override
    public String toString() {
        return "Review{" +
                "comment='" + comment + '\'' +
                ", rating=" + rating +
                ", email='" + email + '\'' +
                '}';
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
