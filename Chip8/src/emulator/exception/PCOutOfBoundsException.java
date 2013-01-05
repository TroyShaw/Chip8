package emulator.exception;

public class PCOutOfBoundsException extends EmulatorException {

	/**
	 * Constructs an <code>PCOutOfBoundsException</code>.
	 */
	public PCOutOfBoundsException() {
		super();
	}
	
	/**
	 * Constructs an <code>PCOutOfBoundsException</code> with the given message.
	 * @param reason the reason
	 */
	public PCOutOfBoundsException(String reason) {
		super(reason);
	}
}