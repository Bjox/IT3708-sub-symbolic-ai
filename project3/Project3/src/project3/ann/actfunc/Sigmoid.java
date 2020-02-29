package project3.ann.actfunc;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Sigmoid extends ActivationFunction {

	protected Sigmoid() {
	}
	
	public static final double K = 1.0;
	
	@Override
	public double f(double x) {
		return 1.0 / (1.0 + Math.exp(-K*x));
	}

}
