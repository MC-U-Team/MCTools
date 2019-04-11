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

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.Locale;

import io.github.troblecodings.mctools.*;
import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * @author MrTroble
 *
 */
public class LangScene extends BasicScene {

	private ListView<String> langs;
	private Path pth;

	/**
	 * @param overview scene
	 */
	public LangScene(OverViewScene scene) {
		this.setOnBackPressed(evt -> UIApp.setScene(scene));
	}

	@Override
	protected void init(GridPane pane) {
		this.pth = Paths.get(Settings.getSetting(StringSetting.WORK_SPACE),
				"src\\main\\resources\\assets\\" + JarTools.getModID() + "\\lang");
		langs = new ListView<String>();
		reload();
		pane.add(langs, 0, 0);

		StyledButton create = new StyledButton("Create");
		create.setOnAction(evt -> {
			ChoiceDialog<Locale> local = new ChoiceDialog<Locale>();
			local.setHeaderText("Choose language!");
						
			CheckBox box = new CheckBox("Import unlocalized names!");
			
			// getTranslationKey
			
			try {
				Method methode = JarTools.getBLOCK().getMethod("getTranslationKey");
				JarTools.find(JarTools.getBLOCK()).forEach(inv -> {
					try {
						System.out.println(methode.invoke(inv));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						ExceptionDialog.stacktrace(e);
					}
				});
			} catch (NoSuchMethodException | SecurityException e) {
				ExceptionDialog.stacktrace(e);
			}
			
			local.getDialogPane().setExpandableContent(box);
			local.getItems().addAll(Locale.getAvailableLocales());
			local.showAndWait().ifPresent(loc -> {
				Path lpath = Paths.get(pth.toString(), loc.getLanguage() + "_" + loc.getCountry() + ".lang");
				if (Files.exists(lpath)) {
					try {
						Files.createFile(lpath);
						reload();
					} catch (IOException e) {
						ExceptionDialog.stacktrace(e);
					}
				}
			});
		});
		pane.add(create, 0, 1);
	}

	public void reload() {
		try {
			Files.list(pth).filter(pt -> pth.toString().endsWith(".lang"))
					.forEach(pt -> langs.getItems().add(pt.getFileName().toString()));
		} catch (IOException e) {
			ExceptionDialog.stacktrace(e);
		}
	}
}
