package bluen.homein.gayo_security.rest;

public class RetrofitURL {
// https://documenter.getpostman.com/view/11376758/2sAYJ9BJbj#3f4384e1-de42-4e57-91cf-ba9f45d67d7b // 포스트맨 문서 url

   public final static String BASE_URL = "https://gayo-smarthome-guard.bluen.co.kr/ "; // REAL SERVER
   public final static String BASE_TEST_URL = "https://gayo-smarthome-guard-dev.azurewebsites.net/"; // TEST SERVER

    //** 메인
    public final static String LOGIN_URL = "dev/login";
    public final static String WEATHER_INFO_URL = "device/wt/info";
    public final static String CURRENT_WORKER_INFO = "main/work/manager/info";

    //** 근무자 변경
    public final static String WORKER_LIST = "work/manager/list";
    public final static String WORKER_CHANGE = "work/update/manager/2";

    //** 근무 기록
    public final static String WORK_RECORD_LIST = "work/manager/his/list";

}
