package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jinstagram.JInstagram;
import org.jinstagram.JInstagramException;
import org.jinstagram.requests.InstagramDirectShareRequest;
import org.jinstagram.requests.InstagramSearchUsernameRequest;
import org.jinstagram.requests.payload.InstagramLoginResult;
import org.jinstagram.requests.payload.InstagramSearchUsernameResult;
import org.jinstagram.requests.payload.StatusResult;
import org.jinstagram.requests.InstagramDirectShareRequest.ShareType;


import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LoginPage extends Application{
	private Scene loginScene;
	private Scene sendingScene;
	private List<String> users;
	private volatile boolean alive=true;
	private Text successfullMsg=new Text("");
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		//***********************************************************
		//login page scene
		//***********************************************************


		BorderPane borderP=new BorderPane();
		Text txtWelcome=new Text("Welcome To Direct Sender Simple App :)");
		txtWelcome.setFont(Font.font("arial", FontWeight.BOLD, 24));
		borderP.setTop(txtWelcome);
		VBox v1box=new VBox(20);
		v1box.setAlignment(Pos.CENTER);
		borderP.setCenter(v1box);
		Text txtNote=new Text("Note: In order to send Direct messages you must "
				+"\n"+ "login with your Instagram username and password.");
		txtNote.setFont(Font.font("arial", FontWeight.BOLD, 14));


		Text txtUsername=new Text("Username: ");
		Text txtPassword=new Text("Password: ");
		txtUsername.setFont(Font.font("arial", FontWeight.BOLD, 14));
		txtPassword.setFont(Font.font("arial", FontWeight.BOLD, 14));
		TextField txtFUsername=new TextField();
		txtFUsername.setStyle("-fx-background-color: lightgray;");
		PasswordField txtFPass=new PasswordField();
		txtFPass.setStyle("-fx-background-color: lightgray;");
		Button btnLogin=new Button("Login");
		btnLogin.setFont(Font.font("arial", FontWeight.BOLD, 14));
		HBox NoteHbox=new HBox(txtNote);
		NoteHbox.setAlignment(Pos.CENTER);
		HBox UnameHbox=new HBox(10,txtUsername,txtFUsername);
		UnameHbox.setAlignment(Pos.CENTER);
		HBox PassHbox=new HBox(10,txtPassword,txtFPass);
		PassHbox.setAlignment(Pos.CENTER);

		HBox butHbox=new HBox(btnLogin);
		butHbox.setAlignment(Pos.CENTER);
		v1box.getChildren().addAll(NoteHbox,UnameHbox,PassHbox,butHbox);
		borderP.setBackground(Background.EMPTY);
		borderP.setAlignment(v1box, Pos.CENTER);
		borderP.setAlignment(txtWelcome, Pos.CENTER);

		BackgroundImage myBI= new BackgroundImage(new Image( LoginPage.class.getResourceAsStream("/insta5.jpg"),560,450,false,true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		borderP.setBackground(new Background(myBI));


		loginScene=new Scene(borderP,530,400);
		loginScene.setFill(Color.ANTIQUEWHITE);



		btnLogin.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if (!txtFUsername.getText().trim().isEmpty() & txtFPass.getText().length() != 0) {
					String passText = new String(txtFPass.getText());
					String userText = new String(txtFUsername.getText());
					//JInstagram instagram = JInstagram.builder().username("v0000r0000jak").password("1951371").build();
					JInstagram instagram = JInstagram.builder().username(userText).password(passText).build();
					instagram.setup();


					try {
						InstagramLoginResult loginResults = instagram.login();
						if (loginResults != null) {
							if(loginResults.getStatus().equals("ok")){
								primaryStage.setScene(sendingScene(instagram, primaryStage));
							}
							else
							{
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Error");
								alert.setHeaderText(null);
								alert.setContentText("Username or password is not correct!");
								alert.showAndWait();
							}
						}
						else
						{
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Error");
							alert.setHeaderText(null);
							alert.setContentText("Something went wrong! Please try again.");
							alert.showAndWait();
						}
					} catch (JInstagramException e) {
						System.out.println("Authentication call failed");
					}

				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("warning");
					alert.setHeaderText(null);
					alert.setContentText("You must fill both fields of username and password to continue!");
					alert.showAndWait();
				}
				// TODO Auto-generated method stub

			}
		});
		primaryStage.setScene(loginScene);
		primaryStage.setTitle("Instagram Direct Sender");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public Scene sendingScene(JInstagram instagram, Stage pStage) throws JInstagramException{

		BorderPane borderP=new BorderPane();
		Text txtNote=new Text("Tip: In order to send direct messages to the group of users, " + "\n"
				+ 			  "write down your message into the below textbox and then "+ "\n"
				+  			  "select a text file that contains usernames in each line of "+ "\n"
				+ 			  "it seperated by enter. Then press the send button in order "+ "\n"
				+ 			  "to send the message to the users's directs.");
		txtNote.setFont(Font.font("arial", 14));
		txtNote.setLayoutX(10);
		txtNote.setLayoutY(10);

		HBox noteHbox=new HBox(10,txtNote);
		noteHbox.setAlignment(Pos.CENTER);

		VBox v1box=new VBox(20);
		v1box.setAlignment(Pos.CENTER);
		borderP.setCenter(v1box);

		Text txtMessage=new Text("Message: ");
		txtMessage.setFont(Font.font("arial", FontWeight.BOLD, 14));

		Text txtTextFile=new Text("Text file of usernames: ");
		txtTextFile.setFont(Font.font("arial", FontWeight.BOLD, 14));

		TextArea txtAMessage=new TextArea();
		txtAMessage.setMaxSize(300, 70);
		FileChooser fileChooser=new FileChooser();
		Button btnChooser=new Button("Browse");
		btnChooser.setFont(Font.font("arial", FontWeight.BOLD, 14));
		HBox messageHbox=new HBox(10,txtMessage,txtAMessage);
		messageHbox.setAlignment(Pos.CENTER);
		HBox fChooserHbox=new HBox(txtTextFile,btnChooser);
		fChooserHbox.setAlignment(Pos.CENTER);
		Text txtTimeLapse=new Text("Time Lapse(seconds): ");
		txtTimeLapse.setFont(Font.font("arial", FontWeight.BOLD, 14));
		TextField txtFFrom=new TextField();
		txtFFrom.setMaxSize(40, 10);
		txtFFrom.setStyle("-fx-background-color: lightgray;");
		Text txtTo=new Text(" _ ");
		txtTo.setFont(Font.font("arial", FontWeight.BOLD, 14));
		TextField txtFTo=new TextField();
		txtFTo.setStyle("-fx-background-color: lightgray;");
		txtFTo.setMaxSize(40, 10);
		HBox TimeLapseHbox=new HBox(txtTimeLapse,txtFFrom,txtTo,txtFTo);
		TimeLapseHbox.setAlignment(Pos.CENTER);
		Button btnSend=new Button("Send message");
		btnSend.setFont(Font.font("arial", FontWeight.BOLD, 14));
		Button btnCancel=new Button("Cancel Sending");
		btnCancel.setFont(Font.font("arial", FontWeight.BOLD, 14));
		HBox sendHbox=new HBox(10,btnSend,btnCancel);
		sendHbox.setAlignment(Pos.CENTER);
		ProgressBar progressBar=new ProgressBar(0);
		successfullMsg.setFont(Font.font("arial", FontWeight.BOLD, 14));

		v1box.getChildren().addAll(noteHbox,messageHbox,TimeLapseHbox,fChooserHbox,progressBar,successfullMsg,sendHbox);
		borderP.setBackground(Background.EMPTY);
		borderP.setAlignment(v1box, Pos.CENTER);
		borderP.setAlignment(txtNote, Pos.CENTER);

		sendingScene=new Scene(borderP,530,500);
		sendingScene.setFill(Color.DARKSALMON);

		btnChooser.setOnAction(e -> {
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("Text Files", "*.txt")
					);
			File selectedFile = fileChooser.showOpenDialog(pStage);
			users=new ArrayList<String>();
			users=selectedUsers(selectedFile);
		});


		class Sender extends Thread{

			private volatile boolean flag=true;
			public void threadStop(){
				flag=false;
			}
			public void threadStart(){
				flag=true;
			}
			@Override
			public void run(){
				List<String> usrs =  new ArrayList<String>();

				InstagramSearchUsernameResult userResult;
				try {
					for(int i=1;i<=users.size() && flag;i++){
						usrs.clear();
						userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(users.get(i-1)));

						usrs.add(String.valueOf(userResult.getUser().getPk()));
						StatusResult res;
						res = instagram
								.sendRequest(InstagramDirectShareRequest
										.builder(ShareType.MESSAGE, usrs)
										.message(txtAMessage.getText()).build());

						progressBar.setProgress(i*1.1/users.size()*1.1);
						System.out.println(i*1.1/users.size()*1.1);
						if(i<users.size()){
							Random r=new Random();
							try {
								Thread.sleep(r.nextInt((Integer.parseInt(txtFTo.getText())-Integer.parseInt(txtFFrom.getText()))*1000)+Integer.parseInt(txtFFrom.getText()));
							} catch (NumberFormatException | InterruptedException e) {
								// TODO Auto-generated catch block
								System.out.println("Thread Error");	
							}
						}
						else{
							//throw new RuntimeException();
							//alive=false;
							successfullMsg.setText("Message has been sent successfully to the users");
						}
					}

				} catch (JInstagramException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}


		Sender sender = new Sender();
		btnSend.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				/*	sender.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

					@Override
					public void uncaughtException(Thread sender, Throwable e) {
						// TODO Auto-generated method stub
						System.out.println("rte");
						//alert();
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Successful sending");
						alert.setHeaderText(null);
						alert.setContentText("Message has been sent successfully to the users.");
						alert.showAndWait();
					}
				});*/
				sender.start();
				sender.threadStart();

				/*new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(alive){
							continue;
						}
						if(!alive)
							alert();
					}

				}).start();
				 */
			}

		});

		btnCancel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				sender.threadStop();
			}});
		return sendingScene;
	}
	public List<String> selectedUsers(File selectedFile){
		List<String> users=new ArrayList<String>();
		try {
			FileReader fr=new FileReader(selectedFile);
			BufferedReader br=new BufferedReader(fr);
			String line=null;

			while((line=br.readLine())!=null){
				users.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("File reading error!");
		}
		return users;
	}
	public void alert() {
		// TODO Auto-generated method stub
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Successful sending");
		alert.setHeaderText(null);
		alert.setContentText("Message has been sent successfully to the users.");
		alert.showAndWait();	
	}

}
