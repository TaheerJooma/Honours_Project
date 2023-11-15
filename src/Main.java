import colouring.ColourTheme;
import colouring.InvertedBlackAndWhite;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import modes.HomeScreen;
import modes.Mode;

public class Main extends Application {
	private ColourTheme Ct;
	private Mode Cm;
	private String currentMode, currentTheme;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage PrimaryStage) {
		StackPane root = new StackPane();
		root.setAlignment(Pos.TOP_LEFT);
		root.setBackground(new Background(new BackgroundFill(Color.grayRgb(245), CornerRadii.EMPTY, Insets.EMPTY)));
		StackPane mainPane = new StackPane();
		mainPane.setAlignment(Pos.TOP_LEFT);
		mainPane.setPickOnBounds(false);
		HBox UIBar = new HBox(150);
		UIBar.setPadding(new Insets(1, 1, 1, 1));
		UIBar.setMaxSize(1350, 25);
		UIBar.setBackground(new Background(new BackgroundFill(Color.grayRgb(230), CornerRadii.EMPTY, Insets.EMPTY)));
		Label cMode = new Label();
		cMode.setTranslateY(4);
		Label cTheme = new Label();
		cTheme.setTranslateY(4);
		MenuBar menubar = new MenuBar();
		menubar.setBackground(new Background(new BackgroundFill(Color.grayRgb(245), CornerRadii.EMPTY, Insets.EMPTY)));
		Menu mode = new Menu("Mode");
		Menu ColorT = new Menu("Color Theme");
		Menu Exit = new Menu("Exit");
		Menu Restart = new Menu("Restart");
		Menu SubMandel = new Menu("Mandelbrot Set");
		Menu SubJulia = new Menu("Filled Julia Set");
		ToggleGroup modeToggle = new ToggleGroup();
		RadioMenuItem Home = new RadioMenuItem("Home Screen");
		Home.setToggleGroup(modeToggle);
		RadioMenuItem MZoom = new RadioMenuItem("Mandelbrot Set Zoom");
		MZoom.setToggleGroup(modeToggle);
		RadioMenuItem MOrbit = new RadioMenuItem("Mandelbrot Set Orbits");
		MOrbit.setToggleGroup(modeToggle);
		RadioMenuItem MImgG = new RadioMenuItem("Mandelbrot Set Image Generator");
		MImgG.setToggleGroup(modeToggle);
		RadioMenuItem JZoom = new RadioMenuItem("Filled Julia Set Zoom");
		JZoom.setToggleGroup(modeToggle);
		RadioMenuItem JExp = new RadioMenuItem("Filled Julia Set Explorer");
		JExp.setToggleGroup(modeToggle);
		RadioMenuItem JImgG = new RadioMenuItem("Filled Julia Set Image Generator");
		JImgG.setToggleGroup(modeToggle);
		ToggleGroup colorToggle = new ToggleGroup();
		RadioMenuItem CBw = new RadioMenuItem("Black And White");
		CBw.setToggleGroup(colorToggle);
		RadioMenuItem CIbw = new RadioMenuItem("Inverted Black And White");
		CIbw.setToggleGroup(colorToggle);
		RadioMenuItem CHsbR = new RadioMenuItem("HSB red");
		CHsbR.setToggleGroup(colorToggle);
		RadioMenuItem CHsbB = new RadioMenuItem("HSB blue");
		CHsbB.setToggleGroup(colorToggle);
		RadioMenuItem CHsbG = new RadioMenuItem("HSB green");
		CHsbG.setToggleGroup(colorToggle);
		MenuItem exit = new MenuItem("EXIT");
		MenuItem restart = new MenuItem("RESTART");
		SubMandel.getItems().addAll(MZoom, MOrbit, MImgG);
		SubJulia.getItems().addAll(JZoom, JExp, JImgG);
		mode.getItems().addAll(Home, SubMandel, SubJulia);
		ColorT.getItems().addAll(CBw, CIbw, CHsbR, CHsbB, CHsbG);
		Exit.getItems().add(exit);
		Restart.getItems().add(restart);
		menubar.getMenus().addAll(mode, ColorT, Restart, Exit);
		UIBar.getChildren().addAll(menubar, cMode, cTheme);
		root.getChildren().addAll(UIBar, mainPane);

		Scene scene = new Scene(root, 1350, 690);
		PrimaryStage.setScene(scene);
		PrimaryStage.setResizable(false);
		PrimaryStage.setTitle("Mandelbrot and Filled Julia Set Explorer");
		PrimaryStage.show();
		scene.setCursor(Cursor.CROSSHAIR);

		Ct = new InvertedBlackAndWhite();
		CIbw.setSelected(true);
		currentTheme = CIbw.getText();
		cTheme.setText("Current Theme : " + currentTheme);
		Cm = new HomeScreen(mainPane, Ct);
		Home.setSelected(true);
		currentMode = Home.getText();
		cMode.setText("Current Mode : " + currentMode);

		restart.setOnAction(e -> {
			try {
				Cm = (modes.Mode) Class.forName("modes." + currentMode.replace(" ", ""))
						.getDeclaredConstructor(StackPane.class, ColourTheme.class).newInstance(mainPane, Ct);
				Ct = (ColourTheme) Class.forName("colouring." + currentTheme.replace(" ", "")).getDeclaredConstructor()
						.newInstance();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			Cm.SetColour(Ct);
		});

		exit.setOnAction(e -> {
			Platform.exit();
		});

		modeToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			RadioMenuItem Mode = (RadioMenuItem) modeToggle.getSelectedToggle();
			currentMode = Mode.getText();
			cMode.setText("Current Mode : " + currentMode);

			try {
				Cm = (modes.Mode) Class.forName("modes." + currentMode.replace(" ", ""))
						.getDeclaredConstructor(StackPane.class, ColourTheme.class).newInstance(mainPane, Ct);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		colorToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			RadioMenuItem Theme = (RadioMenuItem) colorToggle.getSelectedToggle();
			currentTheme = Theme.getText();
			cTheme.setText("Current Theme : " + currentTheme);

			try {
				Ct = (ColourTheme) Class.forName("colouring." + currentTheme.replace(" ", "")).getDeclaredConstructor()
						.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Cm.SetColour(Ct);
		});

		mainPane.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Cm.UpdateUI(mouseEvent);
			}
		});
	}
}