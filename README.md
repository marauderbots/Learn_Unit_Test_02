# Learn_Unit_Test_02
Introduction to Unit Testing - State Machine

A simple state machine with 6 active stages and a stop stage.

[OurStateMachine.java](src/OurStateMachine.java)

[RampStages.java](src/RampStages.java)

```
public void loop() {
	// Our should move logic all lives in shouldMoveStage()
	if (shouldMoveStage()) {
		// We need to move to the next stage
		currentStage = getNextRampState(currentStage);
	}

	// Just for example purposes, print our current state
	System.out.println(currentStage);
}
```


[TestStateMachine.java](test/TestStateMachine.java)

## Next Stage Test
Starting at Stage1 and ending on Stage6, this test verifies that our steps did not change.

```
void testGetNextRampState() {
	OurStateMachine osm = new OurStateMachine();

	assertEquals(osm.getNextRampState(RampStages.Stage1), RampStages.Stage2);
	assertEquals(osm.getNextRampState(RampStages.Stage2), RampStages.Stage3);
	assertEquals(osm.getNextRampState(RampStages.Stage3), RampStages.Stage4);
	assertEquals(osm.getNextRampState(RampStages.Stage4), RampStages.Stage5);
	assertEquals(osm.getNextRampState(RampStages.Stage5), RampStages.Stage6);
	assertEquals(osm.getNextRampState(RampStages.Stage6), RampStages.Stop);
}
```

## Should Move Stage Test
Verify based on the timeout that we do or do not step to the next stage.

```
void testShouldMoveStage() throws InterruptedException {
	OurStateMachine osm = new OurStateMachine();
	osm.setStageTimeout(200);
	
	// First run should always be false
	assertFalse(osm.shouldMoveStage());
	Thread.sleep(200);
	// If we wait past our stageTimeout we should move
	assertTrue(osm.shouldMoveStage());
	// Verify again that we are not moving on the next call
	assertFalse(osm.shouldMoveStage());
}
```

## Single Stage Step Test
Verify that we move to the next stage using the loop method with a forced timeout to simulate a call from a loop.

```
void testSingleStageStep() throws InterruptedException {
	OurStateMachine osm = new OurStateMachine(RampStages.Stage1);
	osm.setStageTimeout(200);
	assertEquals(osm.getCurrentStage(), RampStages.Stage1);

	osm.loop();
	Thread.sleep(400);
	osm.loop();

	assertEquals(osm.getCurrentStage(), RampStages.Stage2);
}
```

## Single Stage No Step Test
Verify that we do not move to the next step using the loop method where we have not reached the timeout.

```
void testSingleStage() throws InterruptedException {
	OurStateMachine osm = new OurStateMachine(RampStages.Stage1);
	osm.setStageTimeout(200);

	osm.loop();
	Thread.sleep(100);
	osm.loop();

	assertTrue(osm.getCurrentStage() == RampStages.Stage1);
}
```

## Full Stage Test
Simulate a full loop scenario where each loop is outside of the timeout. Each looop shoud move to a new stage. After the loop completes we verify that we exited due to a stop and not a loop counter.

```
void testFullStage() throws InterruptedException {
	// Our constants
	final int MAX_LOOP_COUNT = 1000;
	final int STAGE_TIMEOUT = 200;

	// Our loop safety net
	int loopCounter = 0;
	RampStages previousStage = null;

	OurStateMachine osm = new OurStateMachine(RampStages.Stage1);
	osm.setStageTimeout(STAGE_TIMEOUT);

	while (osm.getCurrentStage() != RampStages.Stop && loopCounter < MAX_LOOP_COUNT) {
		Thread.sleep(STAGE_TIMEOUT);
		osm.loop();

		// We should always step in this loop 
		// since we sleep the same amount of time as our timeout
		assertNotEquals(osm.getCurrentStage(), previousStage);
		previousStage = osm.getCurrentStage();

		loopCounter++;
	}
	
	// Verify that we exited on Stop
	assertEquals(osm.getCurrentStage(), RampStages.Stop);
	
	// If we exited on loop count, we had a problem
	assertTrue(loopCounter < MAX_LOOP_COUNT);
}
```
