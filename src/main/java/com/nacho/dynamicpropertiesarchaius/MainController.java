package com.nacho.dynamicpropertiesarchaius;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nacho.dynamicpropertiesarchaius.service.ArchaiusMongoPropertiesService;
import com.nacho.dynamicpropertiesarchaius.service.ArchaiusZookeeperPropertiesService;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    private ArchaiusZookeeperPropertiesService zk;

    @Autowired
    private ArchaiusMongoPropertiesService mongo;

    @GetMapping(path = "ping")
    public @ResponseBody String ping() {
        return "pong";
    }

    @GetMapping(path = "/property/zk/{nombre}")
    public @ResponseBody String zookeeper(@PathVariable("nombre") final String nombre) {
        return zk.getString(nombre);
    }

    @GetMapping(path = "/property/mongo/{nombre}")
    public @ResponseBody String mongo(@PathVariable("nombre") final String nombre) {
        return mongo.getString(nombre);
    }
}
