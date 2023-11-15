package colouring;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HSBgreen extends ColourTheme {
	public void colour(BufferedImage img, int x, int y, int totalItr, int escItr) {
		if (escItr == totalItr) {
			img.setRGB(x, y, Black);
		} else {
			Color color = Color.getHSBColor(((float) escItr / (float) totalItr) + 0.25f, 1.0f, 1.0f);
			img.setRGB(x, y, color.getRGB());
		}
	}
}
