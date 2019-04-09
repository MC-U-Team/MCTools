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

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 * @author MrTroble
 *
 */
public class ExceptionDialog extends Alert{

	public ExceptionDialog(Throwable th) {
		super(AlertType.ERROR);
		this.setTitle("Exception");
		this.setHeaderText("Error: An exception was raised");
		
		TextArea area = new TextArea();
		area.setEditable(false);
		area.setText(th.toString());
		
		this.getDialogPane().setExpandableContent(area);
	}
	
}
