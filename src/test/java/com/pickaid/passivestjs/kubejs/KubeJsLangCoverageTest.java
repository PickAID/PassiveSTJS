package com.pickaid.passivestjs.kubejs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KubeJsLangCoverageTest {
    private static final Pattern REQUIRED_KEY_PATTERN = Pattern.compile("'(kubejs\\.passivestjs\\.[^']+)'");
    private static final Set<String> REQUIRED_SKILLTREE_BUILTIN_KEYS = Set.of(
            "block.skilltree.workbench",
            "recipe.skilltree.workbench_item_bonus",
            "skill_bonus.skilltree.more_item_bonuses",
            "skill_bonus.skilltree.more_item_bonuses.one",
            "item_condition.skilltree.equipment_type.any",
            "item_condition.skilltree.equipment_type.armor",
            "item_condition.skilltree.equipment_type.axe",
            "item_condition.skilltree.equipment_type.axe.plural",
            "item_condition.skilltree.equipment_type.boots",
            "item_condition.skilltree.equipment_type.bow",
            "item_condition.skilltree.equipment_type.bow.plural",
            "item_condition.skilltree.equipment_type.chestplate",
            "item_condition.skilltree.equipment_type.chestplate.plural",
            "item_condition.skilltree.equipment_type.crossbow",
            "item_condition.skilltree.equipment_type.crossbow.plural",
            "item_condition.skilltree.equipment_type.helmet",
            "item_condition.skilltree.equipment_type.helmet.plural",
            "item_condition.skilltree.equipment_type.hoe",
            "item_condition.skilltree.equipment_type.hoe.plural",
            "item_condition.skilltree.equipment_type.leggings",
            "item_condition.skilltree.equipment_type.melee_weapon",
            "item_condition.skilltree.equipment_type.melee_weapon.plural",
            "item_condition.skilltree.equipment_type.pickaxe",
            "item_condition.skilltree.equipment_type.pickaxe.plural",
            "item_condition.skilltree.equipment_type.ranged_weapon",
            "item_condition.skilltree.equipment_type.ranged_weapon.plural",
            "item_condition.skilltree.equipment_type.shield",
            "item_condition.skilltree.equipment_type.shield.plural",
            "item_condition.skilltree.equipment_type.shovel",
            "item_condition.skilltree.equipment_type.shovel.plural",
            "item_condition.skilltree.equipment_type.sword",
            "item_condition.skilltree.equipment_type.sword.plural",
            "item_condition.skilltree.equipment_type.tool",
            "item_condition.skilltree.equipment_type.tool.plural",
            "item_condition.skilltree.equipment_type.trident",
            "item_condition.skilltree.equipment_type.trident.plural",
            "item_condition.skilltree.equipment_type.weapon",
            "item_condition.skilltree.equipment_type.weapon.plural"
    );
    private static final Pattern LOCALE_BLOCK_PATTERN = Pattern.compile(
            "ClientEvents\\.lang\\('([^']+)'\\s*,\\s*event\\s*=>\\s*\\{\\s*event\\.addAll\\(\\{([\\s\\S]*?)\\}\\)\\s*\\}\\)",
            Pattern.MULTILINE
    );
    private static final Pattern ENTRY_KEY_PATTERN = Pattern.compile("'([^']+)'\\s*:");

    @Test
    void sampleClientLangCoversAllContentTranslationKeys() throws IOException {
        Set<String> requiredKeys = extractRequiredKeys();
        String langScript = Files.readString(Path.of("examples/kubejs/passivestjs/06_client_lang_entries.js").toAbsolutePath());

        Matcher blockMatcher = LOCALE_BLOCK_PATTERN.matcher(langScript);
        int blockCount = 0;
        while (blockMatcher.find()) {
            blockCount++;
            String locale = blockMatcher.group(1);
            Set<String> localeKeys = extractEntryKeys(blockMatcher.group(2));
            assertTrue(
                    localeKeys.containsAll(requiredKeys),
                    locale + " is missing translation keys: " + missingKeys(requiredKeys, localeKeys)
            );
            assertTrue(
                    localeKeys.containsAll(REQUIRED_SKILLTREE_BUILTIN_KEYS),
                    locale + " is missing built-in SkillTree translation keys: "
                            + missingKeys(REQUIRED_SKILLTREE_BUILTIN_KEYS, localeKeys)
            );
        }

        assertTrue(blockCount == 2, "Expected en_us and zh_cn lang entry blocks");
    }

    private static Set<String> extractRequiredKeys() throws IOException {
        Set<String> keys = new LinkedHashSet<>();
        for (String relativePath : new String[] {
                "examples/kubejs/passivestjs/02_server_skill_tree_content.js",
                "examples/kubejs/passivestjs/05_server_requirement_listener_chain.js"
        }) {
            String script = Files.readString(Path.of(relativePath).toAbsolutePath());
            Matcher matcher = REQUIRED_KEY_PATTERN.matcher(script);
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }
        }
        return keys;
    }

    private static Set<String> extractEntryKeys(String blockBody) {
        Set<String> keys = new LinkedHashSet<>();
        Matcher matcher = ENTRY_KEY_PATTERN.matcher(blockBody);
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        return keys;
    }

    private static Set<String> missingKeys(Set<String> required, Set<String> actual) {
        Set<String> missing = new LinkedHashSet<>(required);
        missing.removeAll(actual);
        return missing;
    }
}
