package io.github.troblecodings.mctools;

import java.nio.file.Path;

public class CreationUtils {

	public static void createModBase(final Path pth, final String modid, final String version) {
		switch (version) {
		case "1.14.4":
			create_1_14_4(pth, modid);
			break;
		}
	}
	
	private static void create_1_14_4(final Path pth, final String modid) {
		
	}
	
}
