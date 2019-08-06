package io.github.troblecodings.mctools.jfxtools.dialog;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.JSONObject;

import io.github.troblecodings.mctools.Cache;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import io.github.troblecodings.mctools.jfxtools.StyledTextfield;
import io.github.troblecodings.mctools.presets.Presets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class CreateItemDialog extends Dialog<String> {

	private final HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
	
	public CreateItemDialog() {
		this.setTitle("Create item");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		final GridPane pane = new GridPane();

		pane.add(new StyledLabel("Item"), 0, 0);

		final StyledTextfield name = new StyledTextfield("name");
		pane.add(name, 1, 0);

		final CheckBox generateModels = new CheckBox("Generate item model");
		generateModels.setSelected(true);
		pane.add(generateModels, 0, 1);
		
		final CheckBox addLangKey = new CheckBox("Add language key");
		addLangKey.setSelected(true);
		pane.add(addLangKey, 0, 1);

		this.setResultConverter(btn -> {
			final String namestr = name.getText();
			if (btn == ButtonType.OK && !namestr.isEmpty()) {
				try {
					Path pth = Paths.get(Cache.getItemDataPath().toString(), namestr + ".json");
					if(Files.exists(pth))
						return null;
					Files.createFile(pth);
					
					Writer writer = Files.newBufferedWriter(pth);
					JSONObject obj = new JSONObject();
					propertiesMap.forEach(obj::put);
					obj.write(writer, 1, 1);
					
					if(addLangKey.isSelected()) {
						Cache.addLangKey(namestr);
						Cache.flushLangJsons();
					}
					
					if (generateModels.isSelected()) {
						String[] arr = new String[] { Cache.getModID(), namestr };
						String json = Presets.get("itemgenerated", arr);
						Path pth2 = Paths.get(Cache.getItemPath().toString(), namestr + ".json");
						Cache.addItemJson(pth2, json);
						Files.write(pth2, json.getBytes());
					}
					return namestr;
				} catch (Throwable e) {
					ExceptionDialog.stacktrace(e);
				}
			}
			return null;
		});

	}

}
