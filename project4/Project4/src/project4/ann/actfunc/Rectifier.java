package project4.ann.actfunc;


public class Rectifier extends ActivationFunction {

	@Override
	public double f(double x) {
		return Math.max(0, x);
	}

}
