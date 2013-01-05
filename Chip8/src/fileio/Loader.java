package fileio;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class offers a single static method to load a file into a byte array, which is then returned.
 *
 * @author Troy Shaw
 */
public final class Loader {

	private Loader() {
		//stop instantiation
	}
	
	/**
	 * Creates and returns a byte array containing the contents of the file.
	 * If any error occurs, null is returned.
	 * 
	 * @param file the file we wish to load
	 * @return the file as a byte array, or null
	 */
	public static byte[] load(File file) {
		byte [] fileData = new byte[(int)file.length()];
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream((new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			dis.readFully(fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileData;
	}
}