package com.kayali_developer.smartphonecafe.utilities;

public class ValidationUtils {
    public static boolean checkEmailValidation(String emailAddress){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }
}
