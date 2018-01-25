package code.ponfee.view.controller;

import java.io.PrintWriter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import code.ponfee.view.auth.Authority;
import code.ponfee.view.entity.Person;

/**
 * http://localhost:8000/spring-mvc-view/auth/person
 * http://localhost:8000/spring-mvc-view/auth/person2
 */
@Controller()
@RequestMapping("/auth")
public class AuthorityController {

    /**
     * 测试post请求
     * @param person
     * @param pw
     */
    @Authority(entrust = "person")
    @RequestMapping("/getage")
    public void get(Person person, PrintWriter pw) {
        System.out.println(person.getName());
        pw.write("age " + person.getAge());
    }

    /**
     * 测试spring页面跳转
     * @param map
     * @return
     */
    @Authority
    @RequestMapping("/person")
    public String person(ModelMap map) {
        map.put("key", "value");
        return "person.ftl";
    }

    @RequestMapping("/person2")
    public String person2(ModelMap map) {
        map.put("key", "value");
        return "person.ftl";
    }

}
