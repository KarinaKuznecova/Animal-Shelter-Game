package base.events;

import base.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static base.constants.Constants.EVENTS_INTERVAL;
import static base.constants.Constants.TEST_MAP_MODE;

public class EventService {

    protected static final Logger logger = LoggerFactory.getLogger(EventService.class);

    int ticks;

    List<Event> eventList = new ArrayList<>();

    public EventService() {
        eventList.add(new GrowFlowerEvent());
        eventList.add(new GrowRandomPlantEvent());
        if (!TEST_MAP_MODE) {
            eventList.add(new AdoptionNPCEvent());
            eventList.add(new WoodInForestEvent());
            eventList.add(new FeatherInForestEvent());
            eventList.add(new MushroomInForestEvent());
        }
    }

    public void update(Game game) {
        ticks++;
        if (ticks == EVENTS_INTERVAL) {
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
