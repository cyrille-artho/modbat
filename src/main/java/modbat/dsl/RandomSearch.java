package modbat.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/** Mark that a test Model is to be run with random testing*/
public @interface RandomSearch {
    String[] value();
}
