package emulator.exception;

public abstract class EmulatorException extends Exception {

	/**
	 * Constructs an <code>EmulatorException</code>.
	 */
	public EmulatorException() {
		super();
	}
	
	/**
	 * Constructs an <code>EmulatorException</code> with the given message.
	 * @param reason the reason
	 */
	public EmulatorException(String reason) {
		super(reason);
	}
}
