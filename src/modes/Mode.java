package modes;

import colouring.ColourTheme;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public abstract class Mode {
	protected StackPane root;
	protected ColourTheme ct;
	protected Button btnDraw;

	public abstract void UpdateUI(MouseEvent mouseEvent);

	protected abstract void ManageButtons();

	public void SetColour(ColourTheme Newct) {
		ct = Newct;
		if (btnDraw != null)
			btnDraw.fire();
	}
}
