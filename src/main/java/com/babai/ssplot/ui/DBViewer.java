/*
 * DBViewer.java
 * 
 * Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
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

package com.babai.ssplot.ui;

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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.babai.ssplot.math.io.NumParse;
import com.babai.ssplot.math.plot.PlotData;

public class DBViewer extends JInternalFrame implements ActionListener {
	private Vector<PlotData> plotlist;
	private JComboBox<String> jcbPlotlist;
	public Vector<Vector<Double>> dataset;

	private int colNo = 0;
	private int rowNo = 0;

	private JTable table;
	private JButton btnPlot;

	private PlotView pv;

	private JButton btnNew, btnLoad, btnSave, btnRow, btnColumn, btnPrint;
	private JButton btnEditProp;
	private JTextField tfXData, tfYData, tfZData;

	// TODO
	@SuppressWarnings("unused")
	private SystemInputFrame input = null;

	private static PlotData zeroData;

	public DBViewer(SystemInputFrame input, PlotView pv) {
		this(null, input, pv);
	}

	public DBViewer(PlotData data, SystemInputFrame input, PlotView pv) {
		setODEInputFrame(input);
		setView(pv);

		plotlist = new Vector<PlotData>();

		/* GUI */
		setTitle("Dataset Viewer");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel pnlPlots = new JPanel();
		pnlPlots.setLayout(new FlowLayout(FlowLayout.LEADING));
		jcbPlotlist = new JComboBox<String>();
		jcbPlotlist.setEditable(false);
		jcbPlotlist.addActionListener(evt -> {
			int id = jcbPlotlist.getSelectedIndex();
			if (id != -1) {
				PlotData pdata = plotlist.get(id);
				if (pdata != null) {
					setDataOnly(pdata);
					applyChanges();
				}
			}
		});

		JLabel lblPlots = new JLabel("<html><body><b>Plots:</b></body></html>");
		btnEditProp = new JButton("Edit Properties...");
		btnEditProp.setToolTipText("Edit the properties of the current plot");
		btnEditProp.addActionListener(evt -> {
			int id = jcbPlotlist.getSelectedIndex();
			String title = JOptionPane.showInputDialog("Title :");
			if (title == null) {
				return;
			}
			
			if (id != -1) {
				PlotData curData = plotlist.get(id);
				curData.setTitle(title);
				updateList();
				setDataOnly(curData);
			}
			applyChanges();
		});

		pnlPlots.add(lblPlots);
		pnlPlots.add(jcbPlotlist);
		pnlPlots.add(btnEditProp);

		JPanel pnlPrefs = new JPanel();
		pnlPrefs.setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel lblXData = new JLabel("X →");
		JLabel lblYData = new JLabel("Y →");
		JLabel lblZData = new JLabel("Z →");
		tfXData = new JTextField("1", 2);
		tfYData = new JTextField("2", 2);
		tfZData = new JTextField("3", 2);
		tfXData.setHorizontalAlignment(JTextField.CENTER);
		tfYData.setHorizontalAlignment(JTextField.CENTER);
		tfZData.setHorizontalAlignment(JTextField.CENTER);
		btnPlot = new JButton("Replot");
		btnPlot.addActionListener(this);
		pnlPrefs.add(new JLabel("<html><body><b>Axes:</b></body></html>"));
		pnlPrefs.add(lblXData);
		pnlPrefs.add(tfXData);
		pnlPrefs.add(lblYData);
		pnlPrefs.add(tfYData);
		pnlPrefs.add(lblZData);
		pnlPrefs.add(tfZData);
		pnlPrefs.add(btnPlot);

		table = new JTable();
		table.setAutoCreateRowSorter(true);

		if (data != null) {
			setData(data);
		}

		JScrollPane scroll = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel pnlEdit = new JPanel();
		pnlEdit.setLayout(new FlowLayout(FlowLayout.LEADING));

		btnNew = new JButton();
		btnLoad = new JButton();
		btnSave = new JButton();
		btnRow = new JButton();
		btnColumn = new JButton();
		btnPrint = new JButton();

		btnNew.setToolTipText("New Data");
		btnLoad.setToolTipText("Load Data from File");
		btnSave.setToolTipText("Save Data");
		btnRow.setToolTipText("Add Row");
		btnColumn.setToolTipText("Add Column");
		btnPrint.setToolTipText("Print Data");

		btnNew.setIcon(new ImageIcon(getClass().getResource("/new_data.png")));
		btnRow.setIcon(new ImageIcon(getClass().getResource("/insert_row.png")));
		btnColumn.setIcon(new ImageIcon(getClass().getResource("/insert_col.png")));
		btnPrint.setIcon(new ImageIcon(getClass().getResource("/printer.png")));
		btnLoad.setIcon(new ImageIcon(getClass().getResource("/open.jpg")));
		btnSave.setIcon(new ImageIcon(getClass().getResource("/save.jpg")));

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
		
		pnlPlots.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		pnlPrefs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		pnlEdit.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		mainPanel.add(pnlPlots);
		mainPanel.add(pnlPrefs);
		mainPanel.add(pnlEdit);
		mainPanel.add(scroll);

		setContentPane(mainPanel);
		pack();
	}

	public void setData(PlotData pdata) {
		plotlist.add(pdata);
		updateList();
		setDataOnly(pdata);
	}

	/** Show the given plot data in the table */
	private void setDataOnly(PlotData pdata) {
		DefaultTableModel model;
		dataset = pdata.getData();
		colNo = dataset.firstElement().size();
		rowNo = dataset.size();

		/* Update the table */
		var headers = new Vector<String>();
		for (int i = 1; i <= colNo; i++) {
			if (i == pdata.getDataCol1()) {
				headers.add("X Data");
				pv.log(String.format("X Max : %f, Min : %f", pdata.getMax(i-1), pdata.getMin(i-1)));
			} else if (i == pdata.getDataCol2()) {
				headers.add("Y Data");
				pv.log(String.format("Y Max : %f, Min : %f", pdata.getMax(i-1), pdata.getMin(i-1)));
			} else {
				headers.add("Column " + i);
				pv.log(String.format("Col %d Max : %f, Min : %f", i, pdata.getMax(i-1), pdata.getMin(i-1)));
			}
		}

		model = new DefaultTableModel(dataset, headers);
		table.setModel(model);

		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < colNo; i++) {
			columns.getColumn(i).setPreferredWidth(10);
		}
	}

	// TODO this should return an Optional
	/** @return the dataset */
	public PlotData getData() {
		//PlotData pdata = new PlotData(dataset); /* Needs improvement */
		var newdataset = new Vector<Vector<Double>>();
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		for (int i = 0; i < model.getRowCount(); i++) {
			var row = new Vector<Double>();
			for (int j = 0; j < model.getColumnCount(); j++) {
				Object o = model.getValueAt(i, j);
				if (o instanceof Double) {
					row.add( (Double) model.getValueAt(i, j) );
				} else if (o instanceof String) {
					row.add(Double.parseDouble((String) model.getValueAt(i, j)));
				} else {
					row.add(-1.0);
				}
			}
			newdataset.add(row);
		}

		int id = jcbPlotlist.getSelectedIndex();
		if (id != -1) {
			PlotData curData = plotlist.get(id);
			curData.setData(newdataset);
			curData.setDataCols(getCol1(), getCol2());
			updateList();
			jcbPlotlist.setSelectedIndex(id);
			return curData;
		} else {
			System.err.println("No data found!");
			return zeroData;
		}
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
	
	// TODO Z is missing

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

	public boolean openFile() {
		var files = new JFileChooser();
		if (files.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			Path dpath = files.getSelectedFile().toPath();
			if (dpath != null) {
				try {
					setData(new PlotData(NumParse.parse(dpath)));
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return false;
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

			String colString = JOptionPane.showInputDialog("No. of columns :");
			String rowString = JOptionPane.showInputDialog("No. of rows :");
			
			if (colString == null || rowString == null) {
				return;
			}
			
			int col = Integer.parseInt(colString);
			int row = Integer.parseInt(rowString);
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

			String colName = JOptionPane.showInputDialog("Column Name?");
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			if (model != null) {
				int rows = model.getRowCount();
				String[] col = new String[rows];
				Arrays.fill(col, "");
				if (colName == "") {
					model.addColumn(colName, col);
				} else {
					model.addColumn("Column " + (model.getColumnCount()+1), col);
				}
				table.setModel(model);
			}

		} else if (evt.getSource() == btnPrint) {

			try {
				table.print();
			} catch (PrinterException e) {
				System.err.println("Can't print!");
				e.printStackTrace();
			}

		} else if (evt.getSource() == btnPlot) {
			applyChanges();
		}
	}

	private void applyChanges() {
		pv.log(String.format("Plotting col %d (y axis) vs col %d (x axis).", this.getCol2(), this.getCol1()));
		pv.clear();
		pv.setCurPlot(getData());
		input.setSystem(getData().getSystem());
	}

	/**
	 * @param pv the pv to set
	 */
	public void setView(PlotView pv) {
		this.pv = pv;
	}

	private void updateList() {
		jcbPlotlist.removeAllItems();
		for (PlotData pdata : plotlist) {
			if (pdata != null) {
				jcbPlotlist.addItem(pdata.getTitle());
			}
		}
		jcbPlotlist.setSelectedIndex(jcbPlotlist.getItemCount()-1);
	}

	public void clear() {
		plotlist.clear();
		//		updateList();
		this.setData(zeroData);
	}

	public void setODEInputFrame(SystemInputFrame odeinput) {
		this.input = odeinput;
	}
}
