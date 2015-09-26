package portablebuildingmod;

/**
 * Describes the relative direction an object is facing.
 *
 */
public enum RelativeFacing {
	NONE, DOWN, UP, AWAY, LEFT, TOWARDS, RIGHT;

	public static RelativeFacing[] HORIZONTALS = { AWAY, LEFT, TOWARDS, RIGHT };
}
