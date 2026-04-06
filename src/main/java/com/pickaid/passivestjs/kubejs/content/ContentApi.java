package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonElement;

public final class ContentApi {
    public static final ContentApi INSTANCE = new ContentApi();

    private ContentApi() {
    }

    public Bonuses bonuses() {
        return Bonuses.INSTANCE;
    }

    public Conditions conditions() {
        return Conditions.INSTANCE;
    }

    public Requirements requirements() {
        return Requirements.INSTANCE;
    }

    public Listeners listeners() {
        return Listeners.INSTANCE;
    }

    public Values values() {
        return Values.INSTANCE;
    }

    public Multipliers multipliers() {
        return Multipliers.INSTANCE;
    }

    public Skills skills() {
        return Skills.INSTANCE;
    }

    public JsonElement json(Object value) {
        return JsonHelper.requireElement(value, "value");
    }

    public String pretty(Object value) {
        return JsonHelper.pretty(value);
    }
}
