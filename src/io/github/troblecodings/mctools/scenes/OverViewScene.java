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

import io.github.troblecodings.mctools.Cache;
import io.github.troblecodings.mctools.UIApp;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * @author MrTroble
 *
 */
public class OverViewScene extends BasicScene {

	private String name = "undefined";
	
	@Override
	protected void preinit() throws Throwable {
		this.name = Cache.getModID();
	}
	
	@Override
	protected void init(GridPane rootpane) {
		StyledLabel label = new StyledLabel(name.toUpperCase());
		label.setTextFill(Color.BLUE);
		rootpane.add(label, 0, 0);

		GridPane pane = new GridPane();
		this.pane.add(pane, 0, 1);
		pane.setHgap(15);
		pane.setVgap(15);
				
		StyledButton lang = new StyledButton("Localisation");
		lang.setOnAction(evt -> UIApp.setScene(new LangScene(this)));
		pane.add(lang, 0, 0);
		
		StyledButton blocks = new StyledButton("Blocks");
		blocks.setOnAction(evt -> {
			
		});
		pane.add(blocks, 1, 0);

		StyledButton items = new StyledButton("Items");
		items.setOnAction(evt -> UIApp.setScene(new ItemScene(this)));
		pane.add(items, 0, 1);
		
		StyledButton gui = new StyledButton("Gui");
		gui.setOnAction(evt -> {
			
		});
		pane.add(gui, 1, 1);
		
		this.setOnBackPressed("Switch workspace", evt -> UIApp.setScene(new SetupScene(this)), 0, 2);
	}
		
}
