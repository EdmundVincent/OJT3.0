package com.collaboportal.common.jwt.config.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * コントローラのメソッド引数に付与することで、
 * JWTトークンからユーザーIDを自動的に取得し、その引数に値を注入するアノテーション。
 *
 * @see UserIdArgumentResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserId {
    /**
     * ユーザーIDが必須かどうかを示します。
     * デフォルトはtrueで、ユーザーIDが取得できない場合は例外がスローされます。
     * falseに設定すると、取得できない場合にnullが引数に渡されます。
     */
    boolean required() default true;
}
