package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This will class holds lots of data, mostly global data.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.1-ALPHA
 */
public class Constants {

    public static long TIMMY_ID = 76593288865394688L;
    public static long JASCH_ID = 148376320726794240L;
    public static long LOG_CHANNEL = 274542577880006656L;
    public static long HOME_SERVER = 224987945638035456L;
    public static String[] STARTUP_ARGUMENTS;
    public static String twitchBot;
    public static String twitchOauth;

    public static int PROFILES_IN_MEMORY = 0;
    public static long STARTUP_TIME;

    public static boolean MUTED = false;
    public static boolean EVENT_ACTIVE = false;
    public static String CURRENT_EVENT;

    public static int INACTIVE_DELAY = 600000;

    public static HashMap<String,SkuddUser> verifyCodes = new HashMap<>();
    public static HashMap<String,String> config = new HashMap<>();
    public static ArrayList<String> whitelistedCommands = new ArrayList<>();

    public static HashMap<String,Boolean> rigged = new HashMap<>();

    public static ArrayList<String> awesomeUser = new ArrayList<>();
    public static HashMap<String,String> awesomePing = new HashMap<>();
    public static HashMap<String,DataTypes> awesomeStrings = new HashMap<>();
    public static ArrayList<String> adminUser = new ArrayList<>();
    public static ArrayList<String> bannedUsers = new ArrayList<>();

}