package io.github.troblecodings.mctools.scenes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.troblecodings.mctools.Cache;
import io.github.troblecodings.mctools.Settings;
import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.UIApp;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import io.github.troblecodings.mctools.jfxtools.StyledTextfield;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class SetupScene extends BasicScene implements EventHandler<ActionEvent> {

	private TextField workspace_path;
	private Label error;

	private final String path;

	public SetupScene() {
		this.path = "";
	}

	public SetupScene(final String path) {
		this.path = path;
	}

	public SetupScene(final BasicScene scene) {
		this(scene, "");
	}

	/**
	 * @param back overview scene
	 * @param Path on creation
	 */
	public SetupScene(final BasicScene scene, final String path) {
		this.path = path;
		this.setOnBackPressed(evt -> UIApp.setScene(scene), 0, 3);
	}

	@Override
	protected void init(GridPane pane) {
		workspace_path = new StyledTextfield("Workspace location");
		workspace_path.setText(path);
		pane.add(workspace_path, 0, 1);

		Button useChooser = new StyledButton("...");
		useChooser.setOnAction(evt -> {
			DirectoryChooser chooser = new DirectoryChooser();
			File fl = chooser.showDialog(getWindow());
			if (fl != null)
				workspace_path.setText(fl.getAbsolutePath());
		});
		pane.add(useChooser, 1, 1);

		Button apply = new StyledButton("Apply");
		apply.setOnAction(this);
		pane.add(apply, 0, 2);

		error = new StyledLabel("");
		error.setTextFill(Color.RED);
		pane.add(error, 0, 0);
		
		StyledButton create = new StyledButton("Create Mod");
		create.setOnAction(evt -> UIApp.setScene(new CreateModScene()));
		pane.add(create, 1, 2);
		
		apply.requestFocus();
	}

	@Override
	public void handle(ActionEvent event) {
		String str = workspace_path.getText();
		if (str == null || str.isEmpty()) {
			error.setText("Error: Path is empty!");
			return;
		}
		Path path = null;
		try {
			path = Paths.get(str);
		} catch (InvalidPathException ex) {
			error.setText("Error: Path is invalid!");
			return;
		}
		if (!Files.isDirectory(path)) {
			error.setText("Error: Path is not a directory!");
			return;
		}
		if (!Files.exists(path)) {
			error.setText("Error: Path does not exist!");
			return;
		}

		try {
			if (Files.list(path).noneMatch(pth -> {
				return pth.getFileName().toString().contentEquals("build.gradle");
			})) {
				error.setText("Error: No build.gradle found!");
				return;
			}
		} catch (IOException e) {
			error.setText("Error: Failed to list directories content!");
			return;
		}

		Settings.setSetting(StringSetting.WORKSPACE, str);
		Cache.clearCache();
		UIApp.setScene(new OverViewScene());
	}

}
