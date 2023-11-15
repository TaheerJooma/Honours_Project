package axes;

import java.text.DecimalFormat;

import javafx.beans.binding.Bindings;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class Axes extends Pane {
	private NumberAxis x;
	private NumberAxis y;

	public Axes(int width, int height, double xMin, double xMax, double xTick, double yMin, double yMax, double yTick,
			double tx, double ty) {
		setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		setPrefSize(width, height);
		setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		x = new NumberAxis(xMin, xMax, xTick);
		x.setSide(Side.BOTTOM);
		x.setMinorTickVisible(false);
		x.tickLabelFontProperty().set(Font.font(12));
		x.setTickLength(4);
		x.setPrefWidth(width);
		x.setLayoutY(height);

		y = new NumberAxis(yMin, yMax, yTick);
		y.setSide(Side.LEFT);
		y.setMinorTickVisible(false);
		y.tickLabelFontProperty().set(Font.font(12));
		y.setTickLength(4);
		y.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number object) {
				DecimalFormat df = new DecimalFormat();
				return df.format(object) + "i";
			}

			@Override
			public Number fromString(String string) {
				return null;
			}

		});
		y.setPrefHeight(height);
		y.layoutXProperty().bind(Bindings.subtract(1, y.widthProperty()));

		setTranslateX(tx);
		setTranslateY(ty);
		getChildren().addAll(x, y);
	}

	public NumberAxis getXAxis() {
		return x;
	}

	public NumberAxis getYAxis() {
		return y;
	}
}
