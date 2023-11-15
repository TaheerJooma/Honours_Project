package colouring;

import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class ColourTheme {
	protected int Black = Color.BLACK.getRGB();

	public abstract void colour(BufferedImage img, int x, int y, int totalItr, int escItr);
}
