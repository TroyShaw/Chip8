package emulator;

import java.util.ArrayList;
import java.util.List;

public class Logger {

	public static List<String> log = new ArrayList<String>();
	
	public static void log(String message) {
		log.add(message);
	}
}
