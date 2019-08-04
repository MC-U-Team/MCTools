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

import io.github.troblecodings.mctools.jfxtools.ExceptionDialog;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

/**
 * @author MrTroble
 *
 */
public class BasicScene extends Scene{

	protected GridPane pane;
	protected StyledButton backbutton;
	
	public BasicScene() {
		super(new GridPane());
		
		this.pane = (GridPane) this.getRoot();
		this.pane.setVgap(15);
		this.pane.setHgap(15);
		this.pane.setAlignment(Pos.CENTER);
		
		try { preinit(); } catch (Throwable e) { ExceptionDialog.stacktrace(e, "Exception during preinit!"); }
		init(this.pane);
	}

	public void setOnBackPressed(String name, EventHandler<ActionEvent> run, int x, int y) {
		if(this.backbutton == null) {
			this.backbutton = new StyledButton(name);
			this.pane.add(this.backbutton, x, y);
		}
		this.backbutton.setOnAction(run);
	}
	
	public void setOnBackPressed(EventHandler<ActionEvent> run, int x, int y) {
		setOnBackPressed("Back", run, x, y);
	}
	
	@SuppressWarnings("deprecation")
	public void setOnBackPressed(EventHandler<ActionEvent> run) {
		setOnBackPressed(run, 0, this.pane.impl_getRowCount());
	}
	
	protected void init(GridPane pane) {}
	
	protected void preinit() throws Throwable {}
}
