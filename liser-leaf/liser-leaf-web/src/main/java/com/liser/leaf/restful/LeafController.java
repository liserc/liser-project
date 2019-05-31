package com.liser.leaf.restful;

import com.liser.leaf.exceptions.LeafServerException;
import com.liser.leaf.exceptions.NoKeyException;
import com.liser.leaf.service.SegmentService;
import com.liser.leaf.service.SnowflakeService;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author LISER
 */
@Slf4j
@RestController
public class LeafController {

    @Autowired
    SegmentService segmentService;

    @Autowired
    SnowflakeService snowflakeService;

    @RequestMapping(value = "/api/segment/get/{key}")
    public Mono<String> getSegmentID(@PathVariable("key") String key) {
        String result = get(key, segmentService.getId(key));
        return Mono.just(result);
    }

    @RequestMapping(value = "/api/snowflake/get/{key}")
    public Mono<String> getSnowflakeID(@PathVariable("key") String key) {
        String result = get(key, snowflakeService.getId(key));
        return Mono.just(result);
    }

    private String get(String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }

        result = id;
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result.toString());
        }

        return String.valueOf(result.getId());
    }
}
