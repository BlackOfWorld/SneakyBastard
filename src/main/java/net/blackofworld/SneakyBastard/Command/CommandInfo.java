package net.blackofworld.SneakyBastard.Command;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String command();

    String description() default "";

    String Syntax();

    CommandCategory category();

    int requiredArgs() default 0;
}
