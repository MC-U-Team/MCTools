package io.github.troblecodings.mctools.jfxtools;

import java.util.Collection;

import javafx.event.EventHandler;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class SearchAbleHandler implements EventHandler<KeyEvent> {

	private String cache = "";
	private ComboBox<String> box = null;
	private final Collection<String> coll;

	public SearchAbleHandler(Collection<String> coll) {
		this.coll = coll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(KeyEvent event) {
		box = (ComboBox<String>) event.getSource();
		if (event.getCharacter().contains("\u0008")) {
			if(cache.length() < 1)
				return;
			cache = cache.substring(0, cache.length() - 1);
			box.getItems().clear();
			coll.stream().filter(str -> str.contains(cache)).forEach(box.getItems()::add);
		} else {
			cache += event.getCharacter();
			coll.stream().filter(str -> !str.contains(cache)).forEach(box.getItems()::remove);
		}
		box.setTooltip(new Tooltip(cache));
	}

	public static void addToDialog(ChoiceDialog<String> dialog, Collection<String> str) {
		((GridPane) dialog.getDialogPane().getContent()).getChildren().filtered(ext -> ext instanceof ComboBox)
				.parallelStream().findFirst()
				.ifPresent(ext -> ext.setOnKeyTyped(new SearchAbleHandler(str)));
	}

}
