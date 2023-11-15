package plotting;

import axes.Axes;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GridLines extends Plot {

	public GridLines(Axes axes, double tx, double ty) {
		HorizontalGridLines(axes);
		VerticalGridLines(axes);
		setTranslateX(tx);
		setTranslateY(ty);
	}

	private void VerticalGridLines(Axes axes) {
		double i_;
		for (double i = axes.getXAxis().getLowerBound() + axes.getXAxis().getTickUnit(); i <= axes.getXAxis()
				.getUpperBound(); i += axes.getXAxis().getTickUnit()) {
			i_ = (Math.round(i * 100000.0) / 100000.0);
			Line Lv = new Line(MapXtoX_(i_, axes), MapYtoY_(axes.getYAxis().getLowerBound(), axes), MapXtoX_(i_, axes),
					MapYtoY_(axes.getYAxis().getUpperBound(), axes));
			Lv.getStrokeDashArray().addAll(4d, 6d);
			if (i_ != 0) {
				Lv.setStroke(Color.LIGHTGRAY);
			} else {
				Lv.setStroke(Color.GRAY);
			}

			getChildren().add(Lv);
		}
	}

	private void HorizontalGridLines(Axes axes) {
		double i_;
		for (double i = axes.getYAxis().getLowerBound() + axes.getYAxis().getTickUnit(); i <= axes.getYAxis()
				.getUpperBound(); i += axes.getYAxis().getTickUnit()) {
			i_ = (Math.round(i * 100000.0) / 100000.0);
			Line Lh = new Line(MapXtoX_(axes.getXAxis().getLowerBound(), axes), MapYtoY_(i_, axes),
					MapXtoX_(axes.getXAxis().getUpperBound(), axes), MapYtoY_(i_, axes));
			Lh.getStrokeDashArray().addAll(4d, 6d);
			if (i_ != 0) {
				Lh.setStroke(Color.LIGHTGRAY);
			} else {
				Lh.setStroke(Color.GRAY);
			}

			getChildren().add(Lh);
		}
	}
}