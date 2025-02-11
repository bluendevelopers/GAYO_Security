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

        @POST(RetrofitURL.LOAD_SETTING_DATA)
        Call<RequestDataFormat.PreferencesBody> loadPreferencesPost(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceInfoBody body);

        @POST(RetrofitURL.LOAD_NETWORK_DATA)
        Call<RequestDataFormat.PreferencesBody> loadNetworkSettingPost(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceInfoBody body);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 메인 화면

    public interface MainInfoApi {

        @POST(RetrofitURL.CURRENT_WORKER_INFO)
        Call<ResponseDataFormat.CurrentWorkerData> currentWorkerPost(@Header("Authorization") String auth, @Body RequestDataFormat.DeviceInfoBody deviceInfoBody);

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

        @POST(RetrofitURL.SAVE_SETTING_DATA)
        Call<ResponseDataFormat.SavePreferencesData> savePreferencesPost(@Header("Authorization") String auth, @Body RequestDataFormat.PreferencesBody preferencesBody);

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

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 근무자 변경 화면

    public interface WorkerChangePageApi {

        @POST(RetrofitURL.WORKER_LIST)
        Call<ResponseDataFormat.WeatherData> workerListPost(@Header("Authorization") String auth, @Body RequestDataFormat.WeatherBody weatherBody);

        @POST(RetrofitURL.WORKER_CHANGE)
        Call<ResponseDataFormat.WeatherData> workerChangePost(@Header("Authorization") String auth, @Body RequestDataFormat.WeatherBody weatherBody);

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
        Call<ResponseDataFormat.FacilityContactListBody> callRecordListPost(@Header("Authorization") String auth, @Body RequestDataFormat.CallRecordListBody callRecordListBody);

        @POST(RetrofitURL.DELETE_CALL_LOG_DATA)
        Call<ResponseDataFormat.FacilityContactListBody> deleteCallRecordPost(@Header("Authorization") String auth, @Body RequestDataFormat.CallRecordListBody callRecordListBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 방문자 목록

    public interface VisitorRecordApi {

        @GET(RetrofitURL.LOAD_VISITOR_TYPE_LIST)
        Call<List<ResponseDataFormat.VisitorRecordType>> loadVisitorTypeListGet(@Header("Authorization") String auth);

        @POST(RetrofitURL.LOAD_VISITOR_LOG_LIST)
        Call<ResponseDataFormat.FacilityContactListBody> callRecordListPost(@Header("Authorization") String auth, @Body RequestDataFormat.VisitorRecordListBody visitorRecordListBody);

        @POST(RetrofitURL.DELETE_VISITOR_LOG_RECORD)
        Call<ResponseDataFormat.FacilityContactListBody> deleteCallRecordPost(@Header("Authorization") String auth, @Body RequestDataFormat.VisitorRecordListBody visitorRecordListBody);

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

