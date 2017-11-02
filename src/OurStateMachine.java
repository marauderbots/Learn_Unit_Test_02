
/**
 * A very basic state machine built
 * @author jason
 *
 */
public class OurStateMachine {

	private RampStages currentStage = RampStages.Stop;
	private long stageTimeout = 200;
	private long lastStageChange;

	/**
	 * This allows us to test the state machine
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// The loop counter is our safety net to prevent an infinite loop
		int loopCounter = 0;
		final int MAX_LOOP_COUNT = 1000;

		OurStateMachine ourStateMachine = new OurStateMachine(RampStages.Stage1);

		while (ourStateMachine.getCurrentStage() != RampStages.Stop && loopCounter < MAX_LOOP_COUNT) {
			ourStateMachine.loop();
			
			// Simulate 50mhz
			Thread.sleep(20);

			// Increment the counter
			loopCounter++;
		}
	}

	/**
	 * Default constructor
	 */
	public OurStateMachine() {
	}
	
	/**
	 * Construct with an initial state
	 * @param currentState
	 */
	public OurStateMachine(RampStages currentState) {
		this.currentStage = currentState;
	}
	
	/**
	 * Our simple state machine moves at a fixed interval.
	 * This works for our example, but in practice won't be ideal.
	 * @return
	 */
	public boolean shouldMoveStage() {
		boolean moveStage = false;
		long currentTime = System.currentTimeMillis();

		if (lastStageChange == 0) {
			lastStageChange = currentTime;
		}

		if ((currentTime - lastStageChange) > stageTimeout) {
			moveStage = true;
			this.lastStageChange = currentTime;
		}

		return moveStage;
	}

	/**
	 * This method is meant to be called in a loop
	 */
	public void loop() {
		// Our should move logic all lives in shouldMoveStage()
		if (shouldMoveStage()) {
			// We need to move to the next stage
			currentStage = getNextRampState(currentStage);
		}

		// Just for example purposes, print our current state
		System.out.println(currentStage);
	}

	/**
	 * Given a current state, this method returns the next
	 * @param rampStage
	 * @return
	 */
	public RampStages getNextRampState(RampStages rampStage) {
		switch (rampStage) {
		case Stage1:
			return RampStages.Stage2;
		case Stage2:
			return RampStages.Stage3;
		case Stage3:
			return RampStages.Stage4;
		case Stage4:
			return RampStages.Stage5;
		case Stage5:
			return RampStages.Stage6;
		case Stage6:
			return RampStages.Stop;
		case Stop:
			// This will fall through to Stop
		default:
			return RampStages.Stop;
		}
	}

	/**
	 * @return the currentStage
	 */
	public RampStages getCurrentStage() {
		return currentStage;
	}

	/**
	 * @param currentStage the currentStage to set
	 */
	public void setCurrentStage(RampStages currentStage) {
		this.currentStage = currentStage;
	}

	/**
	 * @return the stageTimeout
	 */
	public long getStageTimeout() {
		return stageTimeout;
	}

	/**
	 * @param stageTimeout the stageTimeout to set
	 */
	public void setStageTimeout(long stageTimeout) {
		this.stageTimeout = stageTimeout;
	}
}
