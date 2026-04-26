package com.pickaid.passivestjs.kubejs.probe;

import com.probejs.ProbeJS;
import com.probejs.features.plugin.DocGenerationEventJS;
import com.probejs.features.plugin.ProbeJSEvents;
import com.probejs.specials.assign.ClassAssignmentManager;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.desc.PrimitiveDescJS;

import java.util.List;
import java.util.Locale;

public final class PassiveSTJSProbeCompat {
    private static boolean installed;

    private PassiveSTJSProbeCompat() {
    }

    public static synchronized void install() {
        if (installed) {
            return;
        }
        installed = true;

        registerClassAssignments();
        ProbeJSEvents.DOC_GEN.listenJava(ScriptType.SERVER, null, PassiveSTJSProbeCompat::onDocGeneration);
    }

    public static synchronized void reinstallAfterServerReload() {
        installed = false;
        install();
    }

    private static Object onDocGeneration(EventJS event) {
        if (event instanceof DocGenerationEventJS docGenerationEvent) {
            addSpecialTypes(docGenerationEvent);
            addSnippets(docGenerationEvent);
        }
        return null;
    }

    private static void registerClassAssignments() {
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            PrimitiveDescJS specialType = new PrimitiveDescJS("Special." + alias.alias());
            if (!ClassAssignmentManager.ASSIGNMENTS.containsEntry(alias.wrapperClass(), specialType)) {
                ClassAssignmentManager.ASSIGNMENTS.put(alias.wrapperClass(), specialType);
            }
        }
    }

    private static void addSpecialTypes(DocGenerationEventJS event) {
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            event.specialType(alias.alias(), specialTypeChoices(alias));
        }
    }

    private static List<Object> specialTypeChoices(PassiveSTJSLegacyProbeIdAliases.IdAlias alias) {
        if (alias.specialTypeReference() != null && !alias.specialTypeReference().isBlank()) {
            return List.of(alias.specialTypeReference());
        }

        List<String> ids = alias.ids().get();
        if (ids.isEmpty()) {
            return List.of("string");
        }
        return ids.stream()
                .sorted()
                .distinct()
                .map(ProbeJS.GSON::toJson)
                .map(Object.class::cast)
                .toList();
    }

    private static void addSnippets(DocGenerationEventJS event) {
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            String snippetName = toSnakeCase(alias.alias());
            event.addSnippet(snippetName, snippetChoices(alias), "PassiveSTJS choices for " + alias.alias());
        }
    }

    private static List<Object> snippetChoices(PassiveSTJSLegacyProbeIdAliases.IdAlias alias) {
        List<String> ids = alias.ids().get();
        if (ids.isEmpty()) {
            return List.of("namespace:path");
        }
        return ids.stream()
                .sorted()
                .distinct()
                .map(Object.class::cast)
                .toList();
    }

    private static String toSnakeCase(String value) {
        return value
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z\\d])([A-Z])", "$1_$2")
                .toLowerCase(Locale.ROOT);
    }
}
