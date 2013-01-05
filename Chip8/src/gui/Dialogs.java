package gui;

import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


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
		//the font is dictated by the current JOptionPane
		Font font = UIManager.getDefaults().getFont("OptionPane.font");
		StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
		style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
		style.append("font-size:" + font.getSize() + "pt;");

		// html content
		JEditorPane ep = new JEditorPane("text/html", "<html><body style=\"" + style + "\">" + 
				"Troyboy Chip8 emulator<br>Version 1.00<p>" +
				"Troy Shaw<br>troyshw@gmail.com<p>" +
				"This software is free and open source.<br>" +
				"Check <a href=\"https://github.com/ArreatsChozen/Chip8.git\">GitHub</a> for the latest version.<br>" + 
				"Feel free to distribute it to your friends! </body></html>");

		// handle link events
		ep.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
					open(e.getURL());
			}
		});

		ep.setEditable(false);
		ep.setBackground(UIManager.getColor("Panel.background"));
		// show
		JOptionPane.showMessageDialog(null, ep);
	}

	/**
	 * Opens the given URL in the computers default browser. <br>
	 * Displays an error message if the URL is malformed, etc.
	 * @param url the url to navigate to
	 */
	private static void open(URL url) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(url.toURI());
			} catch (IOException | URISyntaxException e) { 
				Dialogs.showFailureDialog("URL failed to load");
			}
		}
		//I don't even think this program can be run without a desktop
	}
}