package service;

import java.util.List;

import model.*;
import storage.Storage;

public class Service {
	private final static Service instance = new Service();
	private User user;
	private Storage storage = Storage.getInstance();
	
	private Service() {}
	
	/*
	 * returns a user if the username and password is corrent
	 * if username or password is not correct it returns null
	 */
	public User login(String username, String password) throws Exception {
    	List<User> users = storage.getUsers();
    	
    	for (User u : users) {
    		if (u.getUsername().equals(username)) {
//    			if (u.checkPassword(password)) {
    				user = u;
//    			}
    		}
    	}
    	
    	throw new Exception("wrong username or password");
    }
	
	public User getUser() {
		return user;
	}
	
	public User createUser(String username, String password) {
		User u = new User(username, password);
		
		storage.addUser(u);
		
		return u;
	}
	
	public void initStorage() {
		createUser("test", "test");
	}
	
	public static Service getInstance() {
		return instance;
	}
}
