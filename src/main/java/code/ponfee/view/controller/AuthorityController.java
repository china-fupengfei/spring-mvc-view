package code.ponfee.view.controller;

import java.io.PrintWriter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import code.ponfee.view.auth.Authority;
import code.ponfee.view.entity.Person;

/**
 * http://localhost:8000/spring-mvc-view/auth/within
 * http://localhost:8000/spring-mvc-view/auth/without
 */
@Controller()
@RequestMapping("/auth")
public class AuthorityController {

    /**
     * 测试post请求
     * @param person
     * @param pw
     */
    @Authority(entrust = "within")
    @RequestMapping("/entrust")
    public void entrust(Person person, PrintWriter pw) {
        System.out.println(person.getName());
        pw.write("age " + person.getAge());
    }

    /**
     * 测试spring页面跳转
     * @param map
     * @return
     */
    @Authority
    @RequestMapping("/within")
    public String within(ModelMap map) {
        map.put("key", "value");
        return "person.ftl";
    }

    @RequestMapping("/without")
    public String without(ModelMap map) {
        map.put("key", "value");
        return "person.ftl";
    }

}
