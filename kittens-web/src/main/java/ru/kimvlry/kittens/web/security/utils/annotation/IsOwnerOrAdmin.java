package ru.kimvlry.kittens.web.security.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@securityAnnotationUtils.isOwnerOrAdmin(authentication.name, #id)")
public @interface IsOwnerOrAdmin {
}