package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

import emulator.Chip8;

/**
 * The main frame that is displayed on-screen.<p>
 * It contains a menubar and the emulator display.
 *
 * @author Troy Shaw
 */
public class EmulatorFrame extends JFrame {
	//The actual emulator
	private Chip8 emulator;
	
	//The controller
	private Controller controller;
	
	//The gui elements
	private DisplayPanel panel;
	private EmulatorInfoPanel registerPanel;
	
	/**
	 * Constructs a new <code>EmulatorFrame</code> with which an emulator is run.
	 */
	public EmulatorFrame() {
		super("Troyboy Chip8 Emulator");
		
		setNativeLAndF();
		initComponents();
		initMenubar();
		setupLayout();
		initFrame();
		//frame is now ready to run a game
		//running of any games must be started by loading a ROM
	}
	
	/**
	 * Initialises the fields of this object.
	 */
	private void initComponents() {
		emulator = new Chip8();
		panel = new DisplayPanel(emulator);
		registerPanel = new EmulatorInfoPanel(emulator);
		
		controller = new Controller(this, emulator, panel, registerPanel);
	}
	
	/**
	 * Initialises the menubar for this frame.
	 */
	private void initMenubar() {
		setJMenuBar(new MenuBar(controller));
	}
	
	/**
	 * Sets the layout, and adds, the various panels to this frame.
	 */
	private void setupLayout() {
		getContentPane().add(panel);
	}
	
	/**
	 * Initialises the frames settings, such as exit conditions, etc
	 */
	private void initFrame() {
		setResizable(false);
		pack();
		center();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Sets the look and feel of the GUI to the current systems Look and feel.
	 */
	private void setNativeLAndF() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			//do nothing. It will default to normal
		}
	}
	
	/**
	 * Centres the screen on the x and y axis.
	 */
	private void center() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		int x = (dim.width - getSize().width) / 2;
		int y = (dim.height - getSize().height) / 2;

		setLocation(x, y);
	}
}