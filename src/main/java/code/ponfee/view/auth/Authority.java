package code.ponfee.view.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * permission 
 * 权限注解，作用于action/controller
 *
 * @author fupf
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authority {

    Rule[] rule() default { Rule.TOKEN, Rule.URL }; // 验证规则

    Type type() default Type.JSON; // 处理类型

    String[] entrust() default {}; // 委托

    String fail() default "{\"code\":201,\"msg\":\"会话超时，请<a style='color:blue;' href='login.html'>重新登录</a>\"}"; // 失败信息
}
