package io.github.troblecodings.mctools;

import java.awt.Dimension;
import java.awt.Toolkit;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.scenes.OverViewScene;
import io.github.troblecodings.mctools.scenes.SetupScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class UIApp extends Application{
	
	public static final Font FONT = Font.font("Arial", 30);
	
	private static Stage stage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		
		if(Settings.getSetting(StringSetting.WORKSPACE) == null) {
			primaryStage.setScene(new SetupScene());
		} else {
			primaryStage.setScene(new OverViewScene());
		}
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    primaryStage.setHeight(dim.getHeight());
	    primaryStage.setWidth(dim.getWidth());
		primaryStage.show();
		primaryStage.setMaximized(true);
	}
	
	public static void setScene(Scene sc) {
		stage.setScene(sc);
	}
	
	public static void main(String[] args) throws Throwable {
		Settings.init();
		launch(args);
	}

}
