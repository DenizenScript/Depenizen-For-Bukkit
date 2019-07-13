package com.denizenscript.depenizen.bukkit.events.plotsquared;

import com.denizenscript.depenizen.bukkit.objects.plotsquared.dPlotSquaredPlot;
import com.github.intellectualsites.plotsquared.bukkit.events.PlayerClaimPlotEvent;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizen.objects.dPlayer;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerClaimPlotScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // plotsquared player claims plotsquaredplot
    // plotsquared player claims <dplotsquaredplot>
    //
    // @Regex ^on plotsquared player [^\s]+ level changes( in ((notable (cuboid|ellipsoid))|([^\s]+)))?$
    //
    // @Cancellable true
    //
    // @Triggers when a player claims a plot.
    //
    // @Context
    // <context.plot> returns the plot the player claimed.
    // <context.auto> returns true if the plot was claimed automatic.
    //
    // @Plugin Depenizen, PlotSquared
    //
    // -->

    public PlayerClaimPlotScriptEvent() {
        instance = this;
    }

    public static PlayerClaimPlotScriptEvent instance;
    public PlayerClaimPlotEvent event;
    public dPlayer player;
    public dPlotSquaredPlot plot;
    public ElementTag auto;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("plotsquared player claims");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String plotName = path.eventArgLowerAt(3);
        if (plotName.equals("plotsquaredplot")) {
            return true;
        }
        dPlotSquaredPlot dplot = dPlotSquaredPlot.valueOf(plotName);
        return dplot != null && dplot.equals(plot);
    }

    @Override
    public String getName() {
        return "PlayerClaimPlotEvent";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(player, null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("plot")) {
            return plot;
        }
        else if (name.equals("auto")) {
            return auto;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlotEnter(PlayerClaimPlotEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        player = dPlayer.mirrorBukkitPlayer(event.getPlayer());
        plot = new dPlotSquaredPlot(event.getPlot());
        auto = new ElementTag(event.wasAuto());
        this.event = event;
        fire(event);
    }
}
