package bluen.homein.gayo_security.rest;

public class RetrofitURL {
// https://documenter.getpostman.com/view/11376758/2sAYJ9BJbj#3f4384e1-de42-4e57-91cf-ba9f45d67d7b // 포스트맨 문서 url

    public final static String BASE_URL = "https://gayo-smarthome-guard.bluen.co.kr/ "; // REAL SERVER
    public final static String BASE_TEST_URL = "https://gayo-smarthome-guard-dev.azurewebsites.net/"; // TEST SERVER
    public final static String WEBSOCKET_SERVER_URL = "wss://gayo-smarthome-guard.bluen.co.kr/ws "; // WebSocket SERVER

    //** 로그인 및 메인
    public final static String LOGIN_URL = "dev/login";
    public final static String WEATHER_INFO_URL = "device/wt/info";
    public final static String CURRENT_WORKER_INFO = "main/work/manager/info";

    //** 앱 부팅시 필요한 데이터
    public final static String LOAD_NETWORK_DATA = "device/network/info";
    public final static String LOAD_SETTING_DATA = "device/setting/info";

    //* 설정
    public final static String SAVE_SETTING_DATA = "device/update/setting";

    //** 연락처 추가
    public final static String DELETE_FACILITY_CONTACT = "device/my/conn/device/del"; //연락처(시설명) 삭제
    public final static String ADD_FACILITY_CONTACT = "device/my/conn/device/prod"; //연락처(시설명) 추가
    public final static String LOAD_FACILITY_CONTACT_LIST = "device/my/conn/list"; //연락처(시설명) 목록

    //** 근무자 변경
    public final static String WORKER_LIST = "work/manager/list";
    public final static String WORKER_CHANGE = "work/update/manager/2";

    //** 근무 기록
    public final static String WORK_RECORD_LIST = "work/manager/his/list";
    public final static String WORKER_PHONE_NUMBER_LIST = "build/manager/list";

    //** 통화 목록
    public final static String LOAD_CALL_LOG_LIST = "device/search/call";
    public final static String DELETE_CALL_LOG_DATA = "device/call/delete/seq";

    //** 방문자 목록
    public final static String LOAD_VISITOR_LOG_LIST = "ent/search/list";
    public final static String DELETE_VISITOR_LOG_RECORD = "ent/delete/seq";

    //** 공통 코드
    public final static String LOAD_WORK_TYPE_LIST = "etc/worktype/list";
    public final static String LOAD_PATROL_MODE_TYPE_LIST = "etc/patrol/list";
    public final static String LOAD_BELL_SOUND_TYPE_LIST = "etc/patrol/list";
    public final static String LOAD_CALL_STATE_TYPE_LIST = "etc/call/state/log/info";
    public final static String LOAD_VISITOR_TYPE_LIST = "etc/ent/type/info";
    public final static String LOAD_UI_BACKGROUND_TYPE_LIST = "etc/ui/mode/info";

}
