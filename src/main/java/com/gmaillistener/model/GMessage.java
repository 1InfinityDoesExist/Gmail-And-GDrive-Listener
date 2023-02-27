package com.gmaillistener.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class GMessage implements Serializable {
	@NotEmpty
	private String serviceAccountJsonFileUrl;
	@NotEmpty
	private String admin;

}
