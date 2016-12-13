import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	    public static void Info(String message)
	    {
	        System.out.println("[" + getFormatedTimeStamp()  +"]" + "|Info|" + message);
	    }
	    
	    public static void Print(String message)
	    {
	        System.out.print("[" + getFormatedTimeStamp()  +"]|" + message);
	    }

	    public static void Error(String message)
	    {
	        System.out.println("[" + getFormatedTimeStamp()  +"]" + "|Error|" + message);
	    }

	    public static void Error(String message, Exception exception)
	    {
	        System.out.println("[" + getFormatedTimeStamp()  +"]" + "|Error|" + message + "|" + exception.getMessage());
	    }

	    private static String getFormatedTimeStamp()
	    {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        Date date = new Date();
	        return dateFormat.format(date);
	    }	
}