package plotting;

import java.util.function.Function;

import axes.Axes;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

public class ParametricPlot extends Plot {
	public ParametricPlot(Function<Double, Double> Xt, Function<Double, Double> Yt, double tMin, double tMax,
			double tInc, Color color, int strokeWidth, Axes axes, double tx, double ty) {
		SetUpPath(color, strokeWidth, axes);
		double t = tMin;
		double x = Xt.apply(t);
		double y = Yt.apply(t);

		path.getElements().add(new MoveTo(MapXtoX_(x, axes), MapYtoY_(y, axes)));
		t += tInc;

		while (t <= tMax) {
			x = Xt.apply(t);
			y = Yt.apply(t);
			path.getElements().add(new LineTo(MapXtoX_(x, axes), MapYtoY_(y, axes)));
			t += tInc;
		}

		setTranslateX(tx);
		setTranslateY(ty);
		getChildren().addAll(path);
	}
}
