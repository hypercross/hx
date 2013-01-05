package hx.utils;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.minecraftforge.common.Configuration;

@Retention(value = RUNTIME)
@Target(value = FIELD)
public @interface Configurable
{

String value() default Configuration.CATEGORY_GENERAL;
}
