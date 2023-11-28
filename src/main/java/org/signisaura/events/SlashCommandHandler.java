package org.signisaura.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;
import org.signisaura.safe.Safe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SlashCommandHandler {
    // assist methods
    // is the user allowed to ...?
     private static boolean isPermitted(SlashCommandInteractionEvent event, Permission permission) {
        return Objects.requireNonNull(event.getMember()).hasPermission(event.getGuildChannel(), permission);
    }

    // sends ephemeral messages
    static void sendEphemeralMessage(SlashCommandInteractionEvent event, String message) {
        event.reply(message).setEphemeral(true).queue();
    }

    // moderationerror: person not allowed to use it
    static void moderationError(SlashCommandInteractionEvent event) {
         sendEphemeralMessage(event, "You are not allowed to use this command!");
    }

    // error: contact the owner of the bot
    static void sendErrorMessage(SlashCommandInteractionEvent event) {
        event.getChannel().sendMessage("Please contact my boss: " +
                event.getJDA().getUserById(Safe.getIdOwner()).getAsMention()).queue();
    }

    // search for specific channel
     static TextChannel getTextChannel(SlashCommandInteractionEvent event, String channelName) {
        Guild guild = event.getGuild();

        List<TextChannel> textChannels = guild.getTextChannelsByName(channelName, true);

        if (textChannels.isEmpty()) {
            sendErrorMessage(event);
        }
        return textChannels.get(0);
    }

    // sends messages to aphel-auditlog
    static void sendMessagesInAuditlog(SlashCommandInteractionEvent event, String message) {
        TextChannel textChannel = SlashCommandHandler.getTextChannel(event, "aphel-auditlog");
        textChannel.sendMessage(message).queue();
    }

    // important methods
    public void executeSlashCommand(SlashCommandInteractionEvent event) {
         String eventName = event.getName().toLowerCase(Locale.ROOT);

         switch(eventName) {
             case "clear":
                 this.clear(event);
                 break;

             case "ban":
                 this.ban(event);
                 break;

             case "pardon":
                 this.pardon(event);
                 break;

             case "banlist":
                 this.sendBanList(event);
                 break;
         } //TODO: create mute, unmute and kick
    }

    // clear
    public void clear(SlashCommandInteractionEvent event) {
         if (!isPermitted(event, Permission.MESSAGE_MANAGE)) {
             moderationError(event);
             return; // it terminates the method
         }

         int count = event.getOption("number").getAsInt();

         if (count > 100 || count <= 0) {
             sendEphemeralMessage(event,"Please provide a number from 1 to 100 !");
         }
         else {
             ArrayList<Message> messages = new ArrayList<>();

             if (!event.getChannel().getIterableHistory().cache(false).isEmpty()) {
                while (count > 0) {
                    for (Message msg : event.getChannel().getIterableHistory().cache(false)) {
                        if (!msg.isPinned()) {
                            messages.add(msg);
                            count--;
                        }
                    }
                }
                 event.getChannel().purgeMessages(messages);
                 sendEphemeralMessage(event, "The messages have been deleted!");
            }
            else {
                sendEphemeralMessage(event, "The list is empty!");
             }
         }
    }

    // ban command
    public void ban(SlashCommandInteractionEvent event) {
         if (!isPermitted(event, Permission.BAN_MEMBERS)) {
             moderationError(event);
             return;
         }

        UserSnowflake user = event.getOption("member").getAsUser();
        String reason = event.getOption("reason").getAsString();
        event.getGuild().ban(user, 7, TimeUnit.DAYS).reason(reason);

        sendMessagesInAuditlog(event, user.getAsMention() + " has been banned. Reason: " + reason);
    }

    // pardon command
    public void pardon(SlashCommandInteractionEvent event) {
         if (!isPermitted(event, Permission.BAN_MEMBERS)) {
             moderationError(event);
             return;
         }

         Guild guild = event.getGuild();
         UserSnowflake user = event.getOption("user").getAsUser();

         try {
             guild.retrieveBan(user).queue();
         } catch (NullPointerException nullexp) {
             sendEphemeralMessage(event, "This person is not banned!");
         }

         guild.unban(user).queue();
         sendMessagesInAuditlog(event, user.getAsMention() + "has been pardoned.");
    }

    // banlist command
    public void sendBanList(SlashCommandInteractionEvent event) {
         if (!isPermitted(event, Permission.MESSAGE_MANAGE)) {
             moderationError(event);
             return;
         }

         Guild guild = event.getGuild();

         try {
             BanPaginationAction bans = guild.retrieveBanList();

             EmbedBuilder embed = new EmbedBuilder();
             embed.setTitle("List of banned people");

             for (Guild.Ban ban : bans) {
                 MessageEmbed.Field field = new MessageEmbed.Field(ban.getUser().getName(), "Reason: " +
                         ban.getReason(), false);
                 embed.addField(field);
             }

             getTextChannel(event, "aphel-auditlog").sendMessageEmbeds(embed.build());

         } catch (NullPointerException nullexp) {
             sendEphemeralMessage(event, "The list is empty!");
         }
    }

    //
}
