package org.pedrohrr.pacts.provider.resources

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/messages")
class MessageResource {

    @GetMapping
    fun getMessage(@RequestParam("parameter") parameter: String): Response {
        return Response("any message in here")
    }

}

data class Response(val message: String)