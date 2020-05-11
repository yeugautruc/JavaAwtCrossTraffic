import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;

public class editor {
	public static JFrame f = new JFrame("Kreuzung Main");
	public static JFileChooser chooser = new JFileChooser();
	public static int rowRead = 0;
	public static int colRead = 0;

	public static JToggleButton[][] button(int[][] status, int row, int col) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // get screen resolution
		JFrame f1 = new JFrame("Kreuzung Initiated");
		JToggleButton[][] b = new JToggleButton[row][col];
		// auto resize image icon, base on screen resolution
		ImageIcon icon = new ImageIcon("kreuzung.jpeg");
		Image img = icon.getImage();
		int breite = 1;
		int hoehe = 1;
		if (((d.height - 200) * col / row) >= d.width) {
			breite = (d.width-50) / col;
			hoehe = (d.width-50) / col;
		} else {
			breite = (d.height - 200) / row;
			hoehe = (d.height - 200) / row;
		}
		Image newimg = img.getScaledInstance(breite, hoehe, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		// create table of toggle buttons, to visualization of cross, set status of all toggle buttons from status array.
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				b[i][j] = new JToggleButton(icon, false);
				b[i][j].setBounds(20 + breite*j, 100 + hoehe* i,breite, hoehe);
				b[i][j].setSelectedIcon(new ImageIcon("leer.png"));
				f1.add(b[i][j]);
				if (status[i][j] == 1)
					b[i][j].setSelected(false);
				else
					b[i][j].setSelected(true);
			}
		}
		JButton save = new JButton("Save");
		JButton back = new JButton("Back");
		back.setBounds(50, 50, 150, 30);
		save.setBounds(200, 50, 150, 30);
		f1.add(save);
		f1.add(back);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rowRead = row;
				colRead = col;
				String outputString = calculationString(b, row, col); // create String with status of all toggle buttons
				System.out.println("Output in file: \n" + outputString);
				try {
					save(outputString); // put String into file: kreuzung.txt 
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f1.setVisible(false);
				f.setVisible(true);
			}
		});

		f1.setSize(d.width, d.height);
		f1.setLayout(null);
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f1.setVisible(true);
		f.setVisible(false);
		return b;
	}

	public static String calculationString(JToggleButton[][] b, int row, int col) {
		String outputString = "";
		int[][] out = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (b[i][j].isSelected() == true) {
					outputString += "0";
					out[i][j] = 0;
				} else {
					outputString += "1";
					out[i][j] = 1;
				}
			}
			outputString += ":";
		}
		JOptionPane.showMessageDialog(f,
				"You will save this data: \n" + show(out) + "\n into Kreuzung.txt in Projects Directory");
		return outputString;
	}

	public static String show(int[][] A) {
		String anzeigen = "";
		for (int i = 0; i < A.length; i++) {
			anzeigen += Arrays.toString(A[i]);
			anzeigen += "\n";
		}
		return anzeigen;
	}

	public static void open() throws IOException {
		int valueOpen = chooser.showOpenDialog(null);
		if (valueOpen == JFileChooser.APPROVE_OPTION) {
			File fileOpened = chooser.getSelectedFile();
			openFile(fileOpened.getAbsolutePath()); // call method to open selected file from directory
		}
	}

	public static void openFile(String filename) throws IOException {
		FileInputStream fi = null;
		BufferedInputStream bi = null;
		String inputString = "";
		try {
			fi = new FileInputStream(filename);
			bi = new BufferedInputStream(fi);
			int i;
			while ((i = bi.read()) != -1) {
				inputString = inputString + (char) i;
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			bi.close();
			fi.close();
		}
		System.out.println("inputString: " + inputString);
		readfile(inputString); 
		int[][] AOUT = new int[rowRead][colRead]; // initiate Status Array
		AOUT = calculateAOUT(inputString, AOUT, rowRead, colRead);// read String again  to get all status of Status Array
		show(AOUT);
		button(AOUT, rowRead, colRead); // create table of toggle button with number of row, column and read status array
		JOptionPane.showMessageDialog(f, "Die zu öffnende Datei ist: " + chooser.getSelectedFile().getName());
	}

	public static void readfile(String a) {
		// read String from file, to assign the value of static rowRead and colRead 
		int rr = 0;
		int cr = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != ':')
				cr++;
			else
				rr++;
		}
		cr = cr / rr;
		rowRead = rr;
		colRead = cr;
	}

	public static int[][] calculateAOUT(String in, int[][] AOUT, int row, int col) {
		int[][] output = new int[row][col];
		int indexrow = 0;
		int indexcol = 0;
		System.out.println(in.length());
		for (int i = 0; i < in.length(); i++) {
			if (indexrow < row) {
				if (in.charAt(i) != ':') {
					String c = "" + in.charAt(i);
					output[indexrow][indexcol++] = Integer.valueOf(c);
					c = "";
				} else {
					indexcol = 0;
					indexrow++;
				}
			}
		}
		return output;
	}

	public static void save(String b) throws IOException {
		DataOutputStream o = new DataOutputStream(new FileOutputStream("Kreuzung.txt"));
		o.writeBytes(b);
		o.close();
	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		JTextField inputRow = new JTextField("9");
		JTextField inputCol = new JTextField("9");
		JLabel labelrow = new JLabel("Row");
		JLabel labelcol = new JLabel("Column");
		labelrow.setBounds(351, 25, 150, 30);
		inputRow.setBounds(351, 50, 150, 30);
		labelcol.setBounds(501, 25, 150, 30);
		inputCol.setBounds(501, 50, 150, 30);
		JButton init = new JButton("Initialisierung");

		f.add(labelrow);
		f.add(labelcol);
		f.add(inputRow);
		f.add(inputCol);
		f.add(init);
		f.setLayout(null);
		f.setVisible(true);
		init.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = Integer.valueOf(inputRow.getText());
				int col = Integer.valueOf(inputCol.getText());
				// read data from JTextField
				int[][] in = new int[row][col];
				for (int i = 0; i < row; i++) {
					for (int j = 0; j < col; j++) {
						in[i][j] = 1;
					}
				}
				button(in, row, col); // input data to create visualization, with status array(all toggle buttons unselected)
			}
		});
		JButton load = new JButton("LOAD");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					open(); // call method open to read: number of row,column and status of toggle table
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		f.add(load);
		load.setBounds(201, 100, 150, 30);
		JButton exit = new JButton("EXIT");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		f.add(exit);
		exit.setBounds(201, 200, 150, 30);
		init.setBounds(201, 50, 150, 30);
		s.close();
		JButton about = new JButton("About Us");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(f, "___---@Author:Gruppe 3, EKDI-SoSe2019-Hochschule OSTFALIA---___");
			}
		});
		f.add(about);
		about.setBounds(201, 150, 150, 30);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); // get resolution of screen to make reasonable setSize 
		f.setSize(d.width / 2, d.height);
		f.setLayout(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
