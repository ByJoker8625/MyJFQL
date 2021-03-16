package org.jokergames.myjfql.event;

import org.jokergames.myjfql.event.listener.Listener;
import org.jokergames.myjfql.exception.EventException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        return Arrays.stream(listener.getClass().getMethods()).filter(method -> method.isAnnotationPresent(EventHandler.class)).map(method -> method.getAnnotation(EventHandler.class)).collect(Collectors.toList());
    }

    public List<EventHandler> getDeclarersByEvent(Event event) {
        return getDeclarersByType(event.getName());
    }

    public List<EventHandler> getDeclarersByType(String type) {
        return listeners.stream().map(this::getDeclarersByListener).flatMap(Collection::stream).filter(declarer -> declarer.type().equals(type)).collect(Collectors.toList());
    }

    public void callEvent(String type, Event event) {
        if (!events.contains(event.getName().toUpperCase())) {
            throw new EventException("Unknown event: " + event.getName());
        }

        listeners.forEach(listener -> {
            Arrays.stream(listener.getClass().getMethods()).filter(method -> method.isAnnotationPresent(EventHandler.class)).forEachOrdered(method -> {
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                if (eventHandler.type().equals(type)) {
                    try {
                        method.invoke(listener, event);
                    } catch (Exception ex) {
                        new EventException(ex).printStackTrace();
                    }
                }
            });
        });
    }

    public List<String> getEvents() {
        return events;
    }

    public List<Listener> getListeners() {
        return listeners;
    }
}
