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

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.regex.Pattern;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * @author MrTroble
 *
 */
public class OverViewScene extends Scene implements Runnable{

	private TextArea area;
	
	public OverViewScene() {
		super(new GridPane());
		area = new TextArea();
		
		GridPane pane = (GridPane) this.getRoot();
		pane.setAlignment(Pos.CENTER);

		String[] directorys = Settings.getSetting(StringSetting.WORK_SPACE).split(Pattern.quote("\\"));
		StyledLabel label = new StyledLabel(directorys[directorys.length - 1]);
		pane.add(label, 0, 0);
		
		area.appendText("Start build");
		area.setEditable(false);
		pane.add(area, 0, 1);
		new Thread(this).start();
	}

	private Path findModMain(Path path) {
		try {
			return Files.find(path, 15, (pth, atr) -> {
				if (pth.getFileName().endsWith(".java")) {
					try {
						return new String(Files.readAllBytes(pth)).contains("@Mod");
					} catch (IOException e) {
						ExceptionDialog dia = new ExceptionDialog(e);
						dia.show();
					}
				}
				return false;
			}).findFirst().get();
		} catch (IOException e) {
			ExceptionDialog dia = new ExceptionDialog(e);
			dia.show();
		}
		return null;
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
		} catch (IOException | InterruptedException e) {
			Platform.runLater(() -> { ExceptionDialog dia = new ExceptionDialog(e);
			dia.show();});
		}
	}

	private void afterCompile(int i) {
		area.appendText("Finished! Code: " + i + "\n\r");
	}
	
 }
