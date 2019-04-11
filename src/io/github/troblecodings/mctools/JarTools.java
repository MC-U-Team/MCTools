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

package io.github.troblecodings.mctools;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

import io.github.troblecodings.mctools.Settings.StringSetting;
import io.github.troblecodings.mctools.scenes.OverViewScene;
import javafx.application.Platform;

/**
 * @author MrTroble
 *
 */
public class JarTools extends Thread {

	private static final String BLOCK_CLASS_LOCATION = "net.minecraft.block.Block";
	private static final String ITEM_CLASS_LOCATION = "net.minecraft.item.Item";
	private static final String MOD_CLASS_LOCATION = "net.minecraftforge.fml.common.Mod";
	
	private static JarTools INSTANCE = new JarTools();
	private static Callback callback;
	
	private URI uri;
	private FileSystem fs;
	private String toml;
	//private String manifest;
	private String modid;
	private String name;
	private String mc_version;
	private Class<?> BLOCK;
	private Class<?> ITEM;
	private Class<?> MAIN;
	private Class<?> MOD;
	private final ArrayList<Class<?>> LOADED_CLASSES = new ArrayList<Class<?>>();

	private HashMap<String, String> METHOD_MAPPING = new HashMap<String, String>();
	
	private JarTools() {}
	
	public static void start(Callback cb) {
		callback = cb;
		INSTANCE.start();
	}
	
	public static Stream<Class<?>> find(Class<?> clzz){
		return INSTANCE.LOADED_CLASSES.stream().filter(cls -> clzz.isAssignableFrom(cls));
	}
	
	public static String getMapping(String str) {
		return INSTANCE.METHOD_MAPPING.get(str);
	}
	
	public static String getModID() {
		return INSTANCE.modid;
	}
		
	public static Class<?> getBLOCK() {
		return INSTANCE.BLOCK;
	}

