package com.example.damas.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "modal";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/historico")
    public String historico() {
        return "historico";
    }
    @GetMapping("/torneio")
    public String torneio() {
        return "torneio";
    }
}
