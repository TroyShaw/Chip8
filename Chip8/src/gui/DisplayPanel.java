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
import emulator.Drawer;
import emulator.KeyController;

/**
 * The panel that displays the game currently being played.
 *
 * @author Troy Shaw
 */
public class DisplayPanel extends JPanel implements Drawer {

	private static Color DEFAULT_COLOR = Color.black;

	private int scale = Controller.DEFAULT_SCALE;

	private KeyController buttonController;
	private Map<Integer, Integer> buttonMapping;
	private BufferedImage image;

	private boolean[][] display = new boolean[64][32]; 

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

		int[] keys = {
				KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3,
				KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD5,
				KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8,
				KeyEvent.VK_NUMPAD9, KeyEvent.VK_NUMPAD0, KeyEvent.VK_SEPARATOR,
				KeyEvent.VK_DIVIDE, KeyEvent.VK_MULTIPLY, KeyEvent.VK_MINUS,
				KeyEvent.VK_ENTER};
		for (int i : keys) buttonMapping.put(i, 1 << i);
//		buttonMapping.put(KeyEvent.VK_UP, Key.up);
//		buttonMapping.put(KeyEvent.VK_DOWN, Key.down);
//		buttonMapping.put(KeyEvent.VK_LEFT, Key.left);
//		buttonMapping.put(KeyEvent.VK_RIGHT, Key.right);
//		buttonMapping.put(KeyEvent.VK_A, Key.a);
//		buttonMapping.put(KeyEvent.VK_B, Key.b);

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

	@Override
	public boolean draw(int x, int y, int length, int I, int[] data) {

		System.out.println("x: " + x + ", y: " + y);
		boolean conflict = false;

		for (int jr = 0; jr < length; jr++) {
			int j = jr % 32;
			int jy = (jr + y) % 32;

			int dat = data[j + I];

			for (int ir = 0; ir < 8; ir++) {
				int i = ir % 64;
				int ix = (ir + x) % 64;

				boolean newP = (dat & (1 << (7 - i))) != 0;
				boolean oldP = display[ix][jy];

				if (!conflict && oldP && !newP) conflict = true;
				//conflict = conflict || (oldP != newP);
				display[ix][jy] = newP ^ oldP;

				for (int x1 = i * scale; x1 < i * scale + scale; x1++) {
					for (int y1 = j * scale; y1 < j * scale + scale; y1++) {
						image.setRGB((x1 + x * scale) % (scale * Chip8.WIDTH), (y1 + y * scale) % (scale * Chip8.HEIGHT), newP ^ oldP ? Color.white.getRGB() : Color.black.getRGB());
					}
				}
			}
		}

		repaint();

		return conflict;
	}

	public void paintAll() {
		repaint();
	}

	@Override
	public void clear() {
		for (int i = 0; i < display.length; i++) 
			for (int j = 0; j < display[i].length; j++)
				display[i][j] = false;

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