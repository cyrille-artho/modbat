package modbat.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/** Specify the weight for method annotated with "@states".
    Note that each state results in a self-transition to be added to
    the model, which overall makes it very likely that annotated
    methods that work over many states are chosen. */
public @interface Weight {
  double value();
}
