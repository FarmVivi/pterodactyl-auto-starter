package fr.farmvivi.pterodactylautostarter.common.event;

import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;

public class PlayerDisconnectEvent extends Event {
    private final CommonPlayer player;

    public PlayerDisconnectEvent(CommonPlayer player) {
        this.player = player;
    }

    public CommonPlayer getPlayer() {
        return player;
    }
}
