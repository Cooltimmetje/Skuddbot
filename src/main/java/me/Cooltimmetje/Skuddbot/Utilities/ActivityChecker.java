package me.Cooltimmetje.Skuddbot.Utilities;

import discord4j.core.object.entity.Message;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAManager;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.TdManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

/**
 * Activity checker to prevent memory leaks.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.2-ALPHA
 */
public class ActivityChecker extends TimerTask {

    /**
     * This checks if a user is active or not, runs every 10 minutes.
     * When it runs, we set every active user to inactive, and inactive (those that were inactive prior to setting everyone inactive) users are saved to the database and unloaded.
     *
     * I also use this to rotate the playing message and to deactivate reactions. Just becuz.
     */
    public void run(){
        Logger.info("Activity check running...");

        for(Server server : ServerManager.servers.values()){
            ArrayList<SkuddUser> done = new ArrayList<>();
            ArrayList<SkuddUser> inactiveDiscord = new ArrayList<>();
            ArrayList<SkuddUser> inactiveTwitch = new ArrayList<>();
            HashMap<String,SkuddUser> discord = server.discordProfiles;
            HashMap<String,SkuddUser> twitch = server.twitchProfiles;
            for(SkuddUser user : discord.values()){
                if(user.isInactive()){
                    inactiveDiscord.add(user);
                    done.add(user);
                } else {
                    user.setInactive(true);
                    done.add(user);
                }
            }
            twitch.values().stream().filter(user -> !done.contains(user)).forEach(user -> {
                if (user.isInactive()) {
                    inactiveTwitch.add(user);
                } else {
                    user.setInactive(true);
                }
            });

            for(SkuddUser su : inactiveDiscord){
                su.unload();
            }
            for(SkuddUser su : inactiveTwitch){
                su.unload();
            }
        }

        MiscUtils.setPlaying(false);
        ServerManager.saveAll();

        ArrayList<Message> temp = new ArrayList<>();
        for (Message message : MessagesUtils.reactions.keySet()){
            long time = Long.parseLong(MessagesUtils.reactions.get(message).get("time")+"");
            long expireTime = Long.parseLong(MessagesUtils.reactions.get(message).get("expireTime")+"");
            if((System.currentTimeMillis() - time) > expireTime){
                temp.add(message);
            }
        }
        for(Message message : temp){
            MessagesUtils.reactions.remove(message);
        }

        FFAManager.runReminders();
        TdManager.runReminders();
    }

}
