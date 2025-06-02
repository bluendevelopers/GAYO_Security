package bluen.homein.gayo_security.rest;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class Retrofit {

    public interface LoginApi {

        @POST(RetrofitURL.LOGIN_URL)
        Call<ResponseDataFormat.LoginData> getAuthInfo(@Body RequestDataFormat.DeviceInfoBody body);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 메인 화면

    public interface MainInfoApi {

        @POST(RetrofitURL.CURRENT_WORKER_INFO)
        Call<ResponseDataFormat.CurrentWorkerInfo> currentWorkerPost(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceInfoBody deviceInfoBody);

        @POST(RetrofitURL.WEATHER_INFO_URL)
        Call<ResponseDataFormat.WeatherData> weatherInfoPost(@Header("Authorization") String auth, @Body RequestDataFormat.WeatherBody weatherBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 설정

    public interface PreferencesApi {

        @GET(RetrofitURL.LOAD_BELL_SOUND_TYPE_LIST)
        Call<List<ResponseDataFormat.BellSoundType>> loadBellSoundTypeListGet(@Header("Authorization") String auth);

        @GET(RetrofitURL.LOAD_UI_BACKGROUND_TYPE_LIST)
        Call<List<ResponseDataFormat.UiBackgroundType>> loadUiBackgroundTypeListGet(@Header("Authorization") String auth);

        @GET(RetrofitURL.LOAD_PATROL_MODE_TYPE_LIST)
        Call<List<ResponseDataFormat.PatrolModeListType>> loadPatrolModeTypeListGet(@Header("Authorization") String auth); // 사용자 설정_순찰모드 설정

        @POST(RetrofitURL.EXPORT_SETTING_DATA)
        Call<ResponseDataFormat.SavePreferencesData> exportPreferencesPost(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceBody deviceBody);

        @POST(RetrofitURL.LOAD_SETTING_DATA)
        Call<RequestDataFormat.DeviceBody> loadDeviceDataPost(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceInfoBody body);

        @POST(RetrofitURL.LOAD_NETWORK_DATA)
        Call<RequestDataFormat.DeviceBody> loadNetworkSettingPost(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceInfoBody body);

        @POST(RetrofitURL.CHANGE_PASSWORD)
        Call<ResponseDataFormat.PasswordDataBody> changePassword(@Header("Authorization") String auth, @Body ResponseDataFormat.PasswordDataBody passwordDataBody);

        @POST(RetrofitURL.SAVE_NETWORK_DATA)
        Call<RequestDataFormat.DeviceBody> saveNetworkData(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceNetworkBody body);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 연락처 추가

    public interface AddFacilityContactApi {

        @POST(RetrofitURL.DELETE_FACILITY_CONTACT)
        Call<ResponseDataFormat.FacilityContactListBody> deleteContactPost(@Header("Authorization") String auth, @Body RequestDataFormat.ContactBody contactBody);

        @POST(RetrofitURL.ADD_FACILITY_CONTACT)
        Call<ResponseDataFormat.FacilityContactListBody> addContactPost(@Header("Authorization") String auth, @Body RequestDataFormat.ContactBody contactBody);

        @POST(RetrofitURL.LOAD_FACILITY_CONTACT_LIST)
        Call<ResponseDataFormat.FacilityContactListBody> loadContactListPost(@Header("Authorization") String auth, @Body RequestDataFormat.ContactBody contactBody);

        @POST(RetrofitURL.LOAD_FACILITY_ALL_CONTACT_LIST)
        Call<List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo>> loadAllContactListPost(@Header("Authorization") String auth, @Body RequestDataFormat.ContactBody contactBody);

        @GET(RetrofitURL.LOAD_ALL_DEVICE_LIST)
        Call<List<RequestDataFormat.DeviceNetworkBody>> loadAllDeviceListPost(@Header("Authorization") String auth);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 근무자 변경 화면

    public interface WorkerChangePageApi {

        @POST(RetrofitURL.WORKER_LIST)
        Call<ResponseDataFormat.WorkerListBody> workerListPost(@Header("Authorization") String auth, @Body RequestDataFormat.WorkerListBody weatherBody);

        @POST(RetrofitURL.WORKER_CHANGE)
        Call<ResponseDataFormat.WorkerListBody> workerChangePost(@Header("Authorization") String auth, @Body RequestDataFormat.WorkerBody workerBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }
    //** 근무 기록 화면

    public interface WorkRecordApi {

        @GET(RetrofitURL.LOAD_WORK_TYPE_LIST)
        Call<List<ResponseDataFormat.EtcType>> loadWorkTypeListGet(@Header("Authorization") String auth);

        @POST(RetrofitURL.WORK_RECORD_LIST)
        Call<ResponseDataFormat.WorkRecordListBody> workRecordListPost(@Header("Authorization") String auth, @Body RequestDataFormat.WorkRecordListBody workRecordListBody);

        @POST(RetrofitURL.WORKER_PHONE_NUMBER_LIST)
        Call<List<ResponseDataFormat.WorkerListBody.WorkerInfo>> workerPhoneNumberListPost(@Header("Authorization") String auth, @Body RequestDataFormat.WorkerBody workerBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 통화 목록

    public interface CallRecordApi {

        @GET(RetrofitURL.LOAD_CALL_STATE_TYPE_LIST)
        Call<List<ResponseDataFormat.EtcType>> loadCallStateTypeListGet(@Header("Authorization") String auth);

        @POST(RetrofitURL.LOAD_CALL_LOG_LIST)
        Call<ResponseDataFormat.CallRecordListBody> callRecordListPost(@Header("Authorization") String auth, @Body RequestDataFormat.CallRecordListBody callRecordListBody);

        @POST(RetrofitURL.DELETE_CALL_LOG_DATA)
        Call<ResponseDataFormat.CallRecordListBody> deleteCallRecordPost(@Header("Authorization") String auth, @Body RequestDataFormat.CallRecordListBody callRecordListBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 방문자 목록

    public interface VisitorHistoryApi {

        @GET(RetrofitURL.LOAD_VISITOR_TYPE_LIST)
        Call<List<ResponseDataFormat.VisitType>> loadVisitorTypeListGet(@Header("Authorization") String auth);

        @POST(RetrofitURL.LOAD_VISITOR_LOG_LIST)
        Call<ResponseDataFormat.VisitHistoryListBody> visitHistoryListPost(@Header("Authorization") String auth, @Body RequestDataFormat.VisitorRecordListBody visitorRecordListBody);

        @POST(RetrofitURL.DELETE_VISITOR_LOG_RECORD)
        Call<ResponseDataFormat.VisitHistoryListBody> deleteCallRecordPost(@Header("Authorization") String auth, @Body RequestDataFormat.VisitorRecordListBody visitorRecordListBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    public static class RetrofitOkHttpClient {

        public static OkHttpClient getOkHttpClient() {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client =
                    new OkHttpClient.Builder().addInterceptor(interceptor).build();
            return client;

        }
    }

}

