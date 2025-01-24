package bluen.homein.gayo_security.rest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseDataFormat {

    public static class LoginData extends MyGson implements Serializable {
        @SerializedName("authorization")
        private String authorization;

        public String getAuthorization() {
            return authorization;
        }
    }

    public static class CurrentWorkerData extends MyGson implements Serializable {
        @SerializedName("managerName")
        private String workerName;
        @SerializedName("managerPhone")
        private String workerPhoneNumber;
        @SerializedName("result")
        private String result;
        @SerializedName("message")
        private String message;

        public String getWorkerName() {
            return workerName;
        }

        public String getWorkerPhoneNumber() {
            return workerPhoneNumber;
        }

        public String getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class WeatherData extends MyGson implements Serializable {

        @SerializedName("tmn")
        private int lowestTemp;
        @SerializedName("tmx")
        private int highestTemp;
        @SerializedName("sky")
        private int weatherConditions;
        @SerializedName("currentTemp")
        private int currentTemp;
        @SerializedName("areaName")
        private String areaName;
        @SerializedName("result")
        private String result;
        @SerializedName("message")
        private String message;

        public String getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }

        public int getHighestTemp() {
            return highestTemp;
        }

        public int getLowestTemp() {
            return lowestTemp;
        }

        public int getWeatherConditions() {
            return weatherConditions;
        }

        public int getCurrentTemp() {
            return currentTemp;
        }

        public String getAreaName() {
            return areaName;
        }
    }

    public static class PageCountInfo extends MyGson implements Serializable {

        @SerializedName("page")
        private int currentPage;
        @SerializedName("totalPageCnt")
        private int totalPageCnt;
        @SerializedName("pageCount")
        private int pageItemCount;

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPageCnt() {
            return totalPageCnt;
        }

        public int getPageItemCount() {
            return pageItemCount;
        }
    }

    public static class WorkRecordListBody extends MyGson implements Serializable {

        @SerializedName("pageCountValue")
        private PageCountInfo pageCountValue;
        @SerializedName("workManagerList")
        List<WorkRecordInfo> workRecordList;
        @SerializedName("result")
        private String result;
        @SerializedName("message")
        private String message;

        public String getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }

        public PageCountInfo getPageCountValue() {
            return pageCountValue;
        }

        public List<WorkRecordInfo> getWorkRecordList() {
            return workRecordList;
        }

        public static class WorkRecordInfo extends MyGson implements Serializable {

            @SerializedName("row_num")
            private int rowNum;

            @SerializedName("workType")
            private String workType;

            @SerializedName("workDay")
            private String workDate;

            @SerializedName("startDate")
            private String workStartTime;

            @SerializedName("endDate")
            private String workEndTime;

            @SerializedName("managerPhone")
            private String workerPhone;

            public String getworkType() {
                return workType;
            }

            public String getWorkDate() {
                return workDate;
            }

            public String getWorkStartTime() {
                return workStartTime;
            }

            public String getWorkEndTime() {
                return workEndTime;
            }

            public String getWorkerPhone() {
                return workerPhone;
            }
        }
    }
}


