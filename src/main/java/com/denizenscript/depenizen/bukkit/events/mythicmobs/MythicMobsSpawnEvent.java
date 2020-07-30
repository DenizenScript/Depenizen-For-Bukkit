package com.denizenscript.depenizen.bukkit.events.mythicmobs;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.depenizen.bukkit.objects.mythicmobs.MythicMobsMobTag;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class MythicMobsSpawnEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // mythicmob mob spawns
    // mythicmob <mob> spawns
    //
    // @Regex ^on mythicmob [^\s]+ spawns$
    //
    // @Switch in:<area> to only process the event if it occurred within a specified area.
    //
    // @Triggers when a MythicMob spawns.
    //
    // @Context
    // <context.mob> Returns the MythicMobTag that is spawning.
    // <context.entity> Returns the EntityTag for the MythicMob.
    // <context.location> Returns a LocationTag of where the MythicMob will spawn.
    // <context.from_spawner> Returns true if the mob was from a spawner.
    // <context.spawner_location> Returns a LocationTag of the spawner that spawned the mob.
    //
    // @Plugin Depenizen, MythicMobs
    //
    // @Group Depenizen
    //
    // -->

    public MythicMobsSpawnEvent() {
        instance = this;
    }

    public static MythicMobsSpawnEvent instance;
    public MythicMobSpawnEvent event;
    public MythicMobsMobTag mob;
    public EntityTag entity;
    public LocationTag location;

    @Override
    public boolean couldMatch(ScriptPath path) {
        return (path.eventLower.startsWith("mythicmob") && (path.eventArgLowerAt(2).equals("spawns")));
    }

    @Override
    public boolean matches(ScriptPath path) {
        String mob = path.eventArgLowerAt(1);

        if (!mob.equals("mob")
                && !mob.equals(CoreUtilities.toLowerCase(this.mob.getMobType().getInternalName()))) {
            return false;
        }

        if (!runInCheck(path, location)) {
            return false;
        }

        return super.matches(path);
    }

    @Override
    public String getName() {
        return "MythicMobsSpawn";
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("mob")) {
            return mob;
        }
        else if (name.equals("entity")) {
            return entity;
        }
        else if (name.equals("location")) {
            return location;
        }
        else if (name.equals("from_spawner")) {
            return new ElementTag(event.isFromMythicSpawner());
        }
        else if (name.equals("spawner_location")) {
            AbstractLocation loc = event.getMythicSpawner().getLocation();
            return new LocationTag(loc.getX(), loc.getY(), loc.getZ(),loc.getWorld().getName());
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onMythicMobSpawns(MythicMobSpawnEvent event) {
        mob = new MythicMobsMobTag(event.getMob());
        entity = new EntityTag(event.getEntity());
        location = new LocationTag(event.getLocation());
        EntityTag.rememberEntity(entity.getBukkitEntity());
        this.event = event;
        fire(event);
        EntityTag.forgetEntity(entity.getBukkitEntity());
    }
}
