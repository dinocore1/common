package com.sciaps.common.hardware;

import java.io.File;
import java.nio.ByteBuffer;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jfree.chart.demo.BarChartDemo1;
import org.jfree.ui.RefineryUtilities;
import org.junit.Test;

import com.sciaps.common.GaussianFunction;
import com.sciaps.common.MagicFunctionMatcher;
import com.sciaps.common.SumFunction;

public class WavelengthCalibrationTest {

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
	
	@Test
	public void merceryTest() throws Exception {
		
		BarChartDemo1 demo = new BarChartDemo1("Bar Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        
		
		RawSpectromerCSVFile rawspectrometerfile = new RawSpectromerCSVFile(new File("data/mercerylibsrawspectromers.csv"));
		
		short[] rawBuffer = Utils.loadRawPixels(rawspectrometerfile.loadSpectromerter(0));
		
		MagicFunctionMatcher m = new MagicFunctionMatcher(null, null);
		double[] diff = m.diffGaussian(copy(rawBuffer), 1, 3, 3);
		
		PolynomialSplineFunction spline = new SplineInterpolator().interpolate(buildArray(0, rawBuffer.length), copy(rawBuffer));
		//spline.derivative().
		
		UnivariateFunction ideaMercery = new SumFunction(new UnivariateFunction[] {
				new GaussianFunction(1, 253.65, 0.03, 0),
				new GaussianFunction(1, 184.95, 0.03, 0),
				new GaussianFunction(1, 237.83, 0.03, 0),
				new GaussianFunction(1, 194.23, 0.03, 0),
		});
		
		
		
		//Spectrometer spec = new Spectrometer(new FloatRange(185, 260), wavelengthmapping);
		
	}
}
