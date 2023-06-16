package com.ib.util.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubEmail {
	private String email;
	private Boolean primary;
	private Boolean verified;
	private String visibility;
}
