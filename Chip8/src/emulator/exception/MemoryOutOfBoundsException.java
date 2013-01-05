package emulator.exception;

public class MemoryOutOfBoundsException extends EmulatorException {

	/**
	 * Constructs an <code>MemoryOutOfBoundsException</code>.
	 */
	public MemoryOutOfBoundsException() {
		super();
	}
	
	/**
	 * Constructs an <code>MemoryOutOfBoundsException</code> with the given message.
	 * @param reason the reason
	 */
	public MemoryOutOfBoundsException(String reason) {
		super(reason);
	}
}