package org.jokergames.jfql.event;

import org.jokergames.jfql.event.listener.Listener;
import org.jokergames.jfql.exception.EventException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class EventService {

    private final List<Listener> listeners;
    private final List<String> events;

    public EventService() {
        this.listeners = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public void registerEvent(String event) {
        events.add(event.toUpperCase());
    }

    public void unregisterEvent(String event) {
        events.remove(event.toUpperCase());
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public List<EventHandler> getDeclarersByListener(Listener listener) {
        final List<EventHandler> declarers = new ArrayList<>();
        Class<?> clazz = listener.getClass();

        for (Method method : clazz.getMethods()) {

            if (method.isAnnotationPresent(EventHandler.class)) {
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                declarers.add(eventHandler);
            }
        }

        return declarers;
    }

    public List<EventHandler> getDeclarersByEvent(Event event) {
        return getDeclarersByType(event.getName());
    }

    public List<EventHandler> getDeclarersByType(String type) {
        List<EventHandler> declarers = new ArrayList<>();

        for (Listener listener : listeners) {
            List<EventHandler> eventHandlers = getDeclarersByListener(listener);

            for (EventHandler declarer : eventHandlers) {
                if (declarer.type().equals(type)) {
                    declarers.add(declarer);
                }
            }

        }

        return declarers;
    }

    public void callEvent(String type, Event event) {
        if (!events.contains(event.getName().toUpperCase())) {
            throw new EventException("Unknown event: " + event.getName());
        }

        for (Listener listener : listeners) {
            Class<?> clazz = listener.getClass();

            for (Method method : clazz.getMethods()) {

                if (method.isAnnotationPresent(EventHandler.class)) {
                    EventHandler eventHandler = method.getAnnotation(EventHandler.class);

                    if (eventHandler.type().equals(type)) {
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

    public List<String> getEvents() {
        return events;
    }

    public List<Listener> getListeners() {
        return listeners;
    }
}
