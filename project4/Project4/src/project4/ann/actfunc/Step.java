package project4.ann.actfunc;


public class Step extends ActivationFunction {
	
	protected Step() {
	}

	@Override
	public double f(double x) {
		return x > 0 ? 1 : 0;
	}

}
