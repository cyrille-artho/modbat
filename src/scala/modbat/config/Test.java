package modbat.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

/** Override the usual initialization when in test mode. */
public @interface Test {
  int intval() default 0; 
  long longval() default 0; 
  // TODO: Add other types as needed.
}

