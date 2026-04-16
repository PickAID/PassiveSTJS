package com.pickaid.passivestjs.kubejs.event;

import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

class SkillTreeContentEventBridgeTest {
    @Test
    void wrapReusesOriginalDataPackAndResourceManager() throws ReflectiveOperationException {
        VirtualKubeJSDataPack pack = new VirtualKubeJSDataPack(true);
        MultiPackResourceManager manager = new MultiPackResourceManager(PackType.SERVER_DATA, List.of(pack));
        DataPackEventJS rawEvent = new DataPackEventJS(pack, manager);

        SkillTreeContentEventJS wrapped = SkillTreeContentEventJS.wrap(rawEvent);

        assertSame(pack, field("virtualDataPack").get(wrapped));
        assertSame(manager, field("wrappedManager").get(wrapped));
    }

    @Test
    void wrapReturnsSameInstanceForAlreadyWrappedEvent() {
        SkillTreeContentEventJS event = new SkillTreeContentEventJS(new VirtualKubeJSDataPack(true), null);

        assertSame(event, SkillTreeContentEventJS.wrap(event));
    }

    private static Field field(String name) throws ReflectiveOperationException {
        Field field = DataPackEventJS.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
