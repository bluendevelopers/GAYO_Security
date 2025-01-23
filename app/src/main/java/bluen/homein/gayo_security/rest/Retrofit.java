package bluen.homein.gayo_security.rest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class Retrofit {

    public interface AuthApi {

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
        Call<ResponseDataFormat.CurrentWorkerData> currentWorkerPost(@Header("Authorization")  String auth, @Body RequestDataFormat.DeviceInfoBody deviceInfoBody);

        @POST(RetrofitURL.WEATHER_INFO_URL)
        Call<ResponseDataFormat.WeatherData> weatherInfoPost(@Header("Authorization")  String auth, @Body RequestDataFormat.WeatherBody weatherBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }

    //** 근무자 변경 화면

    public interface WorkerChangePageApi {

        @POST(RetrofitURL.WORKER_LIST)
        Call<ResponseDataFormat.WeatherData> workerListPost(@Header("Authorization")  String auth,@Body RequestDataFormat.WeatherBody weatherBody);

        @POST(RetrofitURL.WORKER_CHANGE)
        Call<ResponseDataFormat.WeatherData> workerChangePost(@Header("Authorization")  String auth,@Body RequestDataFormat.WeatherBody weatherBody);

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(RetrofitURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(RetrofitOkHttpClient.getOkHttpClient())
                .build();

    }
    //** 근무 기록 화면

    public interface WorkRecordApi {

        @POST(RetrofitURL.WORK_RECORD_LIST)
        Call<ResponseDataFormat.WorkRecordListBody> workRecordListPost(@Header("Authorization")  String auth,@Body RequestDataFormat.WorkRecordListBody workRecordListBody);

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

