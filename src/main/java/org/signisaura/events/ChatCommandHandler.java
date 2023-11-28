package org.signisaura.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.signisaura.safe.Safe;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class ChatCommandHandler {
    // static variables
    private static final String prefix = "!";

    // Is bot owner?
    public boolean isCreator(@NotNull MessageReceivedEvent event) {
        return event.getAuthor().equals(event.getJDA().getUserById(Safe.getIdOwner()));
    }

    // Not the creator
    public void notCreator(@NotNull MessageReceivedEvent event) {
        MessageEmbed errorEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(event.getAuthor().getAsMention() + ", you are not my boss!")
                .setImage("https://media.tenor.com/dkcA2z7o8rUAAAAd/no-head-shaking.gif")
                .setFooter("GIF found on tenor.com")
                .build();

        event.getChannel().sendMessageEmbeds(errorEmbed).queue();
    }

    // execute chatcommands followed by !
    public void executeChatCommands(@NotNull MessageReceivedEvent event) {
        Message msg = event.getMessage();
        String messageString = msg.getContentRaw().toLowerCase(Locale.ROOT);

        switch (messageString) {
            case prefix + "enableSlash":
                enableSlashCommands(event);
                break;

            case prefix + "disableSlash":
                disableSlashCommands(event);
                break;
        }
    }

    // Enable Slashcommands per server
    public void enableSlashCommands(@NotNull MessageReceivedEvent event) {
        if (isCreator(event)) {
            event.getGuild().updateCommands().addCommands(getCommandDataArray());
            event.getChannel().sendMessage("Slashcommands are enabled!").queue();
        }
        else {
            notCreator(event);
        }
    }

    // Disable Slashcommands per server
    public void disableSlashCommands(@NotNull MessageReceivedEvent event) {
        if (isCreator(event)) {
            event.getGuild().updateCommands().addCommands().queue();
            event.getChannel().sendMessage("Slashcommands are disabled!").queue();
        }
        else {
            notCreator(event);
        }
    }

    // list of all slashcommands and their description
    private ArrayList<CommandData> getCommandDataArray() {
        ArrayList<CommandData> commandDataArrayList = new ArrayList<>();

        commandDataArrayList.add(new CommandDataImpl("clear", "deletes the last n unpinned messages.")
                .addOption(OptionType.INTEGER, "number", "How many messages would you like to delete?", true));
        commandDataArrayList.add(new CommandDataImpl("ban", "bans the member.")
                .addOption(OptionType.USER, "member", "the member to be banned.", true)
                .addOption(OptionType.STRING, "reason", "why should the user be banned?", true));
        commandDataArrayList.add(new CommandDataImpl("pardon", "unbans the user.")
                .addOption(OptionType.USER, "user", "the user to be unbanned", true));
        commandDataArrayList.add(new CommandDataImpl("banlist", "retrieves a list of banned users."));
        commandDataArrayList.add(new CommandDataImpl("kick", "kicks an user from the server.")
                .addOption(OptionType.USER, "member", "the user to be kicked", true)
                .addOption(OptionType.STRING, "reason", "why should the user be kicked?", true));
        commandDataArrayList.add(new CommandDataImpl("mute", "puts the user in time out for an amount of time.")
                .addOption(OptionType.USER, "member", "the member to be put in time out.", true)
                .addOption(OptionType.INTEGER, "duration", "the duration of the time out.", true)
                .addOption(OptionType.STRING, "time unit", "choose from: minutes, hours, days", true, true));
        commandDataArrayList.add(new CommandDataImpl("unmute", "removes a time out from the user.")
                .addOption(OptionType.USER, "member", "the member from whom the time out will be removed.", true));

        return commandDataArrayList;
    }
}
