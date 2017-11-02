import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestStateMachine {

	@BeforeAll
	/**
	 * This method will run once at the beginning
	 * 
	 * @throws Exception
	 */
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	/**
	 * This method will run once at the end
	 * 
	 * @throws Exception
	 */
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	/**
	 * This method will run once before each @Test method
	 * 
	 * @throws Exception
	 */
	void setUp() throws Exception {
	}

	@AfterEach
	/**
	 * This method will run once after each @Test method
	 * 
	 * @throws Exception
	 */
	void tearDown() throws Exception {
	}

	@Test
	/**
	 * Test getNextRampState(RampStages rampStage) with each known stage
	 */
	void testGetNextRampState() {
		OurStateMachine osm = new OurStateMachine();

		assertEquals(osm.getNextRampState(RampStages.Stage1), RampStages.Stage2);
		assertEquals(osm.getNextRampState(RampStages.Stage2), RampStages.Stage3);
		assertEquals(osm.getNextRampState(RampStages.Stage3), RampStages.Stage4);
		assertEquals(osm.getNextRampState(RampStages.Stage4), RampStages.Stage5);
		assertEquals(osm.getNextRampState(RampStages.Stage5), RampStages.Stage6);
		assertEquals(osm.getNextRampState(RampStages.Stage6), RampStages.Stop);
	}

	@Test
	/**
	 * Test shouldMoveStage()
	 * @throws InterruptedException
	 */
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

	@Test
	/**
	 * Test loop() with a single stage step
	 * @throws InterruptedException
	 */
	void testSingleStageStep() throws InterruptedException {
		OurStateMachine osm = new OurStateMachine(RampStages.Stage1);
		osm.setStageTimeout(200);
		assertEquals(osm.getCurrentStage(), RampStages.Stage1);

		osm.loop();
		Thread.sleep(400);
		osm.loop();

		assertEquals(osm.getCurrentStage(), RampStages.Stage2);
	}

	@Test
	/**
	 * Test loop() that should not step
	 * @throws InterruptedException
	 */
	void testSingleStage() throws InterruptedException {
		OurStateMachine osm = new OurStateMachine(RampStages.Stage1);
		osm.setStageTimeout(200);

		osm.loop();
		Thread.sleep(100);
		osm.loop();

		assertTrue(osm.getCurrentStage() == RampStages.Stage1);
	}

	@Test
	/**
	 * This test simulates a slow loop that always takes us to the next stage
	 * @throws InterruptedException
	 */
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
}
