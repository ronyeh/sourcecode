package edu.stanford.hci.sourcecode.moss;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import papertoolkit.util.DebugUtils;
import edu.stanford.hci.sourcecode.DiscoverSourceFiles;

/**
 * <p>
 * You will need to install Perl. See ActivePerl (for Windows) or use Cygwin. On a Mac OSX/UNIX
 * system, you should already have Perl in the terminal.
 * 
 * See Alex Aiken's MOSS page for instructions on how to install and use MOSS.
 * 
 * We will use MOSS in combination with text search and eyeballing to do our copied code analysis.
 * The data will be tagged by hand and saved into an Excel spreadsheet.
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

	public static void main(String[] args) {
		// String projectName = "4Corners"; // DONE
		// String projectName = "Anototators"; // DONE
		// String projectName = "BigBrother"; // DONE
		// String projectName = "BluJay"; // DONE
		// String projectName = "ET"; // DONE
		// String projectName = "GeoPhoto"; // DONE
		// String projectName = "GGiD"; // DONE
		String projectName = "Jigsaw";
		// String projectName = "JSandA"; // DONE
		// String projectName = "JURMs"; // DONE
		// String projectName = "KMAT"; // DONE
		// String projectName = "Melody"; // DONE
		// String projectName = "O"; // DONE
		// String projectName = "PollPrecision"; // DONE
		// String projectName = "SEKS"; // DONE
		// String projectName = "Team42"; // DONE
		// String projectName = "ThePenIsMightier"; // DONE

		String baseProjectName = "PaperToolkit";
		// String baseProjectName = "PaperToolkitDemos";
		// String baseProjectName = "CS160HelloWorld";
		// String baseProjectName = "web";

		new MOSS(projectName, baseProjectName);
	}

	public MOSS(String projectName, String baseProjectName) {
		// run moss through perl
		// moss.pl -l java -d <directoryContainingJavaFiles> <directoryContainingJavaFiles>
		String prefix = "files/growncode/";

		// #1, To compare within a CS160 project, we just submit all the files from that project
		// #2, To compare between project and the toolkit, we submit two directories
		// #3, To compare between project and hello world, we submit the two directories also...
		// Then, we go through the returned web page by hand, and annotate what MOSS thinks are
		// similarities. We tag these similarities and produce a graph! =)
		String projectJavaFiles = prefix + "p_" + projectName + "/*.java";
		String baseJavaFiles = prefix + "b_" + baseProjectName + "/*.java";

		// DebugUtils.println(projectJavaFiles);
		// DebugUtils.println(baseJavaFiles);

		// The between project matching seems to be one-to-one copies. I'd like to find
		// one-to-many...should I not use the -d flag?
		String mossCommand = "cmd /c bash --login -c \"cd projects/SourceCodeAnalysis; perl externalBin/moss.pl -l java -m 1000000 "
				// + "-d"
				+ projectJavaFiles + " " + baseJavaFiles + "\"";

		// sanity check on MOSS! submit the same projects
		// OK, it returns 99% =)
		// String mossCommand = "cmd /c bash --login -c \"cd projects/SourceCodeAnalysis; perl
		// externalBin/moss.pl -l java -m 1000000 -d "
		// + baseJavaFiles + " " + prefix + "b_PaperToolkitClone/*.java\"";

		DebugUtils.println(mossCommand);
		run(mossCommand);

		// run("cmd /c dir");
		// run("cmd /c bash --login -c \"perl -v\"");
	}

	/**
	 * Doesn't work.
	 * 
	 * @param path
	 * @return
	 */
	private String buildListOfFiles(File path) {
		String prefix = path.getPath();

		final DiscoverSourceFiles files = new DiscoverSourceFiles(path);
		final List<File> listOfFiles = files.getListOfFiles();

		StringBuilder sb = new StringBuilder();
		for (File f : listOfFiles) {
			sb.append(prefix + "\\" + f.getName() + " ");
		}

		return sb.toString().trim();
	}

	/**
	 * R3 v 4Corners http://moss.stanford.edu/results/103112459
	 */
	private void run(String command) {
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
