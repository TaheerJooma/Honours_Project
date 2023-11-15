package modes;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import colouring.ColourTheme;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MandelbrotSetImageGenerator extends Mode {
	private int imgWidth, imgHeight, TotalIterations;
	private double xi, xf, yi, yf;
	private GridPane captureInput, progressview;
	private Label lblInvalidInput;
	private TextField tfWidth, tfHeight, tfIterations, tfXi, tfXf, tfYi, tfYf;
	private Button btnSubmit, btnBack;
	private static final int threads = 4;
	private Task<Void> task;

	public MandelbrotSetImageGenerator(StackPane root, ColourTheme ct) {
		this.root = root;
		this.ct = ct;

		captureInput = new GridPane();
		captureInput.setPickOnBounds(false);
		captureInput.setAlignment(Pos.CENTER);
		captureInput.setStyle("-fx-font-size: 16px;");
		captureInput.setVgap(10);
		captureInput.setHgap(10);
		HBox itrRow = new HBox(10);
		itrRow.setAlignment(Pos.CENTER);
		tfWidth = new TextField();
		tfHeight = new TextField();
		tfXi = new TextField();
		tfXf = new TextField();
		tfYi = new TextField();
		tfYf = new TextField();
		tfIterations = new TextField();
		Label lblSize = new Label("Image size");
		lblSize.setUnderline(true);
		Label lblPlotRange = new Label("Plot range and iterations");
		lblPlotRange.setUnderline(true);
		lblInvalidInput = new Label("Invalid input");
		lblInvalidInput.setTextFill(Color.RED);
		lblInvalidInput.setVisible(false);
		btnSubmit = new Button("Generate");
		btnSubmit.setPrefWidth(280);
		btnBack = new Button("Back");
		btnBack.setPrefWidth(100);
		GridPane.setHalignment(lblSize, HPos.CENTER);
		captureInput.add(lblSize, 0, 0, 4, 1);
		captureInput.addRow(1, new Label("Width : "), tfWidth, new Label("Height : "), tfHeight);
		GridPane.setHalignment(lblPlotRange, HPos.CENTER);
		captureInput.add(lblPlotRange, 0, 3, 4, 1);
		captureInput.addRow(4, new Label("xi : "), tfXi, new Label("xf : "), tfXf);
		captureInput.addRow(5, new Label("yi : "), tfYi, new Label("yf : "), tfYf);
		itrRow.getChildren().addAll(new Label("Maximum iterations : "), tfIterations);
		GridPane.setHalignment(itrRow, HPos.CENTER);
		captureInput.add(itrRow, 0, 6, 4, 1);
		GridPane.setHalignment(btnSubmit, HPos.CENTER);
		captureInput.add(btnSubmit, 0, 7, 4, 1);
		GridPane.setHalignment(lblInvalidInput, HPos.CENTER);
		captureInput.add(lblInvalidInput, 0, 8, 4, 1);
		root.getChildren().setAll(captureInput);
		ManageButtons();
	}

	public void UpdateUI(MouseEvent mouseEvent) {
	}

	protected void ManageButtons() {
		btnSubmit.setOnAction(clicked -> {
			boolean correctInput = true;
			try {
				imgWidth = Integer.parseInt(tfWidth.getText());
				imgHeight = Integer.parseInt(tfHeight.getText());
				xi = Double.parseDouble(tfXi.getText());
				xf = Double.parseDouble(tfXf.getText());
				yi = Double.parseDouble(tfYi.getText());
				yf = Double.parseDouble(tfYf.getText());
				TotalIterations = Integer.parseInt(tfIterations.getText());

				if (xf <= xi || yf <= yi || imgWidth < 4 || imgHeight < 4 || TotalIterations < 1)
					correctInput = false;
			} catch (NumberFormatException nfe) {
				correctInput = false;
			} finally {
				lblInvalidInput.setVisible(!correctInput);
				if (correctInput) {
					MakeImage();
				}
			}
		});

		btnBack.setOnAction(clicked -> {
			root.getChildren().setAll(captureInput);
		});
	}

	private void MakeImage() {
		progressview = new GridPane();
		progressview.setPickOnBounds(false);
		progressview.setAlignment(Pos.CENTER);
		progressview.setVgap(10);
		progressview.setHgap(10);
		AtomicInteger progress = new AtomicInteger(0);
		int TotalProgress = imgHeight * imgWidth;
		ProgressBar progBar = new ProgressBar(0);
		ProgressIndicator progIndicator = new ProgressIndicator();
		Label progLabel = new Label();

		VBox vbox = new VBox(5);
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.getChildren().addAll(progBar, progIndicator);
		vbox.getChildren().addAll(progLabel, hbox);
		progressview.addRow(0, vbox);
		GridPane.setHalignment(btnBack, HPos.CENTER);
		progressview.addRow(1, btnBack);
		progressview.setScaleX(1.5);
		progressview.setScaleY(1.5);
		root.getChildren().setAll(progressview);

		task = new Task<Void>() {
			@Override
			public Void call() {
				try {
					if (!isCancelled()) {
						updateMessage("Generating image...");
						double Dh = (yf - yi) / imgHeight;
						double Dw = (xf - xi) / imgWidth;
						BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

						for (int i = 0; i < threads; i++) {
							int ys = (int) (i * (imgHeight / (double) threads));
							int ye = (int) ((i + 1) * (imgHeight / (double) threads));

							new Thread(() -> {
								double C_Re, C_Im, Z_Re, Z_Im, Z_Re_new, Z_Im_new;
								int iterations;

								for (int y = ys; y < ye; y++) {
									C_Im = yf - y * Dh;
									for (int x = 0; x < imgWidth; x++) {
										C_Re = xi + x * Dw;
										iterations = 0;
										Z_Re = C_Re;
										Z_Im = C_Im;
										while (Z_Re * Z_Re + Z_Im * Z_Im <= 4 && iterations < TotalIterations) {
											Z_Re_new = Z_Re * Z_Re - Z_Im * Z_Im + C_Re;
											Z_Im_new = 2 * Z_Re * Z_Im + C_Im;
											Z_Re = Z_Re_new;
											Z_Im = Z_Im_new;
											iterations++;
										}
										ct.colour(img, x, y, TotalIterations, iterations);
										progress.getAndIncrement();
									}
								}
							}).start();
						}
						while (true) {
							updateProgress(progress.get(), TotalProgress);
							if (progress.get() == TotalProgress)
								break;
						}
						updateProgress(progress.get(), TotalProgress);
						updateMessage("Writing Image to file...");
						updateMessage(
								SavetoFile("Mandelbrot_" + imgWidth + "x" + imgHeight + "_" + TotalIterations, img));
					} else
						updateMessage("Task got cancelled");
				} catch (Exception e) {
					e.printStackTrace();
					updateMessage("Error: " + e);
				}
				return null;
			}
		};
		progBar.progressProperty().bind(task.progressProperty());
		progIndicator.progressProperty().bind(task.progressProperty());
		progLabel.textProperty().bind(task.messageProperty());
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	private String SavetoFile(String FileName, BufferedImage img) {
		String message = "";
		File f = null;
		BufferedOutputStream ImageOut = null;
		try {
			Date date = new Date();
			SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			f = new File("Program Images/" + dformat.format(date) + ".png");
			ImageOut = new BufferedOutputStream(new FileOutputStream(f));
			ImageIO.write(img, "png", ImageOut);
		} catch (IOException ioe) {
			System.out.println("Error " + ioe);
		} catch (Exception e) {
			System.out.println("Error " + e);
		} finally {
			message = (f.exists() == true) ? "File Created Successfully" : "File Creation Unsuccessful";
			f = null;
			try {
				ImageOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Runtime.getRuntime().gc();
		}
		return message;
	}
}
