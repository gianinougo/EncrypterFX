package com.example.encrypterfx;
/*
Arreglar botones cuando finalice las tareas.
 */
import com.example.encrypterfx.model.Encrypter;
import com.example.encrypterfx.model.FileData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class MainController implements Initializable {

    /*
    Menages the main view controller
    @author Ugo Gianino
    @version 1.0
     */

    private static String FILE_FOLDER = "/files/";
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblResult;
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
            Path of = Path.of(filePath);
            if (!(Files.exists(Paths.get(of.getParent() + "/files/encrypted/")))){
                Files.createDirectory(Paths.get(of.getParent() + "/files/encrypted/"));
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
                executor.execute(() -> {
                    long t1 = System.currentTimeMillis();
                    try {
                        encrypter.encryption(f.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    long t2 = System.currentTimeMillis();
                        Platform.runLater(()-> {
                            listFiles.getItems().add(new FileData(fileName, f.toPath(), t2 -t1));
                        });
                });
            }
        }
        executor.shutdown();
    }


    public boolean check(String filePath){
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        List<Future<Boolean>> futures = new ArrayList<>();
        List<Callable<Boolean>> callables = new ArrayList<>();

        File location = new File(filePath);
        File[] contents = location.listFiles();

        for (File f : contents) {
            String fileName =  f.toPath().getFileName().toString();
            if (!f.isDirectory()) {
                long t1 = System.currentTimeMillis();
                callables.add(encrypter.checkSignature(f.toPath()));
                Platform.runLater(()-> {
                    listFiles.getItems().add(new FileData(fileName, f.toPath(), 0));
                });
            }
        }

        try{
            futures = executor.invokeAll(callables);
            executor.shutdown();
            return futures.stream().allMatch(future -> {
                try{
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (InterruptedException ex){
        }
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listFiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileData>() {
            @Override
            public void changed(ObservableValue<? extends FileData> observableValue,
                                FileData oldValue, FileData newValue) {

                executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
                Future<Boolean> future = null;

                if (newValue != null){
                    if (newValue.getPath().toString().contains("encrypted")){
                        future = executor.submit(encrypter.checkSignature(newValue.getPath()));
                    } else {
                        future = executor.submit(encrypter.checkSignature(Paths.get(newValue.getPath().getParent().toString()
                                + "/encrypted/")));
                    }
                }

                try {
                    lblResult.setText(future.get()?"Valid Signature":"Invalid Signature");
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        service = new ScheduledService<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        long total = executor.getTaskCount();
                        long pending = executor.getCompletedTaskCount();
                        Platform.runLater(() ->{
                            lblStatus.setText(pending + " of " + total + " task finished ");
                        });
                        return executor.isTerminated();
                    }
                };
            }
        };

        service.setDelay(Duration.millis(500));
        service.setPeriod(Duration.seconds(1));
        service.setOnSucceeded(e -> {
            if(service.getValue()){
                service.cancel();
                btnSign.setDisable(false);
                btnCheck.setDisable(false);
            }
        });

    }
}