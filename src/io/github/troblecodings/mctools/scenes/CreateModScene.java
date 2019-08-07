package io.github.troblecodings.mctools.scenes;

import java.io.File;
import java.nio.file.Paths;

import io.github.troblecodings.mctools.CreationUtils;
import io.github.troblecodings.mctools.UIApp;
import io.github.troblecodings.mctools.jfxtools.StyledButton;
import io.github.troblecodings.mctools.jfxtools.StyledLabel;
import io.github.troblecodings.mctools.jfxtools.StyledTextfield;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

public class CreateModScene extends BasicScene {

	@Override
	protected void init(GridPane pane) {
		pane.add(new StyledLabel("Location"), 0, 0);

		final StyledTextfield workspacePath = new StyledTextfield("Workspace location");
		pane.add(workspacePath, 1, 0);

		final StyledButton useChooser = new StyledButton("...");
		useChooser.setOnAction(evt -> {
			DirectoryChooser chooser = new DirectoryChooser();
			File fl = chooser.showDialog(getWindow());
			if (fl != null)
				workspacePath.setText(fl.getAbsolutePath());
		});
		pane.add(useChooser, 2, 0);

		pane.add(new StyledLabel("Name"), 0, 1);

		final StyledTextfield textfield = new StyledTextfield("modid");
		pane.add(textfield, 1, 1);
		
		pane.add(new StyledLabel("Namespace"), 0, 2);

		final StyledTextfield namespace = new StyledTextfield("namespace");
		pane.add(namespace, 1, 2);

		pane.add(new StyledLabel("Version"), 0, 3);

		final ChoiceBox<String> versionSelector = new ChoiceBox<String>();
		versionSelector.getItems().addAll("1.14.4");
		versionSelector.getSelectionModel().select(0);
		pane.add(versionSelector, 1, 3);

		final StyledButton apply = new StyledButton("Apply");
		apply.setOnAction(evt -> {
			pane.getChildren().clear();
			pane.add(new StyledLabel("Loading"), 0, 0);
			CreationUtils.createModBase(Paths.get(workspacePath.getText()), textfield.getText(),
					namespace.getText(), versionSelector.getSelectionModel().getSelectedItem());
		});
		pane.add(apply, 1, 4);

		this.setOnBackPressed(evt -> UIApp.setScene(new SetupScene(workspacePath.getText())), 0, 4);
	}
}
