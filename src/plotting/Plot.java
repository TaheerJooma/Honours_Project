package plotting;

import axes.Axes;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

public class Plot extends Pane {
	protected Path path;

	protected void SetUpPath(Color color, double strokeWidth, Axes axes) {
		path = new Path();
		path.setStroke(color);
		path.setStrokeWidth(strokeWidth);
		path.setClip(new Rectangle(0, 0, axes.getPrefWidth(), axes.getPrefHeight()));

		setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
		setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
	}

	public static double MapXtoX_(double x, Axes axes) {
		double range = Math.abs(axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound());
		double dx = axes.getPrefWidth() / range;
		return x * dx + (axes.getPrefWidth() / (range / -axes.getXAxis().getLowerBound()));
	}

	public static double MapYtoY_(double y, Axes axes) {
		double range = Math.abs(axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound());
		double dy = axes.getPrefHeight() / range;
		return -y * dy + (axes.getPrefHeight() / (range / axes.getYAxis().getUpperBound()));
	}

	public static double MapX_toX(double x, Axes axes) {
		double range = Math.abs(axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound());
		double dx = axes.getPrefWidth() / range;
		return ((x - (axes.getPrefWidth() / (range / -axes.getXAxis().getLowerBound()))) / dx);

	}

	public static double MapY_toY(double y, Axes axes) {
		double range = Math.abs(axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound());
		double dy = axes.getPrefHeight() / range;
		return (((axes.getPrefHeight() / (range / axes.getYAxis().getUpperBound())) - y) / dy);
	}
}
