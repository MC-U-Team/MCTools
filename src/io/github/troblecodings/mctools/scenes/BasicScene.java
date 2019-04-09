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

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

/**
 * @author MrTroble
 *
 */
public class BasicScene extends Scene{

	protected GridPane pane;
	
	public BasicScene() {
		super(new GridPane());
		
		this.pane = (GridPane) this.getRoot();
		this.pane.setVgap(15);
		this.pane.setHgap(15);
		this.pane.setAlignment(Pos.CENTER);
		
		init(this.pane);
	}

	protected void init(GridPane pane) {}
}
