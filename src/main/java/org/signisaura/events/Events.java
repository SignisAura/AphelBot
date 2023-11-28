package org.signisaura.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Events extends ListenerAdapter {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // bot joins a server
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();

        // creates auditlog channel
        guild.createTextChannel("aphel-auditlog")
                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .queue();
    }
}
