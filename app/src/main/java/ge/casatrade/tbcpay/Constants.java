package ge.casatrade.tbcpay;

/**
 * Created by Gio on 2/25/17. For CasaTrade(C)
 */

public class Constants {
    public static final String SALT = "eKn7L2vCT2zNQj69";
    public static final int REQUEST_START_SCAN = 1;

/*    public static final String URL_DEFAULT_ROOT  = "https://www.gpscontrol.ge/casautil/system/php/ws/";
    public static final String URL_GET_BASIC_INFO_LIST = URL_DEFAULT_ROOT + "getinfo.php";
    public static final String URL_LOGIN = URL_DEFAULT_ROOT + "login.php";
    public static final String URL_DEVICE_INFO = URL_DEFAULT_ROOT + "deviceinfo.php";
    public static final String URL_CONTROLLERS = URL_DEFAULT_ROOT + "controllers_list.php";
    public static final String URL_COMPANIES = URL_DEFAULT_ROOT + "company_list.php";
    public static final String URL_POST = URL_DEFAULT_ROOT + "postjob.php";
    public static final String URL_POST_IMAGE = URL_DEFAULT_ROOT + "saveimg.php";
    public static final String URL_CHECK_TOKEN = URL_DEFAULT_ROOT + "checklogin.php";
    public static final String URL_GET_JOB_IMAGE = URL_DEFAULT_ROOT + "get_job_img.php";*/


//    private static final String URL_DEFAULT_ROOT  = "http://10.10.0.55/tbcpay/tbcutil/";
    private static final String URL_DEFAULT_ROOT  = "http://92.241.78.217/tbcutil/";
    public static final String URL_LOGIN = URL_DEFAULT_ROOT + "user/login/";
    public static final String URL_GET_TERMINALS = URL_DEFAULT_ROOT + "terminal/list/";
    public static final String URL_GET_TERMINALS_BY_IMEI = URL_DEFAULT_ROOT + "terminaldevice/list/";
    public static final String URL_ASSIGN_DEVICE = URL_DEFAULT_ROOT + "terminal/assigndevice/";
    public static final String URL_GET_CATEGORIES = URL_DEFAULT_ROOT + "event/catlist/";
    public static final String URL_GET_COMMANDS = URL_DEFAULT_ROOT + "terminal/commands/";
    public static final String URL_EXECUTE_COMMAND = URL_DEFAULT_ROOT + "terminal/cmdexecute/";
    public static final String URL_GET_EVENTS = URL_DEFAULT_ROOT + "event/list/";
    public static final String URL_UPLOAD = URL_DEFAULT_ROOT + "terminal/complete/";


    public static final String URL_POST = URL_DEFAULT_ROOT + "installation/postjob/";
    public static final String URL_POST_IMAGE = URL_DEFAULT_ROOT + "installation/saveimg/";
    public static final String URL_GET_JOB_IMAGE = URL_DEFAULT_ROOT + "installation/getimg/";

}
