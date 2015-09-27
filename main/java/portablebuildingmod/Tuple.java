package portablebuildingmod;

/**
 * Tiny class to make ranges easier.
 *
 */
public class Tuple {
	/**
	 * Get an increasing range of numbers between [0,number] if
	 * <code>number</code> > 0, or [number + 1, 1] if <code>number</code> < 0.
	 * 
	 * @param number
	 *            One end of the range. Can be both positive and negative.
	 * @return An increasing range of numbers.
	 */
	public static Tuple getIncreasingRange(int number) {
		if (number > 0) {
			return new Tuple(0, number);
		} else if (number < 0) {
			return new Tuple(number + 1, 1);
		} else {
			return new Tuple(0, 0);
		}
	}

	protected int start, end;

	public Tuple(int start, int end) {
		this.start = start;
		this.end = end;
	}
}
