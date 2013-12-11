package com.sciaps.common.math;

public class CorrelationCoefficient {

	private float[] a;
	private float abar;

	public CorrelationCoefficient(float[] a) {
		this.a = a;
		this.abar = mean(a, 0, a.length);
	}
	
	public float[] filter(float[] y) {
		float[] out = new float[y.length];
		
		
		for(int x=0;x<y.length-a.length;x++){
			double top = 0, bottom1 = 0, bottom2 = 0;
			double ybar = mean(y, x, a.length);
			
			for(int i=0;i<a.length;i++){
				top += (a[i] - abar)*(y[i+x] - ybar);
				
				bottom1 += (a[i] - abar)*(a[i] - abar);
				bottom2 += (y[i+x] - ybar)*(y[i+x] - ybar);
			}
			
			out[x] = (float) (top / Math.sqrt(bottom1 * bottom2));
		}
		
		return out;
	}
	
	private static float mean(float[] vector, int start, int len) {
		double sum = 0;
		for(int i=start;i<start+len;i++) {
			sum += vector[i];
		}
		return (float) (sum / len);
	}
}
