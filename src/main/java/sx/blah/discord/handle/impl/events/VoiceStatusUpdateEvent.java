package sx.blah.discord.handle.impl.events;

import sx.blah.discord.handle.IEvent;
import sx.blah.discord.handle.obj.User;

/**
 * Created by hetoan2 on 10/23/2015.
 */
public class VoiceStatusUpdateEvent implements IEvent {
    private final User userJoined;
    private boolean isSuppressed;

    public VoiceStatusUpdateEvent(User user, boolean suppressed) {
        this.isSuppressed = suppressed;
        this.userJoined = user;
    }

    public boolean getIsSuppressed() {
        return isSuppressed;
    }

    public User getUser() {
        return userJoined;
    }
}
