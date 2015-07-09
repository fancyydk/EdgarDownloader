/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgardownloader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
import javafx.scene.layout.HBox;
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
        grid.add(directory, 0, row);
        grid.add(directoryTextField, 1, row);
        grid.add(browseBtn, 2, row);
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
        
        // buttons
        HBox btnHBox = new HBox(8);
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
                    downloadIndex(ftpClient, index, saveDirectory);
                }
            }
        });
        Button extractIndexBtn = new Button("Extract Indices");
        extractIndexBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                extractIndex(index, saveDirectory);
            }
        });
        Button get10kBtn = new Button("Get 10-K");
        get10kBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                get10K(ftpClient, index, saveDirectory);
            }
        });
        btnHBox.getChildren().addAll(indexBtn, extractIndexBtn, get10kBtn);
        grid.add(btnHBox, 1, row);
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
        grid.add(status, 1, row);
        grid.add(statusClearBtn, 2, row);
        row++;
        
        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
    }
    
    private void downloadIndex(FTPClient ftpClient, EdgarIndex index, String downloadDirectory) {
        int port = 21;
        String user = "anonymous";
        String password = "anonymous";
        String filepath = "/edgar/full-index/";
        String server = "ftp.sec.gov";
        
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            for (int year = index.getStartYear(); year <= index.getEndYear(); year++) {
                int firstQuarter = EdgarIndex.FIRST_QUARTER;
                int lastQuarter = EdgarIndex.LAST_QUARTER;
                
                if (year == index.getStartYear()) {
                    firstQuarter = index.getStartQuarter();
                }
                if (year == index.getEndYear()) {
                    lastQuarter = index.getEndQuarter();
                }
                
                for (int quarter = firstQuarter; quarter <= lastQuarter; quarter++) {
                    String filename = "company" + year + "_" + quarter + ".zip";
                    String localFile = downloadDirectory + File.separator + filename;
                    String remoteFile = filepath + year + "/QTR" + quarter + "/company.zip";
            
                    File downloadFile = new File(localFile);
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                    boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
                    outputStream.close();

                    if (success) {
                        status.appendText(filename + " has been downloaded successfully.\n");
                        
                        File directory = new File(downloadDirectory + File.separator + "index");
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        
                        ZipInputStream zis = new ZipInputStream(new FileInputStream(localFile));
                        ZipEntry ze = zis.getNextEntry();
                        byte[] buffer = new byte[1024];
                        
                        while (ze != null) {
                            String newFileName = year + "Q" + quarter + "_" + ze.getName();
                            File newFile = new File(directory + File.separator + newFileName);
                            FileOutputStream fos = new FileOutputStream(newFile);
                            
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            
                            fos.close();
                            ze = zis.getNextEntry();
                            
                            status.appendText(filename + " has been successfully extracted to " + newFileName + "\n");
                        }
                        
                        zis.closeEntry();
                        zis.close();
                        
                        downloadFile.delete();
                    } else {
                        status.appendText(filename + " has NOT been downloaded successfully.\n");
                    }
                }
            }
        } catch (IOException ex) {
            status.appendText("Error: " + ex.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                status.appendText("Error: " + ex.getMessage());
            }
        }
    }
    
    private void extractIndex(EdgarIndex index, String downloadDirectory) {
        for (int year = index.getStartYear(); year <= index.getEndYear(); year++) {
            int firstQuarter = EdgarIndex.FIRST_QUARTER;
            int lastQuarter = EdgarIndex.LAST_QUARTER;

            if (year == index.getStartYear()) {
                firstQuarter = index.getStartQuarter();
            }
            if (year == index.getEndYear()) {
                lastQuarter = index.getEndQuarter();
            }

            for (int quarter = firstQuarter; quarter <= lastQuarter; quarter++) {
                String fileName = downloadDirectory + File.separator + "index" + File.separator + year + "Q" + quarter + "_company.idx";
                String outputDirectory = downloadDirectory + File.separator + "index" + File.separator;
                String outputFileName = year + "Q" + quarter + "_company.txt";
                String output = outputDirectory + outputFileName;
                boolean realInfoStarted = false;
                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    
                    File fout = new File(output);
                    FileOutputStream fos = new FileOutputStream(fout);

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.equals("---------------------------------------------------------------------------------------------------------------------------------------------")) {
                            realInfoStarted = true;
                            continue;
                        }
                        
                        if (!realInfoStarted) {
                            continue;
                        }
                        
                        String essentials = line.substring(62);
                        String[] pieces = essentials.split("\\s+");
                        
                        if (!pieces[0].equals("10-K")) {
                            continue;
                        }
                        
                        bw.write(pieces[1] + " " + pieces[3]);
                        bw.newLine();
                    }
                    
                    bw.close();
                    
                    status.appendText(outputFileName + " has been successfully created.\n");
                } catch (IOException ex) {
                    status.appendText("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    private void get10K(FTPClient ftpClient, EdgarIndex index, String downloadDirectory) {
        int port = 21;
        String user = "anonymous";
        String password = "anonymous";
        String server = "ftp.sec.gov";
        
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            for (int year = index.getStartYear(); year <= index.getEndYear(); year++) {
                int firstQuarter = EdgarIndex.FIRST_QUARTER;
                int lastQuarter = EdgarIndex.LAST_QUARTER;
                
                if (year == index.getStartYear()) {
                    firstQuarter = index.getStartQuarter();
                }
                if (year == index.getEndYear()) {
                    lastQuarter = index.getEndQuarter();
                }
                
                for (int quarter = firstQuarter; quarter <= lastQuarter; quarter++) {
                    String indexFileName = downloadDirectory + File.separator + "index" + File.separator + year + "Q" + quarter + "_company.txt";
                    BufferedReader br = new BufferedReader(new FileReader(indexFileName));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] pieces = line.split("\\s+");

                        String filename = pieces[1].substring(pieces[1].lastIndexOf("/")+1);
                        String localFile = downloadDirectory + File.separator + "Y" + year + File.separator + "Q" + quarter + File.separator + pieces[0] + File.separator + filename;
                        String remoteFile = pieces[1];
                        
                        File downloadFile = new File(localFile);
                        downloadFile.getParentFile().mkdirs();

                        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                        boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
                        outputStream.close();

                        if (success) {
                            status.appendText(filename + " has been downloaded successfully.\n");
                        } else {
                            status.appendText(filename + " has NOT been downloaded successfully.\n");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            status.appendText("Error: " + ex.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                status.appendText("Error: " + ex.getMessage());
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
