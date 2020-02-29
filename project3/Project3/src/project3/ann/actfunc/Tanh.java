package project3.ann.actfunc;


public class Tanh extends ActivationFunction {

	@Override
	public double f(double x) {
		return Math.expm1(2 * x) / (Math.exp(2 * x) + 1);
	}

}
