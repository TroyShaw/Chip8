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
	 * Creates and returns a byte array containing the contents of the file.<br>
	 * If any error occurs, null is returned.<br>
	 * <p>
	 * The file object can't be null, and must point to a valid file.
	 * 
	 * @param file the file we wish to load
	 * @return the file as a byte array, or null if something went wrong
	 */
	public static byte[] load(File file) {
		if (file == null) throw new NullPointerException();
		if (!file.exists()) throw new IllegalArgumentException("File must exist");
		
		//create the byte array of appropriate size
		byte [] fileData = new byte[(int)file.length()];
		
		//open the file
		DataInputStream dis = null;
		try {
			dis = new DataInputStream((new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			return null;
		}
		
		//read it
		try {
			dis.readFully(fileData);
		} catch (IOException e) {
			return null;
		} finally {
			//try close it
			try {
				dis.close();
			} catch (IOException e) {
				//oh well
			}
		}
		
		//return the data
		return fileData;
	}
}