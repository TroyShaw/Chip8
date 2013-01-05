package emulator;

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import emulator.exception.EmulatorException;

public class Chip8 implements KeyController {

	/** Width of default emulator in pixels */
	public static final int WIDTH = 64;

	/** Height of default emulator in pixels */
	public static final int HEIGHT = 32;

	private static final int MAX_REGISTERS = 16;
	private static final int MAX_MEMORY = 4096;
	private static final int MAX_STACK = 16;

	private boolean[][] pixels;
	private boolean drawFlag;

	private int[] memory;
	private int[] register;
	private int[] stack;
	private int PC, SP, I, awaitedKeyRegister;
	private boolean[] keys;
	private volatile int delayTimer, soundTimer;
	private boolean awaitingKey;

	public Chip8() {
		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (soundTimer == 1) Toolkit.getDefaultToolkit().beep();
				delayTimer = Math.max(delayTimer - 1, 0);
				soundTimer = Math.max(soundTimer - 1, 0);

				//this is an optimization if needed
				//max num/ 0 
				//delayTimer = (delayTimer - 1) & ~((delayTimer -1) >> 28);
				//soundTimer = (soundTimer - 1) & ~((soundTimer -1) >> 28);
			}
		};

		timer.schedule(task, 0, 17);
	}

	/**
	 * Loads the program into memory and reinitialises variables. <br>
	 * The program cannot be null, and cannot have length equal or greater than 3584 bytes (4096 - 512).
	 * @param program the program
	 */
	public void loadProgram(byte[] program) {
		if (program == null) throw new NullPointerException("program cannot be null");
		if (program.length >= 3584) throw new IllegalArgumentException("program cannot be longer than 3584 bytes");

		pixels = new boolean[WIDTH][HEIGHT];
		drawFlag = false;

		stack = new int[MAX_STACK];
		register = new int[MAX_REGISTERS];
		memory = new int[MAX_MEMORY];
		for (int i = 0; i < font.length; i++) memory[i] = font[i] & 0xFF;
		for (int i = 0; i < program.length; i++) memory[i + 512] = program[i] & 0xFF;
		PC = 512;
		SP = 0;
		I = 0;
		delayTimer = 0;
		soundTimer = 0;
		keys = new boolean[16];
		awaitedKeyRegister = 0;
		awaitingKey = false;
	}

	/**
	 * Does a single 'tick' of the emulator. <br>
	 * An exception may be thrown for many reasons, including stack over/under flow, memory outofbounds, etc.
	 * @throws EmulatorException
	 */
	public void tick() throws EmulatorException {
		//if we're awaiting a key, simply return
		if (awaitingKey) return;

		//otherwise proceed like normal
		int hi = memory[PC] & 0xFF;
		int low = memory[PC + 1] & 0xFF;
		int opcode = (hi << 8) | low;
		PC += 2;

		System.out.println("Executing opcode: " + Integer.toString(opcode, 16).toUpperCase() + " at PC = " + (PC - 2));

		//reset the drawflag
		drawFlag = false;

		//0xy0 for x/ y
		//00nn for nn
		//0nnn for nnn
		int x = hi & 0xF;
		int y = (low & 0xF0) >> 4;
		int n = low & 0xF;
		int nn = low;
		int nnn = (x << 8) | low;

		switch (hi >> 4) {
		case 0x0:
			switch (low) {
			case 0xE0: clearScreen();		break;
			case 0xEE: PC = stack[--SP];	break;
			default: invalidOpcode(opcode);
			}
			break;
		case 0x1: PC = nnn;		 								break;
		case 0x2: stack[SP++] = PC; PC = nnn;					break;
		case 0x3: if (register[x] == nn) PC += 2;				break;
		case 0x4: if (register[x] != nn) PC += 2;				break;
		case 0x5: if (register[x] == register[y]) PC += 2;		break;
		case 0x6: register[x] = nn;								break;
		case 0x7: register[x] = (register[x] + nn) & 0xFF;		break;
		case 0x8: 
			switch(low & 0xF) {
			case 0x0: register[x]  = register[y];				break;
			case 0x1: register[x] |= register[y];				break;
			case 0x2: register[x] &= register[y];				break;
			case 0x3: register[x] = (register[x] ^ register[y]) & 0xFF;		break;
			case 0x4: 
				register[x] += register[y];
				register[15] = register[x] > 0xFF ? 1 : 0;
				register[x] &= 0xFF;
				break;
			case 0x5: 
				register[15] = register[y] > register[x] ? 0 : 1;
				register[x] -= register[y];
				//register[15] = register[x] < 0 ? 0 : 1;
				register[x] &= 0xFF;
				break;
			case 0x6: 
				register[15] = register[x] & 0x1;
				register[x] = (register[x] >> 1) & 0xFF;
				break;
			case 0x7: 
				register[15] = register[x] > register[y] ? 0 : 1;
				register[x] = (register[y] - register[x]) & 0xFF;
				break;
			case 0xE: 
				register[15] = (register[x] >> 7);
				register[x] = (register[x] << 1) & 0xFF;
				break;
			default: invalidOpcode(opcode);
			}
			break;
		case 0x9: if (register[x] != register[y]) PC += 2;				break;
		case 0xA: I = nnn;												break;
		case 0xB: PC = (nnn + register[0]) & 0xFFFF;					break;
		case 0xC: register[x] = ((int) (Math.random() * 0xFF)) & nn; 	break;
		case 0xD: draw(register[x], register[y], n);					break;
		case 0xE: 
			switch (low) {
			case 0x9E: if (keys[register[x]]) PC += 2; 	break;
			case 0xA1: if (!keys[register[x]]) PC += 2; 	break;
			default: invalidOpcode(opcode);
			}
			break;
		case 0xF: 
			switch (low) {
			case 0x07: register[x] = delayTimer;		break;
			case 0x0A: awaitKeyPress(x);				break;
			case 0x15: delayTimer = register[x];		break;
			case 0x18: soundTimer = register[x];		break;
			case 0x1E:
				register[15] = I + register[x] > 0xFFF ? 1 : 0;
				I = (I + register[x]) & 0xFFF;	
				break;
			case 0x29: I = register[x] * 5;				break;
			case 0x33: 
				memory[I] = register[x] / 100;
				memory[I + 1] = (register[x] / 10) % 10;
				memory[I + 2] = (register[x] % 100) % 10;
				break;
			case 0x55: for (int i = 0; i <= x; i++) memory[I + i] = register[i]; I += x + 1;		break;
			case 0x65: for (int i = 0; i <= x; i++) register[i] = memory[I + i]; I += x + 1;		break;
			default: invalidOpcode(opcode);
			}
			break;
		default: invalidOpcode(opcode);
		}
	}

	private void draw(int x, int y, int height) {
		register[15] = 0;
		for (int j = 0; j < height; j++) {
			int dat = memory[j + I];

			for (int i = 0; i < 8; i++) {
				if ((dat & (0x80 >> i)) == 0) continue;

				int rx = (i + x) % 64;
				int ry = (j + y) % 32;

				if (pixels[rx][ry]) register[15] = 1;
				pixels[rx][ry] ^= true;
			}
		}

		drawFlag = true;
	}

	private void clearScreen() {
		for (int i = 0; i < pixels.length; i++)
			for (int j = 0; j < pixels[i].length; j++)
				pixels[i][j] = false;
		drawFlag = true;
	}

	private void awaitKeyPress(int register) {
		awaitedKeyRegister = register;
		awaitingKey = true;
	}

	private void invalidOpcode(int opcode) {
		System.out.println("Invalid opcode: " + Integer.toString(opcode, 16).toUpperCase() + " at PC = " + (PC + 2));
		System.exit(-1);
	}

	@Override
	public void keyInteracted(int i, boolean pressed) {
		if (awaitingKey) {
			register[awaitedKeyRegister] = 1 << i;
			awaitingKey = false;
		}
		keys[i] = pressed;
	}

	/**
	 * Gets the register file containing v0-v15. v15 is the carry flag.
	 * @return the register
	 */
	public int[] getRegister() {
		return register;
	}

	/**
	 * Gets the program counter.
	 * @return returns the PC
	 */
	public int getPC() {
		return PC;
	}

	/**
	 * Gets the stack pointer (will be between 0 and 15
	 * @return the stack pointer
	 */
	public int getSP() {
		return SP;
	}

	/**
	 * Returns the stack, a 16 level address array.
	 * @return the stack
	 */
	public int[] getStack() {
		return stack;
	}

	/**
	 * Returns the chip8 memory. It is a 4096 byte array (stored as ints)
	 * @return the memory
	 */
	public int[] getMemory() {
		return memory;
	}

	/**
	 * Returns the I register.
	 * @return the I register
	 */
	public int getIRegister() {
		return I;
	}

	/**
	 * Returns the delay timer.
	 * @return the delay timer
	 */
	public int getDelayTimer() {
		return delayTimer;
	}

	/**
	 * Returns the sound timer.
	 * @return the sound timer
	 */
	public int getSoundTimer() {
		return soundTimer;
	}

	/**
	 * Returns the draw flag, which indicates if the emulator needs to be redrawn this cycle.
	 * @return the draw flag
	 */
	public boolean getDrawFlag() {
		return drawFlag;
	}

	public boolean[][] getPixelData() {
		return pixels;
	}

	/**
	 * Returns the currently held keys (the first 16 bits of this int represent the keys)
	 * @return the keys
	 */
	public boolean[] getKeys() {
		return keys;
	}

	/** This is the fontset for the emulator */
	private short[] font = { 
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
			0x20, 0x60, 0x20, 0x20, 0x70, // 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
			0x90, 0x90, 0xF0, 0x10, 0x10, // 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
			0xF0, 0x10, 0x20, 0x40, 0x40, // 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
			0xF0, 0x90, 0xF0, 0x90, 0x90, // A
			0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
			0xF0, 0x80, 0x80, 0x80, 0xF0, // C
			0xE0, 0x90, 0x90, 0x90, 0xE0, // D
			0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
			0xF0, 0x80, 0xF0, 0x80, 0x80  // F
	};
}