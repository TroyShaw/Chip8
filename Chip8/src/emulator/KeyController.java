package emulator;

import emulator.exception.InvalidKeyException;

public interface KeyController {

	/**
	 * Signals that the given key was either pressed or released.<br>
	 * The key must be between 0 and 15 inclusive.
	 * 
	 * @param k the key
	 * @param pressed true if pressed, false if released
	 * @throws InvalidKeyException if not in range 0-15 inclusive
	 */
	public void keyInteracted(int k, boolean pressed) throws InvalidKeyException;
}
