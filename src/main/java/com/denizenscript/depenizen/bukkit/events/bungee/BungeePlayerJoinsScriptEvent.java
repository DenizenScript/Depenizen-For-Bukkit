package com.denizenscript.depenizen.bukkit.events.bungee;

import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.dPlayer;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class BungeePlayerJoinsScriptEvent extends BukkitScriptEvent {

    // <--[event]
    // @Events
    // bungee player joins network
    //
    // @Regex ^on bungee player joins network$
    //
    // @Triggers when a player joins the Bungee network (but has not yet entered a server).
    //
    // @Context
    // <context.name> returns the connecting player's name.
    // <context.uuid> returns the connection player's UUID.
    //
    // -->

    public BungeePlayerJoinsScriptEvent() {
        instance = this;
    }

    public static BungeePlayerJoinsScriptEvent instance;

    public String name;

    public UUID uuid;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return s.startsWith("bungee player joins network");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return true;
    }

    @Override
    public String getName() {
        return "BungeePlayerJoins";
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        OfflinePlayer player = null;
        try {
            player = Bukkit.getOfflinePlayer(uuid);
        }
        catch (IllegalArgumentException ex) {
            // Ignore.
        }
        return new BukkitScriptEntryData(player == null ? null : new dPlayer(player), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("name")) {
            return new ElementTag(name);
        }
        else if (name.equals("uuid")) {
            return new ElementTag(uuid.toString());
        }
        return super.getContext(name);
    }
}
