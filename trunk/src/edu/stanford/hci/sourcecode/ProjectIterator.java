package edu.stanford.hci.sourcecode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Source Files are grouped by Project, as determined by the name of the directory under some root
 * directory.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class ProjectIterator {

	public static void main(String[] args) {
		SourceFileIterator sourceFileIterator = new SourceFileIterator(new File(
				"files/SortedSourceFilePaths.txt"));
		ProjectIterator projIterator = new ProjectIterator(new File(
				"C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160"),
				sourceFileIterator);

		boolean hasMore = true;
		while (hasMore) {
			String projName = projIterator.getCurrentProjectName();
			DebugUtils.println("The current project is named: " + projName + ".");
			DebugUtils.println(projIterator.getSourceFilesForProject(projName));
			hasMore = projIterator.nextProject();
		}
	}

	private File root;

	private SourceFileIterator sourceIterator;

	/**
	 * Use this object in a different way, without a pregenerated SourceFilePaths.txt file
	 * 
	 * @param rootPath
	 */
	public ProjectIterator(File rootPath) {
		root = rootPath.getAbsoluteFile();
	}

	/**
	 * All project names are children of the root path. If there are 10 directories there, there are
	 * 10 projects.
	 * 
	 * @param rootPath
	 * @param srcIterator
	 */
	public ProjectIterator(File rootPath, SourceFileIterator srcIterator) {
		root = rootPath.getAbsoluteFile();
		sourceIterator = srcIterator;
		sourceIterator.reset();
	}

	public List<File> getAllProjectPaths() {
		return Arrays.asList(root.listFiles());
	}

	/**
	 * There's probably an easier way to do this by doing String operations.
	 * 
	 * @return
	 */
	public String getCurrentProjectName() {
		return getProjectNameOfSourceFile(sourceIterator.getCurrentFile());
	}

	/**
	 * @param sourceFile
	 * @return
	 */
	public String getProjectNameOfSourceFile(File sourceFile) {
		File currentFile = sourceFile;
		File parent = currentFile.getParentFile();

		// if the parent is the root, then the current file is the project directory's name
		while (!parent.getAbsoluteFile().equals(root)) {

			// otherwise, keep traversing up directories
			currentFile = parent;
			parent = parent.getParentFile();
		}
		// DebugUtils.println(currentFile);
		return currentFile.getName();
	}

	/**
	 * @param projectName
	 * @return
	 */
	public SourceFileIterator getSourceFilesForProject(String projectName) {
		SourceFileIterator allSourceFiles = sourceIterator.getCopy();
		List<File> filteredFiles = new ArrayList<File>();
		do {
			File currFile = allSourceFiles.getCurrentFile();
			if (getProjectNameOfSourceFile(currFile).equals(projectName)) {
				filteredFiles.add(currFile);
			}
		} while (allSourceFiles.nextSourceFile());
		// nextSourceFile has returned false, which means it has wrapped (i.e., it has no more new
		// files)
		return new SourceFileIterator(filteredFiles);
	}

	/**
	 * @return true if we found a new project
	 */
	public boolean nextProject() {
		final String originalProjectName = getCurrentProjectName();
		final File originalFile = sourceIterator.getCurrentFile();

		sourceIterator.nextSourceFile(); // advance
		File currFile = sourceIterator.getCurrentFile();
		boolean hasMore = true; // we have not yet wrapped around

		while (!currFile.equals(originalFile)) {
			// DebugUtils.println(currFile);
			if (!getCurrentProjectName().equals(originalProjectName)) {
				return hasMore;
			}
			hasMore = sourceIterator.nextSourceFile(); // advance
			currFile = sourceIterator.getCurrentFile();
		}
		return hasMore;
	}
}
