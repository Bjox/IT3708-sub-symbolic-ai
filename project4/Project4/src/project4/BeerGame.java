package project4;

import static project4.ea.problems.BeerTrackerConfig.*;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class BeerGame {
	
	public static final int MAX_TIMESTEP = 600;
	
	/** The falling object. */
	private int object = 0;
	
	/** Height of the falling object. 0 = top, 14 = bottom. */
	private int object_h = 0;
	
	/** Size of current object. */
	private int object_len = 0;
	
	/** The player tracker. */
	private int tracker = 31; // Tracker start pos far right: 0...00011111
	
	/** Current game timestep. */
	private int timestep = 0;
	
	/** Total number of captures performed. */
	private int captures = 0;
	
	/** Total number of dodges performed (avoidance). */
	private int dodges = 0;
	
	/** Number of small objects (1-4) spawned during the run. */
	private int smallObjects = 0;
	
	/** Number of big objects (5-6) spawned during the run. */
	private int bigObjects = 0;
	
	/** Score. */
	private double score = 0;
	
	/** String desc of last event. */
	private String lastEvent = "";
	
	/** Active scenario mode. */
	private final int scenario;
	
	/* Last action performed. */
	private Action lastAction = Action.NOTHING;
	
	/**
	 * 
	 * @param startPos The start position of the tracker.
	 * @param scenario
	 */
	public BeerGame(int startPos, int scenario) {
		this.scenario = scenario;
		tracker = rotl(tracker, startPos);
		spawnObject();
	}
	
	public void reset(int startPos) {
		tracker      = rotl(31, startPos);
		timestep     = 0;
		captures     = 0;
		dodges       = 0;
		smallObjects = 0;
		bigObjects   = 0;
		score        = 0;
		spawnObject();
	}
	
	public boolean tick() {
		if (hasEnded()) return false;
		
		if (object_h == 14) spawnObject(); // Spawn new object
		else object_h++; // Make the object fall
		
		checkObject();
		
		timestep++;
		return true;
	}
	
	private void checkObject() {
		if (object_h == 14) {
			int colMask = tracker & object & 0x3FFF_FFFF; // Merge detection
			
			switch (scenario) {
				case SCENARIO_STANDARD: scoreStandard(colMask); break;
				case SCENARIO_PULL: scorePull(colMask); break;
				case SCENARIO_NOWRAP: scoreNowrap(colMask); break;
				default: throw new RuntimeException("Invalid scenario.");
			}
			
			object = 0;
		}
	}
	
	private void scoreStandard(int colMask) {
		// STANDARD
		if (colMask == object) { // Capture
			captures++;
			lastEvent = "CAPTURED";

			if (object_len <= 4) { // Small 
				score += 0.5;
			} else {              // Big
				score -= 0.5;
			}
		}
		else if (colMask == 0) { // Dodge
			dodges++;
			lastEvent = "AVOIDED";

			if (object_len > 4) { // Big
				score += 0.5;
			} else {             // Small
				score -= 1;
			}
		}
		else {                    // Partially
			lastEvent = "PARTIALLY";
			score -= 0.5;
		}
	}
	
	private void scorePull(int colMask) {
		// PULL
		if (colMask == object) { // Capture
			captures++;
			lastEvent = "CAPTURED";

			if (object_len <= 4) { // Small 
				score += 0.5;
				if (lastAction == Action.PULL) score += 0.3;
			} else {              // Big
				score -= 0.5;
			}
		}
		else if (colMask == 0) { // Dodge
			dodges++;
			lastEvent = "AVOIDED";

			if (object_len > 4) { // Big
				score += 0.5;
				if (lastAction == Action.PULL) score += 0.9;
			} else {             // Small
				score -= 1;
			}
		}
		else {                    // Partially
			lastEvent = "PARTIALLY";
			score -= 0.5;
		}
	}
	
	private void scoreNowrap(int colMask) {
		// NOWRAP
		if (colMask == object) { // Capture
			captures++;
			lastEvent = "CAPTURED";

			if (object_len <= 4) { // Small
				score += 0.5;
			} else {             // Big
				score -= 0.5;
			}
		}
		else if (colMask == 0) { // Dodge
			dodges++;
			lastEvent = "AVOIDED";

			if (object_len > 4) { // Big
				score += 0.7;
			} else {             // Small
				score -= 1;
			}
		}
		else {                 // Partially
			lastEvent = "PARTIALLY";
			
			if (object_len > 4) {// Big
				score -= 0.8;
			} else {
				score -= 0.5;
			}
			
		}
	}
	
	public void performAction(Action agentAction) {
		lastAction = agentAction;
		
		switch (agentAction) {
			case LEFT:
				moveLeft();
				break;
			case RIGHT:
				moveRight();
				break;
			case PULL:
				if (scenario != SCENARIO_PULL) throw new RuntimeException("Pull action performed on non-pull scenario!");
				if (object != 0) {
					object_h = 14;
					checkObject();
				}
				break;
		}
	}
	
	public void setSensorVector(Vector sensorVec) {
		int trackCpy = tracker;
		int objCpy = object;
		
		if ((trackCpy & 0x2000_0001) == 0x2000_0001) { // Rotate away from wrap around
			trackCpy = rotl(trackCpy, 4);
			objCpy = rotl(objCpy, 4);
		}
		
		int trackLastPos = trackCpy;
		trackCpy = trackCpy / (trackCpy & -trackCpy); // Shift to rightmost position
		trackLastPos = ~trackLastPos & (trackLastPos - 1); // Mask for bits below lowest bit set
		objCpy = rotr(objCpy, popcnt(trackLastPos)); // Rotate obj to correct position
		
		int colMask = trackCpy & objCpy & 0x3FFF_FFFF;
		for (int i = 0; i < 5; i++) {
			sensorVec.value[4-i] = (colMask & (1 << i)) >>> i;
		}
		
		if (scenario == SCENARIO_NOWRAP) {
			// 5=left sensor, 6=right sensor
			sensorVec.value[5] = (tracker >>> 29) & 1;
			sensorVec.value[6] = tracker & 1;
		}
	}
	
	public Action getLastAction() {
		return lastAction;
	}
	
	public boolean hasEnded() {
		return timestep == MAX_TIMESTEP;
	}
	
	public int getTimestep() {
		return timestep;
	}
	
	public int getCaptures() {
		return captures;
	}

	public int getDodges() {
		return dodges;
	}

	public int getSmallObjects() {
		return smallObjects;
	}

	public int getBigObjects() {
		return bigObjects;
	}
	
	public int getTracker() {
		return tracker;
	}
	
	public int getObject() {
		return object;
	}
	
	public int getObjectHeight() {
		return object_h;
	}
	
	public double getScore() {
		return score;
	}

	public int getObjectLen() {
		return object_len;
	}
	
	
	private void moveLeft() {
		if (scenario == SCENARIO_NOWRAP && tracker == 0x3E00_0000) return;
		tracker = rotl(tracker, 1);
	}
	
	private void moveRight() {
		if (scenario == SCENARIO_NOWRAP && tracker == 0x1F) return;
		tracker = rotr(tracker, 1);
	}
	
	/**
	 * Resets the object height and spawns a new 
	 * object of random size at a random location.
	 */
	private void spawnObject() {
		object_h = 0;
		
		object_len = Util.randomInt(1, 7); // Size in range [1,6]
		int pos = Util.randomInt(0, 31 - (scenario == SCENARIO_NOWRAP ? object_len : 1)); // Pos in range [0,29]
		
		object = (1 << object_len) - 1;
		object = rotl(object, pos);
		
		if (object_len <= 4) smallObjects++;
		else bigObjects++;
	}
	
	/**
	 * Rotate right.
	 * @param v
	 * @param shift
	 * @return 
	 */
	private static int rotr(int v, int shift) {
		return ((v >>> shift) | (v << (30 - shift))) & 0x3FFF_FFFF;
	}
	
	/**
	 * Rotate left.
	 * @param v
	 * @param shift
	 * @return 
	 */
	private static int rotl(int v, int shift) {
		return ((v << shift) | (v >>> (30 - shift))) & 0x3FFF_FFFF;
	}
	
	/**
	 * Population count.
	 * @param i
	 * @return 
	 */
	private static int popcnt(int i) {
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
	}
	
	
	public String getInfoStr() {
		return String.format("Timestep: %1$-10sScore: %2$-10sCaptures: %3$-10sAvoidance: %4$-10sLast event: %5$-10s", timestep, String.format("%.1f", score), captures, dodges, lastEvent);
	}

	@Override
	public String toString() {
		String infoStr = getInfoStr();
		
		String trackStr = Integer.toBinaryString(tracker);
		while (trackStr.length() < 32) {
			trackStr = "0" + trackStr;
		}
		trackStr = trackStr.substring(2);
		trackStr = trackStr.replaceAll("0", ".");
		trackStr = trackStr.replaceAll("1", "#");
		
		String objectStr = Integer.toBinaryString(object);
		while (objectStr.length() < 32) {
			objectStr = "0" + objectStr;
		}
		objectStr = objectStr.substring(2);
		objectStr = objectStr.replaceAll("0", ".");
		objectStr = objectStr.replaceAll("1", "#");
		
		return infoStr + "\n" + objectStr + "\n" + (13 - object_h) + "\n" + trackStr + "\n";
	}
	
	
	
	public static enum Action {
		LEFT, RIGHT, NOTHING, PULL;
	}
	
}
