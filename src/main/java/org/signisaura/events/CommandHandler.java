package org.signisaura.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandHandler extends ListenerAdapter {
    // this class ensures the connection between Handlers

    // Instance variables
    private final SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
    private final ChatCommandHandler chatCommandHandler = new ChatCommandHandler();

    // static variables


    // Enable/Disable Slashcommands
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        chatCommandHandler.executeChatCommands(event);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        slashCommandHandler.executeSlashCommand(event);
    }
}
