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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.babai.ssplot.math.io.NumParse;
import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.util.InfoLogger;

public class DataViewer extends JInternalFrame implements ActionListener {
	private Vector<PlotData> plotlist;
	private JComboBox<String> jcbPlotlist;
	public Vector<Vector<Double>> dataset;

	private int colNo = 0;
	private int rowNo = 0;

	private JTable table;
	private JButton btnPlot;

	private JButton btnNew, btnLoad, btnSave, btnRow, btnColumn, btnPrint;
	private JButton btnEditProp;
	private JLabel lblZData;
	private JComboBox<Integer> jcbXData, jcbYData, jcbZData;
	
	private InfoLogger logger;
	private Consumer<PlotData> updater;

	private static final PlotData zeroData = new PlotData();
	
	public DataViewer(InfoLogger logger) {
		this.logger = logger;
		plotlist = new Vector<PlotData>();

		/* GUI */
		setTitle("Dataset Viewer");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
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
					updateView();
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
				updatePlotList();
				setDataOnly(curData);
				updateView();
			}
		});

		pnlPlots.add(lblPlots);
		pnlPlots.add(jcbPlotlist);
		pnlPlots.add(btnEditProp);

		JPanel pnlPrefs = new JPanel();
		pnlPrefs.setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel lblXData = new JLabel("X → Col");
		JLabel lblYData = new JLabel("Y → Col");
		lblZData = new JLabel("Z → Col");
		jcbXData = new JComboBox<>();
		jcbYData = new JComboBox<>();
		jcbZData = new JComboBox<>();
		btnPlot = new JButton("Replot");
		btnPlot.addActionListener(this);
		pnlPrefs.add(new JLabel("<html><body><b>Axes:</b></body></html>"));
		pnlPrefs.add(lblXData);
		pnlPrefs.add(jcbXData);
		pnlPrefs.add(lblYData);
		pnlPrefs.add(jcbYData);
		pnlPrefs.add(lblZData);
		pnlPrefs.add(jcbZData);
		pnlPrefs.add(btnPlot);

		table = new JTable();
		table.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		table.setShowGrid(true);
		table.setGridColor(Color.GRAY);
		table.setAutoCreateRowSorter(true);
		// Add paste support from spreadsheet
		table.getInputMap().put(
			KeyStroke.getKeyStroke(
				KeyEvent.VK_V,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "Paste");
		table.getActionMap().put("Paste", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pasteFromClipboard(table);
			}
		});

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

	public DataViewer(PlotData data, InfoLogger logger) {
		this(logger);
		if (data != null) {
			setData(data);
		}
	}
	
	public void setData(PlotData pdata) {
		plotlist.add(pdata);
		updatePlotList();
		setDataOnly(pdata);
	}
	
	private static void pasteFromClipboard(JTable table) {
		try {
			String clipboardText = (String) Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.getData(DataFlavor.stringFlavor);

			int startRow = table.getSelectedRow();
			int startCol = table.getSelectedColumn();

			String[] rows = clipboardText.split("\n");

			for (int i = 0; i < rows.length; i++) {
				String[] cells = rows[i].split("\\s+");
				for (int j = 0; j < cells.length; j++) {
					int row = startRow + i;
					int col = startCol + j;
					if (row < table.getRowCount() && col < table.getColumnCount()) {
						table.setValueAt(cells[j].trim(), row, col);
					}
				}
			}
		} catch (UnsupportedFlavorException | IOException ex) {
			ex.printStackTrace();
		}
	}

	/** Show the given plot data in the table */
	private void setDataOnly(PlotData pdata) {
		DefaultTableModel model;
		dataset = pdata.getData();
		colNo = pdata.getColumnCount();
		rowNo = pdata.getRowCount();

		populateAxisSelectors(pdata);
		
		/* Update the table */
		var headers = new Vector<String>();
		for (int i = 1; i <= colNo; i++) {
			if (i == pdata.getDataCol1()) {
				headers.add("X Data");
				logger.log(String.format("X Max : %f, Min : %f", pdata.getMax(i-1), pdata.getMin(i-1)));
			} else if (i == pdata.getDataCol2()) {
				headers.add("Y Data");
				logger.log(String.format("Y Max : %f, Min : %f", pdata.getMax(i-1), pdata.getMin(i-1)));
			} else {
				headers.add("Column " + i);
				logger.log(String.format("Col %d Max : %f, Min : %f", i, pdata.getMax(i-1), pdata.getMin(i-1)));
			}
		}
		
		lblZData.setEnabled(colNo > 2);
		jcbZData.setEnabled(colNo > 2);

		table.setModel(new DefaultTableModel(dataset, headers));

		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < colNo; i++) {
			columns.getColumn(i).setPreferredWidth(10);
		}
	}

	private void populateAxisSelectors(PlotData pdata) {
		jcbXData.removeAllItems();
		jcbYData.removeAllItems();
		jcbZData.removeAllItems();
		
		for (int i = 1; i <= pdata.getColumnCount(); i++) {
			jcbXData.addItem(i);
			jcbYData.addItem(i);
			jcbZData.addItem(i);
		}
		
		jcbXData.setSelectedItem(pdata.getDataCol1());
		jcbYData.setSelectedItem(pdata.getDataCol2());
		jcbZData.setSelectedItem(pdata.getDataCol3());
	}

	// TODO this should return an Optional
	/** @return the dataset */
	public PlotData getData() {
		var newdataset = new Vector<Vector<Double>>();
		var model = (DefaultTableModel) table.getModel();

		for (int i = 0; i < model.getRowCount(); i++) {
			var row = new Vector<Double>();
			for (int j = 0; j < model.getColumnCount(); j++) {
				Object o = model.getValueAt(i, j);
				if (o instanceof Double) {
					row.add( (Double) model.getValueAt(i, j) );
				} else if (o instanceof String) {
					row.add(Double.parseDouble((String) model.getValueAt(i, j)));
				} else {
					row.add(-1.0); // FIXME why -1.0?
				}
			}
			newdataset.add(row);
		}

		int id = jcbPlotlist.getSelectedIndex();
		if (id != -1) {
			PlotData curData = plotlist.get(id);
			curData.setData(newdataset);
			curData.setDataCols(getCol1(), getCol2());
			updatePlotList();
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
	
	/** @return the number of columns in the dataset */
	public int getColumnNo() {
		return colNo;
	}

	public int getCol1() {
		return (Integer) jcbXData.getSelectedItem();
	}

	public int getCol2() {
		return (Integer) jcbYData.getSelectedItem();
	}
	
	public int getCol3() {
		return (Integer) jcbZData.getSelectedItem();
	}

	public void toggleColumnChanger() {
		jcbXData.setEnabled(!jcbXData.isEnabled());
		jcbYData.setEnabled(!jcbYData.isEnabled());
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
		var data = new Vector<Vector<Double>>(); 
		for (int i = 0; i < rowNo; i++) {
			var row = new Vector<Double>();
			for (int j = 0; j < colNo; j++) {
				row.add(Double.parseDouble(table.getValueAt(i, j).toString()));
			}
			data.add(row);
		}

		var files = new JFileChooser();
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
			String filler    = JOptionPane.showInputDialog("Fill with :");
			
			if (colString == null || rowString == null) {
				return;
			}
			
			Double fillWith = filler == null ? 0 : Double.parseDouble(filler);
			
			colNo = Integer.parseInt(colString);
			rowNo = Integer.parseInt(rowString);
			
			var dataset = new Vector<Vector<Double>>();
			for (int i = 0; i < rowNo; i++) {
				var row = new Vector<Double>();
				for (int j = 0; j < colNo; j++) {
					row.add(fillWith);
				}
				dataset.add(row);
			}
			setData(new PlotData(dataset));

//			Vector<String> headers = new Vector<String>();
//			for (int i = 1; i <= colNo; i++) {
//				headers.add("Column " + i);
//			}
//
//			table.setModel(new DefaultTableModel(headers, rowNo));

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
			updateView();
		}
	}
	
	private void updateView() {
		logger.log(String.format("Plotting col %d (y axis) vs col %d (x axis)", this.getCol2(), this.getCol1()));
		updater.accept(getData());
	}

	private void updatePlotList() {
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
		this.setData(zeroData);
	}
	
	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
