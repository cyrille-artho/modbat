package modbat.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

/** Define value of variable that has to set
  * if annotated option is non-null/0/empty. */
public @interface Requires {
  String opt();
  String equals()	default "";
  String notEquals()	default "";
}
