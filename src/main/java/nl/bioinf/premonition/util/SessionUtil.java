package nl.bioinf.premonition.util;
import javax.servlet.http.HttpSession;
import java.util.Random;

/*
Author: Nils Mooldijk
 */

public class SessionUtil {

    /**
     * Returns user ID based on current session.
     */
    public static String getUserID(HttpSession session){

        String user_id = null;
        if(session.getAttribute("user_id") == null){
            long generateLong = new Random().nextLong();
            user_id = generateLong +"";
            session.setAttribute("user_id", user_id);
        }
        else {
            user_id = (String)session.getAttribute("user_id");
        }

        return user_id;
    }
}
