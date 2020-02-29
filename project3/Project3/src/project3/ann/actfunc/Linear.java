package project3.ann.actfunc;


public class Linear extends ActivationFunction {
	
	public static final double K = 1.0;

	@Override
	public double f(double x) {
		return K*x;
	}

}
