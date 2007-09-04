package edu.stanford.hci.sourcecode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import papertoolkit.util.DebugUtils;

public class ProjectStatistics {

	private SourceFileIterator sourceFiles;
	private String projectName;
	private boolean checkForRonYehCode = false;
	private boolean checkForHelloWorldCode = true;
	private boolean checkForWebCode = false;

	/**
	 * @param projName
	 * @param sourceFileIterator
	 */
	public ProjectStatistics(String projName, SourceFileIterator sourceFileIterator) {
		projectName = projName;
		sourceFiles = sourceFileIterator;
		DebugUtils.println(sourceFileIterator.getNumberOfSourceFiles()
				+ " files loaded into project.");
	}

	/**
	 * @return
	 */
	public boolean containsGrownCode() {
		sourceFiles.reset();
		DebugUtils.println("Project: " + projectName);

		String strRon = "Ron";
		String strYeh = "Yeh";
		String strRonYeh = "ronyeh";

		String strHelloWorldTip1 = "Iterate through all the regions on the sheet.";
		String strHelloWorldTip2 = "YOUR JOB: RENDER THE MAP IMAGE AT THE APPROPRIATE";
		String strHelloWorldTip3 = "INSTANTIATE A SHEET OBJECT FOR THE MAP INTERFACE";
		// boilerplate for starting an app
		String strHelloWorldTip4 = "// the application has to know about this pen";

		String strJavaExample1 = "creating and showing this application's GUI.";
		String strJavaExample1a = "Create the GUI and show it.  For thread safety,";
		String strJavaExample2 = "TextInputDemo.java uses these additional files";

		do {
			String fileString = sourceFiles.getCurrentFileAsString();

			if (checkForRonYehCode) {
				// Evidence of code from Ron Yeh
				if (fileString.contains(strYeh) && fileString.contains(strRon)) {
					DebugUtils.println("Found " + strRon + " and " + strYeh + " in "
							+ sourceFiles.getCurrentFile());
				} else if (fileString.contains(strRonYeh)) {
					DebugUtils
							.println("Found " + strRonYeh + " in " + sourceFiles.getCurrentFile());
				}
			}

			if (checkForHelloWorldCode) {
				// Evidence of code from TAs / Hello World Assignment
				if (fileString.contains(strHelloWorldTip2)) {
					DebugUtils.println("Found " + strHelloWorldTip2 + " in "
							+ sourceFiles.getCurrentFile());
				} else if (fileString.contains(strHelloWorldTip1)) {
					DebugUtils.println("Found " + strHelloWorldTip1 + " in "
							+ sourceFiles.getCurrentFile());
				} else if (fileString.contains(strHelloWorldTip3)) {
					DebugUtils.println("Found " + strHelloWorldTip3 + " in "
							+ sourceFiles.getCurrentFile());
				} else if (fileString.contains(strHelloWorldTip4)) {
					DebugUtils.println("Found " + strHelloWorldTip4 + " in "
							+ sourceFiles.getCurrentFile());
				}
			}

			if (checkForWebCode) {
				// Evidence of code from Java Examples
				if (fileString.contains(strJavaExample1) && fileString.contains(strJavaExample1a)) {
					DebugUtils.println("Found " + strJavaExample1 + " in "
							+ sourceFiles.getCurrentFile());
				}

				// Evidence of code from Java Examples
				if (fileString.contains(strJavaExample2)) {
					DebugUtils.println("Found " + strJavaExample2 + " in "
							+ sourceFiles.getCurrentFile());
				}

			}

		} while (sourceFiles.nextSourceFile());

		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// get a project iterator and get source files for each project
		SourceFileIterator sourceFileIterator = new SourceFileIterator(new File(
				"files/SortedSourceFilePaths.txt"));
		ProjectIterator projIterator = new ProjectIterator(new File(
				"C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160"),
				sourceFileIterator);

		List<ProjectStatistics> projects = new ArrayList<ProjectStatistics>();

		boolean hasMore = true;
		while (hasMore) {
			String projName = projIterator.getCurrentProjectName();
			DebugUtils.println("Project: " + projName);
			SourceFileIterator sourceFilesForProject = projIterator
					.getSourceFilesForProject(projName);
			projects.add(new ProjectStatistics(projName, sourceFilesForProject));
			hasMore = projIterator.nextProject();
		}

		DebugUtils.println("");
		DebugUtils.println("");

		for (ProjectStatistics proj : projects) {
			proj.containsGrownCode();
		}
	}

}
