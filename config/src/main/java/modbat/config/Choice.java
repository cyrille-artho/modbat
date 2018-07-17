package modbat.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

/** Specify a choice of various values.
  * Symbolic constants are defined in a particular class. */
public @interface Choice {
  String[] value();
  String definedIn() default "";
}
