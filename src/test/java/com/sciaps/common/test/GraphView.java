package com.sciaps.common.test;

import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public class GraphView extends ApplicationFrame {

	public GraphView(String title, XYDataset dataset) {
		super(title);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
	}
	
	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart retval = ChartFactory.createXYLineChart("", 
				"Wavelength", 
				"Intensity", 
				dataset,
				PlotOrientation.VERTICAL,
				false, false, false);
		
		
		return retval;
	}

}
