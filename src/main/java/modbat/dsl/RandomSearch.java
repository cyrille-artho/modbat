package modbat.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/** Mark that a field is to be traced at run-time (monitored against
    changes). */
public @interface RandomSearch {
    String[] value();
}
