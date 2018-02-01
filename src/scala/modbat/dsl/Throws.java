package modbat.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/** Specify which exceptions a method annotated with "@states" may throw. */
public @interface Throws {
  String[] value();
}
