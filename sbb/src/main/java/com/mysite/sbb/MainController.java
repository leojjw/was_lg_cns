package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/sbb")
    @ResponseBody
    public String index() {
        return "hello";
    }

    @GetMapping("/")
    public String root() {
        return "main_page";
    }

    @GetMapping("/search")
    public String search() {
        return "searching_chat";
    }
}
