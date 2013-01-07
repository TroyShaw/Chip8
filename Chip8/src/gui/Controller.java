package gui;

import java.io.File;

import javax.swing.JFrame;

import emulator.Chip8;
import emulator.exception.EmulatorException;
import fileio.Loader;


/**
 * The controller for this program. It runs and coordinates the GUI components with the Chip8 emulator object.
 *
 * @author Troy Shaw
 */
public class Controller {

	//this needs to be 2^n, where n is a member of {0,1,2,3}
	private final static int MAX_SCALE = 8;

	public final static int DEFAULT_SCALE = 4;
	public static boolean SOUND_ENABLED = true;
	public static boolean PAUSED = false;

	//emulator runs the game
	private Chip8 emulator;
	//panel which displays the game
	private DisplayPanel panel;
	//infoPanel, not currently used
	private EmulatorInfoPanel infoPanel;
	//parent frame
	private JFrame frame;
	//the currently running thread, the game tick happens here
	private Thread currentThread;
	//signals to the current thread to stop running
	private boolean hasQuit;
	//cached program used for a restart
	private byte[] program;

	/**
	 * Constructs a new controller with the given paramaters.
	 * @param frame
	 * @param emulator
	 * @param panel
	 * @param infoPanel
	 */
	public Controller(JFrame frame, Chip8 emulator, DisplayPanel panel, EmulatorInfoPanel infoPanel) {
		if (frame == null || emulator == null || panel == null || infoPanel == null) 
			throw new NullPointerException();
		
		this.frame = frame;
		this.emulator = emulator;
		this.panel = panel;
		this.infoPanel = infoPanel;
	}

	/**
	 * Resizes, repaints, and repacks the graphical display for the game.<br>
	 * The given scale must be between 1 and 8 inclusive, and if greater than 1, must be a multiple of 2.
	 *
	 * @param scale the scaler 
	 */
	public void resizeDisplay(int scale) {
		if (scale > MAX_SCALE || scale <= 0) 
			throw new IllegalArgumentException("scale must be in range 0 - " + MAX_SCALE);
		if (scale != 1 && scale % 2 != 0) throw new IllegalArgumentException("scale must be 1, or divisble by 2");
		
		panel.resizeDisplay(scale);
		frame.pack();
	}

	/**
	 * Restarts the emulator with the current game.<br>
	 * If no game has been initially loaded, this method does nothing.
	 */
	public void reset() {
		//if we haven't loaded a game, reset does nothing
		if (currentThread == null) return;

		//stop whatever is currently running
		stopGame();
		//start new game
		startNewGame(program);
	}

	/**
	 * Starts a new game, loading it from the given file.<br>
	 * The file cannot be null.
	 * @param file
	 */
	public void startNewGame(File file) {
		if (file == null) throw new NullPointerException();
		byte[] program = null;
		
		try {
			program = Loader.load(file);
		} catch (IllegalArgumentException e) {
			Dialogs.showFailureDialog("File not found");
			return;
		} catch (NullPointerException e) {
			Dialogs.showFailureDialog("File was null");
			return;
		}
		
		if (program == null) {
			Dialogs.showFailureDialog("Error while loading file");
			return;
		}
		
		//stop the current game
		stopGame();
		//start new game
		startNewGame(program);
	}
	
	/**
	 * Stops the current game thread. <br>
	 * If no game has been initialized (the thread is null) this does nothing. <br>
	 * If the game is already finished, this does nothing. <br>
	 * Otherwise this method stops the game.
	 */
	private void stopGame() {
		if (currentThread != null && currentThread.isAlive()) {
			hasQuit = true;

			while (currentThread.isAlive())
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					//ignore
				}
		}
	}

	private void startNewGame(byte[] program) {
		try {
			emulator.loadProgram(program);
		} catch (IllegalArgumentException e) {
			Dialogs.showFailureDialog(e.getMessage());
			return;
		}
		
		this.program = program;

		hasQuit = false;
		panel.clear();

		currentThread = new Thread() {
			@Override
			public void run() {
				while (!hasQuit) {
					if (!PAUSED) {
						try {
							emulator.tick();
							infoPanel.update();
							if (emulator.getDrawFlag()) panel.draw(emulator.getPixelData());
						} catch (EmulatorException e) {
							Dialogs.showFailureDialog(e.getMessage());
							hasQuit = true;
						}
					}

					try {
						//we need a sleep (even modest) so that key detection works properly
						//we can assume ticks take similar time and this will suffice
						Thread.sleep(1);
					} catch (InterruptedException e) {
						//ignore
					}
				}
			}
		};

		currentThread.start();
	}
}