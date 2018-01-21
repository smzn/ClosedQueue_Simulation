package closedqueuesimulation;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Graph extends Frame implements ActionListener,WindowListener{
	
	public Graph(ClosedQueue_simulation qsim, int node_index[]) {
		addWindowListener(this);
		this.setTitle("ClosedQueue");
		
		ArrayList<Integer> timequeue[] = qsim.getTimequeue();
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		for(int i = 0; i < 20000; i++) {
			data.addValue(timequeue[0].get(i), node_index[0]+"", i+"");
			data.addValue(timequeue[1].get(i), node_index[1]+"", i+"");
			data.addValue(timequeue[2].get(i), node_index[2]+"", i+"");
			data.addValue(timequeue[3].get(i), node_index[3]+"", i+"");
			//data.addValue(timequeue[4].get(i), node_index[4]+"", i+"");
			//data.addValue(timequeue[5].get(i), node_index[5]+"", i+"");
		}
	    JFreeChart chart = ChartFactory.createLineChart("Closed Queue","Time","Customer",data,PlotOrientation.VERTICAL,true,false,false);
	    ChartPanel cpanel = new ChartPanel(chart);
	    add(cpanel, BorderLayout.CENTER);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
