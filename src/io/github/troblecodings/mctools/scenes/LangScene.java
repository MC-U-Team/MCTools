/*-*****************************************************************************
 * Copyright 2018 MrTroble
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package io.github.troblecodings.mctools.scenes;

import java.io.BufferedWriter;
import java.nio.file.Files;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;

/**
 * @author MrTroble
 *
 */
public class LangScene extends BasicScene {

	private ArrayList<String> langList;
	private ArrayList<String> currentKeyList;
	
	/**
	 * @param overview scene
	 */
	public LangScene(OverViewScene scene) {
		this.setOnBackPressed("Apply", evt -> {
			Cache.flushLangJsons();
			UIApp.setScene(scene);
		}, 0, 3);
	}

	@Override
	protected void preinit() throws Throwable {
		langList = new ArrayList<String>();
		currentKeyList = Cache.getLangKeys();
		Cache.getLangJsons().forEach((pth, obj) -> langList.add(pth.getFileName().toString().replace(".json", "")));
	}
	
	@Override
	protected void init(GridPane pane) {
		pane.add(new StyledLabel("Languages"), 0, 0);
		pane.add(new StyledLabel("Keys"), 1, 0);

		final ListView<String> langs = new ListView<String>();
		langs.getItems().addAll(langList);
		langs.setOnMouseClicked(evt -> {
			try {
				final ChangingDialog dialog = new ChangingDialog(Paths.get(Cache.getLangPath().toString(),
						langs.getSelectionModel().getSelectedItem() + ".json"));
				dialog.showAndWait();
			} catch (Throwable e) {
				ExceptionDialog.stacktrace(e);
			}
		});
		pane.add(langs, 0, 1);
		
		final ListView<String> currentKeys = new ListView<String>();
		currentKeys.getItems().addAll(currentKeyList);
		pane.add(currentKeys, 1, 1);

		final StyledButton create = new StyledButton("Create");
		create.setOnAction(evt -> {
			ChoiceDialog<String> local = new ChoiceDialog<String>();
			local.setHeaderText("Choose language!");
			local.getItems().addAll(Lookups.LANG_LOOKUP);
			local.setSelectedItem("en_us");
			local.showAndWait().ifPresent(loc -> createNewLang(loc));
		});
		pane.add(create, 0, 2);
		
		final StyledButton add = new StyledButton("Add");
		add.setOnAction(evt -> {
			final TextInputDialog input = new TextInputDialog();
			input.showAndWait().ifPresent(str -> {
				try {
					currentKeyList = Cache.addLangKey(str);
					currentKeys.getItems().clear();
					currentKeys.getItems().addAll(currentKeyList);
				} catch (Throwable e) {
					ExceptionDialog.stacktrace(e);
				}
			});
		});
		pane.add(add, 1, 2);
	}
	
	private void createNewLang(String name) {
		try {
		final Path file = Paths.get(Cache.getLangPath().toString(), name + ".json");
		if(Files.exists(file)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText("Language file already exists!");
			alert.show();
			return;
		}		
		ChangingDialog dialog = new ChangingDialog(file);
		if(dialog.showAndWait().filter(btn -> btn == ButtonType.OK).isPresent()) {
			langList.clear();
			Cache.getLangJsons().forEach((pth, obj) -> langList.add(pth.getFileName().toString().replace(".json", "")));
		}
		} catch(Throwable e) {
			ExceptionDialog.stacktrace(e);
		}
	}
	
	class ChangingDialog extends Alert {
		
		public ChangingDialog(final Path path) throws Throwable {
			super(AlertType.CONFIRMATION);			
			GridPane pane = new GridPane();
			
			JSONObject obj = Cache.getLangJsons().get(path);
			if(obj == null)
				obj = Cache.addLangJson(path).get(path);
			obj.toMap().forEach((str, str2) -> {
				@SuppressWarnings("deprecation")
				int i = pane.impl_getRowCount();
				pane.add(new StyledLabel(str), 0, i);
				StyledTextfield field = new StyledTextfield("");
				field.setText(str2.toString());
				pane.add(field, 1, i);
			});
			
			this.getDialogPane().setContent(pane);
			final JSONObject tmp = obj;
			this.setResultConverter((btn) -> {
				if(btn == ButtonType.OK) {
					try {
						if(!Files.exists(path))
							Files.createFile(path);
						for (int i = 0; i < tmp.length(); i++) {
							StyledLabel lab = (StyledLabel) pane.getChildren().get(i * 2);
							StyledTextfield tx = (StyledTextfield) pane.getChildren().get(i * 2 + 1);
							tmp.put(lab.getText(), tx.getText());
						}
						BufferedWriter writer = Files.newBufferedWriter(path);
						tmp.write(writer, 1, 1);
						writer.close();
					} catch(Throwable e) {
						ExceptionDialog.stacktrace(e);
					}
				}
				return btn;
			});
		}
		
	}
}
