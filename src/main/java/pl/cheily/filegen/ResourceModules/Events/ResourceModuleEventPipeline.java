package pl.cheily.filegen.ResourceModules.Events;

import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ResourceModuleEventPipeline {
    private final PropertyChangeSupport pcs;

    public ResourceModuleEventPipeline() {
        pcs = new PropertyChangeSupport(this);
    }

    public void push(ResourceModuleEventType event, ResourceModule module) {
        pcs.firePropertyChange(event.toString(), null, module);
    }

    public void subscribe(ResourceModuleEventType eventType, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(eventType.toString(), listener);
    }

    public void subscribeToAll(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void unsubscribe(ResourceModuleEventType eventType, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(eventType.toString(), listener);
    }

    public void unsubscribeFromAll(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

}
