

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.application.*;
import javafx.event.EventHandler;

public class Main extends Application{
	
	private Button newProjectBtn;
	private Button openExcelBtn;
	private Button openProjectBtn;

	private Stage primaryStage;
	private BorderPane mainLayout;
	
	
	
	public void start(final Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Rabbit Catcher");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("screens/view/Screen1.fxml"));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		newProjectBtn = (Button) scene.lookup("#new_project_btn");
		openExcelBtn = (Button) scene.lookup("#new_project_btn");
		openProjectBtn = (Button) scene.lookup("#new_project_btn");
		
		newProjectBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent arg0) {
				new FXMLLoader();
				try {
					Scene newScene = new Scene((Pane) FXMLLoader.load(Main.class.getResource("screens/view/Screen2.fxml")));
					primaryStage.setScene(newScene);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);

	}

}
