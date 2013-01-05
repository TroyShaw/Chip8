package emulator;

public interface Drawer {

	/**
	 * Draws at the given space with the given length with a xor function.
	 * If any squares go from on to off, this function returns true, else false.
	 * @param x
	 * @param y
	 * @param length
	 * @return
	 */
	public boolean draw(int x, int y, int length, int I, int[] data);
	
	/**
	 * Completely clears the display back to the default colour.
	 */
	public void clear();
	
}
