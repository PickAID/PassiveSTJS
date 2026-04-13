package com.pickaid.passivestjs.kubejs.registry;

import dev.latvian.mods.kubejs.registry.RegistryInfo;

public enum PSTSerializerFamily {
    SKILL_BONUSES {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.SKILL_BONUSES;
        }
    },
    LIVING_MULTIPLIERS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.LIVING_MULTIPLIERS;
        }
    },
    LIVING_CONDITIONS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.LIVING_CONDITIONS;
        }
    },
    DAMAGE_CONDITIONS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.DAMAGE_CONDITIONS;
        }
    },
    ITEM_CONDITIONS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.ITEM_CONDITIONS;
        }
    },
    ENCHANTMENT_CONDITIONS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.ENCHANTMENT_CONDITIONS;
        }
    },
    EVENT_LISTENERS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.EVENT_LISTENERS;
        }
    },
    FLOAT_FUNCTIONS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.FLOAT_FUNCTIONS;
        }
    },
    SKILL_REQUIREMENTS {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.SKILL_REQUIREMENTS;
        }
    },
    ITEM_BONUSES {
        @Override
        public RegistryInfo<?> registryInfo() {
            return PSTRegistryInfos.ITEM_BONUSES;
        }
    };

    public abstract RegistryInfo<?> registryInfo();
}
