package com.example.encrypterfx.model;

import java.nio.file.Path;

public class FileData {

    public String fileName;
    public Path path;

    private long totalTime;

    public FileData(String fileName, Path path, long totalTime) {
        this.fileName = fileName;
        this.path = path;
        this.totalTime = totalTime;
    }

    public String getFileName() {
        return fileName;
    }


    public Path getPath() {
        return path;
    }


    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
