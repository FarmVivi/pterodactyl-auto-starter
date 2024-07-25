package fr.farmvivi.pterodactylautostarter.common.event;

import fr.farmvivi.pterodactylautostarter.common.CommonPlayer;
import fr.farmvivi.pterodactylautostarter.common.CommonServer;

public class ServerConnectEvent extends Event implements CancellableEvent {
    private final CommonPlayer player;

    private CommonServer target;

    private boolean cancelled = false;

    public ServerConnectEvent(CommonPlayer player, CommonServer target) {
        this.player = player;
        this.target = target;
    }

    public CommonPlayer getPlayer() {
        return player;
    }

    public CommonServer getTarget() {
        return target;
    }

    public void setTarget(CommonServer target) {
        this.target = target;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
