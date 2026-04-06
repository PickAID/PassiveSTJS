package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;

@SuppressWarnings("unchecked")
public abstract class TypedJsonBuilder<T extends TypedJsonBuilder<T>>
        implements JsonFragment {
    protected final JsonObject json;

    protected TypedJsonBuilder(String type) {
        this.json = JsonHelper.typedObject(type);
    }

    protected final T self() {
        return (T) this;
    }

    public String type() {
        return json.get("type").getAsString();
    }

    public JsonObject json() {
        return toJson().getAsJsonObject();
    }

    public T raw(String key, Object value) {
        json.add(key, JsonHelper.requireElement(value, key));
        return self();
    }

    public T optionalRaw(String key, Object value) {
        JsonElement element = JsonHelper.optionalElement(value);
        if (element == null) {
            json.remove(key);
        } else {
            json.add(key, element);
        }
        return self();
    }

    public T string(String key, String value) {
        if (value == null || value.isBlank()) {
            json.remove(key);
        } else {
            json.addProperty(key, value);
        }
        return self();
    }

    public T integer(String key, Number value) {
        if (value == null) {
            json.remove(key);
        } else {
            json.addProperty(key, value.intValue());
        }
        return self();
    }

    public T number(String key, Number value) {
        if (value == null) {
            json.remove(key);
        } else {
            json.addProperty(key, value.doubleValue());
        }
        return self();
    }

    public T bool(String key, Boolean value) {
        if (value == null) {
            json.remove(key);
        } else {
            json.addProperty(key, value);
        }
        return self();
    }

    @Override
    public JsonObject toJson() {
        return JsonHelper.copy(json).getAsJsonObject();
    }
}
