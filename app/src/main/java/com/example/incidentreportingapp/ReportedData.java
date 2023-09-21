package com.example.incidentreportingapp;

public class ReportedData {
    private String author;
    private String location;
    private String category;
    private String description;
    private String imagePaths;
    private String currentTimeAndDate;
    private String videoUrl;


    public ReportedData() {
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

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getCurrentTimeAndDate() {
        return currentTimeAndDate;
    }

    public void setCurrentTimeAndDate(String currentTimeAndDate) {
        this.currentTimeAndDate = currentTimeAndDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
