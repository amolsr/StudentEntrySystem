package com.student.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXDatePicker;

import com.student.dao.Dao;

import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class Admin extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Admin frame;
	private JLabel lblFrom;
	private JLabel lblTo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
							((LookAndFeelInfo) Array.get(UIManager.getInstalledLookAndFeels(), 1)).getClassName());
					frame = new Admin();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public Admin() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setResizable(false);
		setBounds(100, 100, 500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		JLabel lblTeamBdcoe = new JLabel("Team BDCoE");
		lblTeamBdcoe.setHorizontalAlignment(SwingConstants.CENTER);
		lblTeamBdcoe.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblTeamBdcoe.setBounds(150, 45, 200, 40);
		getContentPane().add(lblTeamBdcoe);

		lblFrom = new JLabel("From");
		lblFrom.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFrom.setBounds(30, 125, 50, 30);
		getContentPane().add(lblFrom);

		lblTo = new JLabel("To");
		lblTo.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTo.setBounds(280, 125, 50, 30);
		getContentPane().add(lblTo);

		JXDatePicker from = new JXDatePicker();
		from.getEditor().setFont(new Font("Tahoma", Font.PLAIN, 13));
		from.setBounds(100, 125, 120, 30);
		from.setDate(Calendar.getInstance().getTime());
		getContentPane().add(from);

		JXDatePicker to = new JXDatePicker();
		to.getEditor().setFont(new Font("Tahoma", Font.PLAIN, 13));
		to.setBounds(340, 125, 120, 30);
		to.setDate(Calendar.getInstance().getTime());
		getContentPane().add(to);

		from.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Calendar calendar = to.getMonthView().getCalendar();
				// starting today if we are in a hurry
				calendar.setTime(from.getDate());
				to.getMonthView().setLowerBound(calendar.getTime());
			}
		});

		String[] columnNames = { " ", "Student No.", "Name", "Work", "Date", "Entry", "Exit" };
		DefaultTableModel mod = new DefaultTableModel(columnNames, 0);
		JTable table = new JTable(mod);
		table.setEnabled(false);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(25);

		JScrollPane pane = new JScrollPane(table);
		pane.setSize(470, 200);
		pane.setLocation(10, 180);
		getContentPane().add(pane);

		JButton btnNewButton = new JButton("GET");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[][] data = Dao.fetchByDate(from.getDate(), to.getDate());
				if (data != null && data.length > 0) {
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.setRowCount(0);
					for (int i = 0; i < data.length; i++) {
						model.addRow(new Object[] { data[i][0], data[i][1], data[i][2], data[i][3], data[i][4],
								data[i][5], data[i][6] });
					}
				} else {
					JOptionPane.showMessageDialog(Admin.this, "No Entry Found", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnNewButton.setBounds(200, 400, 80, 30);
		getContentPane().add(btnNewButton);

		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainContainer.main(new String[] {});
				frame.dispose();
			}
		});
		Back.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Back.setBounds(30, 400, 80, 30);
		getContentPane().add(Back);

		JButton Export = new JButton("Export");
		Export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String header[] = new String[table.getColumnCount()];
				String body[][] = new String[table.getRowCount()][table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					header[i] = table.getColumnName(i);
				}
				for (int i = 0; i < table.getRowCount(); i++) {
					for (int j = 0; j < table.getColumnCount(); j++) {
						body[i][j] = String.valueOf(table.getValueAt(i, j));
					}
				}
				try {
					Dao.getDocument(header, body);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		Export.setFont(new Font("Tahoma", Font.PLAIN, 13));
		Export.setBounds(380, 400, 80, 30);
		getContentPane().add(Export);
	}
}
