package emulator;

public interface KeyController {

	/**
	 * Signals that the given button was either pressed or released.
	 * 
	 * @param b the button
	 * @param pressed true if pressed, false if released
	 */
	public void keyInteracted(int b, boolean pressed);
}
