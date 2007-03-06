package edu.stanford.hci.sourcecode.moss;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;
import edu.stanford.hci.sourcecode.DiscoverSourceFiles;
import edu.stanford.hci.sourcecode.ProjectIterator;

/**
 * <p>
 * Use this to consolidate Java files into one directory so that MOSS can process them.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class SourceCodeCollector {

	/**
	 * Given a parent directory and a destination directory, it finds ALL java files under the
	 * parent directory, and copies it to the destination directory. If there is a conflict between
	 * file names, it will prepend extra path information to differentiate the two.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String rootDir = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160\\";
		ProjectIterator projectIterator = new ProjectIterator(new File(rootDir));
		List<File> allProjectPaths = projectIterator.getAllProjectPaths();
		DebugUtils.println(allProjectPaths);

		String targetDir = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\SourceCodeAnalysis\\files\\growncode\\";

		int count = 0;

		for (File projPath : allProjectPaths) {
			String projName = projPath.getName();
			String destName = "p_" + projName;
			DebugUtils.println("Copying *.java files from " + projPath + " to " + destName);

			File destDir = new File(targetDir, destName);
			destDir.mkdir();

			count += copyOneProject(projPath, destDir);
		}

		DebugUtils.println("");
		DebugUtils.println(count + " source files copied.");
	}

	private static int copyOneProject(File projRoot, File destPath) {

		int count = 0;

		DiscoverSourceFiles files = new DiscoverSourceFiles(projRoot);
		List<File> listOfFiles = files.getListOfFiles();
		for (File f : listOfFiles) {

			File destFile = new File(destPath, f.getName());

			// make sure the file names are unique
			File parentPath = f.getParentFile();
			while (destFile.exists()) {
				System.err.println(destFile + " already exists. Changing the name.");
				destFile = new File(destPath, parentPath.getName() + "_" + destFile.getName());
				parentPath = parentPath.getParentFile();
			}

			FileUtils.copy(f, destFile);
			count++;
		}

		DebugUtils.println("Copied:");
		files.listAllFiles();
		return count;
	}
}
