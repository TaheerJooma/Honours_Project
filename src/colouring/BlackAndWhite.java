package colouring;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class BlackAndWhite extends ColourTheme {
	public void colour(BufferedImage img, int x, int y, int totalItr, int escItr) {
		if (escItr == totalItr) {
			img.setRGB(x, y, Black);
		} else
			img.setRGB(x, y, Color.WHITE.getRGB());
	}
}
