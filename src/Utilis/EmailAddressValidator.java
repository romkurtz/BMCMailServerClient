package Utilis;

import Client.EmailClient;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** Represents an email validator class
 * @author Kurtz, Rom
 * @version 1.0
 */
public final class EmailAddressValidator
{
    /**
     * the logger of the class
     */
    private static final Logger _logger = Logger.getLogger(EmailAddressValidator.class.getName());

    private EmailAddressValidator()
    {
        //should never getHere
    }


    /**
     * the following restrictions are imposed in the email address' local part by using this regex:
     *
     *     It allows numeric values from 0 to 9.
     *     Both uppercase and lowercase letters from a to z are allowed.
     *     Allowed are underscore “_”, hyphen “-“, and dot “.”
     *     Dot isn't allowed at the start and end of the local part.
     *     Consecutive dots aren't allowed.
     *     For the local part, a maximum of 64 characters are allowed.
     *
     *     Restrictions for the domain part in this regular expression include:
     *
     *     It allows numeric values from 0 to 9.
     *     We allow both uppercase and lowercase letters from a to z.
     *     Hyphen “-” and dot “.” aren't allowed at the start and end of the domain part.
     *     No consecutive dots.
     *
     *     credit to baeldung.com
     * @param email the email address to validate
     * @return true is email is valid, false otherwise
     */
    public static boolean IsValidEmail(String email)
    {
        _logger.entering(EmailAddressValidator.class.getName(), "IsValidEmail");

        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        Pattern pat = Pattern.compile(regexPattern);
        if (email == null)
            return false;
        _logger.exiting(EmailAddressValidator.class.getName(), "IsValidEmail");
        return pat.matcher(email).matches();
    }

    /**
     * validate vendor
     * @param vendor the vendor to validate
     * @param supportedVendors the supported vendors
     * @return true if valid vendor, false otherwise
     */
    public static boolean IsValidEmailVendor(String vendor, String[] supportedVendors)
    {
        return Arrays.asList(supportedVendors).contains(vendor);
    }
}
