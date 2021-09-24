/*
 * DBViewer.java
 * 
 * Copyright 2021 Subhraman Sarkar <subhraman@subhraman-Inspiron>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

package math.plot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class DBViewer extends JFrame implements ActionListener {
    public Vector<Vector<Double>> dataset;
    
    private int colNo = 0;
    private int rowNo = 0;

    private JTable table;
    public JButton btnPlot;
    
    private JButton btnNew, btnLoad, btnSave, btnRow, btnColumn, btnPrint;
    private JTextField tfXData, tfYData;
    
    public DBViewer() {
    	this(null);
    }
    
	public DBViewer(Vector<Vector<Double>> data) {
        /* GUI */
		setTitle("Dataset Viewer");
		getContentPane().setLayout(new BorderLayout());

        JPanel pnlPrefs = new JPanel();
        JLabel lblXData = new JLabel("X data column :");
        JLabel lblYData = new JLabel("Y data column :");
        tfXData = new JTextField("1", 4);
        tfYData = new JTextField("2", 4);
        btnPlot = new JButton("Apply");
        pnlPrefs.add(lblXData);
        pnlPrefs.add(tfXData);
        pnlPrefs.add(lblYData);
        pnlPrefs.add(tfYData);
        pnlPrefs.add(btnPlot);
        
        table = new JTable();
        table.setAutoCreateRowSorter(true);
        
		if (data != null) {
			setData(data);
		} else {
			setBounds(500, 100, 700, 600);
		}
        
		JScrollPane scroll = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
				);
        JPanel pnlTable = new JPanel();
        pnlTable.setLayout(new FlowLayout());
        pnlTable.add(scroll);
        pnlTable.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        
        JPanel pnlEdit = new JPanel();
        btnNew = new JButton("New Data");
        btnLoad = new JButton("Load Data");
        btnSave = new JButton("Save Data");
        btnRow = new JButton("Add Row");
        btnColumn = new JButton("Add Column");
        btnPrint = new JButton("Print");
        btnNew.addActionListener(this);
        btnLoad.addActionListener(this);
        btnSave.addActionListener(this);
        btnRow.addActionListener(this);
        btnColumn.addActionListener(this);
        btnPrint.addActionListener(this);
        pnlEdit.add(btnNew);
        pnlEdit.add(btnLoad);
        pnlEdit.add(btnSave);
        pnlEdit.add(btnColumn);
        pnlEdit.add(btnRow);
        pnlEdit.add(btnPrint);
        
        getContentPane().add(pnlPrefs, BorderLayout.NORTH);
		getContentPane().add(pnlTable, BorderLayout.CENTER);
		getContentPane().add(pnlEdit, BorderLayout.SOUTH);
		
		this.pack();
		this.setResizable(false);
	}

    public void setData(Vector<Vector<Double>> data) {
    	DefaultTableModel model;
        dataset = data;
        colNo = data.get(0).size();
        rowNo = data.size();

        /* Update the table */
        Vector<String> headers = new Vector<String>();
        for (int i = 1; i <= colNo; i++) {
            headers.add("Column " + i);
		}
        
        model = new DefaultTableModel(data, headers);
        table.setModel(model);
        
		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < colNo; i++) {
            columns.getColumn(i).setPreferredWidth(20);
		}
		
		if (80*colNo > 500) {
            setBounds(500, 100, 80*colNo, 600);
        } else {
        	setBounds(500, 100, 500, 600);
        }
    }

    /** @return the dataset */
    public Vector<Vector<Double>> getData() {
        return dataset;
    }

    /** @return the number of rows in the dataset */
    public int getRowNo() {
        return rowNo;
    }
    
    public int getCol1() {
    	return Integer.parseInt(tfXData.getText());
    }
    
    public int getCol2() {
    	return Integer.parseInt(tfYData.getText());
    }

    /** @return the number of columns in the dataset */
    public int getColumnNo() {
        return colNo;
    }

    public void addListener(ActionListener l) {
        btnPlot.addActionListener(l);
    }

    public void toggleColumnChanger() {
        tfXData.setEnabled(!tfXData.isEnabled());
        tfYData.setEnabled(!tfYData.isEnabled());
        btnPlot.setEnabled(!btnPlot.isEnabled());
    }
    
    public void openFile() {
		JFileChooser files = new JFileChooser();
		int stat = files.showOpenDialog(this);
		File f = null;
		if (stat == JFileChooser.APPROVE_OPTION) {
			f = files.getSelectedFile();
            Path dpath = f.toPath();
            if (dpath != null) {
                try {
					Vector<Vector<Double>> data = NumParse.parse(dpath);
					setData(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
		}
	}
    
    public void saveFile() {
    	Vector<Vector<Double>> data = new Vector<Vector<Double>>(); 
    	for (int i = 0; i < rowNo; i++) {
    		Vector<Double> row = new Vector<Double>();
    		for (int j = 0; j < colNo; j++) {
				row.add(Double.parseDouble(table.getValueAt(i, j).toString()));
			}
    		data.add(row);
    	}
    	
    	/*IntStream.range(0, colNo).forEach(
    			m -> IntStream.range(0, rowNo).forEach(
    					n -> System.out.println(data.get(n).get(m))));*/
    	
		JFileChooser files = new JFileChooser();
		int stat = files.showSaveDialog(this);
		File f = null;
		if (stat == JFileChooser.APPROVE_OPTION) {
			f = files.getSelectedFile();
            Path dpath = f.toPath();
            if (dpath != null) {
                NumParse.write(data, dpath);
            }
		}
    }

	@Override
	public void actionPerformed(ActionEvent evt) {	
		if (evt.getSource() == btnNew) {
			
			int col = Integer.parseInt(JOptionPane.showInputDialog("No. of columns :"));
			int row = Integer.parseInt(JOptionPane.showInputDialog("No. of rows :"));
			colNo = col;
			rowNo = row;
			
			Vector<String> headers = new Vector<String>();
	        for (int i = 1; i <= col; i++) {
	            headers.add("Column " + i);
			}
			
			DefaultTableModel model = new DefaultTableModel(headers, row);

			table.setModel(model);
			
		} else if (evt.getSource() == btnLoad) {
			openFile();
		} else if (evt.getSource() == btnSave) {
			saveFile();
		} else if (evt.getSource() == btnRow) {
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			if (model != null) {
				int cols = model.getColumnCount();
				String[] row = new String[cols];
				Arrays.fill(row, "");
				model.addRow(row);
				table.setModel(model);
			}
			
		} else if (evt.getSource() == btnColumn) {
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			if (model != null) {
				int rows = model.getRowCount();
				String[] col = new String[rows];
				Arrays.fill(col, "");
				model.addColumn(col);
				table.setModel(model);
			}
			
		} else if (evt.getSource() == btnPrint) {
			try {
				table.print();
			} catch (PrinterException e) {
				System.err.println("Can't print!");
				e.printStackTrace();
			}
		}
	}
	
	
}
