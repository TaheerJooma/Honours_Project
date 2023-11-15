package modes;

import java.util.ArrayList;

import axes.Axes;
import colouring.ColourTheme;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import julia.JuliaSet;
import plotting.Plot;

public class FilledJuliaSetZoom extends Mode {
	private int index, iterations = 100;
	private double K_Re, K_Im;
	private boolean correctInput;
	private ArrayList<JuliaSet> allJulias;
	private Axes currentAxes;
	private GridPane captureInput, info;
	private Label lblSetName, lblPosition, lblxMin, lblxMax, lblyMin, lblyMax, lblCentreX, lblCentreY, lblWidth,
			lblHeight, lblZoomFactor, lblIterations, lblInvalidInput, lblInputError;
	private TextField tfK_Re, tfK_Im, tfIterations;
	private Button btnEdit, btnSubmit, btnForward, btnBack;
	private Rectangle ZoomBox;

	public FilledJuliaSetZoom(StackPane root, ColourTheme ct) {
		this.root = root;
		this.ct = ct;

		captureInput = new GridPane();
		captureInput.setAlignment(Pos.CENTER);
		captureInput.setPickOnBounds(false);
		captureInput.setStyle("-fx-font-size: 16px;");
		captureInput.setVgap(10);
		captureInput.setHgap(10);
		tfK_Re = new TextField();
		tfK_Im = new TextField();
		lblInvalidInput = new Label("Invalid input");
		lblInvalidInput.setTextFill(Color.RED);
		lblInvalidInput.setVisible(false);
		btnSubmit = new Button("Submit");
		captureInput.add(new Label("Filled Julia Set of z\u00B2 + c"), 0, 0, 2, 1);
		captureInput.add(new Label("Where -2.5 \u2264 Re(c) \u2264 2.5 and -2 \u2264 Im(c) \u2264 2"), 0, 1, 2, 1);
		captureInput.add(new Label("Re(c) = "), 0, 2, 1, 1);
		captureInput.add(tfK_Re, 1, 2, 1, 1);
		captureInput.add(new Label("Im(c) = "), 0, 3, 1, 1);
		captureInput.add(tfK_Im, 1, 3, 1, 1);
		GridPane.setHalignment(btnSubmit, HPos.CENTER);
		captureInput.add(btnSubmit, 0, 4, 2, 1);
		GridPane.setHalignment(lblInvalidInput, HPos.CENTER);
		captureInput.add(lblInvalidInput, 0, 5, 2, 1);
		root.getChildren().setAll(captureInput);
		CaptureInput();
	}

	private void CaptureInput() {
		btnSubmit.setOnAction(clicked -> {
			correctInput = true;
			try {
				K_Re = Double.parseDouble(tfK_Re.getText());
				K_Im = Double.parseDouble(tfK_Im.getText());

				if (K_Re < -2.5 || K_Re > 2.5 || K_Im < -2 || K_Im > 2)
					correctInput = false;
			} catch (NumberFormatException nfe) {
				correctInput = false;
			} finally {
				lblInvalidInput.setVisible(!correctInput);
				if (correctInput) {
					ZoomSetUp();
				}
			}
		});
	}

