/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgardownloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author fancyydk
 */
public class EdgarDownloader extends Application {
    private String saveDirectory;
    private EdgarIndex index;
    private FTPClient ftpClient;
    private final TextArea status = new TextArea();
    
    @Override
    public void start(Stage primaryStage) {
        
        // initialize
        index = new EdgarIndex();
        ftpClient = new FTPClient();
        
        setUpUI(primaryStage);
        primaryStage.show();
    }
    
    private void setUpUI(Stage primaryStage) {
        int row = 0;
        primaryStage.setTitle("Edgar Downloader");
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        // Welcome
        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, row, 3, 1);
        row++;
        
        // Directory
        Label directory = new Label("Directory:");
        TextField directoryTextField = new TextField();
        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                final DirectoryChooser directoryChooser = new DirectoryChooser();
                final File selectedDirectory = directoryChooser.showDialog(primaryStage);
                if (selectedDirectory != null) {
                    saveDirectory = selectedDirectory.getAbsolutePath();
                    directoryTextField.setText(saveDirectory);
                }
            }
        });
        Button ftpBtn = new Button("Get File");
        ftpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!directoryTextField.getText().equals("")) {
                    setUpFTP();
                } else {
                    status.appendText("Please select a directory.\n");
                }
            }
        });
        grid.add(directory, 0, row);
        grid.add(directoryTextField, 1, row);
        grid.add(browseBtn, 2, row);
        grid.add(ftpBtn, 3, row);
        row++;
        
        // Start year
        row++;
        row++;
        Label sYear = new Label("Start year:");
        TextField sYearTextField = new TextField();
        grid.add(sYear, 0, row);
        grid.add(sYearTextField, 1, row);
        row++;
        
        // Start quarter
        Label sQuarter = new Label("Start quarter:");
        TextField sQuarterTextField = new TextField();
        grid.add(sQuarter, 0, row);
        grid.add(sQuarterTextField, 1, row);
        row++;
        
        // End year
        Label eYear = new Label("End year:");
        TextField eYearTextField = new TextField();
        grid.add(eYear, 0, row);
        grid.add(eYearTextField, 1, row);
        row++;
        
        // End quarter
        Label eQuarter = new Label("End quarter:");
        TextField eQuarterTextField = new TextField();
        grid.add(eQuarter, 0, row);
        grid.add(eQuarterTextField, 1, row);
        row++;
        
        // Get Index File
        Button indexBtn = new Button("Get Index Files");
        indexBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                boolean checkSuccessful = true;
                
                // Check start year
                if (sYearTextField.getText().equals("")) {
                    status.appendText("Please enter a start year.\n");
                    checkSuccessful = false;
                }
                
                // Check start quarter
                if (sQuarterTextField.getText().equals("")) {
                    status.appendText("Please enter a start quarter.\n");
                    checkSuccessful = false;
                }
                
                // Check end year
                if (eYearTextField.getText().equals("")) {
                    status.appendText("Please enter an end year.\n");
                    checkSuccessful = false;
                }
                
                // Check end quarter
                if (eQuarterTextField.getText().equals("")) {
                    status.appendText("Please enter an end quarter.\n");
                    checkSuccessful = false;
                }
                
                if (checkSuccessful) {
                    index.setAll(sYearTextField.getText(), sQuarterTextField.getText(), eYearTextField.getText(), eQuarterTextField.getText());
                    //setUpFTP();
                }
            }
        });
        grid.add(indexBtn, 1, row);
        row++;
        
        // Status
        row++;
        row++;
        Label statusLabel = new Label("Status:");
        Button statusClearBtn = new Button("Clear status");
        statusClearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                status.clear();
            }
        });
        grid.add(statusLabel, 0, row);
        grid.add(status, 1, row, 2, 1);
        grid.add(statusClearBtn, 3, row);
        row++;
        
        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
    }
    
    private void setUpFTP() {
        int port = 21;
        String user = "anonymous";
        String password = "anonymous";
        String filepath = "/edgar/data/886475/";
        String filename = "0001019056-10-000046.txt";
        String server = "ftp.sec.gov";
        String localFile = saveDirectory + "/" + filename;
        String remoteFile = filepath + filename;
        
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            File downloadFile = new File(localFile);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
            outputStream.close();
            
            if (success) {
                status.setText(filename + " has been downloaded successfully.");
            } else {
                status.setText(filename + " has NOT been downloaded successfully.");
            }
            
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
