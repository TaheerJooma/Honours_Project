package plotting;

import java.util.function.Function;

import axes.Axes;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;

public class GeneralPlot extends Plot {
	public GeneralPlot(Function<Double, Double> f, String PlotType, double Min, double Max, double Inc, Color color,
			int StrokeWidth, Axes axes, double tx, double ty) {
		SetUpPath(color, StrokeWidth, axes);
		double y;
		double x;

		switch (PlotType.toLowerCase()) {
		case "y":
			x = Min;
			y = f.apply(x);

			path.getElements().add(new MoveTo(MapXtoX_(x, axes), MapYtoY_(y, axes)));
			x += Inc;

			while (x <= Max) {
				y = f.apply(x);
				path.getElements().add(new LineTo(MapXtoX_(x, axes), MapYtoY_(y, axes)));
				x += Inc;
			}
			break;

		case "x":
			y = Min;
			x = f.apply(y);

			path.getElements().add(new MoveTo(MapXtoX_(x, axes), MapYtoY_(y, axes)));
			y += Inc;

			while (y <= Max) {
				x = f.apply(y);
				path.getElements().add(new LineTo(MapXtoX_(x, axes), MapYtoY_(y, axes)));
				y += Inc;
			}
			break;

		default:
			System.err.println("Invalid PlotType :" + PlotType);
		}

		setTranslateX(tx);
		setTranslateY(ty);
		getChildren().add(path);
	}
}
