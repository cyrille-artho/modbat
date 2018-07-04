package modbat.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

/** Specify the permissible range of a value. */
public @interface Range {
  int min() default Integer.MIN_VALUE;
  int max() default Integer.MAX_VALUE;
  long ulmin() default Long.MIN_VALUE; // limit is treated as unsigned long
  long ulmax() default Long.MAX_VALUE; // limit is treated as unsigned long
  double dmin() default Double.NEGATIVE_INFINITY;
  double dmax() default Double.POSITIVE_INFINITY;
}

