package com.pelatro.signup.payload;



public class SignupRequest {
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getReconfirmPassword() {
		return reconfirmPassword;
	}
	public void setReconfirmPassword(String reconfirmPassword) {
		this.reconfirmPassword = reconfirmPassword;
	}
	private String name;
    private String username;
    private String email;
    private String password;
    private String reconfirmPassword;

    // Getters and Setters
}

