package modes;

import java.util.ArrayList;

import axes.Axes;
import colouring.ColourTheme;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mandelbrot.MandelbrotSet;
import plotting.Plot;

public class MandelbrotSetZoom extends Mode {
	private int index, iterations = 100;
	private ArrayList<MandelbrotSet> allMandelbrots;
	private Axes currentAxes;
	private GridPane info;
	private Label lblPosition, lblxMin, lblxMax, lblyMin, lblyMax, lblCentreX, lblCentreY, lblWidth, lblHeight,
			lblZoomFactor, lblIterations, lblInputError;
	private TextField tfIterations;
	private Button btnForward, btnBack;
	private Rectangle ZoomBox;

	public MandelbrotSetZoom(StackPane root, ColourTheme ct) {
		this.root = root;
		this.ct = ct;

		ZoomBox = new Rectangle(0, 0, 0, 0);
		ZoomBox.setVisible(false);
		ZoomBox.setFill(Color.WHITE);
		ZoomBox.setOpacity(0.4);
		ZoomBox.setStroke(Color.DARKORANGE);
		ZoomBox.setStrokeWidth(2.5);

		info = new GridPane();
		info.setVgap(20);
		info.setHgap(20);
		info.setTranslateX(900);
		info.setTranslateY(80);

		lblPosition = new Label();
		lblPosition.setStyle("-fx-font: 18px Cambria;");
		lblxMin = new Label("x Min = ");
		lblxMax = new Label("x Max = ");
		lblyMin = new Label("y Min = ");
		lblyMax = new Label("y Max = ");
		lblCentreX = new Label("Centre x = ");
		lblCentreY = new Label("Centre y = ");
		lblWidth = new Label("Width = ");
		lblHeight = new Label("Height = ");
		lblZoomFactor = new Label("Zoom Factor = ");
		lblIterations = new Label("Iterations : ");
		lblInputError = new Label("Iterations must be a natural number");
		lblInputError.setTextFill(Color.RED);
		lblInputError.setVisible(false);
		tfIterations = new TextField(String.valueOf(iterations));
		tfIterations.setMaxWidth(100);
		btnDraw = new Button("Redraw");
		btnForward = new Button("Forward");
		btnForward.setDisable(true);
		btnBack = new Button("Back");
		btnBack.setDisable(true);

		info.add(lblPosition, 0, 0, 3, 1);
		info.add(lblxMin, 0, 1, 3, 1);
		info.add(lblxMax, 0, 2, 3, 1);
		info.add(lblyMin, 0, 3, 3, 1);
		info.add(lblyMax, 0, 4, 3, 1);
		info.add(lblCentreX, 0, 5, 3, 1);
		info.add(lblCentreY, 0, 6, 3, 1);
		info.add(lblWidth, 0, 7, 3, 1);
		info.add(lblHeight, 0, 8, 3, 1);
		info.add(lblZoomFactor, 0, 9, 3, 1);
		info.add(lblIterations, 0, 14, 1, 1);
		info.add(tfIterations, 1, 14, 3, 1);
		HBox buttons = new HBox();
		buttons.setSpacing(30);
		buttons.getChildren().addAll(btnDraw, btnForward, btnBack);
		info.add(buttons, 0, 15, 3, 1);
		info.add(lblInputError, 0, 16, 3, 1);

		allMandelbrots = new ArrayList<>();
		currentAxes = new Axes(805, 644, -2.5, 1, 0.35, -1.4, 1.4, 0.28, 12, 38);
		MandelbrotSet mndlset = new MandelbrotSet(currentAxes, 100, 12, 38, ct);
		allMandelbrots.add(mndlset);
		index = 0;
		UpdateInfo(currentAxes);
		root.getChildren().setAll(mndlset, ZoomBox, info);
		ManageButtons();
	}

	public void UpdateUI(MouseEvent mouseEvent) {
		if (mouseEvent.getX() >= 12 && mouseEvent.getX() <= 817 && mouseEvent.getY() >= 38
				&& mouseEvent.getY() <= 682) {
			lblPosition.setText("Z = " + Plot.MapX_toX(mouseEvent.getX() - 12, currentAxes) + " + "
					+ Plot.MapY_toY(mouseEvent.getY() - 38, currentAxes) + " i");
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
				ZoomBox.setTranslateX(mouseEvent.getX());
				ZoomBox.setTranslateY(mouseEvent.getY());
			}
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				ZoomBox.setVisible(true);
				double dl = mouseEvent.getX();
				if ((ZoomBox.getTranslateY() + 0.8 * (dl - ZoomBox.getTranslateX())) <= 682) {
					ZoomBox.setWidth(dl - ZoomBox.getTranslateX());
					ZoomBox.setHeight(0.8 * (dl - ZoomBox.getTranslateX()));
				}
			}
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				double xi = Plot.MapX_toX(ZoomBox.getTranslateX() - 12, currentAxes);
				double xf = Plot.MapX_toX(ZoomBox.getTranslateX() + ZoomBox.getWidth() - 12, currentAxes);
				double yi = Plot.MapY_toY(ZoomBox.getTranslateY() + ZoomBox.getHeight() - 38, currentAxes);
				double yf = Plot.MapY_toY(ZoomBox.getTranslateY() - 38, currentAxes);

