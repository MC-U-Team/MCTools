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

import java.util.regex.Pattern;

import io.github.troblecodings.mctools.*;
import io.github.troblecodings.mctools.JarTools.Callback;
import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.jfxtools.*;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * @author MrTroble
 *
 */
public class OverViewScene extends BasicScene implements Callback{

	private static TextArea area;

	@Override
	protected void init(GridPane pane) {
		String[] directorys = Settings.getSetting(StringSetting.WORK_SPACE).split(Pattern.quote("\\"));
		StyledLabel label = new StyledLabel(directorys[directorys.length - 1]);
		label.setTextFill(Color.BLUE);
		pane.add(label, 0, 0);

		area = new TextArea();
		area.appendText("Start build!\n\r");
		area.setEditable(false);
		pane.add(area, 0, 1);
		JarTools.start(this);
		
		StyledButton back = new StyledButton("Switch workspace");
		back.setOnAction(evt -> UIApp.setScene(new SetupScene(this)));
		pane.add(back, 0, 2);
	}
		
	public static void log(Object str) {
		log(str.toString());
	}
	
	public static void log(Object str, Object st) {
		log(str.toString() + "=" + st.toString());
	}
	
	public static void log(String str) {
		Platform.runLater(() -> area.appendText(str + System.lineSeparator()));
	}

	@Override
	public void runAfterCompile(String modid, String name) {
		GridPane pane = new GridPane();
		this.pane.add(pane, 1, 1);
		pane.setHgap(15);
		pane.setVgap(15);
		pane.add(new StyledLabel("Mod ID"), 0, 0);
		pane.add(new StyledLabel(modid), 1, 0);
		pane.add(new StyledLabel("Name"), 0, 1);
		pane.add(new StyledLabel(name), 1, 1);
				
		StyledButton lang = new StyledButton("Localisation");
		lang.setOnAction(evt -> UIApp.setScene(new LangScene(this)));
		pane.add(lang, 0, 2);
		
		StyledButton blocks = new StyledButton("Blocks");
		blocks.setOnAction(evt -> {
			
		});
		pane.add(blocks, 1, 2);

		StyledButton items = new StyledButton("Items");
		items.setOnAction(evt -> {
			
		});
		pane.add(items, 0, 3);
		
		StyledButton gui = new StyledButton("Gui");
		gui.setOnAction(evt -> {
			
		});
		pane.add(gui, 1, 3);
	}
}
