package com.example.encrypterfx.model;

import java.nio.file.Path;


/*
Class to define the file data structure
 */
public class FileData {

    public String fileName;
    public Path path;
    private long totalTime;


    /*
        Constructor method file data structure
     */
    public FileData(String fileName, Path path, long totalTime) {
        this.fileName = fileName;
        this.path = path;
        this.totalTime = totalTime;
    }

    /*
    get file name
     */
    public String getFileName() {
        return fileName;
    }

    /*
    get path name
    @param path
     */
    public Path getPath() {
        return path;
    }

    /*
    get total time
    @param totalTime
     */
    public long getTotalTime() {
        return totalTime;
    }

    /*
    Set the total time
    @param totalTime
     */
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    /*
    Overrides toString() method
    @return fileName
     */
    @Override
    public String toString() {
        return fileName;
    }
}
