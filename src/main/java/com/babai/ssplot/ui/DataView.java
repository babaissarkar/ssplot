/*
 * DBView.java
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
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.babai.ssplot.math.io.NumParse;
import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.plot.PlotData.PlotType;
import com.babai.ssplot.util.InfoLogger;
import com.babai.ssplot.util.UIHelper;
import com.babai.ssplot.ui.controls.DUI.Text;
import com.babai.ssplot.ui.controls.UIFrame;
import static com.babai.ssplot.ui.controls.DUI.*;

public class DataView extends UIFrame {
	private Vector<PlotData> plotlist;
	private Vector<Vector<Double>> dataset;

	private int colNum = 0;
	private int rowNum = 0;

	private JTable table;
	private JButton btnPlot, btnInfo, btnEditProp;

	private JComboBox<String> jcbPlotlist;
	// Selects which data column is plotted in which axis
	private Vector<JComboBox<Integer>> jcbColMapper;
	
	private InfoLogger logger;
	private Consumer<PlotData> updater;
	
	public DataView(InfoLogger logger) {
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
		
		btnInfo = button()
			.text("Info")
			.onClick(() -> {
				int id = jcbPlotlist.getSelectedIndex();
				var pdataOpt = getData();
				if (id == -1 || pdataOpt.isEmpty()) return;
				JOptionPane.showMessageDialog(this, pdataOpt.get().info(), "Dataset Information", JOptionPane.INFORMATION_MESSAGE);
			});

		var pnlPlots = hbox(
			label(Text.bold("Plots:")),
			jcbPlotlist,
			btnInfo,
			btnEditProp
		);

		jcbColMapper = new Vector<JComboBox<Integer>>();
		var axes = PlotType.LINES3.axes();
		var pnlPrefs = hbox(label(Text.bold("Axes:")));
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
		UIHelper.bindAction(table, "Paste", "ctrl v", () -> pasteFromClipboard(table));

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
		var btnPaste  = button()
			.icon("/paste.png")
			.tooltip("Paste Data from Spreadsheet")
			.onClick(() -> pasteFromClipboard(table));
		btnPlot = button()
			.text("Replot")
			.tooltip("Replot Data")
			.margin(10, 15, 10, 15)
			.onClick(this::updateView);

		var pnlEdit = hbox(
			btnNew,
			btnLoad,
			btnPaste,
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
			.resizable(true)
			.iconifiable(true)
			.closable(true)
			.maximizable(false)
			.packFrame();
		
		clear();
	}

	public DataView(PlotData data, InfoLogger logger) {
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
		updatePlotList();
		for (var cbox : jcbColMapper) {
			cbox.removeAllItems();
			cbox.setEnabled(false);
		}
		table.setModel(new DefaultTableModel());
		populateAxisSelectors(null);
	}

	/** Show the given plot data in the table */
	private void setDataOnly(PlotData pdata) {
		if (pdata == null) return;
		
		dataset = pdata.getData();
		colNum = pdata.getColumnCount();
		rowNum = pdata.getRowCount();

		populateAxisSelectors(pdata);
		
		// Update the table
		var headers = new Vector<String>();
		var mappings = pdata.getDataColMapping();
		for (int i = 0; i < colNum; i++) {
			boolean isKnownColumn = false;
			for (var entry : mappings.entrySet()) {
				if (entry.getValue() == i) {
					headers.add(entry.getKey() + " Data");
					isKnownColumn = true;
					break;
				}
			}
			
			if (!isKnownColumn) {
				headers.add("Column " + (i+1));
			}
		}
		
		logger.log(Text.tag("html", pdata.info().replace("\n", Text.LBREAK)));
		
		jcbColMapper.lastElement().setEnabled(colNum > 2);

		table.setModel(new DefaultTableModel(dataset, headers));

		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < colNum; i++) {
			columns.getColumn(i).setPreferredWidth(10);
		}
	}

	private void populateAxisSelectors(PlotData pdata) {
		jcbPlotlist.setEnabled(pdata != null);
		btnInfo.setEnabled(pdata != null);
		btnPlot.setEnabled(pdata != null);
		btnEditProp.setEnabled(pdata != null);
		
		if (pdata == null) return;
		
		int i = 0;
		for (var axis : pdata.getPltype().axes()) {
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
	public int getRowNum() {
		return rowNum;
	}
	
	/** @return the number of columns in the dataset */
	public int getColumnNum() {
		return colNum;
	}

	private void pasteFromClipboard(JTable table) {
		try {
			String clipboardText = (String) Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.getData(DataFlavor.stringFlavor);

			int startRow = table.getSelectedRow();
			int startCol = table.getSelectedColumn();
			if (startRow == -1) {
				startRow = 0;
			}
			if (startCol == -1) {
				startCol = 0;
			}

			String[] rows = clipboardText.split("\n");

			// Determine new size
			int newRowCount = Math.max(table.getRowCount(), startRow + rows.length);
			int newColCount = table.getColumnCount();

			// Find max columns in clipboard
			for (String row : rows) {
				String[] cells = row.split("\\s+");
				newColCount = Math.max(newColCount, startCol + cells.length);
			}

			// Initialize vector of vectors with existing data or zeros
			Vector<Vector<Double>> data = new Vector<>();
			for (int r = 0; r < newRowCount; r++) {
				Vector<Double> rowVector = new Vector<>();
				for (int c = 0; c < newColCount; c++) {
					Object value = (r < table.getRowCount() && c < table.getColumnCount())
							? table.getValueAt(r, c) : null;
					try {
						rowVector.add(value != null ? Double.parseDouble(value.toString()) : 0.0);
					} catch (NumberFormatException e) {
						rowVector.add(0.0); // fallback if existing value isn't a number
					}
				}
				data.add(rowVector);
			}

			// Populate new data from clipboard
			for (int i = 0; i < rows.length; i++) {
				String[] cells = rows[i].split("\\s+");
				for (int j = 0; j < cells.length; j++) {
					int row = startRow + i;
					int col = startCol + j;
					if (row < data.size() && col < data.get(row).size()) {
						try {
							data.get(row).set(col, Double.parseDouble(cells[j].trim()));
						} catch (NumberFormatException e) {
							data.get(row).set(col, 0.0); // fallback for invalid number
						}
					}
				}
			}

			// Set data using your defined method
			setData(new PlotData(data));
		} catch (UnsupportedFlavorException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public int getCol(int idx) {
		return (Integer) jcbColMapper.get(idx).getSelectedIndex();
	}
	
	public void newData() {
		var dialog = new TableDimInputDialog(this);
		dialog.setVisible(true);

		if (!dialog.isCancelled()) {
			rowNum = dialog.getRowNum();
			colNum = dialog.getColNum();
			var dataset = new Vector<Vector<Double>>();
			for (int i = 0; i < rowNum; i++) {
				var row = new Vector<Double>();
				for (int j = 0; j < colNum; j++) {
					row.add(dialog.getFillWith());
				}
				dataset.add(row);
			}
			setData(new PlotData(dataset));
		} else {
			return;
		}
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
		for (int i = 0; i < rowNum; i++) {
			var row = new Vector<Double>();
			for (int j = 0; j < colNum; j++) {
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
			String[] col = new String[model.getRowCount()];
			Arrays.fill(col, "");
			if (!colName.isEmpty()) {
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
	
	public class TableDimInputDialog extends JDialog {
		private int colNum;
		private int rowNum;
		private double fillWith = 0.0;
		private boolean cancelled = false;

		public TableDimInputDialog(Component parent) {
			super(SwingUtilities.getWindowAncestor(parent), "Table Dimensions");
			
			var colField = input().numeric(true).chars(5).text("1");
			var rowField = input().numeric(true).chars(5).text("1");
			var fillerField = input().numeric(true).chars(5).text("0");

			// Create OK and Cancel buttons
			var okButton = button()
				.text("OK")
				.onClick(() -> {
					if (colField.empty() || rowField.empty()) {
						JOptionPane.showMessageDialog(TableDimInputDialog.this, "Columns and rows cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						colNum = colField.intValue();
						rowNum = rowField.intValue();
						fillWith = fillerField.value();
						cancelled = false;
						dispose(); // Close the dialog
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(TableDimInputDialog.this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				});

			var cancelButton = button()
				.text("Cancel")
				.onClick(() -> {
					colNum = -1;
					rowNum = -1;
					fillWith = -1;
					cancelled = true;
					dispose(); // Close the dialog
				});

			setContentPane(
				vbox(
					grid()
						.row()
							.column(label("No. of columns:"))
							.column(colField)
						.row()
							.column(label("No. of rows:"))
							.column(rowField)
						.row()
							.column(label("Fill with (optional):"))
							.column(fillerField)
						.emptyBorder(20),
					hbox(okButton, cancelButton)
				)
			);
			
			pack();
			setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
			setLocationRelativeTo(SwingUtilities.getWindowAncestor(parent));
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}

		public int getColNum() {
			return colNum;
		}

		public int getRowNum() {
			return rowNum;
		}
		
		public boolean isCancelled() {
			return cancelled;
		}

		public double getFillWith() {
			return fillWith;
		}

	}
}
