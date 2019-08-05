package io.github.troblecodings.mctools.scenes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONObject;

import io.github.troblecodings.mctools.Cache;
import io.github.troblecodings.mctools.UIApp;
import io.github.troblecodings.mctools.jfxtools.ExceptionDialog;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import io.github.troblecodings.mctools.jfxtools.StyledTextfield;
import io.github.troblecodings.mctools.lookups.Lookups;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

public class ItemScene extends BasicScene {

	private ArrayList<String> names;

	public ItemScene(OverViewScene scene) {
		this.setOnBackPressed(evt -> UIApp.setScene(scene));
	}

	@Override
	protected void preinit() throws Throwable {
		names = new ArrayList<String>();
		Cache.getItemJsons().forEach((pth, obj) -> names.add(pth.getFileName().toString().replace(".json", "")));
	}

	@Override
	protected void init(GridPane pane) {
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
		
	}

	class InfoDialog extends Dialog<JSONObject> {

		private final GridPane pane;
		private final JSONObject jsonobj;
		
		public InfoDialog(final JSONObject obj) {
			this.initStyle(StageStyle.DECORATED);
			this.pane = new GridPane();
			this.jsonobj = obj;
			this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
			this.getDialogPane().setContent(pane);
			this.setTitle("Model properties");
			this.setResultConverter(btn -> {
				if (btn == ButtonType.APPLY) {
					return jsonobj;
				}
				return null;
			});
			this.setWidth(1000);
			this.setHeight(800);
			this.pane.setPrefSize(1000, 800);
			reload();
		}
		
		private void reload() {
			this.pane.getChildren().clear();
			addButton(pane, jsonobj);
			
			jsonobj.keySet().forEach(key -> propertie(key, jsonobj, jsonobj.get(key), this.pane));
		}

		private void addButton(GridPane pane, final JSONObject obj) {
			StyledButton add = new StyledButton("+");
			add.setOnAction(evt -> {
				ChoiceDialog<String> dialog = new ChoiceDialog<String>();
				Lookups.ITEMMODEL_LOOKUP.keySet().forEach(str -> dialog.getItems().add(str));
				
				ChoiceBox<String> suggestion = new ChoiceBox<String>();
				dialog.getDialogPane().setExpandableContent(suggestion);
				dialog.selectedItemProperty().addListener((ob, o, n) -> {
					suggestion.getItems().clear();
					Lookups.ITEMMODEL_LOOKUP.getJSONArray(dialog.getSelectedItem()).forEach(obj2 -> suggestion.getItems().add(obj2.toString()));
				});
				
				dialog.showAndWait().ifPresent(str -> {
					if (obj.has(str)) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error!");
						alert.setHeaderText("Model property already exists!");
						alert.show();
						return;
					}
					String selected = suggestion.getSelectionModel().getSelectedItem();
					obj.put(str, selected == null ? "":selected);
					reload();
				});
			});
			pane.add(add, 0, 0);
		}
		
		private void propertie(final String name, final JSONObject par, final Object value, final GridPane pane) {
			final GridPane pane2 = new GridPane();

			StyledButton remove = new StyledButton("-");
			remove.setOnAction(evt -> {
				par.remove(name);
				reload();
			});
			pane2.add(remove, 0, 0);

			StyledLabel namel = new StyledLabel(name);
			pane2.add(namel, 1, 0);

			if (value instanceof JSONObject) {
				final JSONObject obj = (JSONObject) value;
				final DialogPane pane3 = new DialogPane();

				final GridPane pane4 = new GridPane();
				
				addButton(pane4, obj);
				
				obj.keySet().forEach(key -> propertie(key, obj, obj.get(key), pane4));

				pane3.setExpandableContent(pane4);
				pane2.add(pane3, 2, 0);
			} else {
				StyledTextfield valuel = new StyledTextfield("");
				valuel.setOnAction(evt -> {
				});
				valuel.setId(name);
				valuel.setText(value.toString());
				pane2.add(valuel, 2, 0);
				
				StyledButton more = new StyledButton("...");
				more.setOnAction(evt -> {
					ChoiceDialog<String> suggestion = new ChoiceDialog<String>();
					Lookups.ITEMMODEL_LOOKUP.getJSONArray(name).forEach(obj2 -> suggestion.getItems().add(obj2.toString()));
					suggestion.showAndWait().ifPresent(str -> valuel.setText(str));
				});
				pane2.add(more, 4, 0);
			}

			@SuppressWarnings("deprecation")
			final int row = pane.impl_getRowCount();
			pane.add(pane2, 0, row);
		}
	}

}
