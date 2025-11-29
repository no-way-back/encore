package com.nowayback.funding.application.client.project;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
	name = "project-service",
	url = "${feign.client.config.project-service.url}"
)
public interface ProjectClient {
}
