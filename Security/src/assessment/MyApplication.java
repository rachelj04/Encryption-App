package assessment;


import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MyApplication extends Application {

   
    private BorderPane root;
    private SecretKey secretkey;
    private String user;
    private DBController db = new DBController();
    private HBox topPane = new HBox();
//    private String masterKey = "ABCDEFGHIJKLMNOP";
    private String masterKey = "ABCDEFGH";
  
    
    ColorPicker cp1 = new ColorPicker();
    ColorPicker cp2 = new ColorPicker();
    
	/**
	 * The main entry point for all JavaFX applications.
	 *
	 * @param primaryStage the primary stage for this application, onto which the
	 *                     application scene caset. Applications may create
	 *                     other stages, if needed, but they will not be primary
	 *                     stages.
	 * @throws IOException
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		root = new BorderPane();
		HBox navBar = createNavBar();
		root.setTop(navBar);

		// Start on the home page
		switchPage("Conventional");

		Scene scene = new Scene(root, 900, 600);
		primaryStage.setTitle("Cryptor app");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	/**
	 * Switch the page content based on the button pressed
	 *
	 * @param page The page to switch to
	 */
	private void switchPage(String page) {
		Node pageContent;
		switch (page) {
		
		case "Conventional":
			pageContent = conventionalContent();
			break;
			
		case "Modern":
			if (user == null) {
				pageContent = requireLoginContent();
			} else {
				pageContent = modernContent();
			}
			
			break;
			
		case "Settings":
			pageContent = settingsContent();
			break;
			
		case "User":
			if (user == null) {
				pageContent = userContent();
			} else {
				pageContent = loggedInContent();
			}
			
			break;
			
		case "LoggedIn":
			pageContent = loggedInContent();
			break;
			
		case "RequireLogin":
			pageContent = requireLoginContent();
			break;
		default:
			return;
		}

		root.setCenter(pageContent);
	}

	/**
	 * Create the navigation bar
	 *
	 * @return HBox The navigation bar
	 */
	private HBox createNavBar() {
		
		topPane.setPadding(new Insets(15));
		topPane.setStyle("-fx-background-color: #9C6860;");
		cp1.setValue(Color.web("#9C6860"));

		topPane.setSpacing(10); // Add spacing for visual separation

		HBox links = new HBox(15);
		links.setAlignment(Pos.CENTER_LEFT);

		// Create buttons
		
		
		Button btnConven = createStyledButton("Conventional");
		Button btnModern = createStyledButton("Modern");
		Button btnSettings = createStyledButton("Settings");
		Button btnUser = createStyledButton("User");

		links.getChildren().addAll(btnConven, btnModern, btnSettings, btnUser);

		// Set logo to the right side
		
		HBox logoContainer = new HBox();
		logoContainer.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(logoContainer, Priority.ALWAYS); // This will push the logo to the right

		topPane.getChildren().addAll(links, logoContainer);

		return topPane;
	}



	/**
	 * Create a button for the navigation bar
	 *
	 * @param text
	 * @return Button A button with the given text
	 */
	private Button createStyledButton(String text) {
		Button button = new Button(text);
		button.setOnAction(e -> switchPage(text));
		button.setStyle(
				"-fx-background-color: transparent; -fx-underline: false; -fx-text-fill: white; -fx-font-size: 16px;");
		// Adding a shadow effect on hover for a modern look
		DropShadow dropShadow = new DropShadow();
		dropShadow.setColor(Color.valueOf("#693043"));
		button.setOnMouseEntered(e -> {
			button.setEffect(dropShadow);
			button.setTextFill(Color.valueOf("#693043"));
		});
		button.setOnMouseExited(e -> {
			button.setEffect(null);
			button.setTextFill(Color.WHITE);
		});
		return button;
	}




	/**
	 * Create the page content for the conventional algorithm page
	 *
	 * @return Node containing the page content for the conventional algorithm page
	 */
	private Node conventionalContent() {
		// Set the overall padding and spacing for the page content
		VBox pageContent = new VBox(20); // Use spacing to separate elements vertically
		pageContent.setPadding(new Insets(20)); // Set padding around the entire content

		// Create the header text
		Text headerText = new Text("Shift cipher");
		headerText.setStyle("-fx-font-size: 24pt;"); // Set the header text size

		// Add the header text to the top of the page content
		pageContent.getChildren().add(headerText);

		
		GridPane conventionalPane = new GridPane();

		Text errorMessage = new Text("");
		errorMessage.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

	
		Text keyLabel = new Text("Key (positive integer)");
		TextField keyField = new TextField();

		keyField.setMaxWidth(50);
		
		Text inputLabel = new Text("Input (ASCII)");
		TextField inputField = new TextField();

		inputField.setPrefWidth(600);

		
		Button encryptBtn = new Button();
		encryptBtn.setPrefSize(150, 30);
		encryptBtn.setText("Encrypt");
		
		Button decryptBtn = new Button();
		decryptBtn.setPrefSize(150, 30);
		decryptBtn.setText("Decrypt");
		
		
		// Create a Label
	    Label outputLabel = new Label("Output");
	    
	    TextArea outputText = new TextArea("");
	    // wrap the label
	    
	    outputText.setPrefWidth(600);


		encryptBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
			
				
				String key = keyField.getText().trim();

				errorMessage.setText("");
				if (key.isEmpty()) {
//                	quantityField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Key cannot be empty");
				} else {
					try {
						int k = Integer.parseInt(key);
						
						if (k <= 0) {
							errorMessage.setFill(Color.RED);
							errorMessage.setText("Quantity must be a positive integer");
						} else if (inputField.getText().isEmpty()) {
							errorMessage.setFill(Color.RED);
							errorMessage.setText("Input cannot be null");
						} else {
						
						ShiftCipher cc = new ShiftCipher(k);
						String input = inputField.getText();
						outputText.setText(cc.encrypt(input));
						}
					} catch (NumberFormatException e) {
						// TODO: handle exception
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Key must be an integer");
					}

				}
			}
		});
		
		decryptBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
			
				
				String key = keyField.getText().trim();

				errorMessage.setText("");
				if (key.isEmpty()) {
//                	quantityField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Key cannot be empty");
				} else {
					try {
						int k = Integer.parseInt(key);

						if (k <= 0) {
							errorMessage.setFill(Color.RED);
							errorMessage.setText("Quantity must be a positive integer");
						} else if (inputField.getText().isEmpty()) {
							errorMessage.setFill(Color.RED);
							errorMessage.setText("Input cannot be null");
						} else {
						ShiftCipher sc = new ShiftCipher(k);
						String input = inputField.getText();
						outputText.setText(sc.decrypt(input));
						}
					} catch (NumberFormatException e) {
						// TODO: handle exception
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Key must be an integer");
					}

				}
			}
		});

		conventionalPane.setVgap(10);
		conventionalPane.setHgap(10);
		conventionalPane.add(errorMessage, 0, 0, 3, 1);
		conventionalPane.add(keyLabel, 0, 1);
		conventionalPane.add(keyField, 1, 1);
		
		conventionalPane.add(inputLabel, 0, 2);
		conventionalPane.add(inputField, 1, 2, 3, 1);
		conventionalPane.add(encryptBtn, 1, 3);
		conventionalPane.add(decryptBtn, 2, 3);
		conventionalPane.add(outputLabel, 0, 4);
		conventionalPane.add(outputText, 1, 4, 3, 1);
		conventionalPane.setAlignment(Pos.CENTER);
		pageContent.getChildren().add(conventionalPane);
		return pageContent;
	}
	
	/**
	 * Create the page content for the modern algorithms page
	 *
	 * @return Node containing the page content for the modern algorithms page
	 */
	private Node modernContent() {
		// Set the overall padding and spacing for the page content
		VBox pageContent = new VBox(20); // Use spacing to separate elements vertically
		pageContent.setPadding(new Insets(20)); // Set padding around the entire content

		// Create the header text
		Text headerText = new Text("Modern Algorithms");
		headerText.setStyle("-fx-font-size: 24pt;"); // Set the header text size

		// Add the header text to the top of the page content
		pageContent.getChildren().add(headerText);

		
		GridPane modernPane = new GridPane();

		Text errorMessage = new Text("");
		errorMessage.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

		Text algLabel = new Text("Algorithm");
		String options[] = { "DES", "AES"};
		ObservableList<String> items1 = FXCollections.observableArrayList(options);
		ComboBox<String> algField = new ComboBox<>(items1);
		algField.setMaxWidth(100);
		
		algField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (newValue) { // when focus lost
				secretkey = null;
				errorMessage.setFill(Color.RED);
				errorMessage.setText("Algorithm changed. Please load / generate key.");
			}
		});


		Text keyLabel = new Text("Key name");
		
		TextField keyField = new TextField();

		keyField.setPrefWidth(150);
		
		Button loadKeyBtn = new Button();
		loadKeyBtn.setPrefSize(150, 30);
		loadKeyBtn.setText("Load");
		
		Button genKeyBtn = new Button();
		genKeyBtn.setPrefSize(150, 30);
		genKeyBtn.setText("Generate");

		Button saveKeyBtn = new Button();
		saveKeyBtn.setPrefSize(150, 30);
		saveKeyBtn.setText("Save");
		
		
		Text fileLabel = new Text("File name (encrypted file)");
		
		TextField fileField = new TextField();

		keyField.setPrefWidth(150);
		
		Button loadFileBtn = new Button();
		loadFileBtn.setPrefSize(150, 30);
		loadFileBtn.setText("Load key file");
		
		Button saveFileBtn = new Button();
		saveFileBtn.setPrefSize(150, 30);
		saveFileBtn.setText("Save key file");

		
		
		
		
		

		Text inputLabel = new Text("Input");
		TextField inputField = new TextField();

		inputField.setPrefWidth(600);

		
		Button encryptBtn = new Button();
		encryptBtn.setPrefSize(150, 30);
		encryptBtn.setText("Encrypt");
		
		Button decryptBtn = new Button();
		decryptBtn.setPrefSize(150, 30);
		decryptBtn.setText("Decrypt");
		
		
		// Create a Label
	    Label outputLabel = new Label("Output");
	    
	    TextArea outputText = new TextArea("");
	    // wrap the label
	    
	    outputText.setPrefWidth(600);
	    
	    
	    
	    loadKeyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
				String keyname = keyField.getText().trim();
				if (keyname.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Key name cannot be empty");
				} else if (!db.existKey(user, keyname)) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Key does not exist");
				} else {
					byte[] key = db.loadKey(user, keyname);
					String alg = db.getAlg(user, keyname);
					
					if (key != null) {
//						AES aes1;
						DES des1;
						try {
//							aes1 = new AES();
//							aes1.setSecretkey(masterKey);
							des1 = new DES();
							des1.setSecretkey(masterKey);
							SecretKey dec_key;
							try {
								algField.getSelectionModel().clearSelection();
//								dec_key = aes1.decryptKey(key);
								dec_key = des1.decryptKey(key, alg);
								errorMessage.setFill(Color.GREEN);
								errorMessage.setText("Key has been loaded");
								algField.getSelectionModel().select(alg);
								secretkey = dec_key;
							} catch (InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException
									| IllegalBlockSizeException | BadPaddingException | NoSuchProviderException
									| ShortBufferException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								errorMessage.setFill(Color.RED);
								errorMessage.setText("Failed to load the key");
							}					
							
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Failed to load the key");
					}
				}
			}
		});
	    
	    genKeyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String alg = algField.getSelectionModel().getSelectedItem();
				if (alg == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must select an algorithm");
					
				} else {
					
					switch (alg) {
					
					case "DES":
						DES des1;
						try {
							des1 = new DES();
							
							KeyGenerator keyGen = KeyGenerator.getInstance("DES");
					        secretkey = keyGen.generateKey(); 
					        
					        
					        errorMessage.setFill(Color.GREEN);
							errorMessage.setText("Key has been generated");
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
						
					case "AES":
						AES aes1;
						try {
							aes1 = new AES();
							
							KeyGenerator keyGen = KeyGenerator.getInstance("AES");
					        secretkey = keyGen.generateKey(); 
					        errorMessage.setFill(Color.GREEN);
							errorMessage.setText("Key has been generated");
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						return;
					}
				}
						
			}
		});
	    
	    
	    saveKeyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String alg = algField.getSelectionModel().getSelectedItem();
				String keyname = keyField.getText().trim();
				
				if (secretkey == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must generate / load key first");
				} else if (alg == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must select an algorithm");
				} else if (keyname.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Key name cannot be empty");
				} else if (db.existKey(user, keyname)) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Key name has been used, please rename the key");
				} else {					
					DES des1;
					try {
						des1 = new DES();
						des1.setSecretkey(masterKey);
						byte[] keyBytes;
						try {
							keyBytes = des1.encryptKey(secretkey);
							if (db.saveKey(user, alg, keyname, keyBytes)) {
								errorMessage.setFill(Color.GREEN);
								errorMessage.setText("Key has been saved");
							} else {
								errorMessage.setFill(Color.RED);
								errorMessage.setText("Failed to save the key");
							}
						} catch (InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException
								| IllegalBlockSizeException | BadPaddingException | NoSuchProviderException
								| ShortBufferException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							errorMessage.setFill(Color.RED);
							errorMessage.setText("Failed to save the key");
						}
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Failed to save the key");
					}		
				}
			}
		});
	    
	    
	    
	    saveFileBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String alg = algField.getSelectionModel().getSelectedItem();
				String filename = fileField.getText().trim();
				
				if (secretkey == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must generate / load key first");
				} else if (alg == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must select an algorithm");
				} else if (filename.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("File name cannot be empty");
				
				} else {
										
					DES des1;
					try {

						des1 = new DES();
						des1.setSecretkey(masterKey);
						byte[] keyBytes;
						
						String path1 = "Key";
						String path2 = "Key";
						keyBytes = secretkey.getEncoded();
						
						switch (alg) {
							case "AES":
								path1 = "Key/AES/plain" + filename;
								path2 = "Key/AES/" + filename;
								break;
							case "DES":
								path1 = "Key/DES/plain" + filename;
								path2 = "Key/DES/" + filename;
								break;
							default:
								break;
						}
							// Write the key to file
							des1.writeKeyToFile(path1, keyBytes);
							
							// Encrypt file
							
								File f1 = new File(path1);
								des1.encryptFile(des1.getFileInBytes(f1), 
										new File(path2),des1.getSecretkey());
								f1.delete();
								errorMessage.setFill(Color.GREEN);
								errorMessage.setText("Key has been saved to file");
					} catch (GeneralSecurityException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Failed to save the key to file");
					}
				}
			}
		});
	    
	    loadFileBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String alg = algField.getSelectionModel().getSelectedItem();
				String filename = fileField.getText().trim();
				
				if (alg == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must select an algorithm");
				} else if (filename.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("File name cannot be empty");
					
					
				} else {
					
					String path1 = "Key";
					String path2 = "Key";
					
					switch (alg) {
					case "AES":
						path1 = "Key/AES/plain" + filename;
						// Encrypted file path
						path2 = "Key/AES/" + filename;
						break;
					case "DES":
						path1 = "Key/DES/plain" + filename;
						path2 = "Key/DES/" + filename;
						break;
					default:
						break;
					}
					
					if (!new File(path2).exists()) {
						errorMessage.setFill(Color.RED);
						errorMessage.setText("No such key file");
					} else {
						
						DES des1;
						try {
							des1 = new DES();
							des1.setSecretkey(masterKey);
							File f1 = new File(path1);
							des1.decryptFile(des1.getFileInBytes(new File(path2)),
									f1, des1.getSecretkey());
							
							secretkey = des1.getKeyFromFile(path2, alg);
							f1.delete();
							errorMessage.setFill(Color.GREEN);
							errorMessage.setText("Key has been loaded from file");
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							errorMessage.setFill(Color.RED);
							errorMessage.setText("Failed to load key from file");
						}
					}
				}
			}
		});
	    

		encryptBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String alg = algField.getSelectionModel().getSelectedItem();
				if (alg == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must select an algorithm");
					
					
				} else if (secretkey == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must have a key");
				} else if (inputField.getText().isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Input cannot be empty");
				} else {
					
					switch (alg) {
					
					case "DES":
						DES des1;
						try {
							des1 = new DES();
							des1.setSecretkey(secretkey);
							String input = inputField.getText();
				            byte[] encText = des1.encryptBC(input);         
				            String base64EncText = Base64.getEncoder().encodeToString(encText);
				            outputText.setText(base64EncText);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
						
					case "AES":
						AES aes1;
						try {
							aes1 = new AES();
							aes1.setSecretkey(secretkey);
							String input = inputField.getText();
				            byte[] encText = aes1.encryptBC(input);         
				            String base64EncText = Base64.getEncoder().encodeToString(encText);
				            outputText.setText(base64EncText);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						return;
					}
				}
						
			}
		});
		
		decryptBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String alg = algField.getSelectionModel().getSelectedItem();
				if (alg == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must select an algorithm");
				} else if (secretkey == null) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Must have a key");
					
				} else if (inputField.getText().isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Input cannot be empty");
				} else {
					errorMessage.setText("");
					switch (alg) {
					
					case "DES":
						DES des1;
						try {
							des1 = new DES();
							des1.setSecretkey(secretkey);
							String input = inputField.getText();
							byte[] byteDataToDecrypt = Base64.getDecoder().decode(input);
							
				            
				            String decText = des1.decryptBC(byteDataToDecrypt);
				            outputText.setText(decText);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
						
					case "AES":
						AES aes1;
						try {
							aes1 = new AES();
							aes1.setSecretkey(secretkey);
							String input = inputField.getText();
							byte[] byteDataToDecrypt = Base64.getDecoder().decode(input);
							
							if (byteDataToDecrypt.length <= AES.IV_LEN) {
								errorMessage.setFill(Color.RED);
								errorMessage.setText("Encrypted text and key doesn't match.");
							} else {
								String decText = aes1.decryptBC(byteDataToDecrypt);
					            outputText.setText(decText);
							}
				            
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						return;
					}
				}
			}
		});

		modernPane.setVgap(10);
		modernPane.setHgap(10);
		modernPane.add(errorMessage, 0, 0, 4, 1);
		modernPane.add(algLabel, 0, 1);
		modernPane.add(algField, 1, 1);
		modernPane.add(keyLabel, 0, 2);
		modernPane.add(keyField, 1, 2);
		modernPane.add(loadKeyBtn, 2, 2);
		modernPane.add(genKeyBtn, 3, 2);
		modernPane.add(saveKeyBtn, 4, 2);
		
		modernPane.add(fileLabel, 1, 3);
		modernPane.add(fileField, 2, 3);
		
		modernPane.add(loadFileBtn, 3, 3);
		modernPane.add(saveFileBtn, 4, 3);
		
		
		modernPane.add(inputLabel, 0, 4);
		modernPane.add(inputField, 1, 4, 4, 1);
		modernPane.add(encryptBtn, 1, 5);
		modernPane.add(decryptBtn, 2, 5);
		modernPane.add(outputLabel, 0, 6);
		modernPane.add(outputText, 1, 6, 4, 1);
		modernPane.setAlignment(Pos.CENTER);
		pageContent.getChildren().add(modernPane);

		return pageContent;
	}

	
	/**
	 * Create the page content for the user log in page
	 *
	 * @return Node containing the page content for the user page
	 */
	private Node userContent() {
		
		if (user == null) {
			
		}
		// Set the overall padding and spacing for the page content
		VBox pageContent = new VBox(20); // Use spacing to separate elements vertically
		pageContent.setPadding(new Insets(20)); // Set padding around the entire content

		// Create the header text
		Text headerText = new Text("User");
		headerText.setStyle("-fx-font-size: 24pt;"); // Set the header text size

		// Add the header text to the top of the page content
		pageContent.getChildren().add(headerText);

		HBox userPane = new HBox(50);
		userPane.setAlignment(Pos.CENTER);
		
		VBox vbox1 = new VBox(20); // Use spacing to separate elements vertically
		vbox1.setPadding(new Insets(20));
		vbox1.setAlignment(Pos.TOP_CENTER);
		
		VBox vbox2 = new VBox(20); // Use spacing to separate elements vertically
		vbox2.setPadding(new Insets(20));
		vbox2.setAlignment(Pos.TOP_CENTER);

		Text errorMessage = new Text("");
		errorMessage.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		pageContent.getChildren().add(errorMessage);
		
		Text loginLabel = new Text("Log in");
		loginLabel.setStyle("-fx-font-size: 12pt;"); 
		
		Text registerLabel = new Text("Register");
		registerLabel.setStyle("-fx-font-size: 12pt;");
		
		GridPane loginPane = new GridPane();
		
		Text usernameLabel1 = new Text("User name");
		TextField inputField1 = new TextField();
		inputField1.setPrefWidth(200);

		
		Text passwordLabel1 = new Text("Password");
		TextField inputField2 = new TextField();
		inputField2.setPrefWidth(200);
		
		loginPane.add(usernameLabel1, 0, 0);
		loginPane.add(inputField1, 1, 0);
		loginPane.add(passwordLabel1, 0, 1);
		loginPane.add(inputField2, 1, 1);
		loginPane.setVgap(10);
		loginPane.setHgap(10);
		
		Button loginBtn = new Button();
		loginBtn.setPrefSize(100, 30);
		loginBtn.setText("Log in");
		
		Line l = new Line(0, 0, 0, 250);

		GridPane registerPane = new GridPane();
		
		Text usernameLabel2 = new Text("User name");
		TextField inputField3 = new TextField();
		inputField3.setPrefWidth(200);
		
		Text passwordLabel2 = new Text("Password");
		TextField inputField4 = new TextField();
		inputField4.setPrefWidth(200);
		
		Text passwordLabel3 = new Text("Confirm Password");
		TextField inputField5 = new TextField();
		inputField5.setPrefWidth(200);
		
		registerPane.add(usernameLabel2, 0, 0);
		registerPane.add(inputField3, 1, 0);
		registerPane.add(passwordLabel2, 0, 1);
		registerPane.add(inputField4, 1, 1);
		registerPane.add(passwordLabel3, 0, 2);
		registerPane.add(inputField5, 1, 2);
		registerPane.setVgap(10);
		registerPane.setHgap(10);
		
		Button registerBtn = new Button();
		registerBtn.setPrefSize(100, 30);
		registerBtn.setText("Register");
		
		
		
		loginBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String userName = inputField1.getText().trim();
				String password = inputField2.getText();
				if (userName.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Username cannot be empty");
				} else if (password.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Password cannot be empty");
				} else {
					errorMessage.setText("");
					if (db.authenticate(userName, password)) {
						user = userName;
						loadSettings(userName);
						switchPage("LoggedIn");
					} else {
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Wrong user name or password");
					}
				}
			}
		});
		
		
		registerBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String userName = inputField3.getText().trim();
				String password1 = inputField4.getText();
				String password2 = inputField5.getText();
				if (userName.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Username cannot be empty");
				} else if (password1.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Password cannot be empty");
				} else if (password2.isEmpty()) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Please confirm password");
				} else if (!password2.equals(password1)) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Passwords does not match");
				} else if (userName.length() > 30) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Username cannot exceed 30 characters");
				} else if (password1.length() < 8 || password1.length() > 20) {
					errorMessage.setFill(Color.RED);
					errorMessage.setText("Password must be 8-20 characters");
				} else {
					errorMessage.setText("");
					if (db.existUsername(userName)) {
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Username has been registered");
					} else {
						if (db.register(userName, password1)) {
							user = userName;
							switchPage("LoggedIn");
						} else {
							errorMessage.setFill(Color.RED);
							errorMessage.setText("Failed to register");
						}
					}
				}
			}
		});
		
		
		
		
		vbox1.getChildren().addAll(loginLabel, loginPane, loginBtn);
		vbox2.getChildren().addAll(registerLabel, registerPane, registerBtn);
		userPane.getChildren().addAll(vbox1, l, vbox2);
		pageContent.getChildren().add(userPane);
		return pageContent;
	}

	/**
	 * Create the page content for the user log in page
	 *
	 * @return Node containing the page content for the user page
	 */
	private Node loggedInContent() {
		// Set the overall padding and spacing for the page content
		VBox pageContent = new VBox(20); // Use spacing to separate elements vertically
		pageContent.setPadding(new Insets(20)); // Set padding around the entire content

		// Create the header text
		Text headerText = new Text("User");
		headerText.setStyle("-fx-font-size: 24pt;"); // Set the header text size

		// Add the header text to the top of the page content
		pageContent.getChildren().add(headerText);

		
		
		VBox vbox1 = new VBox(20); // Use spacing to separate elements vertically
		vbox1.setPadding(new Insets(20));
		vbox1.setAlignment(Pos.TOP_CENTER);

		Text errorMessage = new Text("");
		errorMessage.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		pageContent.getChildren().add(errorMessage);
		
		Text loginLabel = new Text("Hi " + user + ", you are logged in!");
		loginLabel.setStyle("-fx-font-size: 12pt;"); 
		
		Button logoutBtn = new Button();
		logoutBtn.setPrefSize(100, 30);
		logoutBtn.setText("Log out");
		
		logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				user = null;
				switchPage("User");
			}
		});
		
		vbox1.getChildren().addAll(loginLabel, logoutBtn);
		pageContent.getChildren().add(vbox1);
		return pageContent;
	}

	/**
	 * Create the page content for the require user log in page
	 *
	 * @return Node containing the page content for the require user log in page
	 */
	private Node requireLoginContent() {
		// Set the overall padding and spacing for the page content
		VBox pageContent = new VBox(20); // Use spacing to separate elements vertically
		pageContent.setPadding(new Insets(20)); // Set padding around the entire content

		

		
		
		VBox vbox1 = new VBox(20); // Use spacing to separate elements vertically
		vbox1.setPadding(new Insets(20));
		vbox1.setAlignment(Pos.TOP_CENTER);

		Text errorMessage = new Text("");
		errorMessage.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		pageContent.getChildren().add(errorMessage);
		
		Text loginLabel = new Text("Log in / register required");
		loginLabel.setStyle("-fx-font-size: 12pt;"); 
		
		Button toLoginBtn = new Button();
		toLoginBtn.setPrefSize(150, 30);
		toLoginBtn.setText("Log in / Register");
		
		toLoginBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
				switchPage("User");
			}
		});
		
		vbox1.getChildren().addAll(loginLabel, toLoginBtn);
		pageContent.getChildren().add(vbox1);
		return pageContent;
	}
	
	
	/**
	 * Create the page content for the settings page
	 *
	 * @return Node containing the page content for the settings page
	 */
	private Node settingsContent() {
		// Set the overall padding and spacing for the page content
		VBox pageContent = new VBox(20); // Use spacing to separate elements vertically
		pageContent.setPadding(new Insets(20)); // Set padding around the entire content

		

		// Create the header text
		Text headerText = new Text("Settings");
		headerText.setStyle("-fx-font-size: 24pt;"); // Set the header text size

		// Add the header text to the top of the page content
		pageContent.getChildren().add(headerText);		
		
		GridPane settingPane = new GridPane(); // Use spacing to separate elements vertically
		
		Text menuLabel = new Text("Menu color");
		Text backgroundLabel = new Text("Background color");

		Text errorMessage = new Text("");
		errorMessage.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		
		
		// Changes background color
				cp1.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						Color color = cp1.getValue();
						topPane.setBackground(new Background(new BackgroundFill(color, 
								CornerRadii.EMPTY, Insets.EMPTY)));
					}
					
				});
		
		
		// Changes background color
				cp2.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						Color color = cp2.getValue();
						root.setBackground(new Background(new BackgroundFill(color, 
								CornerRadii.EMPTY, Insets.EMPTY)));
					}
					
				});
		
		Button saveBtn = new Button();
		saveBtn.setPrefSize(100, 30);
		saveBtn.setText("Save");
		
		saveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (user == null) {
					switchPage("User");
				} else {
					if (db.saveSettings(user, cp1.getValue().toString(), cp2.getValue().toString())) {
						errorMessage.setFill(Color.GREEN);
						errorMessage.setText("Settings has been saved");
					} else {
						errorMessage.setFill(Color.RED);
						errorMessage.setText("Failed to save settings");
					}
				}
				
			}
		});
		
		settingPane.add(errorMessage, 0, 0, 2, 1);
		settingPane.add(menuLabel, 0, 1);
		settingPane.add(cp1, 1, 1);
		settingPane.add(backgroundLabel, 0, 2);
		settingPane.add(cp2, 1, 2);
		settingPane.add(saveBtn, 0, 3, 2, 1);
		pageContent.getChildren().add(settingPane);
		settingPane.setHgap(10);
		settingPane.setVgap(10);
		settingPane.setAlignment(Pos.CENTER);
		return pageContent;
	}


	/**
	 * Create a VBox to display information
	 *
	 * @param title
	 * @param count
	 * @return VBox A box containing the title and count
	 */
	private VBox createInfoBox(String title, int count) {
		VBox box = new VBox(10); // Add vertical spacing between children
		box.setAlignment(Pos.CENTER); // Center the children vertically and horizontally

		// Style the VBox with padding, border, and background color
		box.setStyle("-fx-padding: 50; -fx-border-style: solid inside;" + "-fx-border-width: 2; -fx-border-insets: 5;"
				+ "-fx-border-radius: 5; -fx-border-color: #333;");

		// Create and style the title text
		Text boxTitle = new Text(title);
		boxTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		// Create and style the count text
		Text countText = new Text(Integer.toString(count));
		countText.setStyle("-fx-font-size: 14px;");

		// Add the title and count to the VBox
		box.getChildren().addAll(boxTitle, countText);

		return box;
	}


	private void loadSettings(String username) {
		Settings settings = db.loadSettings(username);
		String menuColor = settings.getMenuColor();
		String backgroundColor = settings.getBackgroundColor();
		
		if (menuColor != null) {
			Color color1 = Color.web(menuColor);
			cp1.setValue(color1);
			topPane.setBackground(new Background(new BackgroundFill(color1, 
					CornerRadii.EMPTY, Insets.EMPTY)));
		}
		
		if (backgroundColor != null) {
			Color color2 = Color.web(backgroundColor);
			cp2.setValue(color2);
			root.setBackground(new Background(new BackgroundFill(color2, 
					CornerRadii.EMPTY, Insets.EMPTY)));
		}
		
		
	}
	
	

	public static void main(String[] args) {
		launch();
	}
}