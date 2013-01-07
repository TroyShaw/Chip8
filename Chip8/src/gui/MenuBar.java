package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

/**
 * Menubar for the emulator program. 
 * Has various ways to control the game, including load, restart, change screen size, etc.
 * 
 * @author Troy Shaw
 */
public class MenuBar extends JMenuBar implements ActionListener {

	private static String USER_DIR = System.getProperty("user.home");

	//for my own personal testing
	@SuppressWarnings("unused")
	private static String MY_DIR = "C:\\Users\\Troy Shaw\\Downloads\\chp8_220\\CHIP8\\GAMES";
	
	//headers
	private JMenu fileMenu, optionsMenu, helpMenu;
	private JMenu size;

	//we use position in this list to know what scale we want
	private List<JRadioButtonMenuItem> scaleButtons;

	//menu items
	private JMenuItem reset, load, exit;
	private JMenuItem controls;
	private JRadioButtonMenuItem mute, pause;
	private JMenuItem help, about;

	//the controller we send events to
	private Controller controller;

	/**
	 * Instantiates a new <code>MenuBar</code> with the given controller.<br>
	 * The controller will have methods called on it asynchronously as the buttons are clicked. 
	 * 
	 * @param controller the controller that will be queried with the menubar actions
	 */
	public MenuBar(Controller controller) {
		if (controller == null) throw new NullPointerException("controller cannot be null");
		this.controller = controller;

		fileMenu = new JMenu("File");
		optionsMenu = new JMenu("Options");
		helpMenu = new JMenu("Help");

		fileMenu.setMnemonic(KeyEvent.VK_F);
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		helpMenu.setMnemonic(KeyEvent.VK_H);

		load = new JMenuItem("Load...");
		pause = new JRadioButtonMenuItem("Pause", false);
		reset = new JMenuItem("Reset");
		exit = new JMenuItem("Exit");

		size = new JMenu("Screen size");
		mute = new JRadioButtonMenuItem("Mute", !Controller.SOUND_ENABLED);
		controls = new JMenuItem("Controls");

		help = new JMenuItem("Help");
		about = new JMenuItem("About");

		scaleButtons = new ArrayList<JRadioButtonMenuItem>();
		for (int i = 0; i < 4; i++) {
			int val = (int) Math.pow(2, i);
			//if the value is equal to the default scale, we 'tick' this radio button
			scaleButtons.add(new JRadioButtonMenuItem(val + "x", val == Controller.DEFAULT_SCALE));
		}

		ButtonGroup b = new ButtonGroup();
		for (JRadioButtonMenuItem button : scaleButtons) b.add(button);

		load.addActionListener(this);
		pause.addActionListener(this);
		reset.addActionListener(this);
		exit.addActionListener(this);

		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		pause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));

		mute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));

		for (JRadioButtonMenuItem button : scaleButtons) button.addActionListener(this);

		mute.addActionListener(this);
		controls.addActionListener(this);

		help.addActionListener(this);
		about.addActionListener(this);

		fileMenu.add(load);
		fileMenu.addSeparator();
		fileMenu.add(pause);
		fileMenu.add(reset);
		fileMenu.addSeparator();
		fileMenu.add(exit);

		optionsMenu.add(size);
		optionsMenu.addSeparator();
		optionsMenu.add(mute);
		//TODO add a listener and some sort of menu when the controls menu-item is selected.
		//optionsMenu.addSeparator();
		//optionsMenu.add(controls);

		for (JRadioButtonMenuItem button : scaleButtons) size.add(button);

		helpMenu.add(help);
		helpMenu.addSeparator();
		helpMenu.add(about);

		add(fileMenu);
		add(optionsMenu);
		add(helpMenu);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == reset) {
			controller.reset();
		} else if (o == load) {
			JFileChooser chooser = new JFileChooser(USER_DIR);
			int result = chooser.showOpenDialog(null);
			if(result == JFileChooser.APPROVE_OPTION) controller.startNewGame(chooser.getSelectedFile());
		} else if (o == exit) {
			System.exit(0);
		} else if (o == help) {
			Dialogs.showHelp();
		} else if (o == about) {
			Dialogs.showAbout();
		} else if (o == pause) {
			Controller.PAUSED ^= true;
		} else if (o == mute) {
			Controller.SOUND_ENABLED ^= true;
		} else if (o instanceof JRadioButtonMenuItem) {
			int i = scaleButtons.indexOf(o);
			if (i != -1) controller.resizeDisplay((int) Math.pow(2, i));
		}
	}
}