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

    public static class CurrentWorkerInfo extends MyGson implements Serializable {
        @SerializedName("managerName")
        private String workerName;
        @SerializedName("managerPhone")
        private String workerPhoneNumber;
        @SerializedName("managerIdx")
        private String workerIdx;
        @SerializedName("result")
        private String result;
        @SerializedName("message")
        private String message;

        public String getWorkerIdx() {
            return workerIdx;
        }

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

    public static class SavePreferencesData extends MyGson implements Serializable {

        @SerializedName("result")
        private String result;
        @SerializedName("message")
        private String message;
        @SerializedName("uiResultCode")
        private String uiResultCode;
        @SerializedName("soundResultCode")
        private String soundResultCode;
        @SerializedName("sleepModeResultCode")
        private String sleepModeResultCode;
        @SerializedName("networkResultCode")
        private String networkResultCode;

        public String getUiResultCode() {
            return uiResultCode;
        }

        public String getSoundResultCode() {
            return soundResultCode;
        }

        public String getSleepModeResultCode() {
            return sleepModeResultCode;
        }

        public String getNetworkResultCode() {
            return networkResultCode;
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
        private PageCountInfo pageCountInfo;
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

        public PageCountInfo getPageCountInfo() {
            return pageCountInfo;
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

            public String getWorkType() {
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

    public static class FacilityContactListBody extends MyGson implements Serializable {

        @SerializedName("pageCountValue")
        private PageCountInfo pageCountInfo;
        @SerializedName("connDeviceList")
        List<FacilityContactInfo> facilityContactList;

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

        public PageCountInfo getPageCountInfo() {
            return pageCountInfo;
        }

        public List<FacilityContactInfo> getFacilityContactList() {
            return facilityContactList;
        }

        public static class FacilityContactInfo extends MyGson implements Serializable {

            @SerializedName("row_num")
            private int rowNum;

            @SerializedName("connDeviceName")
            private String facilityName;

            @SerializedName("connDeviceIP")
            private String facilityIPAddress;

            public int getRowNum() {
                return rowNum;
            }

            public String getFacilityName() {
                return facilityName;
            }

            public String getFacilityIPAddress() {
                return facilityIPAddress;
            }
        }

    }

    public static class VisitHistoryListBody extends MyGson implements Serializable {

        @SerializedName("pageCountValue")
        private PageCountInfo pageCountInfo;

        @SerializedName("hisList")
        List<VisitHistoryInfo> visitorList;

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

        public PageCountInfo getPageCountInfo() {
            return pageCountInfo;
        }

        public List<VisitHistoryInfo> getVisitHistoryList() {
            return visitorList;
        }

        public static class VisitHistoryInfo extends MyGson implements Serializable {

            @SerializedName("row_num")
            private int rowNum;

            @SerializedName("hisSeq")
            private String hisSeq;
            @SerializedName("codeName")
            private String visitType;
            @SerializedName("hisDay")
            private String visitDate;
            @SerializedName("hisTime")
            private String visitingTime;
            @SerializedName("hisFileName")
            private String visitHistoryFileName;
            @SerializedName("histFileType")
            private String visitHistoryFileType;

            public int getRowNum() {
                return rowNum;
            }

            public String getHisSeq() {
                return hisSeq;
            }

            public String getVisitType() {
                return visitType;
            }

            public String getVisitDate() {
                return visitDate;
            }

            public String getVisitingTime() {
                return visitingTime;
            }

            public String getVisitHistoryFileName() {
                return visitHistoryFileName;
            }

            public String getVisitHistoryFileType() {
                return visitHistoryFileType;
            }
        }

    }

    public static class WorkerListBody extends MyGson implements Serializable {

        @SerializedName("pageCountValue")
        private PageCountInfo pageCountInfo;

        @SerializedName("currentManager")
        private CurrentWorkerInfo currentManagerInfo;

        @SerializedName("residentsList")
        List<WorkerInfo> workerList;

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

        public PageCountInfo getPageCountInfo() {
            return pageCountInfo;
        }

        public CurrentWorkerInfo getCurrentManagerInfo() {
            return currentManagerInfo;
        }

        public List<WorkerInfo> getWorkerList() {
            return workerList;
        }

        public static class WorkerInfo extends MyGson implements Serializable {

            @SerializedName("rowNum")
            private int rowNum;
            @SerializedName("resiHp")
            private String phoneNumber;
            @SerializedName("managerIdx")
            private String idx;
            @SerializedName("managerPhone")
            private String managerPhone; // 근무 기록 필터용

            public WorkerInfo(String managerPhone) {
                this.managerPhone = managerPhone;
            }

            public String getIdx() {
                return idx;
            }

            public int getRowNum() {
                return rowNum;
            }

            public String getManagerPhone() {
                return managerPhone;
            }

            public String getPhoneNumber() {
                return phoneNumber;
            }
        }

    }

    public static class CallRecordListBody extends MyGson implements Serializable {

        @SerializedName("pageCountValue")
        private PageCountInfo pageCountInfo;
        @SerializedName("callList")
        List<CallRecordInfo> callRecordList;

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

        public PageCountInfo getPageCountInfo() {
            return pageCountInfo;
        }

        public List<CallRecordInfo> getCallRecordList() {
            return callRecordList;
        }

        public static class CallRecordInfo extends MyGson implements Serializable {

            @SerializedName("rowNum")
            private int rowNum;

            @SerializedName("callseq")
            private int callSeq;

            @SerializedName("codeName")
            private String callType;

            @SerializedName("startDate")
            private String startDate;

            @SerializedName("callMiniute")
            private String callMinute;

            @SerializedName("callSecond")
            private String callSecond;

            @SerializedName("callLocName")
            private String callLocName;

            public int getRowNum() {
                return rowNum;
            }

            public int getCallSeq() {
                return callSeq;
            }

            public String getCallType() {
                return callType;
            }

            public String getStartDate() {
                return startDate;
            }

            public String getCallMinute() {
                return callMinute;
            }

            public String getCallSecond() {
                return callSecond;
            }

            public String getCallLocName() {
                return callLocName;
            }
        }

    }

    // 근무 or 통화 구분
    public class EtcType {
        @SerializedName("codeNumber")
        private String codeNumber;

        @SerializedName("codeName")
        private String codeName;

        public String getCodeNumber() {
            return codeNumber;
        }

        public String getCodeName() {
            return codeName;
        }
    }

    // 방문자 기록 구분
    public class VisitType {
        @SerializedName("entranceCode")
        private String entranceCode;

        @SerializedName("entranceName")
        private String entranceName;

        public String getEntranceCode() {
            return entranceCode;
        }

        public String getEntranceName() {
            return entranceName;
        }
    }

    // ui 바탕 화면 리스트
    public class UiBackgroundType {
        @SerializedName("uiCode")
        private String uiCode;

        @SerializedName("uiName")
        private String uiName;

        public String getUiCode() {
            return uiCode;
        }

        public String getUiName() {
            return uiName;
        }
    }

    // 벨소리 리스트
    public class BellSoundType {
        @SerializedName("soundCode")
        private String soundCode;

        @SerializedName("soundName")
        private String soundName;

        public String getSoundCode() {
            return soundCode;
        }

        public String getSoundName() {
            return soundName;
        }
    }

    // 순찰 모드 리스트
    public class PatrolModeListType {
        @SerializedName("patrolModeCode")
        private String patrolModeCode;

        @SerializedName("patroModeName")
        private String patroModeName;

        public String getPatrolModeCode() {
            return patrolModeCode;
        }

        public String getPatroModeName() {
            return patroModeName;
        }
    }

}


