package julia;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import axes.Axes;
import colouring.ColourTheme;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class JuliaSet extends Pane {
	private BufferedImage img;
	private int TotalIterations;
	private double C_Re, C_Im;
	private Axes axes;
	private static final int threads = 4;
	private ExecutorService executor;

	public JuliaSet(Axes axes, int TotalIterations, double C_Re, double C_Im, double tx, double ty, ColourTheme ct) {
		this.TotalIterations = TotalIterations;
		this.axes = axes;
		this.C_Re = C_Re;
		this.C_Im = C_Im;
		img = new BufferedImage((int) axes.getXAxis().getPrefWidth(), (int) axes.getYAxis().getPrefHeight(),
				BufferedImage.TYPE_INT_RGB);
		executor = Executors.newFixedThreadPool(threads);
		ArrayList<Future<Void>> futures = new ArrayList<>();
		double Dh = (axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound())
				/ axes.getYAxis().getPrefHeight();
		double Dw = (axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound())
				/ axes.getXAxis().getPrefWidth();

		for (int i = 0; i < threads; i++) {
			int yi = (int) (i * (axes.getYAxis().getPrefHeight() / (double) threads));
			int yf = (int) ((i + 1) * (axes.getYAxis().getPrefHeight() / (double) threads));

			futures.add(executor.submit(new Callable<Void>() {
				@Override
				public Void call() {
					double tempZ_Re, tempZ_Im, Z_Re, Z_Im, Z_Re_new, Z_Im_new;
					int iterations;

					for (int y = yi; y < yf; y++) {
						tempZ_Im = axes.getYAxis().getUpperBound() - y * Dh;
						for (int x = 0; x < axes.getXAxis().getPrefWidth(); x++) {
							tempZ_Re = axes.getXAxis().getLowerBound() + x * Dw;
							iterations = 0;
							Z_Re = tempZ_Re;
							Z_Im = tempZ_Im;
							while (Z_Re * Z_Re + Z_Im * Z_Im < 4 && iterations < TotalIterations) {
								Z_Re_new = Z_Re * Z_Re - Z_Im * Z_Im + C_Re;
								Z_Im_new = 2 * Z_Re * Z_Im + C_Im;
								Z_Re = Z_Re_new;
								Z_Im = Z_Im_new;
								iterations++;
							}
							ct.colour(img, x, y, TotalIterations, iterations);
						}
					}
					return null;
				}
			}));
		}
		executor.shutdown();

		try {
			for (Future<Void> f : futures) {
				f.get();
			}

			setTranslateX(tx);
			setTranslateY(ty);
			getChildren().add(new ImageView(SwingFXUtils.toFXImage(img, null)));

			executor.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public Axes GetAxes() {
		return axes;
	}

	public int GetIterations() {
		return TotalIterations;
	}

	public double GetC_Re() {
		return C_Re;
	}

	public double GetC_Im() {
		return C_Im;
	}

}
