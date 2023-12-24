package lc.bungeeauth.managers;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.SecureRandom;


public class SecureUtils {
    public static String encriptPassword(String password){
        String generatedPassword = null;
        generatedPassword = DigestUtils.sha256Hex(password);
        return generatedPassword;
    }

    public static String generateRandomPassword(){
        SecureRandom random = new SecureRandom();
        String text = new BigInteger((5 * AuthManager.getMinpasslenght()) +1, random).toString(32);
        return text;
    }


}
