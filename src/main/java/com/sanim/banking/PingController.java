package com.sanim.banking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Two things combined, @Controller says instances of this class handles HTTP requests, at startup it would
// scan for stuff like this to register its methods with the request-routing system, @ResponseBody is saying that
// whatever these methods return should be serialised into a HTTP request, rather than just a plain string that
// would be like {returned_value}.html
@RestController
public class PingController {
    // @GetMapping when provided with a path, whenever a HTTP GET request appears for this path, then call
    // this method, shorthand for: @RequestMapping(method = RequestMethod.GET, value = "/ping")
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
