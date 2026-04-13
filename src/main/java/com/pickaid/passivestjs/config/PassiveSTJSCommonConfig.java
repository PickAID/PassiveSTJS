package com.pickaid.passivestjs.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class PassiveSTJSCommonConfig {
    public static final PassiveSTJSCommonConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<PassiveSTJSCommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(PassiveSTJSCommonConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private final ForgeConfigSpec.BooleanValue editorDisabled;
    private final ForgeConfigSpec.BooleanValue translationDraftExportEnabled;

    private PassiveSTJSCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("PassiveSTJS shared integration settings")
                .push("editor");

        editorDisabled = builder
                .comment("Disables Passive Skill Tree's built-in editor command and editor screen entry points.")
                .comment("Enabled by default because the upstream editor is unstable with PassiveSTJS-managed content.")
                .comment("Set to false only if you explicitly want to use Passive Skill Tree's editor anyway.")
                .translation("passivestjs.config.editor.disabled")
                .define("disabled", true);

        builder.pop();

        builder.push("translation_draft_export");

        translationDraftExportEnabled = builder
                .comment("Exports a generated skill tree translation draft to config/passivestjs/generated_skilltree_lang/lang.json after content reload.")
                .comment("The draft only contains discovered translation keys with empty string values.")
                .comment("Disabled by default because the draft file is regenerated and can overwrite previous draft edits.")
                .translation("passivestjs.config.translation_draft_export.enabled")
                .define("enabled", false);

        builder.pop();
    }

    public static boolean editorDisabled() {
        return SPEC.isLoaded()
                ? INSTANCE.editorDisabled.get()
                : INSTANCE.editorDisabled.getDefault();
    }

    public static boolean translationDraftExportEnabled() {
        return SPEC.isLoaded()
                ? INSTANCE.translationDraftExportEnabled.get()
                : INSTANCE.translationDraftExportEnabled.getDefault();
    }
}
