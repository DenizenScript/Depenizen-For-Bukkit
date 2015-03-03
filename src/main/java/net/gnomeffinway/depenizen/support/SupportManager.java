package net.gnomeffinway.depenizen.support;

import net.aufdemrand.denizen.objects.dNPC;
import net.aufdemrand.denizencore.objects.ObjectFetcher;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.objects.properties.PropertyParser;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.ReplaceableTagEvent;
import net.aufdemrand.denizencore.tags.TagManager;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.depends.Depends;
import net.gnomeffinway.depenizen.Depenizen;
import org.bukkit.event.Listener;

import java.util.*;

public class SupportManager {

    private final Depenizen depenizen;
    private final PropertyParser propertyParser;
    private final Map<String, Support> additionalTags;
    private boolean hasNewObjects = false;

    public SupportManager(Depenizen depenizen) {
        this.depenizen = depenizen;
        this.propertyParser = DenizenAPI.getCurrentInstance().getPropertyParser();
        this.additionalTags = new HashMap<String, Support>();
        TagManager.registerTagEvents(this);
    }

    public void register(Support support) {
        if (support.hasObjects()) {
            for (Class<? extends dObject> object : support.getObjects()) {
                ObjectFetcher.registerWithObjectFetcher(object);
            }
            hasNewObjects = true;
        }
        if (support.hasProperties()) {
            for (Map.Entry<Class<? extends Property>, Class<? extends dObject>[]> prop
                    : support.getProperties().entrySet()) {
                for (Class<? extends dObject> obj : prop.getValue()) {
                    if (obj.equals(dNPC.class) && Depends.citizens == null) continue;
                    propertyParser.registerProperty(prop.getKey(), obj);
                }
            }
        }
        if (support.hasEvents()) {
            for (Class<? extends Listener> event : support.getEvents()) {
                try {
                    depenizen.getServer().getPluginManager().registerEvents(event.newInstance(), depenizen);
                } catch (Exception e) {
                    dB.echoError(e);
                }
            }
        }
        if (support.hasAdditionalTags()) {
            for (String tag : support.getAdditionalTags()) {
                this.additionalTags.put(tag.toLowerCase(), support);
            }
        }
    }

    public void registerNewObjects() {
        if (hasNewObjects) {
            try { ObjectFetcher._initialize(); }
            catch (Exception e) {
                dB.echoError(e);
            }
            hasNewObjects = false;
        }
    }

    private String findAdditionalTags(String name, Attribute attribute) {
        for (String tag : additionalTags.keySet()) {
            if (name.equalsIgnoreCase(tag)) {
                return additionalTags.get(tag).additionalTags(attribute);
            }
        }
        return null;
    }

    @TagManager.TagEvents
    public void tagListener(ReplaceableTagEvent event) {
        if (event.replaced()) return;
        Attribute attribute = new Attribute(event.raw_tag, event.getScriptEntry());
        String replaced = null;
        String name = event.getName().toLowerCase();
        for (String tag : additionalTags.keySet()) {
            if (name.startsWith(tag)) {
                replaced = additionalTags.get(tag).additionalTags(attribute);
                return;
            }
        }
        if (replaced != null) {
            event.setReplaced(replaced);
        }
    }
}