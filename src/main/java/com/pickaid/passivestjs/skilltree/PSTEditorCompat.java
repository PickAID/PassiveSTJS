package com.pickaid.passivestjs.skilltree;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.File;

public final class PSTEditorCompat {
    private static final Component EDITOR_DISABLED_MESSAGE = Component.literal(
            "PassiveSTJS disabled the Passive Skill Tree editor. Set editor.disabled=false in passivestjs-common.toml to re-enable it."
    );

    private PSTEditorCompat() {
    }

    public static void ensureParentDirectories(File file) {
        if (file == null) {
            return;
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public static Component editorDisabledMessage() {
        return EDITOR_DISABLED_MESSAGE.copy();
    }

    public static void showEditorDisabledMessage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(editorDisabledMessage(), false);
        }
    }
}
