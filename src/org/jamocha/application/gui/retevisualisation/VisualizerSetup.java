/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.application.gui.retevisualisation;

public class VisualizerSetup {

	public int offsetX;

	public int offsetY;

	public float scaleX;

	public float scaleY;

	public int lineStyle;

	final static int LINE = 1;

	final static int QUARTERELLIPSE = 2;

	public VisualizerSetup() {
		offsetX = offsetY = 0;
		scaleX = scaleY = 1;
	}

}
