package com.collaboportal.common.jwt.config.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * コントローラのメソッド引数に付与することで、
 * JWTトークンから企画IDのリストを自動的に取得し、その引数に値を注入するアノテーション。
 *
 * @see KikakuIDArgumentResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface KikakuIDs {
    /**
     * 企画IDが必須かどうかを示します。
     * デフォルトはtrueで、企画IDが取得できない場合は例外がスローされます。
     * falseに設定すると、取得できない場合にnullが引数に渡されます。
     */
    boolean required() default true;
}
