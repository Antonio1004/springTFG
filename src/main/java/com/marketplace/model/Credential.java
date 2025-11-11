package com.marketplace.model;

public class Credential {
    private String email;
    private String password;

    public Credential() {
    }

    

    public Credential(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}



	// Getters y setters


    public String getPassword() {
        return password;
    }

    

    public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public void setPassword(String password) {
        this.password = password;
    }
}