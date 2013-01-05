package emulator;

public enum Key {
	left	(1 << 0),
	right	(1 << 1),
	up		(1 << 2),
	down	(1 << 3),
	a		(1 << 4),
	b		(1 << 5),
	c		(1 << 6),
	d 		(1 << 7),
	e		(1 << 8),
	f		(1 << 9);
	
	public final int code;
	
	Key(int code) {
		this.code = code;
	}
}
