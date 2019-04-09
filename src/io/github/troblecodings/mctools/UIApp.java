package io.github.troblecodings.mctools;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.scenes.*;
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
		
		if(Settings.getSetting(StringSetting.WORK_SPACE) == null) {
			primaryStage.setScene(new SetupScene());
		} else {
			primaryStage.setScene(new OverViewScene());
		}
		
	    primaryStage.setMaximized(true);
		primaryStage.show();
	}
	
	public static void setScene(Scene sc) {
		stage.setScene(sc);
		stage.setMaximized(false);
		stage.setMaximized(true);
	}
	
	public static void main(String[] args) throws Throwable {
		Settings.init();
		launch(args);
	}

}
