package io.github.troblecodings.mctools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.zip.ZipFile;

import io.github.troblecodings.mctools.jfxtools.dialog.CreationDialog;
import io.github.troblecodings.mctools.jfxtools.dialog.ExceptionDialog;
import io.github.troblecodings.mctools.presets.Presets;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.StageStyle;

public class CreationUtils {

	public static void createModBase(final Path pth, final String preset, final String modid, final String namespace, final String version) {
		Cache.updateModid(modid);
		try {
			// TODO VERSIONING
			URL url = new URL(
					"http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.14.4-28.0.45/forge-1.14.4-28.0.45-mdk.zip");

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			ReadableByteChannel bytechannel = Channels.newChannel(con.getInputStream());
			Path forge = Paths.get(pth.toString(), "Forge.zip");
			if (!Files.exists(forge)) {
				FileOutputStream fos = new FileOutputStream(forge.toString());
				fos.getChannel().transferFrom(bytechannel, 0, Long.MAX_VALUE);
				fos.close();
			}

			ZipFile file = new ZipFile(forge.toFile());
			file.stream().forEach(entry -> {
				try {
					Path c = Paths.get(pth.toString(), entry.getName());
					if (entry.isDirectory()) {
						if (!Files.exists(c))
							Files.createDirectories(c);
						return;
					}
					if (Files.exists(c)) {
						Files.delete(c);
					}
					ReadableByteChannel chn = Channels.newChannel(file.getInputStream(entry));
					Files.createDirectories(c.getParent());
					Files.createFile(c);
					FileOutputStream fos = new FileOutputStream(c.toFile());
					fos.getChannel().transferFrom(chn, 0, Long.MAX_VALUE);
					fos.close();
					chn.close();
				} catch (Throwable e) {
					ExceptionDialog.stacktrace(e);
				}
			});
			file.close();

			Files.delete(forge);

			writePreset(pth, "buildgradle_" + preset, "build.gradle", namespace, modid);
			if(preset.contentEquals("uteamcore")) {
				writePreset(pth, "buildproperties", "build.properties", modid);
			}
			
			ProcessBuilder prb = new ProcessBuilder("cmd", "/C", "gradlew.bat", "genEclipseRuns", "eclipse");
			prb.directory(pth.toFile());
			Process pro = prb.start();

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.getButtonTypes().clear();

			TextArea field = new TextArea();
			alert.getDialogPane().setContent(field);
			alert.initStyle(StageStyle.UNDECORATED);

			new Thread(() -> {
				Scanner sc = new Scanner(pro.getInputStream());
				while (sc.hasNextLine()) {
					String string = (String) sc.nextLine();
					Platform.runLater(() -> field.appendText(string + System.lineSeparator()));
				}
				sc.close();
				Platform.runLater(() -> alert.getButtonTypes().add(ButtonType.OK));
			}).start();

			alert.showAndWait();
			pro.waitFor();

			Path main = Paths.get(pth.toString(), "src\\main\\java");
			Files.walkFileTree(main, LambdaFileVisit.create((pth2, attr) -> {
				try { Files.delete(pth2); } catch (Throwable e) {
					Files.list(pth2).forEach(pth3 -> {
						try { Files.delete(pth3); } catch (Throwable ex) { }
					});
				}
				return FileVisitResult.CONTINUE;
			}));

			String withnamespc = main.toString() + "\\" + namespace.replace(".", "\\");

			String[] folders = { "proxy", "init", "item", "block", "autogen" };
			for (String folder : folders) {
				Path p = Paths.get(withnamespc, folder);
				if (Files.notExists(p))
					Files.createDirectories(p);
			}

			switch (version) {
			case "1.14.4":
				create_1_14_4(pth, preset, modid, namespace);
				break;
			}

		} catch (Throwable e) {
			ExceptionDialog.stacktrace(e);
		}
	}

	private static void create_1_14_4(final Path pth, final String preset, final String modid, final String namespace) throws Throwable {		
		String dirs = "\\src\\main\\java\\" + namespace.replace(".", "\\");
		writePreset(pth, "modmain_" + preset, dirs + "\\ModMain.java", namespace, modid);
		writePreset(pth, "clientproxy_" + preset, dirs + "\\proxy\\ClientProxy.java", namespace);
		writePreset(pth, "commonproxy_" + preset, dirs + "\\proxy\\CommonProxy.java", namespace);
		writePreset(pth, "modblocks_" + preset, dirs + "\\init\\ModBlocks.java", namespace);
		writePreset(pth, "moditems_" + preset, dirs + "\\init\\ModItems.java", namespace);
		writePreset(pth, "moditemgroups_" + preset, dirs + "\\init\\ModItemGroups.java", namespace);
		writePreset(pth, "autogen", dirs + "\\autogen\\Autogen.java", namespace);
		writePreset(pth, "toml_" + preset, "\\src\\main\\resources\\META-INF\\mods.toml");
	}
	
	private static void writePreset(final Path pth, final String pname, final String name, final String... data) throws Throwable {
		if(Presets.NEEDS_DIALOG.contains(pname)) {
			CreationDialog dialog = new CreationDialog(pname, Presets.BASIC_MOD_CREATION, false);
			dialog.showAndWait().ifPresent(json -> 
			{
				try {
					Files.write(Paths.get(pth.toString(), name), json.getBytes());
				} catch (IOException e) {
					ExceptionDialog.stacktrace(e);
				}
			});
			return;
		}
		Files.write(Paths.get(pth.toString(), name),
				Presets.get(pname, Presets.BASIC_MOD_CREATION, data).getBytes());
	}

}
