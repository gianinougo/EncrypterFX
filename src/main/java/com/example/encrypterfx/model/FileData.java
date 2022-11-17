package com.example.encrypterfx.model;

public class FileData {

    public String fileName;
    public String imagePath;


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


    @Override
    public String toString() {
        return "FileData{" +
                "fileName='" + fileName + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
