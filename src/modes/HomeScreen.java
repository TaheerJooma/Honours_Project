package modes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import colouring.ColourTheme;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class HomeScreen extends Mode {
	public HomeScreen(StackPane root, ColourTheme ct) {
		try {
			Image img = new Image(new FileInputStream("HomeScreen/Home.png"));
			ImageView imgview = new ImageView(img);
			imgview.setFitHeight(645);
			imgview.setFitWidth(1330);
			imgview.setTranslateX(10);
			imgview.setTranslateY(36);
			root.getChildren().setAll(imgview);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void UpdateUI(MouseEvent mouseEvent) {
	}

	protected void ManageButtons() {
	}
}
