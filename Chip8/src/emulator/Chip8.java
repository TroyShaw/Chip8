package emulator;

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
	
	private Drawer drawer;
	
	private int[] memory;
	private int[] register;
	private int[] stack;
	private int PC, SP, I, keys, awaitedKeyRegister;
	private volatile int delayTimer, soundTimer;
	private boolean awaitingKey;

	public Chip8() {
		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
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
	 * Registers the drawer with this emulator. The drawer cannot be null
	 * @param drawer the drawer
	 */
	public void registerDrawer(Drawer drawer) {
		if (drawer == null) throw new NullPointerException("drawer cannot be null");
		this.drawer = drawer;
	}

	/**
	 * Loads the program into memory and reinitialises variables. <br>
	 * The program cannot be null, and cannot have length equal or greater than 3584 bytes (4096 - 512).
	 * @param program the program
	 */
	public void loadProgram(byte[] program) {
		if (program == null) throw new NullPointerException("program cannot be null");
		if (program.length >= 3584) throw new IllegalArgumentException("program cannot be longer than 3584 bytes");
		
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
		keys = 0;
		awaitedKeyRegister = 0;
		awaitingKey = false;
	}

	public void tick() throws EmulatorException{
		//if we're awaiting a key, simply return
		if (awaitingKey) return;
		
		//otherwise proceed like normal
		int hi = memory[PC] & 0xFF;
		int low = memory[PC + 1] & 0xFF;
		int opcode = (hi << 8) | low;
		PC += 2;
		
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
			case 0xE0: drawer.clear();		break;
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
			case 0x3: register[x] ^= register[y];				break;
			case 0x4: 
				register[x] += register[y];
				register[15] = register[x] > 0xFF ? 1 : 0;
				register[x] &= 0xFF;
				break;
			case 0x5: 
				register[x] -= register[y];
				register[15] = register[x] < 0 ? 0 : 1;
				register[x] &= 0xFF;
				break;
			case 0x6: 
				register[15] = register[x] & 0x1;
				register[x] = register[x] >>> 1;
				break;
			case 0x7: 
				register[x] = register[y] - register[x];
				register[15] = register[x] < 0 ? 0 : 1;
				register[x] &= 0xFF;
				break;
			case 0xE: 
				register[15] = (register[x] >> 8) & 0x1;
				register[x] = register[x] << 1;
				break;
			default: invalidOpcode(opcode);
			}
			break;
		case 0x9: if (register[x] != register[y]) PC += 2;				break;
		case 0xA: I = nnn;												break;
		case 0xB: PC = nnn + register[0x0];								break;
		case 0xC: register[x] = ((int) (Math.random() * 0xFF)) & nn; 	break;
		case 0xD: draw(register[x], register[y], n);					break;
		case 0xE: 
			switch (low) {
			case 0x9E: if ((keys & register[x]) != 0) PC += 2; 	break;
			case 0xA1: if ((keys & register[x]) == 0) PC += 2; 	break;
			default: invalidOpcode(opcode);
			}
			break;
		case 0xF: 
			switch (low) {
			case 0x07: register[x] = delayTimer;		break;
			case 0x0A: awaitKeyPress(x);				break;
			case 0x15: delayTimer = register[x];		break;
			case 0x18: soundTimer = register[x];		break;
			case 0x1E: I = (I + register[x]) & 0xFFFF;	break;
			case 0x29: I = register[x] * 5;				break;
			case 0x33: 
				memory[I] = register[x] / 100;
				memory[I + 1] = (register[x] / 10) % 10;
				memory[I + 2] = (register[x] % 100) % 10;
				break;
			case 0x55: for (int i = 0; i < 15; i++) memory[I + i] = register[i]; break;
			case 0x65: for (int i = 0; i < 15; i++) register[i] = memory[I + i]; break;
			default: invalidOpcode(opcode);
			}
			break;
		default: invalidOpcode(opcode);
		}
		
		//System.out.println("Executed opcode: " + Integer.toString(opcode, 16).toUpperCase() + " at PC = " + (PC + 2));
	}

	private void draw(int x, int y, int length) {
		register[15] = drawer.draw(x, y, length, I, memory) ? 1 : 0;
		//System.out.println("draw at (" + x + ", " + y + "), height " + length);
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
			register[awaitedKeyRegister] = i;
			awaitingKey = false;
		}
		keys = pressed ? (keys | i) : (keys & ~i);
	}
	
	public int[] getRegister() {
		return register;
	}
	
	public int getPC() {
		return PC;
	}
	
	public int getSP() {
		return SP;
	}
	
	public int[] getStack() {
		return stack;
	}
	
	public int[] getMemory() {
		return memory;
	}
	
	public int getIRegister() {
		return I;
	}
	
	public int getDelayTimer() {
		return delayTimer;
	}
	
	public int getSoundTimer() {
		return soundTimer;
	}
	
	public int getKeys() {
		return keys;
	}
	
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