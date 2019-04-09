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

package io.github.troblecodings.mctools;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * @author MrTroble
 *
 */
public class OverViewScene extends Scene implements Runnable {

	private TextArea area;
	private String data;
	private String modid;
	
	public OverViewScene() {
		super(new GridPane());
		area = new TextArea();

		GridPane pane = (GridPane) this.getRoot();
		pane.setAlignment(Pos.CENTER);
		pane.setVgap(15);
		pane.setHgap(15);

		String[] directorys = Settings.getSetting(StringSetting.WORK_SPACE).split(Pattern.quote("\\"));
		StyledLabel label = new StyledLabel(directorys[directorys.length - 1]);
		label.setTextFill(Color.BLUE);
		pane.add(label, 0, 0);

		area.appendText("Start build!\n\r");
		area.setEditable(false);
		pane.add(area, 0, 1);
		new Thread(this).start();
		
		StyledButton back = new StyledButton("Switch workspace");
		back.setOnAction(evt -> UIApp.setScene(new SetupScene()));
		pane.add(back, 0, 5);
	}

	@Override
	public void run() {
		try {
			ProcessBuilder builder = new ProcessBuilder("powershell", ".\\gradlew", "build");
			builder.directory(new File(Settings.getSetting(StringSetting.WORK_SPACE)));
			Process process = builder.start();
			Scanner sc = new Scanner(process.getInputStream());
			sc.useDelimiter("\n\r");
			sc.forEachRemaining(line -> {
				Platform.runLater(() -> area.appendText(line + "\n\r"));
			});
			sc.close();
			sc = new Scanner(process.getErrorStream());
			sc.useDelimiter("\n\r");
			sc.forEachRemaining(line -> {
				Platform.runLater(() -> area.appendText(line + "\n\r"));
			});
			int i = process.waitFor();
			sc.close();
			Platform.runLater(() -> afterCompile(i));
		} catch (Throwable e) {
			Platform.runLater(() -> {
				ExceptionDialog dia = new ExceptionDialog(e);
				dia.show();
			});
		}
	}

	private void afterCompile(int i) {
		if (i != 0) {
			area.appendText("Failed! Code: " + i + "\n\r");
			return;
		}
		area.appendText("Success! Gathering information!\n\r");
		Path path = Paths.get(Settings.getSetting(StringSetting.WORK_SPACE), "build/libs");
		try {
			Files.list(path).findFirst().ifPresent(pth -> {
				try {
					Map<String, String> env = new HashMap<>();
					env.put("create", "true");
					
					URI uri = URI.create("jar:file:/" + pth.toString().replace("\\", "/"));
					FileSystem fs = null;
					try { fs = FileSystems.getFileSystem(uri); } catch (Exception e) {
						if(fs == null) fs = FileSystems.newFileSystem(uri, env, null);
					}
					this.data = new String(Files.readAllBytes(fs.getPath("META-INF/mods.toml")));
					this.init();
				} catch (Throwable e) {
					ExceptionDialog dia = new ExceptionDialog(e);
					dia.show();
				}
			});
		} catch (Throwable e) {
			ExceptionDialog dia = new ExceptionDialog(e);
			dia.show();
		}
	}
	
	private void init() {		
		GridPane pane = new GridPane();
		((GridPane)this.getRoot()).add(pane, 1, 1);
		pane.setHgap(15);
		pane.setVgap(15);
		pane.add(new StyledLabel("Mod ID"), 0, 0);
		pane.add(new StyledLabel(modid = find("modId")), 1, 0);
		pane.add(new StyledLabel("Name"), 0, 1);
		pane.add(new StyledLabel(find("displayName")), 1, 1);
		
		area.appendText("Looking for folder structure..." + System.lineSeparator());
		initFolders("lang", "blockstates", "textures\\blocks", "textures\\items", "textures\\gui", "models\\block", "models\\item");
	}
	
	private void initFolders(String... strings) {
		for (String string : strings) {
			Path pth = Paths.get(Settings.getSetting(StringSetting.WORK_SPACE), "src\\main\\resources\\assets\\" + this.modid + "\\" + string);
			if(!Files.exists(pth)) {
				area.appendText("creating " + pth.toString() + System.lineSeparator());
				try {
					Files.createDirectories(pth);
				} catch (Throwable e) {
					ExceptionDialog dia = new ExceptionDialog(e);
					dia.show();
				}
			}
		}
	}
	
	private String find(String id) {
		return this.data.split(id)[1].split("\"")[1];
	}

}
