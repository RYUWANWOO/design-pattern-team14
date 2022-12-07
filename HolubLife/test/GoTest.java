import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoTest {
    @DisplayName("Still lifes single tick test")
    @Test
    void StillSiStngleTick() {
        Clock clock = Clock.getInstance();

        Neighborhood still = new Neighborhood(8, new Resident());
        still.setAmActive(true);
        ((Resident) still.getGrid()[1][1]).setAmALive(true);
        ((Resident) still.getGrid()[1][2]).setAmALive(true);
        ((Resident) still.getGrid()[2][1]).setAmALive(true);
        ((Resident) still.getGrid()[2][2]).setAmALive(true);

        Universe oscUniverse = new Universe(clock, still);
        clock.tick();

        assertAll(
                // It should same before and after
                () -> assertTrue(((Resident) still.getGrid()[1][1]).isAmAlive()),
                () -> assertTrue(((Resident) still.getGrid()[1][2]).isAmAlive()),
                () -> assertTrue(((Resident) still.getGrid()[2][1]).isAmAlive()),
                () -> assertTrue(((Resident) still.getGrid()[2][2]).isAmAlive())
        );
    }

    @DisplayName("Oscillator single tick test")
    @Test
    void OscillatorSingleTick() {
        Clock clock = Clock.getInstance();

        Neighborhood oscillator = new Neighborhood(8, new Resident());
        oscillator.setAmActive(true);
        ((Resident) oscillator.getGrid()[1][1]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][2]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][3]).setAmALive(true);

        Universe oscUniverse = new Universe(clock, oscillator);
        clock.tick();

        assertAll(
                // Before single tick
                () -> assertFalse(((Resident) oscillator.getGrid()[1][1]).isAmAlive()),
                () -> assertFalse(((Resident) oscillator.getGrid()[1][3]).isAmAlive()),

                // After single tick
                () -> assertTrue(((Resident) oscillator.getGrid()[0][2]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[1][2]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[2][2]).isAmAlive())
        );
    }

    @DisplayName("Oscillator double tick test")
    @Test
    void OscillatorDoubleTick() {
        Clock clock = Clock.getInstance();

        Neighborhood oscillator = new Neighborhood(8, new Resident());
        oscillator.setAmActive(true);
        ((Resident) oscillator.getGrid()[1][1]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][2]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][3]).setAmALive(true);

        Universe oscUniverse = new Universe(clock, oscillator);
        clock.tick();
        clock.tick();

        assertAll(
                // After double tick
                () -> assertTrue(((Resident) oscillator.getGrid()[1][1]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[1][2]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[1][3]).isAmAlive())
        );
    }

    @DisplayName("Oscillator halt test")
    @Test
    void OscillatorHalt() {
        Clock clock = Clock.getInstance();

        Neighborhood oscillator = new Neighborhood(8, new Resident());
        oscillator.setAmActive(true);
        ((Resident) oscillator.getGrid()[1][1]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][2]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][3]).setAmALive(true);

        Universe oscUniverse = new Universe(clock, oscillator);
        clock.startTicking(0);

        assertAll(
                // No difference
                () -> assertTrue(((Resident) oscillator.getGrid()[1][1]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[1][2]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[1][3]).isAmAlive())
        );
    }
}
