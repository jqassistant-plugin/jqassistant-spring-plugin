package org.jqassistant.plugin.spring.test.set.application.valid;

import org.jqassistant.plugin.spring.test.set.application.valid.service.Service;

import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Controller
public class Controller {

    private Service service;

    @Autowired
    public Controller(Service service) {
        this.service = service;
    }
}
