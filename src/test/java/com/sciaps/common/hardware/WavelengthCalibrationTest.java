package com.sciaps.common.hardware;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jfree.chart.demo.BarChartDemo1;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;
import org.junit.Before;
import org.junit.Test;

import com.sciaps.common.GaussianFunction;
import com.sciaps.common.MagicFunctionMatcher;
import com.sciaps.common.SumFunction;
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

	public double[] copy(short[] in) {
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
		short[] rawBuffer = Utils.loadRawPixels(rawspectrometerfile.loadSpectromerter(0));
		
		mDataset.addSeries(0, createDataset(copy(rawBuffer)));

		MagicFunctionMatcher m = new MagicFunctionMatcher(null, null);
		mDataset.addSeries(1, createDataset(m.getScaleSpace(copy(rawBuffer), 12f)));
		mDataset.addSeries(2, createDataset(m.getScaleSpace(copy(rawBuffer), 24f)));
		
		
		
		double[] diff = m.diffGaussian(copy(rawBuffer), 12, 3, 3);
		mDataset.addSeries(3, createDataset(diff));
		ArrayList<Integer> keypoints = m.getKeyPoints(diff);
		
		

		PolynomialSplineFunction spline = new SplineInterpolator().interpolate(buildArray(0, rawBuffer.length), copy(rawBuffer));
		//spline.derivative().

		UnivariateFunction ideaMercery = new SumFunction(new UnivariateFunction[] {
				new GaussianFunction(1, 253.65, 0.03, 0),
				new GaussianFunction(1, 184.95, 0.03, 0),
				new GaussianFunction(1, 237.83, 0.03, 0),
				new GaussianFunction(1, 194.23, 0.03, 0),
		});

		GraphView demo = new GraphView("Mercery Test", mDataset);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		
		System.out.println("done");

		//Spectrometer spec = new Spectrometer(new FloatRange(185, 260), wavelengthmapping);

	}
}
