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
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.babai.ssplot.math.io.NumParse;
import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.plot.PlotData.PlotType;
import com.babai.ssplot.util.InfoLogger;

import com.babai.ssplot.ui.controls.UIFrame;
import static com.babai.ssplot.ui.controls.DUI.*;

public class DataViewer extends UIFrame {
	private Vector<PlotData> plotlist;
	private JComboBox<String> jcbPlotlist;
	public Vector<Vector<Double>> dataset;

	private int colNo = 0;
	private int rowNo = 0;

	private JTable table;
	private JButton btnPlot, btnEditProp;

	// Selects which data column is plotted in which axis
	private Vector<JComboBox<Integer>> jcbColMapper;
	
	private InfoLogger logger;
	private Consumer<PlotData> updater;
	
	public DataViewer(InfoLogger logger) {
		this.logger = logger;
		plotlist = new Vector<PlotData>();

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

		btnEditProp = button()
			.text("Edit Properties...")
			.tooltip("Edit the properties of the current plot")
			.onClick(() -> {
				int id = jcbPlotlist.getSelectedIndex();
				String title = JOptionPane.showInputDialog("Title :");
				if (title == null || id == -1) return;
				
				PlotData curData = plotlist.get(id);
				curData.setTitle(title);
				updatePlotList();
				setDataOnly(curData);
				updateView();
			});

		var pnlPlots = hbox(
			label("<html><body><b>Plots:</b></body></html>"),
			jcbPlotlist,
			button()
				.text("View System")
				.onClick(() -> {
					int id = jcbPlotlist.getSelectedIndex();
					if (id == -1) return;
					JOptionPane.showMessageDialog(this,
						plotlist.get(id).getSystem().toString()
					);
				}),
			btnEditProp
		);

		jcbColMapper = new Vector<JComboBox<Integer>>();
		var axes = PlotType.THREED.axes();
		var pnlPrefs = hbox(label("<html><body><b>Axes:</b></body></html>"));
		for (var axis : axes) {
			pnlPrefs.add(label(axis + " → Col"));
			var cbox = new JComboBox<Integer>();
			jcbColMapper.add(cbox);
			pnlPrefs.add(cbox);
		}

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

		var scroll = scrollPane(table);
		scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		var btnNew    = button()
			.icon("/new_data.png")
			.tooltip("New Data")
			.onClick(this::newData);
		var btnLoad   = button()
			.icon("/open.jpg")
			.tooltip("Load Data from File")
			.onClick(this::openFile);
		var btnSave   = button()
			.icon("/save.jpg")
			.tooltip("Save Data")
			.onClick(this::saveFile);
		var btnRow    = button()
			.icon("/insert_row.png")
			.tooltip("Add Row")
			.onClick(this::addRow);
		var btnColumn = button()
			.icon("/insert_col.png")
			.tooltip("Add Column")
			.onClick(this::addColumn);
		var btnPrint  = button()
			.icon("/printer.png")
			.tooltip("Print Data")
			.onClick(this::printData);
		btnPlot = button()
			.text("Replot")
			.tooltip("Replot Data")
			.margin(10, 15, 10, 15)
			.onClick(this::updateView);

		var pnlEdit = hbox(
			btnNew,
			btnLoad,
			btnSave,
			btnColumn,
			btnRow,
			btnPrint,
			Box.createHorizontalStrut(30),
			btnPlot
		);
		
		pnlPlots.emptyBorder(5, 5, 0, 5);
		pnlPrefs.emptyBorder(0, 5, 0, 5);
		pnlEdit.emptyBorder(5, 5, 5, 5);

		this.title("Dataset Viewer")
			.closeOperation(JFrame.HIDE_ON_CLOSE)
			.content(
				vbox(
					pnlPlots,
					pnlPrefs,
					pnlEdit,
					scroll)
				.emptyBorder(10))
			.packFrame();
	}

	public DataViewer(PlotData data, InfoLogger logger) {
		this(logger);
		setData(data);
	}
	
	public void setData(PlotData pdata) {
		if (pdata == null) return;
		
		plotlist.add(pdata);
		updatePlotList();
		setDataOnly(pdata);
	}
	
	public void clear() {
		plotlist.clear();
		table.setModel(new DefaultTableModel());
	}

