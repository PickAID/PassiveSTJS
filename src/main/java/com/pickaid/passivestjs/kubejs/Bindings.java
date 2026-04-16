package com.pickaid.passivestjs.kubejs;

import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.runtime.PSTItemView;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTSkillView;
import com.pickaid.passivestjs.kubejs.runtime.PSTTreeView;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class Bindings {
    public static final Bindings INSTANCE = new Bindings();

    private Bindings() {
    }

    @Info(value = "Returns the runtime PST player helper view for the given player.", params = {
            @Param(name = "player", value = "The player whose PST runtime state should be exposed.")
    })
    public PSTPlayerView player(Player player) {
        if (player == null || !PlayerSkillsProvider.hasSkills(player)) {
            return null;
        }
        return new PSTPlayerView(player, PlayerSkillsProvider.get(player));
    }

    @Info(value = "Returns the read-only PST skill definition view for the given skill id.", params = {
            @Param(name = "id", value = "The PST skill id to look up.")
    })
    public PSTSkillView skill(PSTSkillId id) {
        if (id == null) {
            return null;
        }

        PassiveSkill skill = SkillsReloader.getSkillById(id.location());
        if (skill == null) {
            return null;
        }
        return new PSTSkillView(skill);
    }

    @Info(value = "Returns the read-only PST skill tree definition view for the given tree id.", params = {
            @Param(name = "id", value = "The PST skill tree id to look up.")
    })
    public PSTTreeView tree(PSTTreeId id) {
        if (id == null) {
            return null;
        }

        PassiveSkillTree tree = SkillTreesReloader.getSkillTrees().get(id.location());
        if (tree == null) {
            return null;
        }
        return new PSTTreeView(tree);
    }

    @Info(value = "Returns the PST item-bonus helper view for the given stack.", params = {
            @Param(name = "stack", value = "The item stack whose PST item bonuses should be exposed.")
    })
    public PSTItemView item(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        return new PSTItemView(stack);
    }
}
