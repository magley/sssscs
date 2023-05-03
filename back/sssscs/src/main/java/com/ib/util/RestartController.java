package com.ib.util;

import com.ib.SssscsApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestartController {

    @PostMapping("/restart")
    public void restart() {
        SssscsApplication.restart();
    }
}
