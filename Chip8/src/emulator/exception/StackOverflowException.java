package emulator.exception;

public class StackOverflowException extends EmulatorException {

	/**
	 * Constructs an <code>StackOverflowException</code>.
	 */
	public StackOverflowException() {
		super();
	}
	
	/**
	 * Constructs an <code>StackOverflowException</code> with the given message.
	 * @param reason the reason
	 */
	public StackOverflowException(String reason) {
		super(reason);
	}
}