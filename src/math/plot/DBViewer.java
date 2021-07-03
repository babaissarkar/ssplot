package math.plot;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class DBViewer extends JFrame {
    public Vector<Vector<Double>> dataset;
    
    public int colNo = 0;
    public int rowNo = 0;

    public JTable table;
    public JButton btnPlot;
    private JTextField tfXData, tfYData;
    
	public DBViewer(Vector<Vector<Double>> data) {
        setData(data);

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
        
		Vector<String> headers = new Vector<String>();
        for (int i = 1; i <= colNo; i++) {
            headers.add("Column " + i);
		}
        
		if (80*colNo > 500) {
            setSize(80*colNo, 600);
        } else {
            setSize(500, 600);
        }
        
		table = new JTable(dataset, headers);
		table.setAutoCreateRowSorter(true);
		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < colNo; i++) {
            columns.getColumn(i).setPreferredWidth(20);
		}
		JScrollPane scroll = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
				);
        JPanel pnlTable = new JPanel();
        pnlTable.add(scroll);
        pnlTable.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        
        getContentPane().add(pnlPrefs, BorderLayout.NORTH);
		getContentPane().add(pnlTable, BorderLayout.CENTER);
	}

    public void setData(Vector<Vector<Double>> data) {
        dataset = data;
        colNo = data.get(0).size();
        rowNo = data.size();

        /* Update the table */
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
}
