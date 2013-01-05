package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import emulator.Key;
import emulator.Chip8;

/**
 * The panel that displays the game currently being played.
 *
 * @author Troy Shaw
 */
public class DisplayPanel extends JPanel {

	//Note: there is synchronized on rescale() and draw(pixelData) because these methods can be called at the same time
	//		and they both alter the same image. 
	//		It is possible to change our image to a smaller one while setting pixels, thus drawing out-of-bounds on the new one.
	
	private static Color DEFAULT_COLOR = Color.black;

	private int scale = Controller.DEFAULT_SCALE;

	private BufferedImage image;
	private Chip8 chip8;

	/**
	 * Creates a new <code>DisplayPanel</code> initialised with the given <code>KeyController</code>
	 * @param keyController
	 */
	public DisplayPanel(Chip8 chip8) {
		this.chip8 = chip8;
		
		//creates our image
		resizeDisplay(scale);
		//makes it the default color
		clear();
		
		//register our key listener to respond to key events
		registerKeyListener();

		//make our panel focusable
		setFocusable(true);
		requestFocusInWindow();
	}

	/**
	 * Sets the key listener to associate the keyboard buttons with the emulators buttons.
	 */
	private void registerKeyListener() {
		final Map<Integer, Integer> buttonMapping = new HashMap<Integer, Integer>();

		//we put all the values from the Key enum into our mapping
		for (Key k : Key.values()) buttonMapping.put(k.getCode(), k.getPosition());

		KeyListener kl = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				interacted(e.getKeyCode(), true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				interacted(e.getKeyCode(), false);
			}

			private void interacted(int val, boolean pushed) {
				Integer b = buttonMapping.get(val);
				if (b != null) chip8.keyInteracted(b, pushed);
			}
		};

		addKeyListener(kl);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Draws the contents of data to the image. 
	 * @param data the pixel data we are drawing
	 */
	public synchronized void draw(boolean[][] data) {
		//we iterate over boolean data
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {

				//then we iterate over appropriate pixels for our current scale
				for (int x = i * scale; x < i * scale + scale; x++) {
					for (int y = j * scale; y < j * scale + scale; y++) {
						Color c = data[i][j] ? Color.white : Color.black;
						image.setRGB(x, y, c.getRGB());
					}
				}
			}
		}
		
		repaint();
	}

	/**
	 * Repaints the game image.
	 */
	public void paintAll() {
		repaint();
	}

	/**
	 * Clears the visual display back to the default color. This does not alter the pixel data stored in the emulator.
	 */
	public void clear() {
		Graphics2D g = image.createGraphics();
		g.setColor(DEFAULT_COLOR);
		g.fillRect(0, 0, image.getWidth(), image.getWidth());

		repaint();
	}

	/**
	 * Resizes the panel and image to the given scale, then redraws the 
	 * @param scale
	 */
	public synchronized void resizeDisplay(int scale) {
		this.scale = scale;
		
		//resize our panel
		Dimension d = new Dimension(Chip8.WIDTH * scale, Chip8.HEIGHT * scale);
		setPreferredSize(d);

		//resize our image
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		//then redraw the pixel data
		draw(chip8.getPixelData());
		//then to our screen
		repaint();
	}
}