package edu.stanford.hci.sourcecode;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * Represents all aspects of a Java Source File. =) For Static Analysis.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * TODO: Use this for viewing files? https://flamingo.dev.java.net/docs/file-viewer.html
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class SourceFile {

	/**
	 * Represents each line of code.
	 */
	private List<String> linesOfCode = new ArrayList<String>();

	private int numBlankLines = -1;

	private int numCloseBraces = -1;

	private int numComments = -1;

	private int numDebugLines = -1;

	private int numOpenBraces = -1;

	private int numOtherLines = -1;

	private int numOtherPrintlns = -1;

	private int numSemicolons = -1;

	private int numSystemErrs = -1;

	private int numSystemOuts = -1;

	private File path;

	private int numClasses = -1;

	private int numPublicClasses = -1;

	private int numInterfaces = -1;

	/**
	 * @param srcFile
	 */
	public SourceFile(File srcFile) {
		path = srcFile;

		// read each line of code into a String
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line = "";
			while ((line = br.readLine()) != null) {
				linesOfCode.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// DebugUtils.println(linesOfCode.size() + " lines of code.");
	}

	/**
	 * I read that a nice way to count the number of Non-Comment Source Statements in a program is to count
	 * the semi-colons (;) and open braces ({).
	 * 
	 * This is mostly just a good approximation... but doesn't cover all the corner cases. Uncomment the else
	 * case to see all the corner cases fly by. There are also false positives, for example in a large block
	 * comment, there may be statements with semicolons that I count. Oh well. =)
	 * 
	 * How should we handle import statements?
	 */
	private void countSemicolonsOpenBracesAndComments() {
		int numSemis = 0;
		int numOpenBrace = 0;
		int numCloseBrace = 0;
		int numComm = 0;
		int numElse = 0;
		int numBlank = 0;

		for (int i = 0; i < linesOfCode.size(); i++) {
			String line = linesOfCode.get(i).trim(); // lose the whitespace
			if (line.endsWith(";")) {
				numSemis++;
			} else if (line.endsWith("{")) {
				numOpenBrace++;
			} else if (line.startsWith("/*") || line.startsWith("*") || line.startsWith("*/")
					|| line.startsWith("//")) {
				numComm++;
			} else if (line.endsWith("}")) {
				numCloseBrace++;
			} else if (line.equals("")) { // just a blank line
				numBlank++;
			} else {
				numElse++;
				// DebugUtils.println(line + "\t\tin line " + (i + 1) + " of " + path);
			}
		}
		numSemicolons = numSemis;
		numOpenBraces = numOpenBrace;
		numComments = numComm;
		numCloseBraces = numCloseBrace;
		numBlankLines = numBlank;
		numOtherLines = numElse;

		// DebugUtils.println(numSemicolons + " semicolons discovered.");
	}

	private void countNumClasses() {
		int numPublic = 0;
		int numAnyClass = 0;
		int numPubInterfaces = 0;

		for (int i = 0; i < linesOfCode.size(); i++) {
			String line = linesOfCode.get(i).trim(); // lose the whitespace
			if (line.contains("class ")) {
				numAnyClass++;
				if (line.contains("public class ") || line.contains("public abstract class ") || line.contains("public static class ")) {
					numPublic++;
				} else {
					System.err.println("Is this a valid class? " + path + " :: "+ line);
				}
			} else if (line.contains("public interface ")) {
				numPubInterfaces++;
			}
		}
		numPublicClasses = numPublic;
		numClasses = numAnyClass;
		numInterfaces = numPubInterfaces;
	}

	/**
	 * <li>TODO: Return a list of the line numbers?
	 * <li>TODO: Does not handle commented out statements. Really, I need a java statement scanner/parser.
	 * <li>TODO: Commented out is OK? Because it was once used.... really.
	 */
	private void countSystemOutsAndErrsAndDebugs() {
		int numSysOuts = 0;
		int numSysErrs = 0;
		int numPrintlns = 0;
		int numDebugs = 0;
		for (int i = 0; i < linesOfCode.size(); i++) {
			String line = linesOfCode.get(i);
			// if (line.contains("System.out")) {
			// // DebugUtils.println("System.out found at line: " + (i + 1));
			// numSysOuts++;
			// } else if (line.contains("System.err")) {
			// numSysErrs++;
			// } else
			if (line.toLowerCase().contains("println")) {
				// DebugUtils.println(path);
				numPrintlns++;
			} else if (line.toLowerCase().contains("debug")) {
				// if it contains anything like "DebugUtils" or "debug this!"
				numDebugs++;
			}
		}
		numSystemOuts = numSysOuts;
		numSystemErrs = numSysErrs;
		numOtherPrintlns = numPrintlns;
		numDebugLines = numDebugs;
		// DebugUtils.println(numSystemOuts + " System.outs discovered.");
	}

	public List<String> getLinesOfCode() {
		return linesOfCode;
	}

	/**
	 * @param rejectTheseLines
	 */
	public StringBuilder getLinesOfCodeExceptThoseMatching(Pattern... rejectTheseLines) {
		StringBuilder sb = new StringBuilder();
		for (String line : linesOfCode) {
			for (Pattern p : rejectTheseLines) {
				if (p.matcher(line).matches()) {
					// System.out.println("Rejected: " + line);
				} else {
					sb.append(line + "\n");
				}
			}
		}
		return sb;
	}

	public int getNumBlankLines() {
		if (numBlankLines == -1) {
			countSemicolonsOpenBracesAndComments();
		}
		return numBlankLines;
	}

	public int getNumCloseBraces() {
		if (numCloseBraces == -1) {
			countSemicolonsOpenBracesAndComments();
		}
		return numCloseBraces;
	}

	public int getNumComments() {
		if (numComments == -1) {
			countSemicolonsOpenBracesAndComments();
		}
		return numComments;
	}

	public int getNumDebugs() {
		if (numDebugLines == -1) {
			countSystemOutsAndErrsAndDebugs();
		}
		return numDebugLines;
	}

	public int getNumLines() {
		return linesOfCode.size();
	}

	public int getNumOpenBraces() {
		if (numOpenBraces == -1) {
			countSemicolonsOpenBracesAndComments();
		}
		return numOpenBraces;
	}

	public int getNumOtherLines() {
		if (numOtherLines == -1) {
			countSemicolonsOpenBracesAndComments();
		}
		return numOtherLines;
	}

	public int getNumOtherPrintlns() {
		if (numOtherPrintlns == -1) {
			countSystemOutsAndErrsAndDebugs();
		}
		return numOtherPrintlns;
	}

	public int getNumSemicolons() {
		if (numSemicolons == -1) {
			countSemicolonsOpenBracesAndComments();
		}
		return numSemicolons;
	}

	public int getNumClasses() {
		if (numClasses == -1) {
			countNumClasses();
		}
		return numClasses;
	}

	public int getNumPublicClasses() {
		if (numPublicClasses == -1) {
			countNumClasses();
		}
		return numPublicClasses;
	}

	public int getNumPublicInterfaces() {
		if (numInterfaces == -1) {
			countNumClasses();
		}
		return numInterfaces;
	}

	public int getNumSystemErrs() {
		if (numSystemErrs == -1) {
			countSystemOutsAndErrsAndDebugs();
		}
		return numSystemErrs;
	}

	public int getNumSystemOuts() {
		if (numSystemOuts == -1) {
			countSystemOutsAndErrsAndDebugs();
		}
		return numSystemOuts;
	}

	public File getPath() {
		return path;
	}

	/**
	 * Open in the default editor. I have set my default *.java editor in Windows to Emacs, even though I code
	 * in Eclipse.
	 */
	public void openFile() {
		try {
			Desktop.getDesktop().open(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Display every line in the console.
	 */
	public void printLinesOfCode() {
		for (String line : linesOfCode) {
			System.out.println(line);
		}
	}

	/**
	 * Display every line except for those that match certain patterns...
	 * 
	 * @param rejectTheseLines
	 *            Reject lines that match this regular expression (e.g., things that start with "import")
	 */
	public void printLinesOfCodeExceptThoseMatching(Pattern... rejectTheseLines) {
		for (String line : linesOfCode) {
			for (Pattern p : rejectTheseLines) {
				if (!p.matcher(line).matches()) {
					System.out.println(line);
				}
				// else {
				// System.out.println("Rejected: " + line);
				// }
			}
		}
	}

	/**
	 * Display only lines that match certain patterns...
	 * 
	 * @param acceptTheseLines
	 *            Reject lines that match this regular expression (e.g., things that start with "import")
	 */
	public void printLinesOfCodeMatching(Pattern... acceptTheseLines) {
		for (String line : linesOfCode) {
			for (Pattern p : acceptTheseLines) {
				if (p.matcher(line).matches()) {
					System.out.println(line);
				}
				// else {
				// System.out.println("Rejected: " + line);
				// }
			}
		}
	}

	public int getNumStatements() {
		return getNumSemicolons() + getNumOpenBraces();
	}
}
