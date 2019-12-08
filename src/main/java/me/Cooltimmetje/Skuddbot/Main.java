package me.Cooltimmetje.Skuddbot;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.User;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.ActivityChecker;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import org.jibble.pircbot.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;

/**
 * This is the bot instance.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */

public class Main {

    private static Skuddbot skuddbot;
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static SkuddbotTwitch skuddbotTwitch;
    private static Timer timer = new Timer();

    public static void main(String[] args){
        Constants.STARTUP_ARGUMENTS = args;

        if(args.length < 5){
            throw new IllegalArgumentException("Startup arguments are invalid: I need a Discord Token, Mysql Username, Mysql Password, Twitch Username and Twitch Token.");
        } else {
            log.info("Setting up...");
            skuddbot = new Skuddbot(args[0]);

            String mysqlPass = args[2];
            if(mysqlPass.equals("-nopass")) mysqlPass = ""; //This is for my local instance, the database has no password there and I am too lazy to set one.

            log.info("Making database connection...");
            MySqlManager.setupHikari(args[1], mysqlPass);
            Constants.twitchBot = args[3];
            Constants.twitchOauth = args[4];
        }
        log.info("Connecting to Twitch...");

        skuddbotTwitch = new SkuddbotTwitch();

        try {
            skuddbotTwitch.connect("irc.chat.twitch.tv", 6667, Constants.twitchOauth);
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }

        MySqlManager.loadAuth();
        MySqlManager.loadBans();
        MySqlManager.loadAdmin();
        MySqlManager.loadAwesomeUsers();
        MySqlManager.loadAwesomeData();
        MySqlManager.loadWhitelistedCommands();
        MySqlManager.loadGlobal();

        log.info("All systems operational. Ready to connect to Discord.");
        skuddbot.login();


        timer.schedule(new ActivityChecker(), Constants.INACTIVE_DELAY, Constants.INACTIVE_DELAY);
        Constants.STARTUP_TIME = System.currentTimeMillis();
    }

    public static Skuddbot getInstance(){
        return skuddbot;
    }

    public static void stopTimer(){
        timer.cancel();
    }

    public static SkuddbotTwitch getSkuddbotTwitch(){
        return skuddbotTwitch;
    }

    public static User self() {
        return getInstance().getSkuddbot().getSelf().block();
    }

    public static DiscordClient client(){
        return getInstance().getSkuddbot();
    }
}
