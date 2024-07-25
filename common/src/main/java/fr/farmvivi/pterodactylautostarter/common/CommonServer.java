package fr.farmvivi.pterodactylautostarter.common;

import fr.farmvivi.pterodactylautostarter.common.ping.CommonServerPing;

/**
 * Represents a server.
 */
public interface CommonServer {
    /**
     * Returns the name of the server.
     *
     * @return the name of the server
     */
    String getName();

    /**
     * Returns the display name of the server.
     *
     * @return the display name of the server
     */
    String getDisplayName();

    /**
     * Returns player count.
     *
     * @return player count
     */
    int getPlayerCount();

    /**
     * Pings the server.
     *
     * @param callback the callback
     */
    void ping(Callback<CommonServerPing> callback);
}
