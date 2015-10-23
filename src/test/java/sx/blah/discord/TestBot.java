/*
 * Discord4J - Unofficial wrapper for Discord API
 * Copyright (c) 2015
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package sx.blah.discord;

import org.json.simple.parser.ParseException;
import sx.blah.discord.handle.IEvent;
import sx.blah.discord.handle.IListener;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.Presences;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.server.ExportException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qt
 * @since 8:00 PM 16 Aug, 2015
 * Project: DiscordAPI
 * <p>
 * General testing bot. Also a demonstration of how to use the bot.
 */
public class TestBot {

	/**
	 * Starts the bot. This can be done any place you want.
	 * The main method is for demonstration.
	 *
	 * @param args Command line arguments passed to the program.
	 */
	public static void main(String... args) {

        String os = System.getProperty("os.name").toLowerCase();

		try {
			DiscordClient.get().login(args[0] /* username */, args[1] /* password */);

            final boolean[] removecmds = {true};

			ArrayList<String> banList = new ArrayList<>();
            ArrayList<User> users = new ArrayList<>();
            ArrayList<String> owners = new ArrayList<>();   // bot owners
            ArrayList<String> admins = new ArrayList<>();   // channel admins
            ArrayList<String> mods = new ArrayList<>();     // channel mods
            ArrayList<String> vips = new ArrayList<>();     // channel vips

            String afk_channel = "102917634458161153";
            String lounge_channel =  "105082957219303424";

            owners.add("102483019427770368");   // set users to be in certain groups
            admins.add("102483019427770368");
            admins.add("105021200056459264");
            admins.add("105004458701819904");
            mods.add("105027884988526592");
            vips.add("105021200056459264");

            Map<String, String> roleIds = new HashMap<String, String>();

            roleIds.put("@admin", "105507446012805120");
            roleIds.put("@admin-afk", "105545325791416320");
            roleIds.put("vip", "105537113717518336");
            roleIds.put("vip-afk", "105537119652458496");
            roleIds.put("@mod", "105072149131186176");
            roleIds.put("@mod-afk", "106883079301308416");
            roleIds.put("@channelmod", "105071661585334272");
            roleIds.put("@channelmod-afk", "106975980144320512");
            roleIds.put("+voice", "105086760274386944");
            roleIds.put("+voice-afk", "105545135281934336");
            roleIds.put("@banned", "105080701279301632");
            roleIds.put("@chatbanned", "106593527282077696");

            String bannedWords = "nigger nigga nig niqqa niglet negro negroid nigglet niggroids nignog nog";


            DiscordClient.get().getDispatcher().registerListener(new IListener<MessageReceivedEvent>() {
				@Override public void receive(MessageReceivedEvent messageReceivedEvent) {
					Message m = messageReceivedEvent.getMessage();

                    // initially set up our users
                    if (users.isEmpty()) {
                        fixUsersRoles();
                    }

                    DateTimeFormatter logdtf, logsavedtf;
                    logdtf 		= DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
                    logsavedtf 	= DateTimeFormatter.ofPattern("YYYYMMdd");

                    //------------------------------------------------------------//
                    // WRITE TO LOG

                    StringBuilder addlog = new StringBuilder();
                    addlog.append(m.getTimestamp().format(logdtf)
                            + " **" + m.getAuthor().getName()
                            + "**: *" + m.getContent() + "*\\n");

                    BufferedWriter bw = null;
                    try {
                        File loc;
                        if(os.contains("windows")) {
                            loc = new File("logs/"
                                    + m.getChannel().getID());
                            loc.mkdirs();
                            bw = new BufferedWriter(
                                    new FileWriter(loc + "/"
                                            + m.getTimestamp().format(logsavedtf)
                                            + ".txt", true));
                        } else {
                            loc = new File("/usr/share/nginx/html/logs/"
                                    + m.getChannel().getID());
                            loc.mkdirs();
                            bw = new BufferedWriter(
                                    new FileWriter(loc + "/"
                                            + m.getTimestamp().format(logsavedtf)
                                            + ".txt", true));
                        }
                        bw.write(addlog.toString());
                        bw.newLine();
                        bw.flush();
                        if(bw != null)
                            bw.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }

                    // mystery code
                    if (m.getAuthor().getName().contains("Basketball-American")) {
                        // search through all the
                        for (String word : m.getContent().split(" ")) {
                            try {
                                if (bannedWords.contains(word))
                                    DiscordClient.get().deleteMessage(m.getID(), m.getChannel().getID());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if(banList.contains(m.getAuthor().getID()) && !m.getAuthor().getID().contains("102483019427770368")) {
                        try {
                            DiscordClient.get().deleteMessage(m.getID(), m.getChannel().getID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (m.getContent().startsWith("!meme")
							|| m.getContent().startsWith("!nicememe")) {
							new MessageBuilder().appendContent("MEMES REQUESTED:", MessageBuilder.Styles.UNDERLINE_BOLD_ITALICS)
                                    .appendContent(" http://niceme.me/").withChannel(messageReceivedEvent.getMessage().getChannel())
                                    .build();
					} else if (m.getContent().startsWith("!clear")) {
						Channel c = DiscordClient.get().getChannelByID(m.getChannel().getID());
						if (null != c) {
							c.getMessages().stream().filter(message -> message.getAuthor().getID()
									.equalsIgnoreCase(DiscordClient.get().getOurUser().getID())).forEach(message -> {
								try {
									Discord4J.logger.debug("Attempting deletion of message {} by \"{}\" ({})", message.getID(), message.getAuthor().getName(), message.getContent());
									DiscordClient.get().deleteMessage(message.getID(), message.getChannel().getID());
								} catch (IOException e) {
									Discord4J.logger.error("Couldn't delete message {} ({}).", message.getID(), e.getMessage());
								}
							});
						}
					} else if (m.getContent().startsWith("!name ") && owners.contains(m.getAuthor().getID())) {
						String s = m.getContent().split(" ", 2)[1];
                        s = StringEscapeUtils.escapeJson(s);
                        System.out.println(s);
						try {
							DiscordClient.get().changeAccountInfo(s, "", "");
//							 m.reply("is this better?");
						} catch (ParseException | IOException e) {
							e.printStackTrace();
						}
					} else if(m.getContent().startsWith("!pm")) {
                        try {
                            PrivateChannel channel = DiscordClient.get().getOrCreatePMChannel(m.getAuthor());
                            new MessageBuilder().withChannel(channel).withContent("SUP DUDE").build();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
					} else if(m.getContent().startsWith("!ban ") && owners.contains(m.getAuthor().getID())){
                        try  {
                            String name = m.getContent().split(" ", 2)[1];
                            for (User u : DiscordClient.get().getGuilds().get(0).getUsers()) {
                                if (u.getName().startsWith(name)) {
                                    banList.add(u.getID());
                                    ArrayList<String> roles = new ArrayList<String>(); // if banned, no other role should be added
                                    roles.add(roleIds.get("@banned"));
                                    u.setRoles(roles);
                                    DiscordClient.get().sendMessage("**" + m.getAuthor().getName() + "** banned **" + u.getName() + "** from the channel.", m.getChannel().getID());
                                    DiscordClient.get().changeRole(DiscordClient.get().getGuilds().get(0), u, roles);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
					} else if(m.getContent().startsWith("!unban ") && owners.contains(m.getAuthor().getID())) {
                        try {
                            String name = m.getContent().split(" ", 2)[1];
                            for (User u : DiscordClient.get().getGuilds().get(0).getUsers()) {
                                if (u.getName().startsWith(name)) {
                                    banList.remove(u.getID());
                                    ArrayList<String> roles = u.getRoles();
                                    roles.remove(roleIds.get("@banned"));
                                    roles.remove(roleIds.get("@chatbanned"));
                                    u.setRoles(roles);
                                    DiscordClient.get().sendMessage("**" + m.getAuthor().getName() + "** pardoned **" + u.getName() + "**.", m.getChannel().getID());
                                    DiscordClient.get().changeRole(DiscordClient.get().getGuilds().get(0), u, roles);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(m.getContent().startsWith("!say ") && owners.contains(m.getAuthor().getID())) {
                        try {
                            String text = m.getContent().split(" ", 2)[1];
                            DiscordClient.get().sendMessage(text, m.getChannel().getID());
                            DiscordClient.get().deleteMessage(m.getID(), m.getChannel().getID());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(m.getContent().startsWith("!hide") || m.getContent().startsWith("!afk")) {
                        try {
                            if (m.getAuthor().getPresence() == Presences.ONLINE) {
                                m.getAuthor().setPresence(Presences.OFFLINE);
                                DiscordClient.get().sendMessage("**" + m.getAuthor().getName() + "** went afk", m.getChannel().getID());
                                // delete the command after we get it to remove chat clutter
                                // {"roles":["105507446012805120","105545325791416320"]}
                                // TODO: make this work on more than just the first guild connected to.
                                ArrayList<String> roles = m.getAuthor().getRoles();
                                if (roles.contains(roleIds.get("@admin")))
                                    roles.add(roleIds.get("@admin-afk"));
                                if (roles.contains(roleIds.get("vip")))
                                    roles.add(roleIds.get("vip-afk"));
                                if (roles.contains(roleIds.get("@mod")))
                                    roles.add(roleIds.get("@mod-afk"));
                                if (roles.contains(roleIds.get("@channelmod")))
                                    roles.add(roleIds.get("@channelmod-afk"));
                                if (roles.contains(roleIds.get("+voice")))
                                    roles.add(roleIds.get("+voice-afk"));

                                try {
                                    DiscordClient.get().changeRole(DiscordClient.get().getGuilds().get(0), m.getAuthor(), roles);
                                } catch (Exception e) {
                                    // e.printStackTrace();
                                }
                                DiscordClient.get().moveUserToChannel(DiscordClient.get().getGuilds().get(0), m.getAuthor(), afk_channel);
//                                DiscordClient.get().deleteMessage(m.getID(), m.getChannel().getID());
                            } else {
                                m.getAuthor().setPresence(Presences.ONLINE);
                                // delete the command after we get it to remove chat clutter
                                ArrayList<String> roles = m.getAuthor().getRoles();

                                if (roles.contains(roleIds.get("@admin-afk")))
                                    roles.remove(roleIds.get("@admin-afk"));
                                if (roles.contains(roleIds.get("vip-afk")))
                                    roles.remove(roleIds.get("vip-afk"));
                                if (roles.contains(roleIds.get("@mod-afk")))
                                    roles.remove(roleIds.get("@mod-afk"));
                                if (roles.contains(roleIds.get("@channelmod-afk")))
                                    roles.remove(roleIds.get("@channelmod-afk"));
                                if (roles.contains(roleIds.get("+voice-afk")))
                                    roles.remove(roleIds.get("+voice-afk"));

                                for(String role : roles)
                                System.out.println(role);

                                try {
                                    DiscordClient.get().changeRole(DiscordClient.get().getGuilds().get(0), m.getAuthor(), roles);
                                } catch (Exception e) {
                                    // e.printStackTrace();
                                }
                                // move players that aren't afk anymore to the lounge channel
                                DiscordClient.get().moveUserToChannel(DiscordClient.get().getGuilds().get(0), m.getAuthor(), lounge_channel);
                                DiscordClient.get().sendMessage("**" + m.getAuthor().getName() + "** returned", m.getChannel().getID());

//                                DiscordClient.get().deleteMessage(m.getID(), m.getChannel().getID());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(owners.contains(m.getAuthor().getID()) && m.getContent().startsWith("!owner ")) {
                        try {
                            String name = m.getContent().split(" ", 2)[1];
                            for (User u : DiscordClient.get().getGuilds().get(0).getUsers()) {
                                if (u.getName().startsWith(name)) {
                                    owners.add(u.getID());
                                    DiscordClient.get().sendMessage("**" + m.getAuthor().getName() + "** gave **" + u.getName() + "** bot ownership.", m.getChannel().getID());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(owners.contains(m.getAuthor().getID()) && m.getContent().startsWith("!revoke ")) {
                        try {
                            String name = m.getContent().split(" ", 2)[1];
                            for (User u : DiscordClient.get().getGuilds().get(0).getUsers()) {
                                // search all users in the room starting with characters in name, do not allow revoking
                                // of privileges from the bot creator.
                                if (u.getName().startsWith(name) && !u.getID().contains("102483019427770368")) {
                                    owners.remove(u.getID());
                                    DiscordClient.get().sendMessage("**" + m.getAuthor().getName() + "** revoked **" + u.getName() + "**'s bot ownership.", m.getChannel().getID());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(owners.contains(m.getAuthor().getID()) && m.getContent().startsWith("!removecmds ")) {
                        try {
                            String bool = m.getContent().split(" ", 2)[1];
                            if (bool.startsWith("on") || bool.startsWith("true"))
                                removecmds[0] = true;
                            else if(bool.startsWith("off") || bool.startsWith("false"))
                                removecmds[0] = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(owners.contains(m.getAuthor().getID()) && m.getContent().startsWith("!fixranks")) {
                        try {
                            fixUsersRoles();
                            for (User u : users) {
                                DiscordClient.get().changeRole(DiscordClient.get().getGuilds().get(0), u, u.getRoles());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(owners.contains(m.getAuthor().getID()) && m.getContent().startsWith("!voice ")) {
                        try {
                            String name = m.getContent().split(" ", 2)[1];
                            for (User u : DiscordClient.get().getGuilds().get(0).getUsers()) {
                                // search all users in the room starting with characters in name, do not allow revoking
                                // of privileges from the bot creator.
                                if (u.getName().startsWith(name) && !u.getID().contains("102483019427770368")) {
                                    ArrayList<String> roles = u.getRoles();
                                    roles.add(roleIds.get("+voice"));
                                    u.setRoles(roles);
                                    DiscordClient.get().changeRole(DiscordClient.get().getGuilds().get(0), u, roles);
                                    DiscordClient.get().sendMessage("**" + m.getAuthor().getName() + "** gave **" + u.getName() + "** speaking priv.", m.getChannel().getID());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        // if we are removing all command messages (starting with !)
                        if (removecmds[0] && m.getContent().startsWith("!"))
                            DiscordClient.get().deleteMessage(m.getID(), m.getChannel().getID());
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
				}

                private void fixUsersRoles() {
                    for (User u : DiscordClient.get().getGuilds().get(0).getUsers()) {
                        ArrayList<String> roles = new ArrayList<>();

                        // first set the roles based off of the 3 main categories of user
                        if (admins.contains(u.getID()))
                            roles.add(roleIds.get("@admin"));
                        else if (banList.contains(u.getID()))
                            roles.add(roleIds.get("@banned"));
                        else if (mods.contains(u.getID()))
                            roles.add(roleIds.get("@mod"));
                        else
                            roles.add(roleIds.get("+voice"));

                        // separately determine if the user is also a VIP
                        if (vips.contains(u.getID()))
                            roles.add(roleIds.get("vip"));
                        u.setRoles(roles);
                        users.add(u);
                    }
                }
            });

			DiscordClient.get().getDispatcher().registerListener(new IListener<InviteReceivedEvent>() {
				@Override public void receive(InviteReceivedEvent event) {
					Invite invite = event.getInvite();
					try {
						Invite.InviteResponse response = invite.details();
						event.getMessage().reply(String.format("you've invited me to join #%s in the %s guild!", response.getChannelName(), response.getGuildName()));
                        invite.accept();
                        DiscordClient.get().sendMessage(String.format("Hello, #%s and the \\\"%s\\\" guild! I was invited by %s!",
                                        response.getChannelName(), response.getGuildName(), event.getMessage().getAuthor()),
								response.getChannelID());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			DiscordClient.get().getDispatcher().registerListener(new IListener<MessageDeleteEvent>() {
				@Override public void receive(MessageDeleteEvent event) {
					try {
						// event.getMessage().reply("you said, \\\"" + event.getMessage().getContent() + "\\\"");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

            DiscordClient.get().getDispatcher().registerListener(new IListener<UserJoinEvent>() {
                @Override public void receive(UserJoinEvent event) {
                    try {
                        String name = event.getUser().getName();
                        DiscordClient.get().sendMessage(name+" joined the channel.", "102483264945545216");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
