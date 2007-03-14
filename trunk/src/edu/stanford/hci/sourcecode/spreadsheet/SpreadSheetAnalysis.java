package edu.stanford.hci.sourcecode.spreadsheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Analyzes the Spreadsheet data that we have hand-coded.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class SpreadSheetAnalysis {

	private static void countGotHeresInEventHandlers(File f) {
		int numLines = 0;
		int numLinesMatchingQuery = 0;
		int numLinesMatchingGotHere = 0;
		int numLinesMatchingEventHandler = 0;

		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					f.getAbsoluteFile())));
			String line = "";
			while ((line = br.readLine()) != null) {
				DebugUtils.println(line);

				boolean gotHere = line.contains("Got Here");
				boolean eventHandler = line.contains("Event Handler");

				if (gotHere) {
					numLinesMatchingGotHere++;
				}

				if (eventHandler) {
					numLinesMatchingEventHandler++;
				}

				if (eventHandler && gotHere) {
					numLinesMatchingQuery++;
				}

				numLines++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// statistics
		DebugUtils.println("");
		DebugUtils.println("Number of Debugging Printlns: " + numLines);
		DebugUtils.println("Number of Printlns Matching Got Here: " + numLinesMatchingGotHere);
		DebugUtils.println("Number of Printlns Matching Event Handler: "
				+ numLinesMatchingEventHandler);
		DebugUtils.println("Number of Printlns Matching Both: " + numLinesMatchingQuery);
	}

	/**
	 * Tags are separated by semicolons (;) and are found in column columnNumber (starting from 0)
	 * 
	 * @param f
	 */
	private static HashMap<String, Integer> countTags(File f, int columnNumber) {
		int numLines = 0;

		// save all the tags in the hash table
		HashMap<String, Integer> tagsFound = new HashMap<String, Integer>();

		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					f.getAbsoluteFile())));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");
				DebugUtils.println(line);
				// DebugUtils.println(columns[columnNumber]);
				String[] tags = columns[columnNumber].split(";");

				for (String tag : tags) {
					tag = tag.trim(); // kill whitespace
					if (tagsFound.containsKey(tag)) {
						tagsFound.put(tag, tagsFound.get(tag) + 1);
					} else {
						tagsFound.put(tag, 1);
					}
				}

				numLines++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// statistics
		DebugUtils.println("");
		DebugUtils.println("Number of Debugging Printlns: " + numLines);
		DebugUtils.println(tagsFound);

		return tagsFound;
	}

	public static void main(String[] args) {
		// File f = new File("files/CS160 Data.SystemOuts.v8.csv");
		// countTags(f, 3);

		File f = new File("files/CS160 Data.InkOperations.v2.csv");
		HashMap<String, Integer> tags = countTags(f, 3);

		File outputFile = new File("files/CS160 Data.InkOperations.TagsOutput.csv");
		outputTagsToSpreadSheet(outputFile, tags);
	}

	private static void outputTagsToSpreadSheet(File outputFile, HashMap<String, Integer> tags) {
		StringBuilder sb = new StringBuilder();
		for (String tag : tags.keySet()) {
			sb.append(tag + ", " + tags.get(tag) + "\n");
		}
		FileUtils.writeStringToFile(sb.toString(), outputFile);
	}
}
