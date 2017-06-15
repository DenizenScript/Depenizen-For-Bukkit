package com.denizenscript.depenizen.bukkit.objects;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import net.aufdemrand.denizen.objects.dCuboid;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.objects.dWorld;
import net.aufdemrand.denizencore.objects.*;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.*;

import java.util.List;
import java.util.UUID;

public class dPlotSquaredPlot implements dObject {

    /////////////////////
    //   OBJECT FETCHER
    /////////////////

    public static dPlotSquaredPlot valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("plotsquaredplot")
    public static dPlotSquaredPlot valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ////////
        // Match town name

        string = string.replace("plotsquaredplot@", "");
        try {
            List<String> split = CoreUtilities.split(string, ',');
            return new dPlotSquaredPlot(new PlotAPI().getPlot(dWorld.valueOf(split.get(2)).getWorld() ,aH.getIntegerFrom(split.get(0)), aH.getIntegerFrom(split.get(1))));
            //return new dPlot(PlotMeCoreManager.getInstance().getPlotById(
            //        new PlotId(aH.getIntegerFrom(split.get(0)), aH.getIntegerFrom(split.get(1))),
            //        new BukkitWorld(dWorld.valueOf(split.get(3)).getWorld())));
        }
        catch (Throwable e) {
            return null;
        }
    }

    public static boolean matches(String arg) {
        return arg.startsWith("plotsquaredplot@");
    }

    /////////////////////
    //   STATIC CONSTRUCTORS
    /////////////////

    Plot plot = null;

    public dPlotSquaredPlot(Plot pl) {
        plot = pl;
    }

    /////////////////////
    //   dObject Methods
    /////////////////

    private String prefix = "PlotSquaredPlot";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public dPlotSquaredPlot setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>' ");
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getObjectType() {
        return "PlotSquaredPlot";
    }

    @Override
    public String identify() {
        return "plotsquaredplot@" + plot.getId().x + "," + plot.getId().y + "," + plot.getDefaultHome().getWorld();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <plotsquaredplot@plot.id_x>
        // @returns Element(Number)
        // @description
        // Returns the plot's X coordinate portion of its ID.
        // @Plugin DepenizenBukkit, PlotMe
        // -->
        if (attribute.startsWith("id_x")) {
            return new Element(plot.getId().x).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <plotsquaredplot@plot.id_Z>
        // @returns Element(Number)
        // @description
        // Returns the plot's Z coordinate portion of its ID.
        // @Plugin DepenizenBukkit, PlotSquared
        // -->
        if (attribute.startsWith("id_z")) {
            return new Element(plot.getId().y).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <plotsquaredplot@plot.home>
        // @returns dLocation
        // @description
        // Returns the plot's current home location.
        // @Plugin DepenizenBukkit, PlotSquared
        // -->
        if (attribute.startsWith("home")) {
            return new dLocation(new Location(Bukkit.getWorld(plot.getHome().getWorld()), plot.getHome().getX(),plot.getHome().getY(),plot.getHome().getZ())).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <plotsquaredplot@plot.world>
        // @returns dWorld
        // @description
        // Returns the plot's world.
        // @Plugin DepenizenBukkit, PlotSquared
        // -->
        if (attribute.startsWith("world")) {
            return dWorld.valueOf(plot.getDefaultHome().getWorld()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <plotsquaredplot@plot.owners>
        // @returns dList(dPlayer)
        // @description
        // Returns a list of all owners of the plot.
        // @Plugin DepenizenBukkit, PlotSquared
        // -->
        if (attribute.startsWith("owners")) {
            dList players = new dList();
            for (UUID uuid : plot.getOwners()) {
                players.add(dPlayer.valueOf(uuid.toString()).identify());
            }
            return players.getAttribute(attribute.fulfill(1));
            //return dPlayer.mirrorBukkitPlayer(Bukkit.getOfflinePlayer()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <plotsquaredplot@plot.trusted>
        // @returns dList(dPlayer)
        // @description
        // Returns a list of all trusted of the plot.
        // @Plugin DepenizenBukkit, PlotSquared
        // -->
        if (attribute.startsWith("trusted")) {
            dList players = new dList();
            for (UUID uuid : plot.getTrusted()) {
                players.add(dPlayer.valueOf(uuid.toString()).identify());
            }
            return players.getAttribute(attribute.fulfill(1));
            //return dPlayer.mirrorBukkitPlayer(Bukkit.getOfflinePlayer()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <plotsquaredplot@plot.members>
        // @returns dList(dPlayer)
        // @description
        // Returns a list of all members of the plot.
        // @Plugin DepenizenBukkit, PlotSquared
        // -->
        if (attribute.startsWith("members")) {
            dList players = new dList();
            for (UUID uuid : plot.getMembers()) {
                players.add(dPlayer.valueOf(uuid.toString()).identify());
            }
            return players.getAttribute(attribute.fulfill(1));
            //return dPlayer.mirrorBukkitPlayer(Bukkit.getOfflinePlayer()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <plotsquaredplot@plot.cuboid>
        // @returns dCuboid
        // @description
        // Returns the plot's cuboid.
        // @Plugin DepenizenBukkit, PlotSquared
        // -->
        if (attribute.startsWith("cuboid")) {
            dWorld world = dWorld.valueOf(plot.getCenter().getWorld());
            Location l1 = new Location(world.getWorld(), plot.getBottomAbs().getX(), 0, plot.getBottomAbs().getZ());
            Location l2 = new Location(world.getWorld(), plot.getTopAbs().getX(), 255, plot.getTopAbs().getZ());
            return new dCuboid(l1, l2).getAttribute(attribute.fulfill(1));
        }

        return new Element(identify()).getAttribute(attribute);

    }

}
