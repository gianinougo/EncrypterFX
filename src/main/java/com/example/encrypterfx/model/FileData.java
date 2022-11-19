package com.example.encrypterfx.model;

public class FileData {

    public String fileName;
    public String imagePath;

    private long totalTime;

    public FileData(String fileName, String imagePath, long totalTime) {
        this.fileName = fileName;
        this.imagePath = imagePath;
        this.totalTime = totalTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "fileName='" + fileName + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
