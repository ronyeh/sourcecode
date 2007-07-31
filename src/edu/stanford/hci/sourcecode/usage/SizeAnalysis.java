package edu.stanford.hci.sourcecode.usage;

import java.io.File;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.sourcecode.Statistics;

/**
 * <p>
 * Measures the Size of the Different CS160 Projects
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.
 * </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a>
 *         (ronyeh(AT)cs.stanford.edu)
 */
public class SizeAnalysis {

	public SizeAnalysis(File rootDirectory) {

		// each subdirectory is a project
		File[] projectRootDirectories = rootDirectory.listFiles();
		for (File projectRoot : projectRootDirectories) {

			DebugUtils.println("Project: " + projectRoot.getName());

			// how many statements in each project?
			Statistics statistics = new Statistics(projectRoot);

			int numClasses = statistics.getNumClasses();
			int numStatements = statistics.getNumStatements();

			DebugUtils.println("Project " + projectRoot.getName() + " has "
					+ numStatements + " total statements across " + numClasses
					+ " classes.");
			DebugUtils.println("\n\n\n");
		}
	}

	public static void main(String[] args) {
		new SizeAnalysis(
				new File(
						"C:/Documents and Settings/Ron Yeh/My Documents/Projects/CS160"));
	}
}
