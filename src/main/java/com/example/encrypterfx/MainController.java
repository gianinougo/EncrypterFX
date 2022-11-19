package com.example.encrypterfx;

import com.example.encrypterfx.model.Encrypter;
import com.example.encrypterfx.model.FileData;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.shape.Path;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainController {
    private static String FILE_FOLDER = "/files/";
    @FXML
    private Button btnSign;
    @FXML
    private Button btnCheck;


    private ThreadPoolExecutor executor;
    private ScheduledService<Boolean> service;
    @FXML
    private ListView<FileData> listFiles;

    Encrypter encrypter = new Encrypter(3);


    public void signFile(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();

        File chosenDirectory = dc.showDialog((Stage)((Node) actionEvent.getSource()).getScene().getWindow());
        if (chosenDirectory != null) {
            FILE_FOLDER = chosenDirectory.getAbsolutePath();
            listFiles.getItems().clear();
            btnSign.setDisable(true);
            btnCheck.setDisable(true);
            service.restart();
            sign(FILE_FOLDER);

        }
    }

    private void sign(String filePath) {

        try{
            if (!(Files.exists(Paths.get(Paths.get(filePath).getParent() + "/encripted/")))){
                Files.createDirectory(Paths.get(Paths.get(filePath).getParent() + "/encripted/"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        File location = new File(filePath);
        File[] contents = location.listFiles();
        for (File f: contents) {
            if (!f.isDirectory()){
                String fileName = f.toPath().getFileName().toString();
                if (!f.isDirectory()) {
                    executor.execute(() -> {
                        long t1 = System.currentTimeMillis();
                        callables.add(encrypter.checkSignature(f.toPath()));
                        Platform.runLater(()-> {
                            listFiles.getItems().add(new FileData(fileName, f.getPath(), 0));
                        });
                    });
                }
            }
        }
    }
}