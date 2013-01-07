package emulator.exception;

public class InvalidKeyException extends EmulatorException {

	/**
	 * Constructs an <code>InvalidKeyException</code>.
	 */
	public InvalidKeyException() {
		super();
	}
	
	/**
	 * Constructs an <code>InvalidKeyException</code> with the given message.
	 * @param reason the reason
	 */
	public InvalidKeyException(String reason) {
		super(reason);
	}
}