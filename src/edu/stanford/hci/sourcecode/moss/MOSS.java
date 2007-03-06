package edu.stanford.hci.sourcecode.moss;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.sourcecode.DiscoverSourceFiles;

/**
 * <p>
 * You will need to install Perl. See ActivePerl (for Windows) or use Cygwin. On a Mac OSX/UNIX
 * system, you should already have Perl in the terminal.
 * 
 * See Alex Aiken's MOSS page for instructions on how to install and use MOSS.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class MOSS {

	private static String buildListOfFiles(File path) {
		String prefix = path.getPath();

		final DiscoverSourceFiles files = new DiscoverSourceFiles(path);
		final List<File> listOfFiles = files.getListOfFiles();

		StringBuilder sb = new StringBuilder();
		for (File f : listOfFiles) {
			sb.append(prefix + "\\" + f.getName() + " ");
		}

		return sb.toString().trim();
	}

	public static void main(String[] args) {

		// run moss through perl
		// moss.pl -l java -d <directoryContainingJavaFiles> <directoryContainingJavaFiles>
		String prefix = "files/growncode/";
		// String projectJavaFiles = buildListOfFiles(new File(prefix + "p_4Corners/"));
		// String toolkitJavaFiles = buildListOfFiles(new File(prefix + "b_PaperToolkit/"));
		String projectJavaFiles = prefix + "p_4Corners/*.java";
		String toolkitJavaFiles = prefix + "b_PaperToolkit/*.java";
		DebugUtils.println(projectJavaFiles);
		DebugUtils.println(toolkitJavaFiles);
		String mossCommand = "cmd /c bash --login -c \"cd projects/SourceCodeAnalysis; perl externalBin/moss.pl -l java -m 1000000 -d "
				+ projectJavaFiles + " " + toolkitJavaFiles + "\"";
		run(mossCommand);
		// run("cmd /c dir");
		// run("cmd /c bash --login -c \"perl -v\"");
	}

	/**
	 * R3 v 4Corners http://moss.stanford.edu/results/103112459
	 */
	private static void run(String command) {
		try {
			Process proc = Runtime.getRuntime().exec(command);
			InputStream inputStream = proc.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			InputStream errorStream = proc.getErrorStream();
			BufferedReader err = new BufferedReader(new InputStreamReader(errorStream));

			String line;
			DebugUtils.println("Output: ");
			while ((line = in.readLine()) != null) {
				DebugUtils.println(line);
			}

			DebugUtils.println("----------------------------");
			DebugUtils.println("Errors: ");
			while ((line = err.readLine()) != null) {
				DebugUtils.println(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