	/** Show the given plot data in the table */
	private void setDataOnly(PlotData pdata) {
		if (pdata == null) return;
		
		dataset = pdata.getData();
		colNo = pdata.getColumnCount();
		rowNo = pdata.getRowCount();

		populateAxisSelectors(pdata);
		
		// Update the table
		var headers = new Vector<String>();
		var mappings = pdata.getDataColMapping();
		for (int i = 0; i < colNo; i++) {
			boolean isKnownColumn = false;
			for (var entry : mappings.entrySet()) {
				if (entry.getValue() == i) {
					headers.add(entry.getKey() + " Data");
					logger.log(String.format("%s Max : %f, Min : %f",
						entry.getKey(), pdata.getMax(i), pdata.getMin(i)));
					isKnownColumn = true;
					break;
				}
			}
			
			if (!isKnownColumn) {
				headers.add("Column " + (i+1));
				logger.log(String.format("Col %d Max : %f, Min : %f",
					i, pdata.getMax(i), pdata.getMin(i)));
			}
		}
		
		jcbColMapper.lastElement().setEnabled(colNo > 2);

		table.setModel(new DefaultTableModel(dataset, headers));

		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < colNo; i++) {
			columns.getColumn(i).setPreferredWidth(10);
		}
	}

	private void populateAxisSelectors(PlotData pdata) {
		jcbPlotlist.setEnabled(pdata != null);
		btnPlot.setEnabled(pdata != null);
		btnEditProp.setEnabled(pdata != null);
		
		int i = 0;
		for (var axis : PlotType.THREED.axes()) {
			var jcbData = jcbColMapper.get(i);
			jcbData.removeAllItems();
			jcbData.setEnabled(pdata != null);
			i++;
			
			if (pdata == null) continue;
			
			for (int j = 1; j <= pdata.getColumnCount(); j++) {
				jcbData.addItem(j);
			}
			if (pdata.getDataCol(axis) < jcbData.getItemCount()) {
				jcbData.setSelectedIndex(pdata.getDataCol(axis));
			}
		}
	}
	
	private void updateView() {
		var pdata = getData();
		if (pdata.isPresent()) {
			logger.log(String.format("Plotting col %d (y axis) vs col %d (x axis)",
				getCol(0) + 1, getCol(1) + 1));
			updater.accept(pdata.get());
		}
	}
	

	// TODO this should return an Optional
	/** @return the dataset */
	public Optional<PlotData> getData() {
		var newdataset = new Vector<Vector<Double>>();
		var model = (DefaultTableModel) table.getModel();

		for (int i = 0; i < model.getRowCount(); i++) {
			var row = new Vector<Double>();
			for (int j = 0; j < model.getColumnCount(); j++) {
				Object o = model.getValueAt(i, j);
				double val = -1.0; // placeholder for unsupported type
				if (o instanceof Double d) {
					val = d;
				} else if (o instanceof String s) {
					val = Double.parseDouble(s);
				}
				row.add(val);
			}
			newdataset.add(row);
		}

		int id = jcbPlotlist.getSelectedIndex();
		if (id != -1) {
			PlotData curData = plotlist.get(id);
			curData.setData(newdataset);
			curData.setDataCols(getCol(0), getCol(1));
			updatePlotList();
			jcbPlotlist.setSelectedIndex(id);
			return Optional.of(curData);
		} else {
			return Optional.empty();
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
	
	public int getCol(int idx) {
		return (Integer) jcbColMapper.get(idx).getSelectedIndex();
	}
	
	public void newData() {
		String colString = JOptionPane.showInputDialog("No. of columns :");
		String rowString = JOptionPane.showInputDialog("No. of rows :");
		String filler    = JOptionPane.showInputDialog("Fill with :");
		
		if (colString == null || rowString == null) {
			return;
		}
		
		Double fillWith = (filler == null) ? 0 : Double.parseDouble(filler);
		
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

//		Vector<String> headers = new Vector<String>();
//		for (int i = 1; i <= colNo; i++) {
//			headers.add("Column " + i);
//		}
//
//		table.setModel(new DefaultTableModel(headers, rowNo));
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
	
	private void addRow() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		if (model != null) {
			int cols = model.getColumnCount();
			String[] row = new String[cols];
			Arrays.fill(row, "");
			model.addRow(row);
			table.setModel(model);
		}
	}
	
	private void addColumn() {
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
	}
	
	private void printData() {
		try {
			table.print();
		} catch (PrinterException e) {
			System.err.println("Can't print!");
			e.printStackTrace();
		}
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

	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
