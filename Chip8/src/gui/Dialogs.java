package gui;

import javax.swing.JOptionPane;


/**
 * Class with various pop-up windows.
 * 
 * @author Troy Shaw
 */
public class Dialogs {
	/**
	 * Displays an error message.
	 * 
	 * @param error the error message to display
	 */
	public static void showFailureDialog(String error) {
		JOptionPane.showMessageDialog(null, error, "Error!", JOptionPane.ERROR_MESSAGE);	
	}

	/**
	 * Displays the programs help.
	 */
	public static void showHelp() {
		String help = 	"" +
				"This is a basic Chip8 emulator\n\n" +
				"Click File -> load, then select a chip8 rom to play.\n\n" + 
				"The File menu also allows you to restart, or pause the emulator.\n\n" + 
				"You can adjust the screen size or mute the game from the Options menu.\n\n" + 
				"The controls are all of q,w,e,a,s,d,z,x,c,1,2,3,4,r,f,v.\n" +
				"It is advised to mash these buttons until you figure out the controls for your specific game\n\n" +
				"Enjoy!" + 
				"";

		JOptionPane.showMessageDialog(null, help);
	}

	/**
	 * Displays the programs 'about'.
	 */
	public static void showAbout() {
		String about = 	"Troyboy Chip8 emulator\nVersion 1.00\n\n" +
				"Troy Shaw\ntroyshw@gmail.com\n\n" +
				"This software is free and open source.\n" +
				"Feel free to distribute it to your friends!";

		JOptionPane.showMessageDialog(null, about);
	}
}