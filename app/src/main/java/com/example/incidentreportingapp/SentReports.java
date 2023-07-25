package com.example.incidentreportingapp;

public class SentReports {
    private String author;
    private String location;
    private String category;
    private String description;
    private String imagePaths;
    private String videoPaths;
    private String currentTimeAndDate;
    private String videoUrl;


    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCurrentTimeAndDate() {
        return currentTimeAndDate;
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

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getVideoPaths() {
        return videoPaths;
    }

    public void setVideoPaths(String videoPaths) {
        this.videoPaths = videoPaths;
    }

    public SentReports() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