	public static Class<?> getITEM() {
		return INSTANCE.ITEM;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
// DECLARE
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");

			String workspace = Settings.getSetting(StringSetting.WORK_SPACE);
// COMPILE START
			ProcessBuilder builder = new ProcessBuilder("powershell", ".\\gradlew", "build");
			builder.directory(new File(workspace));
			Process process = builder.start();
			Scanner sc = new Scanner(process.getInputStream());
			sc.useDelimiter("\n\r");
			sc.forEachRemaining(line -> {
				OverViewScene.log(line);
			});
			sc.close();
			sc = new Scanner(process.getErrorStream());
			sc.useDelimiter("\n\r");
			sc.forEachRemaining(line -> {
				OverViewScene.log(line);
			});
			int i = process.waitFor();
			sc.close();

			if (i != 0) {
				OverViewScene.log("Failed! Code: " + i);
				return;
			}
// GET MAPPING
			Path mcp = Paths.get("C:\\Users\\" + System.getenv("USERNAME") + "\\.gradle\\caches\\modules-2\\files-2.1\\de.oceanlabs.mcp");
			Files.find(mcp, 20, (mptm, attr) -> mptm.toString().endsWith(".zip")).sorted((pth1, pth2) -> 
			{
				try {
					return Files.readAttributes(pth2, BasicFileAttributes.class).lastModifiedTime().compareTo(Files.readAttributes(pth1, BasicFileAttributes.class).lastModifiedTime());
				} catch (IOException e) {
					ExceptionDialog.stacktrace(e);
				}
				return 0;
			}).findFirst().ifPresent(zip -> {
				URI uri = URI.create("jar:file:/" + zip.toString().replace("\\", "/"));
				OverViewScene.log(uri);
				FileSystem mcpfs = null;
				try{
					mcpfs = FileSystems.getFileSystem(uri);
				} catch( Throwable th) {
					try {
						mcpfs = FileSystems.newFileSystem(uri, env);
					} catch (IOException e) {
						ExceptionDialog.stacktrace(e);
					}
				}
				try {
					Files.readAllLines(mcpfs.getPath("methods.csv")).forEach(str -> METHOD_MAPPING.put(str.split(",")[0], str.split(",")[1]));
				} catch (IOException e) {
					ExceptionDialog.stacktrace(e);
				}
			});
			
			OverViewScene.log("Success! Gathering information!");
// COMPILE END
// START DISCOVERY			
			// INIT FILESYSTEM
			Path buildlibs = Paths.get(workspace, "build/libs");

			Files.list(buildlibs).findFirst()
					.ifPresent(pth -> this.uri = URI.create("jar:file:/" + pth.toString().replace("\\", "/")));
			try {
				this.fs = FileSystems.getFileSystem(uri);
			} catch (Exception e) {
				if (this.fs == null)
					this.fs = FileSystems.newFileSystem(uri, env, null);
			}
			// DISCOVER META
			this.toml = new String(Files.readAllBytes(this.fs.getPath("META-INF/mods.toml")));
			//this.manifest = new String(Files.readAllBytes(this.fs.getPath("META-INF/MANIFEST.MF")));

			this.modid = find(this.toml, "modId");
			this.name = find(this.toml, "displayName");
// LOAD CLASSES
			
			ArrayList<URL> urls = new ArrayList<URL>();
			urls.add(new URL(this.uri.toString() + "!/"));
			try(Stream<Path> stream = Files.find(Paths.get("C:\\Users\\" + System.getenv("USERNAME") +"\\.gradle\\caches\\"), 50, (pth, atr) -> {
				return pth.toString().endsWith(".jar");
			})){
				stream.forEach(pth -> {
					try {
						urls.add(pth.toUri().toURL());
					} catch (MalformedURLException e1) {
						ExceptionDialog.stacktrace(e1);
					}
				});
			}
			URLClassLoader loader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]),
					this.getClass().getClassLoader());
			
			// PRE LOAD CERTAIN CLASSES
			this.BLOCK = loader.loadClass(BLOCK_CLASS_LOCATION);
			this.ITEM = loader.loadClass(ITEM_CLASS_LOCATION);
			this.MOD = loader.loadClass(MOD_CLASS_LOCATION);

			for (Method bl : this.BLOCK.getMethods()) {
				OverViewScene.log(bl);
			}
			OverViewScene.log(this.ITEM);
			
			// LOAD ALL MOD RELATED CLASSES
			Path path = this.fs.getRootDirectories().iterator().next();
			Files.find(path, 20, (pth, attr) -> {
				return pth.toString().endsWith(".class");
			}).forEach(pth -> {
				String str = pth.toString().replaceFirst("/", "").replace("/", ".").replace(".class", "");
				OverViewScene.log(str);
				try {
					Class<?> cls = loader.loadClass(str);
					LOADED_CLASSES.add(cls);
				} catch (Throwable e) {
					ExceptionDialog.stacktrace(e, "Couldn't load class " + str + "\n\rMissing library?");
				}
			});
			LOADED_CLASSES.stream().filter(cls -> cls.isAnnotationPresent((Class<? extends Annotation>) MOD)).findFirst().ifPresent(cl -> MAIN = cl);
		} catch (Throwable e) {
			ExceptionDialog.stacktrace(e);
		}
		OverViewScene.log("Looking for folder structure...");
		initFolders("lang", "blockstates", "textures\\blocks", "textures\\items", "textures\\gui", "models\\block", "models\\item");

		Platform.runLater(() -> JarTools.callback.runAfterCompile(modid, name));
	}

	private static String find(String data, String id) {
		return data.split(id)[1].split("\"")[1];
	}
	
	private void initFolders(String... strings) {
		for (String string : strings) {
			Path pth = Paths.get(Settings.getSetting(StringSetting.WORK_SPACE), "src\\main\\resources\\assets\\" + this.modid + "\\" + string);
			if(!Files.exists(pth)) {
				OverViewScene.log("Creating " + pth.toString());
				try {
					Files.createDirectories(pth);
				} catch (Throwable e) {
					ExceptionDialog dia = new ExceptionDialog(e);
					dia.show();
				}
			}
		}
	}	

	@FunctionalInterface
	public interface Callback{
		
		void runAfterCompile(String modid, String name);
		
	}

}

