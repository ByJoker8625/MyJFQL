package de.jokergames.jfql.event;

import de.jokergames.jfql.event.listener.Listener;
import de.jokergames.jfql.exception.EventException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class EventService {

    private final List<Listener> listeners;

    public EventService() {
        listeners = new ArrayList<>();
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public void callEvent(String type, Event event) {
        for (Listener listener : listeners) {
            Class<?> clazz = listener.getClass();

            for (Method method : clazz.getMethods()) {

                if (method.isAnnotationPresent(EventDeclarer.class)) {
                    EventDeclarer eventDeclarer = method.getAnnotation(EventDeclarer.class);

                    if (eventDeclarer.type().equals(type)) {
                        try {
                            method.invoke(listener, event);
                        } catch (Exception ex) {
                            new EventException(ex).printStackTrace();
                        }
                    }
                }
            }

        }
    }

    public List<Listener> getListeners() {
        return listeners;
    }
}
