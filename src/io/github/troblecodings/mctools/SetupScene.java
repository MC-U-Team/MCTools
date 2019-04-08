package io.github.troblecodings.mctools;

import java.io.*;
import java.nio.file.*;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.jfxtools.*;
import javafx.geometry.Pos;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class SetupScene extends Scene implements EventHandler<ActionEvent>{

	private TextField workspace_path;
	private Label error;
	
	public SetupScene() {
		super(new GridPane());
		
		GridPane pane = (GridPane) this.getRoot();
		pane.setAlignment(Pos.CENTER);
		pane.setVgap(15);
		pane.setHgap(15);
		
		workspace_path = new StyledTextfield("Workspace location");
		pane.add(workspace_path, 0, 0);

		Button use_chooser = new StyledButton("...");
		use_chooser.setOnAction(evt -> {
			DirectoryChooser chooser = new DirectoryChooser();
			File fl = chooser.showDialog(getWindow());
			if(fl != null) workspace_path.setText(fl.getAbsolutePath());
		});
		pane.add(use_chooser, 1, 0);
		
		Button apply = new StyledButton("Apply");
		apply.setOnAction(this);
		pane.add(apply, 0, 1);
		
		error = new StyledLabel("");
		error.setTextFill(Color.RED);
		pane.add(error, 0, 2);
		
		apply.requestFocus();
	}

	@Override
	public void handle(ActionEvent event) {
		String str = workspace_path.getText();
		if(str.isEmpty()) {
			error.setText("Error: Path is empty!");
			return;
		}
		Path path = null;
		try { path = Paths.get(str); } catch ( InvalidPathException ex) {
			error.setText("Error: Path is invalid!");
			return;
		}
		if(!Files.isDirectory(path)) {
			error.setText("Error: Path is not a directory!");
			return;
		}
		if(!Files.exists(path)) {
			error.setText("Error: Path does not exist!");
			return;
		}
		
		try {
			if(!Files.list(path).anyMatch(pth -> {
				return pth.getFileName().toString().contentEquals("build.gradle");
			})) {
				error.setText("Error: No build.gradle found!");
				return;
			}
		} catch (IOException e) {
			error.setText("Error: Failed to list directories content!");
			return;
		}
		
		Settings.setSetting(StringSetting.WORK_SPACE, str);
		UIApp.setScene(new OverViewScene());
	}
	
}
