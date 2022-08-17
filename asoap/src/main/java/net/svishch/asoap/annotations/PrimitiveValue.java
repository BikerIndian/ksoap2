package net.svishch.asoap.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * SOAP Envelope namespace
 * Author: Vladimir Svishch
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
public @interface PrimitiveValue {
}

