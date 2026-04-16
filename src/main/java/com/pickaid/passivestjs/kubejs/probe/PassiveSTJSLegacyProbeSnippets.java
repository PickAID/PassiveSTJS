package com.pickaid.passivestjs.kubejs.probe;

import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.snippet.Snippet;
import zzzank.probejs.lang.snippet.SnippetDump;

import java.util.List;
import java.util.Locale;

final class PassiveSTJSLegacyProbeSnippets {
    private PassiveSTJSLegacyProbeSnippets() {
    }

    static void addSnippets(SnippetDump dump) {
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            String prefix = "@" + toSnakeCase(alias.alias());
            Snippet snippet = dump.snippet(alias.alias())
                    .prefix(prefix)
                    .description("PassiveSTJS choices for " + alias.alias());
            List<String> choices = jsonChoices(alias.ids().get());
            if (choices.isEmpty()) {
                snippet.tabStop(1, ProbeJS.GSON.toJson("namespace:path"));
            } else {
                snippet.choices(choices);
            }
        }
    }

    private static List<String> jsonChoices(List<String> ids) {
        return ids.stream()
                .sorted()
                .distinct()
                .map(ProbeJS.GSON::toJson)
                .toList();
    }

    private static String toSnakeCase(String value) {
        return value
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z\\d])([A-Z])", "$1_$2")
                .toLowerCase(Locale.ROOT);
    }
}
