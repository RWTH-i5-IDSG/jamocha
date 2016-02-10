/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.gui;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.Jamocha;
import org.jamocha.gui.network.NetworkVisualisation;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.common.Warning;

import java.io.*;
import java.util.Arrays;
import java.util.Queue;
import java.util.prefs.Preferences;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class JamochaGui extends Application {

    static File file = null;
    private TextArea log;
    private Stage primaryStage = null;
    private Jamocha jamocha;
    private final PrintStream out = System.out;
    private final PrintStream err = System.err;
    private NetworkVisualisation networkVisualisation;

    private Scene generateScene() {
        final TabPane tabPane = new TabPane();
        tabPane.setSide(Side.LEFT);

        this.log = new TextArea();
        final Tab logTab = new Tab("Log");
        logTab.setContent(this.log);
        logTab.setClosable(false);

        final Tab networkTab = new Tab("Network");
        networkTab.setClosable(false);
        final ScrollPane scrollPane = new ScrollPane();
        networkTab.setContent(scrollPane);

        this.networkVisualisation = new NetworkVisualisation(this.jamocha.getNetwork());
        this.networkVisualisation.setTranslateX(10);
        this.networkVisualisation.setTranslateY(10);
        this.networkVisualisation.update();
        scrollPane.setContent(this.networkVisualisation);

        tabPane.getTabs().addAll(logTab, networkTab);

        final Scene scene = new Scene(tabPane);
        tabPane.prefHeightProperty().bind(scene.heightProperty());
        tabPane.prefWidthProperty().bind(scene.widthProperty());
        return scene;
    }

    @AllArgsConstructor
    private class LogOutputStream extends OutputStream {

        private final TextArea textArea;

        @Override
        public void write(final int b) throws IOException {
            this.textArea.appendText(String.valueOf((char) b));
        }
    }

    private void loadState(final Stage primaryStage) {
        final Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        // get window location from user preferences: use x=100, y=100, width=400, height=400 as
        // default
        primaryStage.setX(userPrefs.getDouble("stage.x", 100));
        primaryStage.setY(userPrefs.getDouble("stage.y", 100));
        primaryStage.setWidth(userPrefs.getDouble("stage.width", 800));
        primaryStage.setHeight(userPrefs.getDouble("stage.height", 600));
    }

    private void saveState(final Stage primaryStage) {
        final Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        userPrefs.putDouble("stage.x", primaryStage.getX());
        userPrefs.putDouble("stage.y", primaryStage.getY());
        userPrefs.putDouble("stage.width", primaryStage.getWidth());
        userPrefs.putDouble("stage.height", primaryStage.getHeight());
    }

    private void loadFile(final File file) {
        try (final WatchingInputStream wis = new WatchingInputStream(new FileInputStream(file))) {
            this.jamocha.loadParser(wis);
            while (true) {
                wis.mark(1000);
                final Pair<Queue<Warning>, String> parserResult = this.jamocha.parse();
                this.networkVisualisation.update();
                if (null == parserResult) return;
                System.out.println(wis.getStringSinceLastMark());
            }
        } catch (final IOException | ParseException e) {
            e.printStackTrace();
            e.printStackTrace(this.err);
        }
    }

    private static class WatchingInputStream extends BufferedInputStream {

        WatchingInputStream(final InputStream in) {
            super(in);
        }

        public String getStringSinceLastMark() {
            return new String(Arrays.copyOfRange(this.buf, this.markpos, this.pos));
        }

    }

    @Override
    public void start(final Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.jamocha = new Jamocha();

        final Scene scene = generateScene();

        if (file == null) {
            final FileChooser fileChooser = new FileChooser();
            final ExtensionFilter filter = new ExtensionFilter("CLIPS files", "*.clips");
            fileChooser.getExtensionFilters().add(filter);
            fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", "*.*"));
            file = fileChooser.showOpenDialog(primaryStage);
        }

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("Jamocha");
        primaryStage.setScene(scene);
        loadState(primaryStage);
        primaryStage.show();
        try (final PrintStream out = new PrintStream(new LogOutputStream(this.log))) {
            System.setOut(out);
            System.setErr(out);

            if (file != null) {
                System.out.println("Opening file: \"" + file.getName() + "\"");
                loadFile(file);
            } else {
                System.out.println("No file selected!");
            }
        }
    }

    @Override
    public void stop() {
        saveState(this.primaryStage);
        this.jamocha.shutdown();
        System.setOut(this.out);
        System.setErr(this.err);
    }

    public static void main(final String[] args) {
        if (args.length > 0) {
            file = new File(args[0]);
            if (!file.exists()) {
                file = null;
            }
        }
        launch(args);
    }

}
