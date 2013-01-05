package emulator.exception;

public class RegisterOutOfBoundsException extends EmulatorException {

	/**
	 * Constructs an <code>RegisterOutOfBoundsException</code>.
	 */
	public RegisterOutOfBoundsException() {
		super();
	}
	
	/**
	 * Constructs an <code>RegisterOutOfBoundsException</code> with the given message.
	 * @param reason the reason
	 */
	public RegisterOutOfBoundsException(String reason) {
		super(reason);
	}
}