package org.jqassistant.plugin.spring.test.set.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(configuration = FeignClientConfiguration.class)
public interface TestFeignClient {
}
