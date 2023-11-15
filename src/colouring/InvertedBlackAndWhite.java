package colouring;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class InvertedBlackAndWhite extends ColourTheme {
	public void colour(BufferedImage img, int x, int y, int totalItr, int escItr) {
		int ratio = (int) (255 * ((float) escItr / (float) totalItr));
		img.setRGB(x, y, new Color(ratio, ratio, ratio).getRGB());
	}
}
