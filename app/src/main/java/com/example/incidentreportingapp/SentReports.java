package com.example.incidentreportingapp;

public class SentReports {
    private String author;
    private String location;
    private String category;
    private String description;
    private String imagePaths;
    private String videoPaths;
    private String currentTimeAndDate;


    public void setVideoUrl(String videoUrl) {
    }

    public void setCurrentTimeAndDate(String currentTimeAndDate) {
        this.currentTimeAndDate = currentTimeAndDate;
    }

    @Override
    public String toString() {
        return "SentReports{" +
                "author='" + author + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", imagePaths='" + imagePaths + '\'' +
                ", videoPaths='" + videoPaths + '\'' +
                '}';
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }

    public SentReports() {
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
