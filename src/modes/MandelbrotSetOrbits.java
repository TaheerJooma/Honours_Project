package modes;

import axes.Axes;
import colouring.ColourTheme;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import mandelbrot.MandelbrotSet;
import plotting.GridLines;
import plotting.Orbit;
import plotting.ParametricPlot;
import plotting.Plot;

public class MandelbrotSetOrbits extends Mode {
	private MandelbrotSet mndlset;
	private Axes axes;
	private GridLines grid;
	private ParametricPlot EscapeRadius, P1boundary, P2boundary;
	private Orbit orbits;
	private GridPane userInput;
	private int mndIterations = 80, plotIterations = 30;
	private Label lblMndlIterations, lblPlotIterations, lblPosition, lblInputError;
	private TextField tfMndlIterations, tfPlotIterations;
	private CheckBox cbgridlines, cbp1Boundary, cbp2Boundary, cbEscRadius;

	public MandelbrotSetOrbits(StackPane root, ColourTheme ct) {
		this.root = root;
		this.ct = ct;

		userInput = new GridPane();
		userInput.setHgap(10);
		userInput.setVgap(10);
		userInput.setTranslateX(730);
		userInput.setTranslateY(520);

		tfMndlIterations = new TextField(Integer.toString(mndIterations));
		tfPlotIterations = new TextField(Integer.toString(plotIterations));
		lblMndlIterations = new Label("Iterations of Mandelbrot set:");
		lblPlotIterations = new Label("Iterations of Orbit:");
		lblPosition = new Label();
		lblPosition.setTranslateX(52);
		lblPosition.setTranslateY(28);
		lblPosition.setStyle("-fx-font: 18px Cambria;");
		lblInputError = new Label("Iterations of orbits and mandelbrot set must be a natural number");
		lblInputError.setTextFill(Color.RED);
		lblInputError.setVisible(false);
		cbgridlines = new CheckBox("Gridlines");
		cbp1Boundary = new CheckBox("Period 1 boundary");
		cbp2Boundary = new CheckBox("Period 2 boundary");
		cbEscRadius = new CheckBox("Escape radius");
		btnDraw = new Button("Redraw");

		HBox checkBoxes = new HBox();
		checkBoxes.setSpacing(45);
		checkBoxes.getChildren().addAll(cbgridlines, cbp1Boundary, cbp2Boundary, cbEscRadius);
		userInput.add(checkBoxes, 0, 0, 4, 1);
		userInput.addRow(1, lblMndlIterations, tfMndlIterations);
		userInput.addRow(2, lblPlotIterations, tfPlotIterations);
		userInput.addRow(3, btnDraw);
		userInput.add(lblInputError, 0, 4, 4, 1);

		axes = new Axes(600, 600, -2.1, 2.1, 0.3, -2.1, 2.1, 0.3, 50, 50);
		grid = new GridLines(axes, 50, 50);
		grid.setVisible(false);
		mndlset = new MandelbrotSet(axes, mndIterations, 50, 50, ct);
		orbits = new Orbit(plotIterations, Color.DARKORANGE, 2, 50, 50);
		EscapeRadius = new ParametricPlot(t -> 2 * Math.cos(t), t -> 2 * Math.sin(t), 0, 2 * Math.PI, 0.01,
				Color.FIREBRICK, 2, axes, 50, 50);
		EscapeRadius.setVisible(false);
		P1boundary = new ParametricPlot(t -> 0.5 * Math.cos(t) - 0.25 * Math.cos(2 * t),
				t -> 0.5 * Math.sin(t) - 0.25 * Math.sin(2 * t), 0, 2 * Math.PI, 0.01, Color.ORANGE, 3, axes, 50, 50);
		P1boundary.setVisible(false);
		P2boundary = new ParametricPlot(t -> 0.25 * Math.cos(t) - 1, t -> 0.25 * Math.sin(t), 0, 2 * Math.PI, 0.01,
				Color.DARKGREEN, 3, axes, 50, 50);
		P2boundary.setVisible(false);
		root.getChildren().setAll(axes, mndlset, grid, EscapeRadius, P1boundary, P2boundary, orbits.GetPane(),
				lblPosition, userInput);
		ManageButtons();
	}

	public void UpdateUI(MouseEvent mouseEvent) {
		if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
			if (mouseEvent.getX() >= 50 && mouseEvent.getX() <= 650 && mouseEvent.getY() >= 50
					&& mouseEvent.getY() <= 650) {
				lblPosition.setText("Z = " + Plot.MapX_toX(mouseEvent.getX() - 50, axes) + " + "
						+ Plot.MapY_toY(mouseEvent.getY() - 50, axes) + " i");
				orbits.PlotOrbit(mouseEvent.getX() - 50, mouseEvent.getY() - 50, axes, 50, 50);
			}
		}
	}

	protected void ManageButtons() {
		btnDraw.setOnAction(clicked -> {
			boolean redraw = true;
			try {
				mndIterations = Integer.parseInt(tfMndlIterations.getText());
				plotIterations = Integer.parseInt(tfPlotIterations.getText());

				if (mndIterations < 1 || plotIterations < 1)
					redraw = false;
			} catch (NumberFormatException nfe) {
				redraw = false;
			} finally {
				lblInputError.setVisible(!redraw);
				if (redraw) {
					mndlset = new MandelbrotSet(axes, mndIterations, 50, 50, ct);
					orbits = new Orbit(plotIterations, Color.DARKORANGE, 2, 50, 50);
					root.getChildren().setAll(axes, mndlset, grid, EscapeRadius, P1boundary, P2boundary,
							orbits.GetPane(), lblPosition, userInput);
				}
			}
		});

		cbgridlines.setOnAction(ticked -> {
			if (cbgridlines.isSelected())
				grid.setVisible(true);
			else
				grid.setVisible(false);
		});

		cbp1Boundary.setOnAction(ticked -> {
			if (cbp1Boundary.isSelected())
				P1boundary.setVisible(true);
			else
				P1boundary.setVisible(false);
		});

		cbp2Boundary.setOnAction(ticked -> {
			if (cbp2Boundary.isSelected())
				P2boundary.setVisible(true);
			else
				P2boundary.setVisible(false);
		});

		cbEscRadius.setOnAction(ticked -> {
			if (cbEscRadius.isSelected())
				EscapeRadius.setVisible(true);
			else
				EscapeRadius.setVisible(false);
		});
	}
}
