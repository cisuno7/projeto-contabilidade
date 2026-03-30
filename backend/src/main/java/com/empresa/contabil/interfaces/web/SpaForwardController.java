package com.empresa.contabil.interfaces.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Permite refresh direto nas rotas do React (client-side) quando o front é servido pelo Spring.
 */
@Controller
public class SpaForwardController {

    @GetMapping({
            "/",
            "/upload",
            "/historico",
            "/clientes",
            "/login",
            "/register"
    })
    public String forwardIndex() {
        return "forward:/index.html";
    }
}
