package com.example.user.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

	private Integer authId;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String address;
}
