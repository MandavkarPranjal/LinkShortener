package com.lossstheaven.linkshortener;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.xml.validation.Validator;
import java.nio.charset.StandardCharsets;

@RequestMapping("")
@RestController
public class LinkShortenerResource {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/{id}")
    public String getLink(@PathVariable String id) {

        String url = redisTemplate.opsForValue().get(id);
        System.out.println("Link Retrived: "+url);

        if (url == null) {
            throw new RuntimeException("There is no shorter link for: "+id);
        }
        return url;
    }

    @PostMapping
    public String createShortLink(@RequestBody String url) {

        UrlValidator urlValidator = new UrlValidator(
                new String[]{"http","https"}
        );

        if (urlValidator.isValid(url)) {
            String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            System.out.println("Shortlink generated: http://localhost:8080/"+id);
            redisTemplate.opsForValue().set(id, url);
            return id;
        }

        throw new RuntimeException("Link Invalid: "+url);
    }
}
