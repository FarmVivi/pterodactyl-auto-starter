package fr.farmvivi.pterodactylautostarter.common;

import fr.farmvivi.pterodactylautostarter.common.listener.EventListener;

import java.io.File;

/**
 * Represents the plugin.
 */
public interface CommonPlugin {
    /**
     * Returns the data folder.
     *
     * @return the data folder
     */
    File getDataFolder();

    /**
     * Adds an event listener.
     *
     * @param eventListener the event listener
     */
    void addEventListener(EventListener eventListener);

    /**
     * Removes an event listener.
     *
     * @param eventListener the event listener
     */
    void removeEventListener(EventListener eventListener);
}
