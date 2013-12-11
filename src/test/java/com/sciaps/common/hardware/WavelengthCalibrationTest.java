package com.sciaps.common.hardware;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jfree.chart.demo.BarChartDemo1;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;
import org.junit.Before;
import org.junit.Test;

import com.sciaps.common.math.GaussianFunction;
import com.sciaps.common.math.MagicFunctionMatcher;
import com.sciaps.common.math.SumFunction;
import com.sciaps.common.math.MagicFunctionMatcher.KeyPoint;
import com.sciaps.common.test.GraphView;

public class WavelengthCalibrationTest {



	private DefaultXYDataset mDataset;

	public double[] buildArray(int from, int len){
		double[] retval = new double[len];
		for(int i=0;i<retval.length;i++){
			retval[i] = i;
		}
		return retval;
	}

	public double[] copy(int[] in) {
		double[] retval = new double[in.length];
		for(int i=0;i<in.length;i++){
			retval[i] = in[i];
		}
		return retval;
	}
	
	public double[][] createDataset(double[] data) {
		double[][] retval = new double[2][data.length];
		
		for(int i=0;i<data.length;i++){
			retval[0][i] = i;
			retval[1][i] = data[i];
		}
		
		return retval;
	}
	
	@Before
	public void setup() {
		mDataset = new DefaultXYDataset();
	}

	@Test
	public void merceryTest() throws Exception {

		RawSpectromerCSVFile rawspectrometerfile = new RawSpectromerCSVFile(new File("data/mercerylibsrawspectromers.csv"));
		double[] rawBuffer = copy(Utils.loadRawPixels(rawspectrometerfile.loadSpectromerter(0)));
		
		
		double[] idealraw;
		{
			UnivariateFunction ideaMercery = new SumFunction(new UnivariateFunction[] {
					new GaussianFunction(10000, 253.65, 0.03, 0),
					new GaussianFunction(10000, 184.95, 0.03, 0),
					new GaussianFunction(10000, 237.83, 0.03, 0),
					new GaussianFunction(10000, 194.23, 0.03, 0),
			});
			
			idealraw = new double[2096];
			final double xdiff = (260.0 - 185.0) / idealraw.length;
			for(int i=0;i<idealraw.length;i++){
				idealraw[i] = ideaMercery.value(i*xdiff + 185);
			}
		}

		MagicFunctionMatcher m = new MagicFunctionMatcher(rawBuffer, idealraw, 2);
		
		
		
		//mDataset.addSeries(1, createDataset(rawBuffer));
		mDataset.addSeries(2, createDataset(idealraw));
		
		double[][] calibratds = new double[2][rawBuffer.length];
		for(int i=0;i<rawBuffer.length;i++){
			calibratds[0][i] = m.getXmapper().value(i);
			calibratds[1][i] = rawBuffer[i];
		}
		mDataset.addSeries(3, calibratds);

		GraphView demo = new GraphView("Mercery Test", mDataset);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		
		System.out.println("done");

		//Spectrometer spec = new Spectrometer(new FloatRange(185, 260), wavelengthmapping);

	}
}
