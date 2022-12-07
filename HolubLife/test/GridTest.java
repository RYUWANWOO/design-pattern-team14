import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {
    @DisplayName("Clear Test")
    @Test
    void Clear() {
        Clock clock = Clock.getInstance();

        Neighborhood oscillator = new Neighborhood(8, new Resident());
        oscillator.setAmActive(true);
        ((Resident) oscillator.getGrid()[1][1]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][2]).setAmALive(true);
        ((Resident) oscillator.getGrid()[1][3]).setAmALive(true);

        Universe oscUniverse = new Universe(clock, oscillator);

        oscUniverse.clear();

        assertAll(
                // After clear board
                () -> assertFalse(((Resident) oscillator.getGrid()[1][1]).isAmAlive()),
                () -> assertFalse(((Resident) oscillator.getGrid()[1][2]).isAmAlive()),
                () -> assertFalse(((Resident) oscillator.getGrid()[1][3]).isAmAlive())
        );
    }






}
