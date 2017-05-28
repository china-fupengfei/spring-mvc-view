package code.ponfee.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping(path = "/")
    public String hello() {
        return "redirect:page/index.html";
    }

}
