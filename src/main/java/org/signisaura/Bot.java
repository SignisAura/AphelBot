package org.signisaura;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.signisaura.safe.Safe;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Bot {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        // Bot loading
        JDA jdaBuilder = JDABuilder.createDefault(Safe.getToken()).enableIntents(getAllIntents())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        // Eventlisteners
        // CommandHandler, Events
        jdaBuilder.awaitReady();
    }

    // Manage intents
    private static ArrayList<GatewayIntent> getAllIntents() {
        return new ArrayList<>(Arrays.asList(GatewayIntent.values()));
    }
}

