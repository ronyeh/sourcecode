package edu.stanford.hci.sourcecode.moss;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;
import edu.stanford.hci.sourcecode.DiscoverSourceFiles;
import edu.stanford.hci.sourcecode.ProjectIterator;
import edu.stanford.hci.sourcecode.SourceFile;

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
	 * @param projRoot
	 * @param destPath
	 * @return
	 */
	private static int consolidateSourceFilesFromOneProject(File projRoot, File destPath) {

		int count = 0;

		final DiscoverSourceFiles files = new DiscoverSourceFiles(projRoot);
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

			// at this point, the file we want is stored in f
			// the destination location is stored in destFile

			// One option is to simply copy the file over...
			// FileUtils.copy(f, destFile);

			// Instead of copying it straight, I'll create a source code object and filter out
			// certain lines that I don't care about
			// like... imports! =)
			SourceFile source = new SourceFile(f);
			StringBuilder lines = source.getLinesOfCodeExceptThoseMatching(Pattern
					.compile("^import.*"));
			FileUtils.writeStringToFile(lines.toString(), destFile);

			count++;
		}

		DebugUtils.println("Copied:");
		files.listAllFiles();
		return count;
	}

	/**
	 * @param projectPaths
	 * @param targetParentDir
	 * @param destDirPrefix
	 */
	private static void consolidateSourceForEachProject(List<File> projectPaths,
			String targetParentDir, String destDirPrefix) {
		int count = 0;

		for (File projPath : projectPaths) {
			String projName = projPath.getName();
			String destName = destDirPrefix + projName;
			DebugUtils.println("Copying *.java files from " + projPath + " to " + destName);

			File destDir = new File(targetParentDir, destName);
			destDir.mkdir();

			count += consolidateSourceFilesFromOneProject(projPath, destDir);
		}

		DebugUtils.println("");
		DebugUtils.println(count + " source files copied.");
	}

	/**
	 * Go through all the projects, and copy the java files over to a new location.
	 */
	private static void consolidateStudentSourceCode() {
		String rootDir = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160\\";
		ProjectIterator projectIterator = new ProjectIterator(new File(rootDir));
		List<File> allProjectPaths = projectIterator.getAllProjectPaths();
		DebugUtils.println(allProjectPaths);

		String targetDir = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\SourceCodeAnalysis\\files\\growncode\\";
		String prefix = "p_"; // the p_ stands for project :)

		consolidateSourceForEachProject(allProjectPaths, targetDir, prefix);
	}

	private static void consolidateToolkitSourceCode() {
		File tkPath1 = new File("C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\PaperToolkit");
		File tkPath2 = new File("C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\PaperToolkitDemos");
		List<File> toolkitPaths = new ArrayList<File>();
		toolkitPaths.add(tkPath1);
		toolkitPaths.add(tkPath2);
		
		String targetDir = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\SourceCodeAnalysis\\files\\growncode\\";
		String prefix = "b_"; // the b_ stands for base code (skeleton code)
		consolidateSourceForEachProject(toolkitPaths, targetDir, prefix);
	}

	private static void consolidateHelloWorldSourceCode() {
		File tkPath1 = new File("C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160 Other\\CS160HelloWorld");
		List<File> toolkitPaths = new ArrayList<File>();
		toolkitPaths.add(tkPath1);
		
		String targetDir = "C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\SourceCodeAnalysis\\files\\growncode\\";
		String prefix = "b_"; // the b_ stands for base code (skeleton code)
		consolidateSourceForEachProject(toolkitPaths, targetDir, prefix);
	}

	/**
	 * Given a parent directory and a destination directory, it finds ALL java files under the
	 * parent directory, and copies it to the destination directory. If there is a conflict between
	 * file names, it will prepend extra path information to differentiate the two.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		consolidateHelloWorldSourceCode();
	}
}
