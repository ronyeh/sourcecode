package edu.stanford.hci.sourcecode;

import java.io.File;

import edu.stanford.hci.r3.util.DebugUtils;

public class Statistics {

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

	public static void main(String[] args) {
		countStatements();
		
		
		countSystemOuts();
	}

	/**
	 * A decent estimate. We overcount in some places... and may undercount in a few places. It
	 * probably is a slight overestimate in the actual number of statements.
	 */
	public static void countStatements() {
		SourceFileIterator sourceFileIterator = new SourceFileIterator(new File(
				"files/SortedSourceFilePaths.txt"));

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

			final int numSemis = srcFile.getNumSemicolons();
			final int numOpenBraces = srcFile.getNumOpenBraces();

			final int numStatements = numSemis + numOpenBraces;

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

		DebugUtils.println("Over " + countOfFiles + " files, we found " + countOfStatements
				+ " statements, for an average of " + (countOfStatements / (float) countOfFiles)
				+ " per file.");
		DebugUtils.println("Num Lines of Comments: " + countOfComments);
		DebugUtils.println("Num Lines of Close Braces: " + countOfCloseBraces);
		DebugUtils.println("Num Blank Lines: " + countOfBlankLines);
		DebugUtils.println("Num Lines Unclassified: " + countOfOtherLines);

		DebugUtils.println(linesRead + " total lines read (including imports and comments).");
		DebugUtils.println(maxStatements + " is the max number of statements we found --> "
				+ maxStatementsFile);
	}

	public static void countSystemOuts() {
		SourceFileIterator sourceFileIterator = new SourceFileIterator(new File(
				"files/SortedSourceFilePaths.txt"));

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

		int countOfPrintlnStatements = countOfSystemErrs + countOfSystemOuts + countOfOtherPrintlns;
		DebugUtils.println("");
		DebugUtils.println("Over " + countOfFiles + " files, we found " + countOfPrintlnStatements
				+ " different println statements, for an average of "
				+ (countOfPrintlnStatements / (float) countOfFiles) + " per file.");
		DebugUtils.println("Over " + countOfFiles + " files, we found " + countOfDebugs
				+ " different \"debug\" statements that were not otherwise in println statements.");

		DebugUtils.println(linesOfCode + " total lines of code (including imports and comments).");
		DebugUtils.println(maxSystemPrintlns
				+ " is the max number of System.*.printlns we found in " + maxSystemPrintlnsFile);

		DebugUtils.println(countOfSystemOuts + " is the number of System.out.printlns.");
		DebugUtils.println(countOfSystemErrs + " is the number of System.err.printlns.");

		// make sure to check for DebugUtils too!
		// or just println in general!
	}

}
