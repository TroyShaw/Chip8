package emulator.exception;

public class StackUnderflowException extends EmulatorException {

	/**
	 * Constructs an <code>StackUnderflowException</code>.
	 */
	public StackUnderflowException() {
		super();
	}
	
	/**
	 * Constructs an <code>StackUnderflowException</code> with the given message.
	 * @param reason the reason
	 */
	public StackUnderflowException(String reason) {
		super(reason);
	}
}