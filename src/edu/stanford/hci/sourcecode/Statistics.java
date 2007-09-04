package edu.stanford.hci.sourcecode;

import java.io.File;
import java.util.List;

import papertoolkit.util.DebugUtils;

// count java files starting from a certain directory

// find all lines with system.out, and figure out what method it's in

// 1) Count % of classes that were copied
// Ron B. Yeh
// certain lines that I've copied
// MOSS / Google?

// 2) What other signs of debugging besides System.outs?

// 3) What Ink Operations?
// How many times did they access the Ink samples?
// JPEG Render, Paint, etc???

// what else?
/**
 * <p>
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
public class Statistics {

	// get all the source files from this project
	private DiscoverSourceFiles srcFiles;

	private List<File> projectFiles;

	private SourceFileIterator srcFileIterator;
	
	
	public Statistics(File projectRoot) {
		srcFiles = new DiscoverSourceFiles(projectRoot);
		projectFiles = srcFiles.getListOfFiles();
		srcFileIterator = srcFiles.getIterator();
	}

	public int getNumStatements() {
		return countStatements(srcFileIterator);
	}

	public int getNumClasses() {
		return countClasses(srcFileIterator);
	}
	
	public static void main(String[] args) {

		// for now, run DiscoverSourceFiles... and then this class.

		File sourceFileListing = new File("files/SortedSourceFilePathsTMP.txt");
		SourceFileIterator sourceFileIterator = new SourceFileIterator(
				sourceFileListing);
		countStatements(sourceFileIterator);
		countClasses(sourceFileIterator);
		// countSystemOuts(sourceFileListing);
	}

	public static int countClasses(SourceFileIterator sourceFileIterator) {
		// look for " class " and/or "public class ", to estimate the number of
		// classes implemented

		DebugUtils.println("====== Count Classes =====");
		sourceFileIterator.reset();
		
		boolean hasMore = true;

		int countOfFiles = 0;
		int linesRead = 0;

		int countOfClasses = 0;
		int countOfPublicClasses = 0;
		int countOfInterfaces = 0;

		while (hasMore) {
			countOfFiles++;

			SourceFile srcFile = sourceFileIterator.getCurrentSourceFile();
			linesRead += srcFile.getNumLines();

			countOfClasses += srcFile.getNumClasses();
			countOfPublicClasses += srcFile.getNumPublicClasses();
			countOfInterfaces += srcFile.getNumPublicInterfaces();

			hasMore = sourceFileIterator.nextSourceFile();
		}

		DebugUtils.println("Num Files: " + countOfFiles);

		DebugUtils.println("Num Classes: " + countOfClasses);
		DebugUtils.println("Num Public Interfaces: " + countOfInterfaces);

		DebugUtils.println("Num Public Classes: " + countOfPublicClasses);

		DebugUtils.println(linesRead
				+ " total lines read (including imports and comments).");

		return countOfClasses;
	}

	/**
	 * A decent estimate. We overcount in some places... and may undercount in a
	 * few places. It probably is a slight overestimate in the actual number of
	 * statements.
	 * 
	 * @param sourceFileListing
	 */
	public static int countStatements(SourceFileIterator sourceFileIterator) {

		DebugUtils.println("====== Count Statements =====");
		sourceFileIterator.reset();

		boolean hasMore = true;

		int maxStatements = Integer.MIN_VALUE;

		int countOfFiles = 0;
		int linesRead = 0;

		File maxStatementsFile = null;

		int countOfStatements = 0;
		int countOfComments = 0;
		int countOfCloseBraces = 0;
		int countOfOtherLines = 0;
		int countOfBlankLines = 0;

		while (hasMore) {
			countOfFiles++;
			SourceFile srcFile = sourceFileIterator.getCurrentSourceFile();
			linesRead += srcFile.getNumLines();

			final int numStatements = srcFile.getNumStatements();

			if (numStatements > maxStatements) {
				maxStatements = numStatements;
				maxStatementsFile = srcFile.getPath();
			}

			countOfStatements += numStatements;
			countOfComments += srcFile.getNumComments();
			countOfCloseBraces += srcFile.getNumCloseBraces();
			countOfOtherLines += srcFile.getNumOtherLines();
			countOfBlankLines += srcFile.getNumBlankLines();

			hasMore = sourceFileIterator.nextSourceFile();
		}

		DebugUtils.println("Over " + countOfFiles + " files, we found "
				+ countOfStatements + " statements, for an average of "
				+ (countOfStatements / (float) countOfFiles) + " per file.");
		DebugUtils.println("Num Lines of Comments: " + countOfComments);
		DebugUtils.println("Num Lines of Close Braces: " + countOfCloseBraces);
		DebugUtils.println("Num Blank Lines: " + countOfBlankLines);
		DebugUtils.println("Num Lines Unclassified: " + countOfOtherLines);

		DebugUtils.println(linesRead
				+ " total lines read (including imports and comments).");
		DebugUtils.println(maxStatements
				+ " is the max number of statements we found --> "
				+ maxStatementsFile);

		DebugUtils.println("COCOMO measure of effort 2.4 * (KLOC ** 1.05): "
				+ (2.4 * Math.pow(countOfStatements / 1000.0, 1.05))
				+ " person-months.");
		
		return countOfStatements;
	}

	public static void countSystemOuts(SourceFileIterator sourceFileIterator) {
		sourceFileIterator.reset();

		boolean hasMore = true;

		// three things we want to measure
		int countOfSystemOuts = 0;
		int countOfSystemErrs = 0;
		int countOfOtherPrintlns = 0;
		int countOfDebugs = 0;

		int maxSystemPrintlns = Integer.MIN_VALUE;

		int countOfFiles = 0;
		int linesOfCode = 0;

		File maxSystemPrintlnsFile = null;

		while (hasMore) {
			countOfFiles++;
			SourceFile srcFile = sourceFileIterator.getCurrentSourceFile();

			linesOfCode += srcFile.getNumLines();

			final int numSysOuts = srcFile.getNumSystemOuts();
			final int numSysErrs = srcFile.getNumSystemErrs();
			final int numOtherPrintlns = srcFile.getNumOtherPrintlns();
			// 
			final int numDebugs = srcFile.getNumDebugs();

			final int numPrintlns = numSysOuts + numSysErrs + numOtherPrintlns;

			if (numPrintlns > maxSystemPrintlns) {
				maxSystemPrintlns = numPrintlns;
				maxSystemPrintlnsFile = srcFile.getPath();
			}

			countOfSystemOuts += numSysOuts;
			countOfSystemErrs += numSysErrs;
			countOfOtherPrintlns += numOtherPrintlns;
			countOfDebugs += numDebugs;

			hasMore = sourceFileIterator.nextSourceFile();
		}

		int countOfPrintlnStatements = countOfSystemErrs + countOfSystemOuts
				+ countOfOtherPrintlns;
		DebugUtils.println("");
		DebugUtils.println("Over " + countOfFiles + " files, we found "
				+ countOfPrintlnStatements
				+ " different println statements, for an average of "
				+ (countOfPrintlnStatements / (float) countOfFiles)
				+ " per file.");
		DebugUtils
				.println("Over "
						+ countOfFiles
						+ " files, we found "
						+ countOfDebugs
						+ " different \"debug\" statements that were not otherwise in println statements.");

		DebugUtils.println(linesOfCode
				+ " total lines of code (including imports and comments).");
		DebugUtils.println(maxSystemPrintlns
				+ " is the max number of System.*.printlns we found in "
				+ maxSystemPrintlnsFile);

		DebugUtils.println(countOfSystemOuts
				+ " is the number of System.out.printlns.");
		DebugUtils.println(countOfSystemErrs
				+ " is the number of System.err.printlns.");

		// make sure to check for DebugUtils too!
		// or just println in general!
	}

}
