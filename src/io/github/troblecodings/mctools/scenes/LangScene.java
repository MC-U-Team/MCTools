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

import java.net.*;
import java.nio.file.*;

import io.github.troblecodings.mctools.*;

/**
 * @author MrTroble
 *
 */
public class LangScene extends BasicScene implements Runnable {

	private final OverViewScene scene;

	/**
	 * @param overview scene
	 */
	public LangScene(OverViewScene scene) {
		this.scene = scene;
		this.setOnBackPressed(evt -> UIApp.setScene(scene));

		new Thread(this).start();
	}

	@Override
	public void run() {
	}

}
