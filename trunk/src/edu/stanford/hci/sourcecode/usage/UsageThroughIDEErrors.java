package edu.stanford.hci.sourcecode.usage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.hci.r3.util.ArrayUtils;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Analyzes usage of our tools:
 * 
 * Sort by Description, Location, Resource, then Path...
 * 
 * <pre>
 * 1) The project must import our tools.
 * 2) Remove our tools from the build path.
 * 3) Eclipse should list a bunch of errors.
 * 4) Export these errors to a text file. (Sorted by Path
 * 5) Parse this text file with this Problems class... to understand how our tool was used.
 * 6) If you format the code beforehand, you can reliably detect instances... i.e., number of times new Type( is called...
 * 7) This approach does not look at commented out chunks, because they are not &quot;errors&quot; in Eclipse.
 * </pre>
 * 
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
public class UsageThroughIDEErrors {

	private Pattern patternImport;
	private Pattern patternTypeNotFound;

	private HashMap<String, Integer> countOfTypes = new HashMap<String, Integer>();

	private Set<String> sameLineClassProject = new HashSet<String>();

	public UsageThroughIDEErrors(File rootPath) {

		setupPatternMatchers();

		// find all text files in this path
		List<File> files = FileUtils.listVisibleFiles(rootPath,
				new String[] { "txt" });

		for (File f : files) {
			DebugUtils.println("");
			DebugUtils.println(f.getName());

			// read in the file
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));

				// drop the first line (headers)
				String firstLine = br.readLine();
				// DebugUtils.println(firstLine);

				String line = "";
				while ((line = br.readLine()) != null) {

					// e.g., The import edu.stanford cannot be resolved
					Matcher matcherImport = patternImport.matcher(line);
					Matcher matcherType = patternTypeNotFound.matcher(line);

					if (matcherImport.find()) {
						// DebugUtils.println("Found: " + line);
					} else if (matcherType.find()) {

						String typeName = matcherType.group(1);

						// check if there was a new instance created
						// do this by loading the actual file, and detecting a
						// call to new TypeName()
						// if (sourceLine.contains("new " + typeName)) {
						// DebugUtils.println(line);
						// }

						// split the fields (tab delimited) so that we can find
						// the class, line number, and project name
						String[] fields = line.split("\t");

						// create a string out of the type_path_file_linenumber
						String uniqueID = typeName + "_" + fields[1] + "_"
								+ fields[2] + "_" + fields[3];

						if (sameLineClassProject.contains(uniqueID)) {
							// do nothing
							System.out
									.println("Duplicate " + uniqueID
											+ " \t: "
											+ Thread.currentThread()
													.getStackTrace()[1]);
						} else {
							sameLineClassProject.add(uniqueID);

							// add to our statistics
							if (countOfTypes.containsKey(typeName)) {
								countOfTypes.put(typeName, countOfTypes
										.get(typeName) + 1); // increment
							} else {
								countOfTypes.put(typeName, 1); // initialize
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

		}

		// discard the first line

		// parse each tab-delimited line

		// Types of Errors
		// Don't Worry about these:
		// -- Import <Package> cannot be resolved...
		// Care about these:
		// ++ <Type> canot be resolved to a type

		// In particular, for those type resolution problems, if we find that
		// new <Type> exists... we count it as an instance

		// We should also track how often different types are accessed,
		// period... It gives us an idea of usage.
		DebugUtils.println(countOfTypes);
		// sort and visualize?

		for (String key : countOfTypes.keySet()) {
			System.out.println(key + "\t" + countOfTypes.get(key));
		}
	}

	private void setupPatternMatchers() {
		patternImport = Pattern.compile("The import .* cannot be resolved");
		patternTypeNotFound = Pattern
				.compile("(.*) cannot be resolved to a type");
	}

	public static void main(String[] args) {
		new UsageThroughIDEErrors(new File(
				"C:/Documents and Settings/Ron Yeh/My Documents/"
						+ "Projects/SourceCodeAnalysis/files/usage"));
	}
}
