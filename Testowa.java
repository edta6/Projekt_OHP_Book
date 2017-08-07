/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testowa;

import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Andrzej Pawlik
 */
public class Testowa extends Application {
    
    private Stage primaryStage;
    private Scene scene;
    private GridPane grid;
    private Text title, actiontarget;
    private HBox hbTitle, hbUserTextField, hbPW, hbBtn, hbAction;
    private Label userName, pw;
    private Button btn;
    private TextField userTextField;
    private PasswordField pwBox;
    private EventHandler key;
    public User login;
    MainWindow window;
    DB db;
    Statement st;
    
    /* 
    Funkcja, która przygotuje okienko logowania wraz z dodanymi dwoma zdarzeniami
    odnośnie kliknięcia w przycisk oraz naciśnięcia klawisza ENTER.
    - grid jest głównym zarządcą rozkładu;
    - pozostałe elementy są dodawane po kolei od góry do dołu;
    - tak samo jest w kodzie pliku Login.css;
    */
    private void prepareScene(){
        
        grid = new GridPane();
        grid.setId("gridPane");
        
        title = new Text("Logowanie");
        title.setId("title");
        
        hbTitle = new HBox();
        hbTitle.setId("hbTitle");
        hbTitle.getChildren().add(title);
        /*
        tutaj w grid.add jest dodany rozkład poziomy i dodatkowo jest użyte scalenie
        komórek (0 kolumna i 0 wiersz -> lewy górny róg) pierwsze dwie cyfry 
        następna cyfra to ile scalić kolumn(2), a następna ile scalić wierszy(1)
        */
        grid.add(hbTitle, 0, 0, 2, 1); 
        
        userName = new Label("User Name:");
        userName.setId("userName");
        grid.add(userName, 0, 1);
        
        userTextField = new TextField();
        userTextField.setId("userTextField");
       
        hbUserTextField = new HBox();
        hbUserTextField.setId("hbUserTextField");
        hbUserTextField.getChildren().add(userTextField);
        grid.add(hbUserTextField, 1, 1);
        
        pw = new Label("Password:");
        pw.setId("userName");
        grid.add(pw, 0, 2);
        
        pwBox = new PasswordField();
        pwBox.setId("userTextField");
        
        hbPW = new HBox();
        hbPW.setId("hbUserTextField");
        hbPW.getChildren().add(pwBox);
        grid.add(hbPW, 1, 2);
         
        btn = new Button("Sign in");
        btn.setId("btnSing");
        
        hbBtn = new HBox(10);
        hbBtn.setId("hbBtn");
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 3, 2, 1);
        
        actiontarget = new Text();
        actiontarget.setId("actiontarget");
        
        hbAction = new HBox(10);
        hbAction.setId("hbBtn");
        hbAction.getChildren().add(actiontarget);
        grid.add(hbAction, 0, 4, 2, 1);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendCon(userTextField.getText(),pwBox.getText());
            }
        });
        
        key = new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                   sendCon(userTextField.getText(),pwBox.getText());
                }
            }
        };
        
        userTextField.setOnKeyPressed(key);
        pwBox.setOnKeyPressed(key);
    
        scene = new Scene(grid);
        scene.getStylesheets().add(Testowa.class.getResource("Login.css").toExternalForm());
    }
    
    @Override
    public void start(Stage stage) {
        
        primaryStage = new Stage();
        
        prepareScene();
        
        primaryStage.setTitle("The book out and trips");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /*
    Funkcja, która ma zasadnie połączyć się z bazą danych jeżeli się uda to 
    wyświetli się okienko logowania jeżeli się nie uda wypisze napis w okienku
    logowania o braku takiego użytkownika
    Funkcja korzysta z klasy DB oraz klasy User.
    */
    public void sendCon(String user, String pswd) {
           
        db = new DB();
        login = new User(user);
        db.DBConect(user, pswd);
        
        
        //DODAWANIE TABELI
        if(sqlQuery("USE OHP;")) {
           sqlQuery("CREATE DATABASE OHP DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;");
           sqlQuery("USE OHP;");
           sqlQuery("CREATE TABLE participants (id_part int PRIMARY KEY, first_name varchar(15), last_name varchar(20), active int);");
           sqlQuery("CREATE TABLE exitreturn (id_part int PRIMARY KEY, exit_return int);");
           sqlQuery("CREATE TABLE targets (id_target int PRIMARY KEY, target_name varchar(20));");
        }
        else sqlQuery("use OHP;");
        
        if(db.isError){
            actiontarget.setText("Zły login lub hasło"); 
        }
        else {
            window = new MainWindow(user);
            window.db = db;
            window.login = login;
            window.show();
            primaryStage.hide(); 
        }      
    }
    
    private boolean sqlQuery(String query) {
        
        boolean error;
        
        try {
            error=false;
            st = db.con.createStatement();
            st.execute(query);    
        } catch (SQLException ex) {
            error = true;
        }
        
        return error;  
    }
     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}