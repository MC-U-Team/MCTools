package io.github.troblecodings.mctools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

import io.github.troblecodings.mctools.jfxtools.dialog.ExceptionDialog;

public class CreationUtils {

	public static void createModBase(final Path pth, final String modid, final String namespace, final String version) {
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

			switch (version) {
			case "1.14.4":
				create_1_14_4(pth, modid, namespace);
				break;
			}
		} catch (Throwable e) {
			ExceptionDialog.stacktrace(e);
		}
	}

	private static void create_1_14_4(final Path pth, final String modid, final String namespace) throws Throwable{
		Path main = Paths.get(pth.toString(), "src\\main\\java");
		Files.list(main).forEach(epath -> {
			try {
				Files.deleteIfExists(epath);
			} catch (IOException e) {
				ExceptionDialog.stacktrace(e);
			}
		});
		
		String withnamespc = main.toString() + "\\" + namespace.replace(".", "\\");
		
		String[] folders = { "proxy", "init", "item", "block"};
		for(String folder : folders) {
			Path p = Paths.get(withnamespc, folder);
			Files.createDirectories(p);
		}
	}

}
