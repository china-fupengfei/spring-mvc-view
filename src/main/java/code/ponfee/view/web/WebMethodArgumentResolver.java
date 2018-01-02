package code.ponfee.view.web;

import java.lang.reflect.Constructor;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class WebMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WebRequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) 
    throws Exception {
        Class<?> clazz = parameter.getParameterType();
        Constructor<?> constructor = clazz.getConstructor(new Class[] { String.class });
        WebRequestParam requestParam = parameter.getParameterAnnotation(WebRequestParam.class);
        return constructor.newInstance(webRequest.getParameter(requestParam.value()));
    }
}
