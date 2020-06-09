package com.denizenscript.depenizen.bukkit.events.crackshot;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.shampaggon.crackshot.events.WeaponScopeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CrackShotPlayerZoomsWeaponScopeEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // crackshot player zooms weapon scope
    //
    // @Regex ^on crackshot player zooms weapon scope$
    //
    // @Triggers when a player zooms their CrackShot weapon scope.
    //
    // @Cancellable true
    //
    // @Context
    // <context.weapon_name> returns the name of the weapon.
    // <context.zoomed> returns whether the player zoomed in.
    //
    // @Plugin Depenizen, CrackShot
    //
    // @Player Always
    //
    // -->

    public static CrackShotPlayerZoomsWeaponScopeEvent instance;
    public WeaponScopeEvent event;
    public PlayerTag player;

    public CrackShotPlayerZoomsWeaponScopeEvent() {
        instance = this;
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new PlayerTag(event.getPlayer()), null);
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("crackshot player zooms weapon scope");
    }

    @Override
    public String getName() {
        return "CrackShotPlayerZoomsWeaponScopeEvent";
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("weapon_name")) {
            return new ElementTag(event.getWeaponTitle());
        }
        else if (name.equals("zoomed")) {
            return new ElementTag(event.isZoomIn());
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onCrackShotPlayerZoomsWeaponScopeEvent(WeaponScopeEvent event) {
        this.event = event;
        fire(event);
    }
}
