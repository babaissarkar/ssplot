package com.babai.ssplot.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.JComboBox;

import com.babai.ssplot.math.plot.Axis;
import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.plot.Plotter;
import com.babai.ssplot.ui.controls.StateVar;
import com.babai.ssplot.ui.controls.UIFrame;
import com.babai.ssplot.ui.controls.UIInput;

import static com.babai.ssplot.ui.controls.DUI.button;
import static com.babai.ssplot.ui.controls.DUI.input;
import static com.babai.ssplot.ui.controls.DUI.label;
import static com.babai.ssplot.ui.controls.DUI.toolbar;
import static com.babai.ssplot.util.UIHelper.bindAction;

/**
 * UIFrame wrapper around a PlotView and adds zoom/rotation etc.
 */
public class PlotFrame extends UIFrame {
	private UIInput zoomField;
	private PlotView pv;
	private StateVar<Boolean> rotationEnabled = new StateVar<>(false);
	
	public PlotFrame(PlotView pv) {
		this.pv = pv;
		
		setSize(Plotter.DEFAULT_W + 35, Plotter.DEFAULT_H + 100);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				// FIXME more embedded magic numbers
				pv.resize(getWidth() - 35, getHeight() - 100);
			}
		});
		
		// Plow View window: Keybindings for movement and actions
		bindAction(this, "left",   "LEFT",  pv::moveLeft);
		bindAction(this, "right",  "RIGHT", pv::moveRight);
		bindAction(this, "up",     "UP",    pv::moveUp);
		bindAction(this, "down",   "DOWN",  pv::moveDown);

		bindAction(this, "plus",   "J", pv::zoomIn);
		bindAction(this, "minus",  "F", pv::zoomOut);
		bindAction(this, "splus",  "H", pv::smallZoomIn);
		bindAction(this, "sminus", "G", pv::smallZoomOut);

		bindAction(this, "rotAp",  "Q", pv::rotateXPlus);
		bindAction(this, "rotAm",  "A", pv::rotateXMinus);
		bindAction(this, "rotBp",  "W", pv::rotateYPlus);
		bindAction(this, "rotBm",  "S", pv::rotateYMinus);
		bindAction(this, "rotCp",  "E", pv::rotateZPlus);
		bindAction(this, "rotCm",  "D", pv::rotateZMinus);
		
		// --- Zoom Section ---
		zoomField = input().text("1").chars(4).numeric(true);
		zoomField.setFocusable(false);
		var zoomLabel = label(" X");
		var zoomInBtn = button()
			.icon("/zoom-in.png")
			.tooltip("Zoom In (x2)")
			.onClick(() -> {
				displayScale();
				pv.zoomIn();
			});
		var zoomOutBtn = button()
			.icon("/zoom-out.png")
			.tooltip("Zoom In (x0.5)")
			.onClick(() -> {
				displayScale();
				pv.zoomOut();
			});
		
		// disable growing
		zoomField.setMaximumSize(zoomField.getPreferredSize());

		// --- Rotation Section ---
		var axisSelector = new JComboBox<Axis>();
		axisSelector.setMaximumSize(axisSelector.getPreferredSize()); // disable growing
		axisSelector.setEnabled(false);
		rotationEnabled.onChange(enabled -> {
			axisSelector.setEnabled(enabled);
			axisSelector.removeAllItems();
			if (enabled) {
				var ptype = pv.getCurPlotType();
				if (ptype.isPresent()) {
					for (var axis : ptype.get().axes()) {
						axisSelector.addItem(axis);
					}
				}
			}
		});
		
		var rotateCWBtn = button()
			.icon("/rotate-cw.png")
			.tooltip("Rotate Clockwise")
			.enabled(rotationEnabled)
			.onClick(() -> {
				Axis axis = (Axis) axisSelector.getSelectedItem();
				if (axis == Axis.Cartesian.X) {
					pv.rotateXPlus();
				} else if (axis == Axis.Cartesian.Y) {
					pv.rotateYPlus();
				} else if (axis == Axis.Cartesian.Z) {
					pv.rotateZPlus();
				} else {
					// TODO polar axis rotations
				}
			});
		
		var rotateCCWBtn = button()
			.icon("/rotate-ccw.png")
			.tooltip("Rotate Counter-clockwise")
			.enabled(rotationEnabled)
			.onClick(() -> {
				Axis axis = (Axis) axisSelector.getSelectedItem();
				if (axis == Axis.Cartesian.X) {
					pv.rotateXMinus();
				} else if (axis == Axis.Cartesian.Y) {
					pv.rotateYMinus();
				} else if (axis == Axis.Cartesian.Z) {
					pv.rotateZMinus();
				} else {
					// TODO polar axis rotations
				}
			});

		var toolbar = toolbar(
			zoomInBtn,
			zoomField,
			zoomLabel,
			zoomOutBtn,
			
			// --- Separator ---
			Box.createRigidArea(new Dimension(10, 0)),
			
			rotateCWBtn,
			label("Axis:"),
			axisSelector,
			rotateCCWBtn
		);
		
		toolbar.setFloatable(false);
		toolbar.setRollover(true);

		add(toolbar, BorderLayout.NORTH);
		add(pv, BorderLayout.CENTER);
		
		this.resizable(true)
			.maximizable(true)
			.iconifiable(true)
			.closable(false);
	}
	
	private void displayScale() {
		zoomField.setText(String.format("%3.1f", pv.getScale()));
	}
	
	public void updateView(PlotData data) {
		pv.setCurPlot(data);
		pv.setCurPlotType(data.getPlotType());
		pv.fit();
		displayScale();
		rotationEnabled.set(data.getPlotType().dim() == 3);
	}
}
