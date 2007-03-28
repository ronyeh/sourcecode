package edu.stanford.hci.sourcecode.spreadsheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Analyzes the Spreadsheet data that we have hand-coded. Basically, this goes through and counts
 * tags, so that we can make fun graphs for fun research papers.
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

	private static final int ASCII_A_VALUE = (int) 'a';

	/**
	 * @param letter
	 * @return
	 */
	private static int convertLetterToNumber(String letter) {
		int asciiValue = (int) letter.toLowerCase().charAt(0) - ASCII_A_VALUE;
		return asciiValue;
	}

	private static String convertNumberToLetter(Integer colNumber) {
		char c = (char) (colNumber + ASCII_A_VALUE);
		return Character.toString(c);
	}

	/**
	 * Count all the lines that contain certain phrases.
	 * 
	 * @param f
	 */
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

	/**
	 * @param f
	 * @param columnLetters
	 *            e.g., {"A", "J", "L"}. This is useful for Excel. It will output a CSV file with
	 *            multiple sections, one for each column that we are interested in...
	 * @param startingRow
	 *            STARTS COUNTING FROM ONE, so that we can match Excel!
	 * @param endingRow
	 *            Both startingRow and endingRow are inclusive...
	 * @return
	 */
	private static HashMap<Integer, HashMap<String, Integer>> countTagsPerColumn(File f,
			String[] columnLetters, int startingRow, int endingRow) {

		// convert the list of column letters to a list of column numbers (counting from 0, that we
		// care about).
		// A-->0, B-->1, C-->2, etc...
		List<Integer> columnNumbers = new ArrayList<Integer>();
		for (String letter : columnLetters) {
			int asciiValue = convertLetterToNumber(letter);
			columnNumbers.add(asciiValue);
			// DebugUtils.println(asciiValue);
		}

		// save all the tags in the hash table
		HashMap<Integer, HashMap<String, Integer>> tagsPerColumn = new HashMap<Integer, HashMap<String, Integer>>();

		int numLines = 0;
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					f.getAbsoluteFile())));
			String line = "";
			while ((line = br.readLine()) != null) {
				// DebugUtils.println("Read: " + line);
				numLines++;

				if (numLines < startingRow) {
					// we haven't gotten to the starting row yet
					continue;
				} else if (numLines > endingRow) {
					// we are past the final line
					break;
				} else {
					String[] columns = line.split(",");
					DebugUtils.println("Row " + numLines + ": " + line);
					for (int colNumber : columnNumbers) {

						// assume you have split your tags by semicolons
						String[] tags = columns[colNumber].split(";");

						HashMap<String, Integer> tagsFound = tagsPerColumn.get(colNumber);
						if (tagsFound == null) {
							DebugUtils.println("Adding new Taglist for Column: " + colNumber);
							tagsFound = new HashMap<String, Integer>();
							tagsPerColumn.put(colNumber, tagsFound);
						}

						for (String tag : tags) {
							tag = tag.trim(); // kill whitespace
							if (tagsFound.containsKey(tag)) {
								tagsFound.put(tag, tagsFound.get(tag) + 1);
							} else {
								tagsFound.put(tag, 1);
							}
						}
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tagsPerColumn;
	}

	private static HashMap<String, Integer> countTagsWhereColumnMatches(File f,
			String tagColumnLetter, String matchColumnLetter, String matchText) {

		// case insensitive
		matchText = matchText.toLowerCase();

		// convert the list of column letters to a list of column numbers (counting from 0, that we
		// care about).
		// A-->0, B-->1, C-->2, etc...
		int tagColNumber = convertLetterToNumber(tagColumnLetter);
		int matchColNumber = convertLetterToNumber(matchColumnLetter);

		DebugUtils.println("Columns: " + tagColNumber + " and " + matchColNumber);
		
		// save all the tags in the hash table
		final HashMap<String, Integer> tagsNumMatches = new HashMap<String, Integer>();

		int numLines = 0;
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					f.getAbsoluteFile())));
			String line = "";
			while ((line = br.readLine()) != null) {
				// DebugUtils.println("Read: " + line);
				numLines++;
				String[] columns = line.split(",");

				// assume you have split your tags by semicolons
				String[] tags = columns[tagColNumber].split(";");


				String toMatch = columns[matchColNumber].toLowerCase();

				DebugUtils.println("Row " + numLines + ": " + columns[tagColNumber] + " : " + toMatch);

				if (!toMatch.equals(matchText)) {
					// didn't match, so next line!
					DebugUtils.println("NEXT!");
					continue;
				} else {
					// matched! so we count it!
					for (String tag : tags) {
						tag = tag.trim(); // kill whitespace
						if (tagsNumMatches.containsKey(tag)) {
							tagsNumMatches.put(tag, tagsNumMatches.get(tag) + 1);
						} else {
							tagsNumMatches.put(tag, 1);
						}
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tagsNumMatches;
	}

	public static void main(String[] args) {
		// File f = new File("files/CS160 Data.SystemOuts.v8.csv");

		// Column D are the tags
		// Column F is Homebrew
		File f = new File("files/CS160 Data.InkOperations.v2.csv");
		HashMap<String,Integer> matches = countTagsWhereColumnMatches(f, "D", "F", "YES"); // for YES Homebrew
		outputTagsToSpreadSheet(new File("files/CS160 Data.InkOperations.TagsMatchesHomebrewOutput.csv"), matches);
	}

	private static void outputTagsPerColumnToSpreadSheet(File outputFile,
			HashMap<Integer, HashMap<String, Integer>> tagsPerColumn) {

		StringBuilder sb = new StringBuilder();

		for (Integer columnNumber : tagsPerColumn.keySet()) {

			sb.append("Column " + convertNumberToLetter(columnNumber).toUpperCase() + "\n");
			HashMap<String, Integer> tagsForThisColums = tagsPerColumn.get(columnNumber);
			for (String tag : tagsForThisColums.keySet()) {
				sb.append(tag + ", " + tagsForThisColums.get(tag) + "\n");
			}
			sb.append("\n\n");
		}
		FileUtils.writeStringToFile(sb.toString(), outputFile);
	}

	/**
	 * @param outputFile
	 * @param tags
	 */
	private static void outputTagsToSpreadSheet(File outputFile, HashMap<String, Integer> tags) {
		StringBuilder sb = new StringBuilder();
		for (String tag : tags.keySet()) {
			sb.append(tag + ", " + tags.get(tag) + "\n");
		}
		FileUtils.writeStringToFile(sb.toString(), outputFile);
	}

	/**
	 * 
	 */
	private static void runAnalyzeMultipleColumns() {
		HashMap<Integer, HashMap<String, Integer>> tags = countTagsPerColumn(new File(
				"files/CS160 Data.ProjectReports.v1.csv"), new String[] { "H", "K", "J" }, 4, 20);
		DebugUtils.println(tags);
		File outputFile = new File("files/CS160 Data.ProjectReports.v1.output.csv");
		outputTagsPerColumnToSpreadSheet(outputFile, tags);
	}
}
