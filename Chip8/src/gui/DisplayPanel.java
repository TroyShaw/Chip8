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
import emulator.KeyController;

/**
 * The panel that displays the game currently being played.
 *
 * @author Troy Shaw
 */
public class DisplayPanel extends JPanel {

	private static Color DEFAULT_COLOR = Color.black;

	private int scale = Controller.DEFAULT_SCALE;

	private KeyController buttonController;
	private Map<Integer, Integer> buttonMapping;
	private BufferedImage image;

	public DisplayPanel() {
		resizeDisplay(scale);

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight();j++) {
				image.setRGB(i, j, DEFAULT_COLOR.getRGB());
			}
		}

		registerKeyListener();

		setFocusable(true);
		requestFocusInWindow();
	}

	/**
	 * Sets the key listener to associate the keyboard buttons with the emulators buttons.
	 */
	private void registerKeyListener() {
		buttonMapping = new HashMap<Integer, Integer>();

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
				if (b != null) buttonController.keyInteracted(b, pushed);
			}
		};

		addKeyListener(kl);
	}

	public void registerButtonController(KeyController buttonController) {
		this.buttonController = buttonController;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	public void draw(boolean[][] data) {
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

	public void paintAll() {
		repaint();
	}

	public void clear() {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, image.getWidth(), image.getWidth());

		repaint();
	}

	public void resizeDisplay(int scale) {
		this.scale = scale;
		Dimension d = new Dimension(Chip8.WIDTH * scale, Chip8.HEIGHT * scale);
		setPreferredSize(d);

		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
	}
}