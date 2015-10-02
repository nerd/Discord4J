package sx.blah.discord;

/**
 * @author qt
 * @since 5:44 PM 15 Aug, 2015
 * Project: DiscordAPI
 * <p>
 * Static class that contains
 * URLs useful to us.
 */
public final class DiscordEndpoints {
    /**
     * The base URL.
     */
    public static final String BASE = "https://discordapp.com/";
    /**
     * The base API location on Discord's servers.
     */
    public static final String APIBASE = BASE + "api";

    /**
     * Websocket hub. All communication (except login and logout)
     * goes through this.
     */
    public static final String WEBSOCKET_HUB = "ws://gateway-zerg.discord.gg/";

    public static final String USERS = APIBASE + "/users/";

    /**
     * Used for logging in.
     */
    public static final String LOGIN = APIBASE + "/auth/login";
    /**
     * Used for logging out.
     */
    public static final String LOGOUT = APIBASE + "/auth/logout";

    /**
     * Servers URL
     */
    public static final String SERVERS = APIBASE + "/guilds/";

    public static final String CHANNELS = APIBASE + "/channels/";

    /**
     * Used for accapting invites
     */
    public static final String INVITE = APIBASE + "/invite/";

    public static final String AVATARS = "https://cdn.discordapp.com/avatars/%s/%s.jpg";
}
