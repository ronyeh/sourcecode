package edu.stanford.hci.sourcecode;

import java.io.File;
import java.util.Collections;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * List all the Java Source Files...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class DiscoverSourceFiles {

	public static String formatData(String paths) {
		paths = paths.replace(", ", "\n");
		paths = paths.replace("[", "");
		paths = paths.replace("]", "");
		return paths;
	}

	public static void main(String[] args) {

		String cs160Path = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160";
		String cs160JURMsPath = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160\\JURMs";

		DiscoverSourceFiles discoverSourceFiles = new DiscoverSourceFiles(new File(cs160Path));
		discoverSourceFiles.writePathsToFile(new File("files/SortedSourceFilePaths.txt"));
	}

	private List<File> allSourceFiles;

	private File rootPath;

	public DiscoverSourceFiles(File rootDir) {
		rootPath = rootDir;

		// find all java source files starting from this path
		allSourceFiles = FileUtils.listVisibleFilesRecursively(rootPath, new String[] { "java" });

		Collections.sort(allSourceFiles);
		
		// How Many Source Files did I analyze?
		DebugUtils.println(allSourceFiles.size() + " Java Source Files");
	}

	public void writePathsToFile(File destFile) {
		String paths = allSourceFiles.toString();
		paths = formatData(paths);
		FileUtils.writeStringToFile(paths, destFile);
	}
}
