import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;
import com.holub.life.model.Cell;

import com.holub.life.view.UniverseView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TickTest {
    @Test
    void SingleTick() {
        Neighborhood grid = new Neighborhood(8, new Resident());
        grid.setAmActive(true);
        ((Resident)grid.getGrid()[1][0]).setAmALive(true);
        ((Resident)grid.getGrid()[1][1]).setAmALive(true);
        ((Resident)grid.getGrid()[1][2]).setAmALive(true);

        Clock clock = Clock.getInstance();
        Universe universe = new Universe(clock,grid);
        UniverseView universeView = new UniverseView(universe,grid);
        clock.tick();

        assertTrue(((Resident) grid.getGrid()[0][0]).isAmAlive());
    }
}
