package com.empresa.contabil.interfaces.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Permite refresh direto nas rotas do React (client-side) quando o front é servido pelo Spring.
 */
@Controller
public class SpaForwardController {

    @GetMapping(value = {
            "/",
            "/upload",
            "/historico",
            "/login",
            "/register"
    }, produces = MediaType.TEXT_HTML_VALUE)
    public String forwardIndex() {
        return "forward:/index.html";
    }

    /**
     * Mesmo path que a API {@code GET /clientes} (JSON), mas só para navegação/refresh (Accept: text/html).
     */
    @GetMapping(value = "/clientes", produces = MediaType.TEXT_HTML_VALUE)
    public String forwardClientes() {
        return "forward:/index.html";
    }
}
