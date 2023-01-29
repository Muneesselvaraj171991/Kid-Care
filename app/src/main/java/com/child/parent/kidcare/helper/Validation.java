package com.child.parent.kidcare.helper;

import com.google.android.material.textfield.TextInputEditText;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Validation {

    //Checks if the country code is valid or not
    public boolean countryCodeExisting( TextInputEditText editText) {
        String currentValue = editText.getText().toString();
        if(currentValue.equals(""))
            return false;

        else if (currentValue.charAt(0) == '+')
        {
            if (currentValue.length() < 3)
                return false;

            else
            {
                int endindex=-1;
                for(int x = 0; x<currentValue.length();x++)
                {
                    if(currentValue.charAt(x)==' ')
                    {endindex = x; break;}
                }
                if(endindex==-1)
                    return false;
                else
                {
                    String dialCode = currentValue.substring(1, endindex);
                    return new CountryCode().isValidDialCode(dialCode);

                }
            }
        }
        else
            return false;

    }

    //Checks if the text is perfect Phone number
    public boolean isValidPhone(String phone) {
        boolean validity = false;
        if(phone.length()<=15 && phone.length()>=5)
            validity=true;
        return validity;
    }

    public static DateTimeFormatter getTimeFormater() {
        return DateTimeFormatter.ofPattern((String)"h:mm a", (Locale)Locale.US);
    }

    public static boolean isValidEmail(String email) {
        return  true;
    }

    public static boolean isValidMobileNumber(String mobileNumber) {
        return true;
    }
}
