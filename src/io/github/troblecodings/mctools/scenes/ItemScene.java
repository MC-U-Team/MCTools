package io.github.troblecodings.mctools.scenes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONObject;

import io.github.troblecodings.mctools.Cache;
import io.github.troblecodings.mctools.UIApp;
import io.github.troblecodings.mctools.jfxtools.SearchAbleHandler;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import io.github.troblecodings.mctools.jfxtools.dialog.CreationDialog;
import io.github.troblecodings.mctools.jfxtools.dialog.ExceptionDialog;
import io.github.troblecodings.mctools.jfxtools.dialog.InfoDialog;
import io.github.troblecodings.mctools.presets.Presets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class ItemScene extends BasicScene {

	private ArrayList<String> names;
	private ArrayList<String> itemnames;

	public ItemScene(OverViewScene scene) {
		this.setOnBackPressed(evt -> UIApp.setScene(scene));
	}

	@Override
	protected void preinit() throws Throwable {
		names = new ArrayList<String>();
		Cache.getItemJsons().forEach((pth, obj) -> names.add(pth.getFileName().toString().replace(".json", "")));
		itemnames = new ArrayList<String>();
		Files.list(Cache.getItemDataPath()).filter(pth -> !Files.isDirectory(pth) && pth.toString().endsWith(".json"))
				.forEach(name -> itemnames.add(name.getFileName().toString().replace(".json", "")));
	}

	@Override
	protected void init(GridPane pane) {
		pane.getChildren().clear();
		pane.add(new StyledLabel("Models"), 0, 0);

		final ListView<String> models = new ListView<String>();
		models.getItems().addAll(names);
		models.setOnMouseClicked(evt -> {
			try {
				final Path path = Paths.get(Cache.getItemPath().toString(),
						models.getSelectionModel().getSelectedItem() + ".json");
				final JSONObject obj = Cache.getItemJsons().get(path);
				InfoDialog dialog = new InfoDialog(obj);
				dialog.showAndWait().ifPresent(json -> {
					try {
						Cache.getItemJsons().put(path, json);
					} catch (Throwable e) {
						ExceptionDialog.stacktrace(e);
					}
				});
			} catch (Throwable e) {
				ExceptionDialog.stacktrace(e);
			}
		});
		pane.add(models, 0, 1);

		StyledButton button = new StyledButton("Add model");
		button.setOnAction(evt -> {
			ChoiceDialog<String> dialog = new ChoiceDialog<String>("Costume");
			dialog.getItems().addAll(Presets.PRESET_NAMES.keySet());
			SearchAbleHandler.addToDialog(dialog, Presets.PRESET_NAMES.keySet());
			dialog.showAndWait().ifPresent(key -> {
				final CreationDialog dia = new CreationDialog(key);
				dia.showAndWait().filter(ButtonType.OK::equals).ifPresent(btn -> {
					String[] arr = dia.getValues();
					String json = Presets.get(key, arr);
					try {
						Path pth2 = Paths.get(Cache.getItemPath().toString(), arr[0] + ".json");
						Cache.addItemJson(pth2, json);
						Files.write(pth2, json.getBytes());
						names.clear();
						Cache.getItemJsons()
								.forEach((pth, obj) -> names.add(pth.getFileName().toString().replace(".json", "")));
						models.getItems().clear();
						models.getItems().addAll(names);
					} catch (Throwable e) {
						ExceptionDialog.stacktrace(e);
					}
				});
			});
		});
		pane.add(button, 0, 2);

		pane.add(new StyledLabel("Items"), 1, 0);

		final ListView<String> items = new ListView<String>();
		items.getItems().addAll(itemnames);
		items.setOnMouseClicked(evt -> {
		});
		pane.add(items, 1, 1);

		StyledButton additem = new StyledButton("Add item");
		additem.setOnAction(evt -> {

		});
		pane.add(additem, 1, 2);
	}
}
