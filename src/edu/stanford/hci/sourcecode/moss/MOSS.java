package edu.stanford.hci.sourcecode.moss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.stanford.hci.r3.util.DebugUtils;

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

	public static void main(String[] args) {

		// run moss through perl

		// moss.pl -l java -d <directoryContainingJavaFiles> <directoryContainingJavaFiles>

		String prefix = "files/growncode/";
		String projectJavaFiles = prefix + "p_4Corners/Ink.java";
		String toolkitJavaFiles = prefix + "b_toolkit/Ink.java";
		
		
		try {
			
			//Process proc = Runtime.getRuntime().exec("cmd /c dir");
			Process proc = Runtime.getRuntime().exec("perl externalBin/moss.pl -l java -d " + projectJavaFiles + " " + toolkitJavaFiles);
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
