package modes;

import axes.Axes;
import colouring.ColourTheme;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import julia.JuliaSet;
import mandelbrot.MandelbrotSet;
import plotting.Plot;

public class FilledJuliaSetExplorer extends Mode {
	private GridPane userInput;
	private Axes Maxes, Jaxes;
	private int mndIterations = 80, jlIterations = 80;
	private double K_Re, K_Im;
	private JuliaSet jlset;
	private MandelbrotSet mndlset;
	private Label lblMndlIterations, lblJlIterations, lblPosition, lblJuliaName, lblInputError;
	private TextField tfMndlIterations, tfJlIterations;
	private Circle point;

	public FilledJuliaSetExplorer(StackPane root, ColourTheme ct) {
		this.root = root;
		this.ct = ct;

		userInput = new GridPane();
		userInput.setVgap(8);
		userInput.setHgap(12);
		userInput.setTranslateY(600);
		userInput.setAlignment(Pos.TOP_CENTER);

		tfMndlIterations = new TextField(Integer.toString(mndIterations));
		tfJlIterations = new TextField(Integer.toString(jlIterations));
		lblMndlIterations = new Label("Iterations of Mandelbrot set:");
		lblJlIterations = new Label("Iterations of Julia set:");
		lblPosition = new Label();
		lblPosition.setTranslateX(94);
		lblPosition.setTranslateY(28);
		lblPosition.setStyle("-fx-font: 16px Cambria;");
		lblJuliaName = new Label();
		lblJuliaName.setTranslateX(754);
		lblJuliaName.setTranslateY(28);
		lblJuliaName.setStyle("-fx-font: 16px Cambria;");
		lblInputError = new Label("Iterations of Julia and Mandelbrot set must be a natural number");
		lblInputError.setTextFill(Color.RED);
		lblInputError.setVisible(false);
		point = new Circle(0, 0, 4, Color.WHITE);
		point.setStrokeWidth(2);
		point.setStroke(Color.DARKORANGE);
		point.setVisible(false);
		btnDraw = new Button("Redraw");

		userInput.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(),
				new ColumnConstraints(450), new ColumnConstraints(), new ColumnConstraints());
		userInput.add(lblMndlIterations, 0, 0);
		userInput.add(tfMndlIterations, 1, 0);
		userInput.add(lblJlIterations, 3, 0);
		userInput.add(tfJlIterations, 4, 0);
		GridPane.setHalignment(btnDraw, HPos.CENTER);
		userInput.add(btnDraw, 0, 1, 5, 1);
		GridPane.setHalignment(lblInputError, HPos.CENTER);
		userInput.add(lblInputError, 0, 2, 5, 1);

		Maxes = new Axes(520, 520, -2.1, 2.1, 0.3, -2.1, 2.1, 0.3, 90, 50);
		Jaxes = new Axes(520, 520, -2.1, 2.1, 0.3, -2.1, 2.1, 0.3, 750, 50);
		mndlset = new MandelbrotSet(Maxes, mndIterations, 90, 50, ct);
		root.getChildren().setAll(Maxes, mndlset, lblPosition, userInput);
		ManageButtons();
	}

	public void UpdateUI(MouseEvent mouseEvent) {
		if (mouseEvent.getX() >= 90 && mouseEvent.getX() <= 610 && mouseEvent.getY() >= 50
				&& mouseEvent.getY() <= 570) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
				lblPosition.setText("Z = " + Plot.MapX_toX(mouseEvent.getX() - 90, Maxes) + " + "
						+ Plot.MapY_toY(mouseEvent.getY() - 50, Maxes) + " i");
			}
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				K_Re = Plot.MapX_toX(mouseEvent.getX() - 90, Maxes);
				K_Im = Plot.MapY_toY(mouseEvent.getY() - 50, Maxes);
				point.setVisible(true);
				point.setTranslateX(mouseEvent.getX() - 6);
				point.setTranslateY(mouseEvent.getY() - 6);
				lblJuliaName.setText("Filled  Julia  Set  of  z\u00B2 + " + K_Re + " + " + K_Im + " i");
				jlset = new JuliaSet(Jaxes, jlIterations, K_Re, K_Im, 750, 50, ct);
				root.getChildren().setAll(Maxes, mndlset, Jaxes, jlset, point, lblPosition, lblJuliaName, userInput);
			}
		} else
			lblPosition.setText("");
	}

	protected void ManageButtons() {
		btnDraw.setOnAction(clicked -> {
			boolean redraw = true;
			try {
				mndIterations = Integer.parseInt(tfMndlIterations.getText());
				jlIterations = Integer.parseInt(tfJlIterations.getText());

				if (mndIterations < 1 || jlIterations < 1)
					redraw = false;
			} catch (NumberFormatException nfe) {
				redraw = false;
			} finally {
				lblInputError.setVisible(!redraw);
				if (redraw) {
					mndlset = new MandelbrotSet(Maxes, mndIterations, 90, 50, ct);
					if (jlset == null) {
						root.getChildren().setAll(Maxes, mndlset, lblPosition, userInput);
					} else {
						jlset = new JuliaSet(Jaxes, jlIterations, K_Re, K_Im, 750, 50, ct);
						root.getChildren().setAll(Maxes, mndlset, Jaxes, jlset, point, lblPosition, lblJuliaName,
								userInput);
					}
				}
			}
		});
	}
}
