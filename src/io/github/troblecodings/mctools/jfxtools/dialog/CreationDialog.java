package io.github.troblecodings.mctools.jfxtools.dialog;

import java.util.HashMap;

import io.github.troblecodings.mctools.Cache;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import io.github.troblecodings.mctools.jfxtools.StyledTextfield;
import io.github.troblecodings.mctools.presets.Presets;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class CreationDialog extends Dialog<String> {

	private String filename;

	public CreationDialog(final String key, final HashMap<String, String[]> map) {
		this(key, map, true);
	}

	public CreationDialog(final String key, final HashMap<String, String[]> map, final boolean namefield) {
		final GridPane pane = new GridPane();

		if (namefield) {
			pane.add(new StyledLabel("Name"), 0, 0);
			pane.add(new StyledTextfield("name"), 1, 0);
		}

		if (key != "Costume") {
			boolean b = true;
			for (String property : map.get(key)) {
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

		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		this.getDialogPane().setContent(pane);
		this.setResultConverter(res -> {
			if (res == ButtonType.CANCEL)
				return null;
			FilteredList<Node> no = pane.getChildren().filtered(node -> node instanceof StyledTextfield);
			String[] values = new String[namefield ? (no.size() - 1) : no.size()];
			filename = ((StyledTextfield) no.get(0)).getText();
			for (int j = namefield ? 1 : 0; j < values.length; j++) {
				StyledTextfield tex = ((StyledTextfield) no.get(j));
				values[j] = tex.getText();
			}
			return Presets.get(key, map, values);
		});
	}

	public String getFilename() {
		return this.filename;
	}

	private String suggestion(final String in) {
		try {
			switch (in) {
			case "modid":
				return Cache.getModID();
			case "logoFile":
				return "logo.png";
			case "displayName":
				return Cache.getModID().substring(0, 1).toUpperCase() + Cache.getModID().substring(1);
			case "uteam_version":
				return "[2.7.0.129,)";
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}

}