				if ((xf - xi) * (yf - yi) > 0) {
					if (index != allMandelbrots.size() - 1) {
						for (int r = allMandelbrots.size() - 1; r > index; r--) {
							allMandelbrots.remove(r);
						}
					}
					currentAxes = new Axes(805, 644, xi, xf, Math.abs(xf - xi), yi, yf, Math.abs(yf - yi), 12, 38);
					MandelbrotSet NewMandelbrotSet = new MandelbrotSet(currentAxes, iterations, 12, 38, ct);
					UpdateInfo(currentAxes);
					allMandelbrots.add(NewMandelbrotSet);
					index++;
					ButtonVisibility();
					root.getChildren().setAll(NewMandelbrotSet, ZoomBox, info);
				}
				ZoomBox.setVisible(false);
				ZoomBox.setWidth(0);
				ZoomBox.setHeight(0);
			}
		} else {
			lblPosition.setText("");
			if (ZoomBox.getWidth() > 0 || ZoomBox.getHeight() > 0) {
				ZoomBox.setVisible(false);
				ZoomBox.setWidth(0);
				ZoomBox.setHeight(0);
			}
		}
	}

	protected void ManageButtons() {
		btnDraw.setOnAction(clicked -> {
			boolean redraw = true;
			try {
				iterations = Integer.parseInt(tfIterations.getText());

				if (iterations < 1)
					redraw = false;
			} catch (NumberFormatException nfe) {
				redraw = false;
			} finally {
				lblInputError.setVisible(!redraw);
				if (redraw) {
					if (index != allMandelbrots.size() - 1) {
						for (int r = allMandelbrots.size() - 1; r > index; r--) {
							allMandelbrots.remove(r);
						}
					}
					MandelbrotSet NewMandelbrotSet = new MandelbrotSet(currentAxes, iterations, 12, 38, ct);
					allMandelbrots.add(NewMandelbrotSet);
					index++;
					ButtonVisibility();
					root.getChildren().setAll(NewMandelbrotSet, ZoomBox, info);
				}
			}
		});

		btnForward.setOnAction(clicked -> {
			index++;
			currentAxes = allMandelbrots.get(index).GetAxes();
			iterations = allMandelbrots.get(index).GetIterations();
			tfIterations.setText(String.valueOf(iterations));
			UpdateInfo(currentAxes);
			MandelbrotSet NewMandelbrotSet = new MandelbrotSet(currentAxes, allMandelbrots.get(index).GetIterations(),
					12, 38, ct);
			ButtonVisibility();
			root.getChildren().setAll(NewMandelbrotSet, ZoomBox, info);
		});

		btnBack.setOnAction(clicked -> {
			index--;
			currentAxes = allMandelbrots.get(index).GetAxes();
			iterations = allMandelbrots.get(index).GetIterations();
			tfIterations.setText(String.valueOf(iterations));
			UpdateInfo(currentAxes);
			MandelbrotSet NewMandelbrotSet = new MandelbrotSet(currentAxes, allMandelbrots.get(index).GetIterations(),
					12, 38, ct);
			ButtonVisibility();
			root.getChildren().setAll(NewMandelbrotSet, ZoomBox, info);

		});
	}

	private void ButtonVisibility() {
		if (index == 0) {
			btnBack.setDisable(true);
		} else {
			btnBack.setDisable(false);
		}

		if (index == allMandelbrots.size() - 1) {
			btnForward.setDisable(true);
		} else {
			btnForward.setDisable(false);
		}
	}

	private void UpdateInfo(Axes CurrentAxes) {
		double xMin = CurrentAxes.getXAxis().getLowerBound();
		double xMax = CurrentAxes.getXAxis().getUpperBound();
		double yMin = CurrentAxes.getYAxis().getLowerBound();
		double yMax = CurrentAxes.getYAxis().getUpperBound();

		lblxMin.setText("x Min = " + xMin);
		lblxMax.setText("x Max = " + xMax);
		lblyMin.setText("y Min = " + yMin);
		lblyMax.setText("y Max = " + yMax);
		lblCentreX.setText("Centre x = " + ((xMax + xMin) / 2));
		lblCentreY.setText("Centre y = " + ((yMax + yMin) / 2));
		lblWidth.setText("Width = " + Math.abs(xMax - xMin));
		lblHeight.setText("Height = " + Math.abs(yMax - yMin));
		lblZoomFactor.setText("Zoom Factor = " + Math.sqrt((9.8 / (Math.abs(xMax - xMin) * Math.abs(yMax - yMin)))));
	}
}
