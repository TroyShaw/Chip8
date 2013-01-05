package emulator.exception;

public class UnknownOpcodeException extends EmulatorException {

	/**
	 * Constructs an <code>UnknownOpcodeException</code>.
	 */
	public UnknownOpcodeException() {
		super();
	}
	
	/**
	 * Constructs an <code>UnknownOpcodeException</code> with the given message.
	 * @param reason the reason
	 */
	public UnknownOpcodeException(String reason) {
		super(reason);
	}
}
