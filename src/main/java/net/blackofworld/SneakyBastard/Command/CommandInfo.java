package net.blackofworld.SneakyBastard.Command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String command();

    String description() default "";

    String syntax() default "";

    int category();

    int requiredArgs() default 0;
}
