package io.github.troblecodings.mctools;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import com.sun.istack.internal.Nullable;

public class LambdaFileVisit implements FileVisitor<Path>{

	private Visiter visit;
	
	private LambdaFileVisit(Visiter v) { this.visit = v; }
	
	public static LambdaFileVisit create(Visiter v) {
		return new LambdaFileVisit(v);
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return visit.visit(dir, null);
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		return visit.visit(file, attrs);
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	@FunctionalInterface
	interface Visiter {
		
		public FileVisitResult visit(Path file, @Nullable BasicFileAttributes attrs) throws IOException;
		
	}

}
