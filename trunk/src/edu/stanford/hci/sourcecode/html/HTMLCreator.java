package edu.stanford.hci.sourcecode.html;

import java.io.File;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;
import edu.stanford.hci.sourcecode.SourceFile;

/**
 * <p>
 * Outputs an HTML file that contains source code, with each line addressable by a target (e.g.,
 * sourcefile.html#line345). The goal is to build an entire website that contains the source files
 * for a project.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class HTMLCreator {

	private SourceFile srcFile;

	/**
	 * We should customize the output directory for sorting the different projects.
	 */
	private File outputDirectory = new File("files/htmlcreator/output");

	private File templateFile = new File("files/htmlcreator/Template.txt");

	private File outputFile;

	private String outputFileString;

	private String srcFileName;

	private int numberOfLines;

	private int numDigitsNeeded;
	
	private String highlightRegionStart = "<span style='color:black;background-color:#ffff66'>";
	private String highlightRegionEnd = "</span>";

	/**
	 * Hopefully your source file doesn't have this many lines of code
	 */
	private static final String ZEROES = "0000000000";

	public HTMLCreator(SourceFile file) {
		srcFile = file;
		srcFileName = srcFile.getPath().getName();
		outputFile = new File(outputDirectory, srcFileName + ".html");
		outputFileString = FileUtils.readFileIntoStringBuffer(templateFile).toString();

		numberOfLines = srcFile.getNumLines();
		numDigitsNeeded = (int) Math.ceil(Math.log10(numberOfLines));
	}

	public void createHTMLForSourceFile() {

		StringBuilder sb = new StringBuilder();
		List<String> linesOfCode = srcFile.getLinesOfCode();
		int lineNumber = 1;
		for (String line : linesOfCode) {
			
			if (line.contains("println")) {
				line  = line.replace("println", highlightRegionStart + "println" + highlightRegionEnd);
			}
			
			
			sb.append("<a name=\"line" + lineNumber + "\"></a>"
					+ formatLineNumberAsString(lineNumber) + ":\t" + line + "\n");
			lineNumber++;
		}

		outputFileString = outputFileString.replaceAll("__SOURCEFILENAME__", srcFileName);
		outputFileString = outputFileString.replaceAll("__SOURCECODE__", sb.toString());

		// output to file
		FileUtils.writeStringToFile(outputFileString, outputFile);
	}

	private String formatLineNumberAsString(int lineNumber) {
		String lineNumberString = lineNumber + "";
		if (lineNumberString.length() < numDigitsNeeded) {
			int numZeroesNeeded = numDigitsNeeded - lineNumberString.length();
			lineNumberString = ZEROES.substring(0, numZeroesNeeded) + lineNumberString;
		}
		return lineNumberString;
	}

	public static void main(String[] args) {
		HTMLCreator creator = new HTMLCreator(new SourceFile(new File(
				"C:\\Documents and Settings\\Ron Yeh\\My Documents\\Projects\\CS160\\"
						+ "4Corners\\src\\FourCornersPanel.java")));

		creator.createHTMLForSourceFile();
	}
}
