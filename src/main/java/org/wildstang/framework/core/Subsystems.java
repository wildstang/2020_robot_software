package org.wildstang.framework.core;

/* TODO: make this a superclass and move method definitions and member decls into here from the
 * yearly enums. We could be reducing code duplication. */
public interface Subsystems {
    public String getName();

    public Class<?> getSubsystemClass();
}
