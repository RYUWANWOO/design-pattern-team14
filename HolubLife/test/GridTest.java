import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    Clock clock = Clock.getInstance();
    Neighborhood oscillator = new Neighborhood(8, new Resident());
    Universe oscUniverse = new Universe(clock, oscillator);

    @BeforeEach
    void SetUp() {
        oscillator.setAmActive(true);
        ((Resident) oscillator.getGrid()[1][1]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][2]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][3]).setAmALive(true);
    }

    @DisplayName("Clear test")
    @Test
    void Clear() {
        oscUniverse.clear();

        assertAll(
                // After clear board
                () -> assertFalse(((Resident) oscillator.getGrid()[1][1]).isAmAlive()),
                () -> assertFalse(((Resident) oscillator.getGrid()[1][2]).isAmAlive()),
                () -> assertFalse(((Resident) oscillator.getGrid()[1][3]).isAmAlive())
        );
    }

    @DisplayName("Store and Load test")
    @Test
    void StoreAndLoad() {
        oscUniverse.doStore();
        oscUniverse.clear();
        oscUniverse.doLoad();

        assertAll(
                // After clear board
                () -> assertTrue(((Resident) oscillator.getGrid()[1][1]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[1][2]).isAmAlive()),
                () -> assertTrue(((Resident) oscillator.getGrid()[1][3]).isAmAlive())
        );
    }
}
