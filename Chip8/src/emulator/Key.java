package emulator;

import java.awt.event.KeyEvent;

/**
 * Enum for the different keys.<br>
 * Each entry has an associated bit (from 0-15) and a KeyEvent virtual-key code.
 *
 * @author Troy Shaw
 */
public enum Key {
	x0	(0x0, KeyEvent.VK_Q),
	x1	(0x1, KeyEvent.VK_W),
	x2	(0x2, KeyEvent.VK_E),
	x3	(0x3, KeyEvent.VK_A),
	x4	(0x4, KeyEvent.VK_S),
	x5	(0x5, KeyEvent.VK_D),
	x6	(0x6, KeyEvent.VK_Z),
	x7	(0x7, KeyEvent.VK_X),
	x8	(0x8, KeyEvent.VK_C),
	x9	(0x9, KeyEvent.VK_1),
	xA	(0xA, KeyEvent.VK_2),
	xB	(0xB, KeyEvent.VK_3),
	xC	(0xC, KeyEvent.VK_4),
	xD	(0xD, KeyEvent.VK_R),
	xE	(0xE, KeyEvent.VK_F),
	xF	(0xF, KeyEvent.VK_V);
	
	private final int code, position;
	
	Key(int position, int code) {
		this.position = position;
		this.code = code;
	}
	
	/**
	 * Returns the keycode associated with this key.
	 * @return the keycode
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Returns the position associated with this key (the hex key value), in the range 0-15 inclusive.
	 * @return the value
	 */
	public int getPosition() {
		return position;
	}
}