	private void ZoomSetUp() {
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
		lblSetName = new Label("Filled  Julia  Set  of  z\u00B2 " + K_Re + " + " + K_Im + " i");
		lblSetName.setUnderline(true);
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
		btnEdit = new Button("Edit");
		btnForward.setDisable(true);
		btnBack = new Button("Back");
		btnBack.setDisable(true);

		info.add(lblPosition, 0, 0, 3, 1);
		info.add(lblSetName, 0, 1, 2, 1);
		info.add(btnEdit, 2, 1, 1, 1);
		info.add(lblxMin, 0, 2, 3, 1);
		info.add(lblxMax, 0, 3, 3, 1);
		info.add(lblyMin, 0, 4, 3, 1);
		info.add(lblyMax, 0, 5, 3, 1);
		info.add(lblCentreX, 0, 6, 3, 1);
		info.add(lblCentreY, 0, 7, 3, 1);
		info.add(lblWidth, 0, 8, 3, 1);
		info.add(lblHeight, 0, 9, 3, 1);
		info.add(lblZoomFactor, 0, 10, 3, 1);
		info.add(lblIterations, 0, 14, 1, 1);
		info.add(tfIterations, 1, 14, 3, 1);
		HBox buttons = new HBox();
		buttons.setSpacing(30);
		buttons.getChildren().addAll(btnDraw, btnForward, btnBack);
		info.add(buttons, 0, 15, 3, 1);
		info.add(lblInputError, 0, 16, 3, 1);

		allJulias = new ArrayList<>();
		currentAxes = new Axes(805, 644, -2.5, 2.5, 0.3, -2, 2, 0.3, 12, 38);
		JuliaSet jlset = new JuliaSet(currentAxes, iterations, K_Re, K_Im, 12, 38, ct);
		allJulias.add(jlset);
		index = 0;
		UpdateInfo(currentAxes);
		root.getChildren().setAll(jlset, ZoomBox, info);
		ManageButtons();
	}

	public void UpdateUI(MouseEvent mouseEvent) {
		if (correctInput) {
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
						if (index != allJulias.size() - 1) {
							for (int r = allJulias.size() - 1; r > index; r--) {
								allJulias.remove(r);
							}
						}
						currentAxes = new Axes(805, 644, xi, xf, Math.abs(xf - xi), yi, yf, Math.abs(yf - yi), 12, 38);
						JuliaSet Newjlset = new JuliaSet(currentAxes, iterations, K_Re, K_Im, 12, 38, ct);
						UpdateInfo(currentAxes);
						allJulias.add(Newjlset);
						index++;
						ButtonManager();
						root.getChildren().setAll(Newjlset, ZoomBox, info);
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
	}

	protected void ManageButtons() {
		btnEdit.setOnAction(clicked -> {
			root.getChildren().setAll(captureInput);
		});

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
					if (index != allJulias.size() - 1) {
						for (int r = allJulias.size() - 1; r > index; r--) {
							allJulias.remove(r);
						}
					}
					JuliaSet Newjlset = new JuliaSet(currentAxes, iterations, K_Re, K_Im, 12, 38, ct);
					allJulias.add(Newjlset);
					index++;
					ButtonManager();
					root.getChildren().setAll(Newjlset, ZoomBox, info);
				}
			}
		});

		btnForward.setOnAction(clicked -> {
			index++;
			currentAxes = allJulias.get(index).GetAxes();
			iterations = allJulias.get(index).GetIterations();
			tfIterations.setText(String.valueOf(iterations));
			UpdateInfo(currentAxes);
			JuliaSet Newjlset = new JuliaSet(currentAxes, allJulias.get(index).GetIterations(), K_Re, K_Im, 12, 38, ct);
			ButtonManager();
			root.getChildren().setAll(Newjlset, ZoomBox, info);
		});

		btnBack.setOnAction(clicked -> {
			index--;
			currentAxes = allJulias.get(index).GetAxes();
			iterations = allJulias.get(index).GetIterations();
			tfIterations.setText(String.valueOf(iterations));
			UpdateInfo(currentAxes);
			JuliaSet Newjlset = new JuliaSet(currentAxes, allJulias.get(index).GetIterations(), K_Re, K_Im, 12, 38, ct);
			ButtonManager();
			root.getChildren().setAll(Newjlset, ZoomBox, info);
		});
	}

	private void ButtonManager() {
		if (index == 0) {
			btnBack.setDisable(true);
		} else {
			btnBack.setDisable(false);
		}

		if (index == allJulias.size() - 1) {
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
		lblZoomFactor.setText("Zoom Factor = " + Math.sqrt((20d / (Math.abs(xMax - xMin) * Math.abs(yMax - yMin)))));
	}
}
