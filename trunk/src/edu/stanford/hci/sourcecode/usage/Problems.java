package edu.stanford.hci.sourcecode.usage;

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
public class Problems {

	
	
	
}
