package com.example.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.auth.dto.CreateProfileRequest;

@FeignClient(name = "user-service")
public interface UserServiceClient {

	@PostMapping("/users/profile")
	void createProfile(@RequestBody CreateProfileRequest request);
}
