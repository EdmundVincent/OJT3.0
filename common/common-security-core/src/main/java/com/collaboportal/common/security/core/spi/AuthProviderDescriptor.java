package com.collaboportal.common.security.core.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthProviderDescriptor {

    private final String id;
    private final List<String> aliases;
    private final int priority;

    public AuthProviderDescriptor(String id, List<String> aliases, int priority) {
        this.id = id;
        this.aliases = aliases == null ? new ArrayList<>() : new ArrayList<>(aliases);
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    public int getPriority() {
        return priority;
    }

    public boolean matches(String type) {
        if (type == null) {
            return false;
        }
        String normalized = type.trim().toLowerCase();
        if (id != null && id.trim().equalsIgnoreCase(normalized)) {
            return true;
        }
        for (String alias : aliases) {
            if (alias != null && alias.trim().equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }
}
