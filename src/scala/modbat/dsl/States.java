package modbat.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/** Specify in which states a method can be called.
  * This adds a self-loop
    "state" -> "state" := method, for each state. */
public @interface States {
  String[] value();
}
