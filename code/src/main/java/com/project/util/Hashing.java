package com.project.util;
import org.mindrot.jbcrypt.BCrypt;

public class Hashing {
	public static String hashPassword(String password) {
		return  BCrypt.hashpw(password, BCrypt.gensalt());
	}
    public static boolean checkPassword(String enteredPassword, String storedHash) {
        return BCrypt.checkpw(enteredPassword, storedHash);
    }
}
