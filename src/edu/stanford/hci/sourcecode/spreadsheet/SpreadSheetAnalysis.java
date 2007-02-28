package edu.stanford.hci.sourcecode.spreadsheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import edu.stanford.hci.r3.util.DebugUtils;

public class SpreadSheetAnalysis {

	public static void main(String[] args) {
		countTags();
	}

	private static void countGotHeresInEventHandlers() {
		int numLines = 0;
		int numLinesMatchingQuery = 0;
		int numLinesMatchingGotHere = 0;
		int numLinesMatchingEventHandler = 0;

		File f = new File("files/CS160 Data.v8.csv");
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
		DebugUtils.println("Number of Printlns Matching Event Handler: " + numLinesMatchingEventHandler);
		DebugUtils.println("Number of Printlns Matching Both: " + numLinesMatchingQuery);
	}

	private static void countTags() {
		int numLines = 0;

		HashMap<String, Integer> tagsFound = new HashMap<String, Integer>();

		File f = new File("files/CS160 Data.v8.csv");
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					f.getAbsoluteFile())));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");
				DebugUtils.println(line);
				// DebugUtils.println(columns[3]);
				String[] tags = columns[3].split(";");

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
	}
}
