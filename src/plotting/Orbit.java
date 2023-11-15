package plotting;

import java.util.function.BiFunction;

import axes.Axes;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

public class Orbit extends Plot {
	private int iterations;
	private double StrokeWidth;
	private Color color;
	private double tx, ty;
	private VBox graphs;
	private LineChart<Number, Number> I_vs_Zxy;
	private LineChart<Number, Number> I_vs_Zmod;

	public Orbit(int iterations, Color color, double StrokeWidth, double tx, double ty) {
		this.iterations = iterations;
		this.color = color;
		this.StrokeWidth = StrokeWidth;
		this.tx = tx;
		this.ty = ty;

		ConfigureGraphs();
	}

	public Pane GetPane() {
		setTranslateX(tx);
		setTranslateY(ty);
		getChildren().add(graphs);
		return this;
	}

	public void PlotOrbit(double x_, double y_, Axes axes, double tx, double ty) {
		getChildren().clear();
		SetUpPath(color, StrokeWidth, axes);
		I_vs_Zxy.getData().clear();
		I_vs_Zmod.getData().clear();

		XYChart.Series<Number, Number> S_Zx = new XYChart.Series<>();
		S_Zx.setName("Re(z)");
		XYChart.Series<Number, Number> S_Zy = new XYChart.Series<>();
		S_Zy.setName("Im(z)");
		XYChart.Series<Number, Number> S_Zmod = new XYChart.Series<>();

		double Dh = (axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound()) / axes.getPrefHeight();
		double Dw = (axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound()) / axes.getPrefWidth();
		double C_Re = axes.getXAxis().getLowerBound() + x_ * Dw;
		double C_Im = axes.getYAxis().getUpperBound() - y_ * Dh;

		double Z_Re, Z_Im, Z_Re_new, Z_Im_new;
		BiFunction<Double, Double, Double> fx = (x, y) -> x * x - y * y + C_Re;
		BiFunction<Double, Double, Double> fy = (x, y) -> 2 * x * y + C_Im;

		Z_Re = 0;
		Z_Im = 0;
		path.getElements().add(new MoveTo(MapXtoX_(Z_Re, axes), MapYtoY_(Z_Im, axes)));

		for (int i = 0; i <= iterations; i++) {
			Z_Re_new = fx.apply(Z_Re, Z_Im);
			Z_Im_new = fy.apply(Z_Re, Z_Im);
			Z_Re = Z_Re_new;
			Z_Im = Z_Im_new;

			S_Zx.getData().add(new XYChart.Data<Number, Number>(i, Z_Re));
			S_Zy.getData().add(new XYChart.Data<Number, Number>(i, Z_Im));
			S_Zmod.getData().add(new XYChart.Data<Number, Number>(i, Math.sqrt((Z_Re * Z_Re) + (Z_Im * Z_Im))));
			path.getElements().add(new LineTo(MapXtoX_(Z_Re, axes), MapYtoY_(Z_Im, axes)));

			if ((Z_Re < axes.getXAxis().getLowerBound()) || (Z_Re > axes.getXAxis().getUpperBound())
					|| (Z_Im < axes.getYAxis().getLowerBound()) || (Z_Im > axes.getYAxis().getUpperBound())) {
				break;
			}
		}

		I_vs_Zxy.getData().add(S_Zx);
		I_vs_Zxy.getData().add(S_Zy);
		I_vs_Zmod.getData().add(S_Zmod);

		S_Zx.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 1.5px;");
		S_Zy.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 1.5px;");
		S_Zmod.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 1.5px;");

		relocate(tx, ty);

		getChildren().addAll(path, graphs);
	}

	private void ConfigureGraphs() {
		NumberAxis X_itr1 = new NumberAxis();
		X_itr1.setAutoRanging(false);
		X_itr1.setUpperBound(iterations);
		X_itr1.setMinorTickVisible(false);
		X_itr1.setLabel("Iterations");

		NumberAxis X_itr2 = new NumberAxis();
		X_itr2.setAutoRanging(false);
		X_itr2.setUpperBound(iterations);
		X_itr2.setMinorTickVisible(false);
		X_itr2.setLabel("Iterations");

		NumberAxis Y_mod = new NumberAxis();
		Y_mod.setMinorTickVisible(false);
		Y_mod.setLabel("|z|");

		NumberAxis Y_Zxy = new NumberAxis();
		Y_Zxy.setMinorTickVisible(false);
		Y_Zxy.setLabel("Re(z) and Im(z)");

		I_vs_Zxy = new LineChart<>(X_itr1, Y_Zxy);
		I_vs_Zxy.setAnimated(false);
		I_vs_Zxy.setCreateSymbols(false);
		I_vs_Zxy.setPrefSize(600, 250);
		I_vs_Zxy.setMaxSize(600, 250);
		I_vs_Zxy.setTitle("Re(z\u2099) and Im(z\u2099) vs Iterations");
		I_vs_Zxy.setBorder(new Border(
				new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		I_vs_Zmod = new LineChart<>(X_itr2, Y_mod);
		I_vs_Zmod.setAnimated(false);
		I_vs_Zmod.setCreateSymbols(false);
		I_vs_Zmod.setPrefSize(600, 180);
		I_vs_Zmod.setMaxSize(600, 180);
		I_vs_Zmod.setTitle("|z\u2099| vs Iterations");
		I_vs_Zmod.setBorder(new Border(
				new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		graphs = new VBox();
		graphs.setSpacing(20);
		graphs.setTranslateX(680);
		graphs.getChildren().addAll(I_vs_Zxy, I_vs_Zmod);
	}
}
