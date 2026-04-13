package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.compat.skilltree.PSTSkillLearningRules;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PSTPlayerView {
    private final Player player;
    private final ServerPlayer serverPlayer;
    private final IPlayerSkills playerSkills;

    public PSTPlayerView(IPlayerSkills playerSkills) {
        this(null, playerSkills);
    }

    public PSTPlayerView(Player player, IPlayerSkills playerSkills) {
        this.player = player;
        this.serverPlayer = player instanceof ServerPlayer server ? server : null;
        this.playerSkills = Objects.requireNonNull(playerSkills, "playerSkills");
    }

    public PSTPlayerView(ServerPlayer serverPlayer, IPlayerSkills playerSkills) {
        this((Player) serverPlayer, playerSkills);
    }

    @Info("Returns the player's current PST skill points.")
    public int skillPoints() {
        return playerSkills.getSkillPoints();
    }

    @Info("Returns whether PST currently marks this player's tree as reset-pending.")
    public boolean treeReset() {
        return playerSkills.isTreeReset();
    }

    @Info(value = "Grants PST skill points to this player, syncs the client view, and returns the new total.", params = {
            @Param(name = "amount", value = "How many skill points to grant.")
    })
    public int grantSkillPoints(int amount) {
        if (amount <= 0) {
            return skillPoints();
        }

        playerSkills.grantSkillPoints(amount);
        syncPlayer();
        return skillPoints();
    }

    @Info(value = "Consumes PST skill points when enough are available, syncs the client view, and returns whether the mutation succeeded.", params = {
            @Param(name = "amount", value = "How many skill points to consume.")
    })
    public boolean consumeSkillPoints(int amount) {
        if (amount < 0) {
            return false;
        }
        if (amount == 0) {
            return true;
        }
        if (skillPoints() < amount) {
            return false;
        }

        playerSkills.setSkillPoints(skillPoints() - amount);
        syncPlayer();
        return true;
    }

    @Info(value = "Returns whether the player currently has the provided skill learned.", params = {
            @Param(name = "id", value = "The PST skill id to check.")
    })
    public boolean hasSkill(PSTSkillId id) {
        if (id == null) {
            return false;
        }
        return hasSkill(id.location());
    }

    private boolean hasSkill(ResourceLocation id) {
        if (id == null) {
            return false;
        }

        for (PassiveSkill skill : playerSkills.getPlayerSkills()) {
            if (skill != null && id.equals(skill.getId())) {
                return true;
            }
        }
        return false;
    }

    @Info(value = "Learns the provided PST skill through PST's canonical runtime learn path without consuming skill points, then syncs the client view.", params = {
            @Param(name = "id", value = "The PST skill id to learn.")
    })
    public boolean learn(PSTSkillId id) {
        PassiveSkill skill = resolveUnlearnedSkill(id == null ? null : id.location());
        if (skill == null) {
            return false;
        }
        return learnCanonical(skill, skillPoints());
    }

    @Info(value = "Learns the provided PST skill without consuming skill points, then syncs the client view.", params = {
            @Param(name = "id", value = "The PST skill id to learn.")
    })
    public boolean learnWithoutSkillPointCost(PSTSkillId id) {
        return learn(id);
    }

    @Info(value = "Uses PST's canonical runtime learn path and only commits the provided skill point cost when learning succeeds, then syncs the client view.", params = {
            @Param(name = "id", value = "The PST skill id to learn."),
            @Param(name = "cost", value = "The skill point cost to consume when learning succeeds.")
    })
    public boolean learnWithSkillPointCost(PSTSkillId id, int cost) {
        if (cost < 0) {
            return false;
        }

        PassiveSkill skill = resolveUnlearnedSkill(id == null ? null : id.location());
        if (skill == null) {
            return false;
        }
        int previousPoints = skillPoints();
        if (previousPoints < cost) {
            return false;
        }
        return learnCanonical(skill, previousPoints - cost);
    }

    @Info(value = "Removes one learned PST skill by id without refunding skill points, then syncs the client view.", params = {
            @Param(name = "id", value = "The PST skill id to remove.")
    })
    public boolean remove(PSTSkillId id) {
        PassiveSkill learnedSkill = findLearnedSkill(id == null ? null : id.location());
        if (learnedSkill == null) {
            return false;
        }

        if (serverPlayer != null) {
            learnedSkill.remove(serverPlayer);
        }
        playerSkills.getPlayerSkills().remove(learnedSkill);
        syncPlayer();
        return true;
    }

    @Info("Resets all currently learned PST skills without refunding skill points, syncs the client view, and returns the current total.")
    public int reset() {
        List<PassiveSkill> learnedSkills = List.copyOf(playerSkills.getPlayerSkills());
        if (serverPlayer != null) {
            for (PassiveSkill skill : learnedSkills) {
                if (skill != null) {
                    skill.remove(serverPlayer);
                }
            }
        }
        playerSkills.getPlayerSkills().clear();
        syncPlayer();
        return skillPoints();
    }

    @Info("Returns the learned PST skill ids in player order.")
    public List<String> learnedSkillIds() {
        List<String> ids = new ArrayList<>();
        for (PassiveSkill skill : playerSkills.getPlayerSkills()) {
            if (skill != null && skill.getId() != null) {
                ids.add(skill.getId().toString());
            }
        }
        return List.copyOf(ids);
    }

    @Info("Returns read-only runtime skill views for all learned skills.")
    public List<PSTSkillView> learnedSkills() {
        List<PSTSkillView> views = new ArrayList<>();
        for (PassiveSkill skill : playerSkills.getPlayerSkills()) {
            if (skill == null || skill.getId() == null) {
                continue;
            }

            PassiveSkill resolved = SkillsReloader.getSkillById(skill.getId());
            views.add(new PSTSkillView(resolved != null ? resolved : skill));
        }
        return List.copyOf(views);
    }

    @Info(value = "Returns the learned PST skill view for the provided skill id, or null when it is not learned.", params = {
            @Param(name = "id", value = "The PST skill id to resolve from the player's learned state.")
    })
    public PSTSkillView learnedSkill(PSTSkillId id) {
        PassiveSkill learned = findLearnedSkill(id == null ? null : id.location());
        if (learned == null) {
            return null;
        }

        PassiveSkill resolved = SkillsReloader.getSkillById(learned.getId());
        return new PSTSkillView(resolved != null ? resolved : learned);
    }

    @Info(value = "Returns whether the player currently has at least one learned skill inside the provided PST tree.", params = {
            @Param(name = "id", value = "The PST tree id to inspect.")
    })
    public boolean hasLearnedInTree(PSTTreeId id) {
        return !learnedSkillsInTree(id).isEmpty();
    }

    @Info(value = "Returns read-only runtime skill views for the learned skills that belong to the provided PST tree.", params = {
            @Param(name = "id", value = "The PST tree id whose learned skills should be returned.")
    })
    public List<PSTSkillView> learnedSkillsInTree(PSTTreeId id) {
        if (id == null) {
            return List.of();
        }

        PassiveSkillTree tree = SkillTreesReloader.getSkillTrees().get(id.location());
        if (tree == null || tree.getSkillIds().isEmpty()) {
            return List.of();
        }

        List<PSTSkillView> views = new ArrayList<>();
        for (var skillId : tree.getSkillIds()) {
            PassiveSkill learned = findLearnedSkill(skillId);
            if (learned == null) {
                continue;
            }

            PassiveSkill resolved = SkillsReloader.getSkillById(skillId);
            views.add(new PSTSkillView(resolved != null ? resolved : learned));
        }
        return List.copyOf(views);
    }

    private PassiveSkill resolveUnlearnedSkill(ResourceLocation id) {
        if (id == null || hasSkill(id)) {
            return null;
        }

        PassiveSkill skill = SkillsReloader.getSkills().get(id);
        if (skill == null || skill.isInvalid()) {
            return null;
        }
        return skill;
    }

    private PassiveSkill findLearnedSkill(ResourceLocation id) {
        if (id == null) {
            return null;
        }

        for (PassiveSkill skill : playerSkills.getPlayerSkills()) {
            if (skill != null && id.equals(skill.getId())) {
                return skill;
            }
        }
        return null;
    }

    @Info(value = "Returns a player-bound runtime skill helper view for the provided skill id, or null when the skill definition does not exist.", params = {
            @Param(name = "id", value = "The PST skill id to look up.")
    })
    public PSTPlayerSkillView skill(PSTSkillId id) {
        if (id == null) {
            return null;
        }
        ResourceLocation skillId = id.location();
        if (skillId == null) {
            return null;
        }

        PassiveSkill resolved = SkillsReloader.getSkillById(skillId);
        if (resolved == null) {
            resolved = SkillsReloader.getSkills().get(skillId);
        }
        if (resolved == null) {
            for (PassiveSkill learned : playerSkills.getPlayerSkills()) {
                if (learned != null && skillId.equals(learned.getId())) {
                    resolved = learned;
                    break;
                }
            }
        }
        if (resolved == null || resolved.isInvalid()) {
            return null;
        }

        return new PSTPlayerSkillView(player, playerSkills, this, resolved);
    }

    private boolean learnCanonical(PassiveSkill skill, int finalSkillPoints) {
        int previousPoints = skillPoints();
        if (previousPoints == 0) {
            playerSkills.grantSkillPoints(1);
        }

        boolean canLearn = player != null
                ? PSTSkillLearningRules.canLearn(player, playerSkills, skill)
                : PSTSkillLearningRules.canLearn(playerSkills, skill);
        if (!canLearn) {
            playerSkills.setSkillPoints(previousPoints);
            return false;
        }

        if (!playerSkills.learnSkill(skill)) {
            playerSkills.setSkillPoints(previousPoints);
            return false;
        }

        playerSkills.setSkillPoints(finalSkillPoints);
        if (serverPlayer != null) {
            skill.learn(serverPlayer, true);
        }
        syncPlayer();
        return true;
    }

    private void syncPlayer() {
        if (serverPlayer == null || NetworkDispatcher.network_channel == null) {
            return;
        }

        NetworkDispatcher.network_channel.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new SyncPlayerSkillsMessage(serverPlayer)
        );
    }
}
