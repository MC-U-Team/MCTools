package io.github.troblecodings.mctools.jfxtools;

import io.github.troblecodings.mctools.Cache;
import io.github.troblecodings.mctools.presets.Presets;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;

public class CreationDialog extends Alert {

	private String[] values;

	public CreationDialog(final String key) {
		super(AlertType.CONFIRMATION);

		final GridPane pane = new GridPane();

		pane.add(new StyledLabel("Item"), 0, 0);
		pane.add(new StyledTextfield("Name"), 1, 0);

		if (key != "Costume") {
			boolean b = true;
			for (String property : Presets.PRESET_NAMES.get(key)) {
				if (b) {
					b = false;
					continue;
				}
				@SuppressWarnings("deprecation")
				final int row = pane.impl_getRowCount();

				pane.add(new StyledLabel(property), 0, row);
				final StyledTextfield text = new StyledTextfield("");
				text.setText(suggestion(property));
				pane.add(text, 1, row);
			}
		}

		this.getDialogPane().setContent(pane);
		this.setResultConverter(res -> {
			FilteredList<Node> no = pane.getChildren().filtered(node -> node instanceof StyledTextfield);
			values = new String[no.size()];
			for (int j = 0; j < values.length; j++) {
				values[j] = ((StyledTextfield) no.get(j)).getText();
			}
			return res;
		});
	}

	public String[] getValues() {
		return values;
	}

	private String suggestion(final String in) {
		try {
			switch (in) {
			case "modid":
				return Cache.getModID();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}

}

