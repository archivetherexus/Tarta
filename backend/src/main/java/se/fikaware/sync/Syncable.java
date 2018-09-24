package se.fikaware.sync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Syncable {
    Class<? extends ObjectSyncer> syncer() default UnsetObjectSyncer.class;
}
