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
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.babai.ssplot.math.io.NumParse;
import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.util.InfoLogger;

import com.babai.ssplot.ui.controls.UIFrame;
import static com.babai.ssplot.ui.controls.DUI.*;

public class DataViewer extends UIFrame implements ActionListener {
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

		var pnlPrefs = hbox(
			label("<html><body><b>Axes:</b></body></html>"),
			
			label("X → Col"),
			jcbXData = new JComboBox<>(),
			label("Y → Col"),
			jcbYData = new JComboBox<>(),
			lblZData = label("Z → Col"),
			jcbZData = new JComboBox<>(),
			
			btnPlot = button().text("Replot").onClick(this::updateView)
		);

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
		
		btnNew    = button().icon("/new_data.png").tooltip("New Data").onClick(this::updateView);
		btnLoad   = button().icon("/open.jpg").tooltip("Load Data from File");
		btnSave   = button().icon("/save.jpg").tooltip("Save Data");
		btnRow    = button().icon("/insert_row.png").tooltip("Add Row");
		btnColumn = button().icon("/insert_col.png").tooltip("Add Column");
		btnPrint  = button().icon("/printer.png").tooltip("Print Data");

		btnLoad.addActionListener(this);
		btnSave.addActionListener(this);
		btnRow.addActionListener(this);
		btnColumn.addActionListener(this);
		btnPrint.addActionListener(this);

		var pnlEdit = hbox(
			btnNew,
			btnLoad,
			btnSave,
			btnColumn,
			btnRow,
			btnPrint);
		
		pnlPlots.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		pnlPrefs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		pnlEdit.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.title("Dataset Viewer")
			.closeOperation(JFrame.HIDE_ON_CLOSE)
			.content(
				vbox(
					pnlPlots,
					pnlPrefs,
					pnlEdit,
					scroll))
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
		
		jcbXData.setEnabled(pdata != null);
		jcbYData.setEnabled(pdata != null);
		jcbZData.setEnabled(pdata != null);
		
		jcbPlotlist.setEnabled(pdata != null);
		btnPlot.setEnabled(pdata != null);
		btnEditProp.setEnabled(pdata != null);
		
		if (pdata == null) return;
		
		for (int i = 1; i <= pdata.getColumnCount(); i++) {
			jcbXData.addItem(i);
			jcbYData.addItem(i);
			jcbZData.addItem(i);
		}
		
		jcbXData.setSelectedItem(pdata.getDataCol1());
		jcbYData.setSelectedItem(pdata.getDataCol2());
		jcbZData.setSelectedItem(pdata.getDataCol3());
	}
	
	private void updateView() {
		var pdata = getData();
		if (pdata.isPresent()) {
			logger.log(String.format("Plotting col %d (y axis) vs col %d (x axis)", this.getCol2(), this.getCol1()));
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
				if (o instanceof Double) {
					row.add((Double) model.getValueAt(i, j));
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
