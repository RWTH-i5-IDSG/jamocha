/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import org.jamocha.Jamocha;
import org.jamocha.languages.clips.parser.generated.ParseException;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class JamochaGui extends Application {

	static File file = null;
	private TextArea log;
	private Stage primaryStage = null;
	private Jamocha jamocha;
	final private PrintStream out = System.out;
	final private PrintStream err = System.err;

	private Scene generateScene() {	
		TabPane tabPane = new TabPane();
		tabPane.setSide(Side.LEFT);

		log = new TextArea();
		log.setEditable(false);	
		Tab logTab = new Tab("Log");
		logTab.setContent(log);
		logTab.setClosable(false);
		
		Tab networkTab = new Tab("Network");
		networkTab.setClosable(false);
		
		tabPane.getTabs().addAll(logTab, networkTab);

		Scene scene = new Scene(tabPane);
		tabPane.prefHeightProperty().bind(scene.heightProperty());
		tabPane.prefWidthProperty().bind(scene.widthProperty());
		return scene;
	}
	
	@AllArgsConstructor
	private class LogOutputStream extends OutputStream {

		final private TextArea textArea;

		@Override
		public void write(int b) throws IOException {
			textArea.appendText(String.valueOf((char) b));
		}

	}

	private void loadState(Stage primaryStage) {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		// get window location from user preferences: use x=100, y=100, width=400, height=400 as
		// default
		primaryStage.setX(userPrefs.getDouble("stage.x", 100));
		primaryStage.setY(userPrefs.getDouble("stage.y", 100));
		primaryStage.setWidth(userPrefs.getDouble("stage.width", 800));
		primaryStage.setHeight(userPrefs.getDouble("stage.height", 600));
	}

	private void saveState(Stage primaryStage) {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		userPrefs.putDouble("stage.x", primaryStage.getX());
		userPrefs.putDouble("stage.y", primaryStage.getY());
		userPrefs.putDouble("stage.width", primaryStage.getWidth());
		userPrefs.putDouble("stage.height", primaryStage.getHeight());
	}

	private void loadFile(File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			jamocha.parse(fileInputStream);
			fileInputStream.close();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			e.printStackTrace(this.err);
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(this.err);
		}
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		jamocha = new Jamocha();

		Scene scene = generateScene();

		if (file == null) {
			FileChooser fileChooser = new FileChooser();
			ExtensionFilter filter = new ExtensionFilter("CLIPS files", "*.clips");
			fileChooser.getExtensionFilters().add(filter);
			file = fileChooser.showOpenDialog(primaryStage);
		}

		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(600);
		primaryStage.setTitle("Jamocha");
		primaryStage.setScene(scene);
		loadState(primaryStage);
		primaryStage.show();
		PrintStream out = new PrintStream(new LogOutputStream(log));
		System.setOut(out);
		System.setErr(out);

		if (file != null) {
			System.out.println("Opening file: \"" + file.getName() + "\"");
			loadFile(file);
		} else {
			System.out.println("No file selected!");
		}
	}

	@Override
	public void stop() {
		saveState(primaryStage);
		jamocha.shutdown();
		System.setOut(this.out);
		System.setErr(this.err);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			file = new File(args[0]);
			if (!file.exists()) {
				file = null;
			}
		}
		launch(args);
	}

}
