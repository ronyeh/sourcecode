package edu.stanford.hci.sourcecode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.SystemUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * Represents a list of source files. We can move between them...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class SourceFileIterator {

	/**
	 * Points to where we are in the pathsList;
	 */
	private int currentIndex = 0;

	private List<File> pathsList;

	public SourceFileIterator(File textFileContainingSourceFilePaths) {
		pathsList = new ArrayList<File>();

		// read it in... line by line
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					textFileContainingSourceFilePaths.getAbsoluteFile())));
			String line = "";
			while ((line = br.readLine()) != null) {
				pathsList.add(new File(line));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugUtils.println(pathsList.size() + " source file paths.");
	}

	public SourceFileIterator(List<File> paths) {
		pathsList = paths;
	}

	public SourceFileIterator getCopy() {
		return new SourceFileIterator(pathsList);
	}

	public File getCurrentFile() {
		// DebugUtils.println(pathsList.get(currentIndex));
		return pathsList.get(currentIndex);
	}

	/**
	 * @return a long string, for quick and dirty text searches... =)
	 */
	public String getCurrentFileAsString() {
		return FileUtils.readFileIntoStringBuffer(pathsList.get(currentIndex)).toString();
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public SourceFile getCurrentSourceFile() {
		return new SourceFile(getCurrentFile());
	}

	public int getNumberOfSourceFiles() {
		return pathsList.size();
	}

	/**
	 * Jumps to the next file with a name that contains this string.
	 * 
	 * Incomplete implementation.
	 * <li>TODO: Disregard the parent paths...? Or Use them?
	 * <li>TODO: Implement wrap?
	 * <li>TODO: Case Sensitivity?
	 * 
	 * @param fileNameSubstring
	 */
	public void jumpToFile(String fileNameSubstring) {
		for (int i = currentIndex; i < pathsList.size(); i++) {
			File currFile = pathsList.get(i);
			if (currFile.getName().contains(fileNameSubstring)) {
				currentIndex = i;
				DebugUtils.println("Match for [" + fileNameSubstring + "] in " + currFile);
				return;
			}
		}
	}

	/**
	 * @param pathSubstring
	 */
	public void jumpToPath(String pathSubstring) {
		int startingPoint = currentIndex;

		while (true) {
			String path = getCurrentFile().getAbsolutePath();
			if (path.contains(pathSubstring)) {
				DebugUtils.println("Match for [" + pathSubstring + "] in " + path);
				return;
			}
			boolean hasMore = nextSourceFile();
			if (!hasMore) {
				return;
			}
		}
	}

	public boolean nextSourceFile() {
		currentIndex++;
		if (currentIndex >= pathsList.size()) {
			currentIndex = 0;
			return false; // no more at the end of this list, wrap to the beginning
		}
		return true; // did not wrap, there's more to be found
	}

	public boolean prevSourceFile() {
		currentIndex--;
		if (currentIndex == -1) {
			currentIndex = pathsList.size() - 1;
			return false; // no more at the beginning of this list, wrap back to the end
		}
		return true; // did not wrap, there's at least one more file to be found
	}

	public void reset() {
		currentIndex = 0;
	}

	public void setCurrentIndex(int newIndex) {
		currentIndex = newIndex;
	}

	public String toString() {
		return pathsList.size() + " source files.";
	}
}
