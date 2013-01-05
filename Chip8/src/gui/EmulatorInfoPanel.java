package gui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import emulator.Chip8;

/**
 * Panel contains info of the various registers and info about the current state of the emulator.<br>
 *
 * @author Troy Shaw
 */
public class EmulatorInfoPanel extends JPanel {

	//there are 16 registers, V0-VF
	//there is the I flag
	//there is the PC
	//there is the SP and the 16 level stack
	
	private Chip8 emulator;
	private JTable registers;
	private JScrollPane scrollBar;
	
	public EmulatorInfoPanel(Chip8 emulator) {
		this.emulator = emulator;
		String[] headers = {"Name", "Value"};
		String[][] data = new String[22][2];
		
		for (int i = 0; i < data.length; i++) data[i][1] = "0";
		for (int i = 0x0; i <= 0xF; i++) data[i][0] = "V" + Integer.toString(i, 16).toUpperCase();
		data[16][0] = "I";
		data[17][0] = "PC";
		data[18][0] = "SP";
		data[19][0] = "Delay timer";
		data[20][0] = "Sound timer";
		data[21][0] = "Keys";
		registers = new JTable(data, headers);
		
		//registers.setFillsViewportHeight(true);
		//registers.getColumnModel().getColumn(0).setMaxWidth(120);
		//registers.getColumnModel().getColumn(1).setMaxWidth(120);
		scrollBar = new JScrollPane(registers);
		scrollBar.setPreferredSize(new Dimension(200, 379));
		add(scrollBar);
		
		setFocusable(false);
		registers.setFocusable(false);
		scrollBar.setFocusable(false);
		
	}
	
	public void update() {
		TableModel m = registers.getModel();
		
		for (int i = 0; i < 0xF; i++) {
			m.setValueAt(Integer.toString(emulator.getRegister()[i]), i, 1);
		}
		m.setValueAt(Integer.toString(emulator.getIRegister()), 16, 1);
		m.setValueAt(Integer.toString(emulator.getPC()), 17, 1);
		m.setValueAt(Integer.toString(emulator.getSP()), 18,1);
		m.setValueAt(Integer.toString(emulator.getDelayTimer()), 19, 1);
		m.setValueAt(Integer.toString(emulator.getSoundTimer()), 20,1);
		//String n = String.format("%16d", Integer.parseInt(Integer.toBinaryString(emulator.getKeys())));
		m.setValueAt(boolString(emulator.getKeys()), 21, 1);
	}
	
	private String boolString(boolean[] info) {
		String s = "";
		
		for (boolean b : info) s += b ? 1 : 0;
		
		return s;
	}
}