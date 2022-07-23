package base.events;

import base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventService {

    protected static final Logger logger = LoggerFactory.getLogger(EventService.class);

    int ticks;
    int eventTime = 1000;

    List<Event> eventList = new ArrayList<>();

    public EventService() {
        eventList.add(new GrowFlowerEvent());
        eventList.add(new FindAnimalEvent());
        eventList.add(new NPCEvent());
        eventList.add(new GrowRandomPlantEvent());
    }

    public void update(Game game) {
        ticks++;
        if (ticks == eventTime) {
            logger.info("Event time");

            calculateChances(game);

            Event event = getEvent();
            if (event != null && event.getChance() > 0) {
                event.startEvent(game);
            }
            ticks = 0;
        }
    }

    Event getEvent() {
        int biggestChance = 0;
        Event eventWithBiggestChance = null;
        for (Event event : eventList) {
            if (event.getChance() > biggestChance) {
                biggestChance = event.getChance();
                eventWithBiggestChance = event;
            }
        }
        return eventWithBiggestChance;
    }

    void calculateChances(Game game) {
        for (Event event : eventList) {
            event.calculateChance(game);
        }
    }

}
