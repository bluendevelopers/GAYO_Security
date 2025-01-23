package bluen.homein.gayo_security.rest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RequestDataFormat {

    public static class DeviceInfoBody implements Serializable {

        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;
        @SerializedName("macAddress")
        private String macAddress;
        @SerializedName("ipAddress")
        private String ipAddress;

        public DeviceInfoBody(String serialCode, String buildingCode) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
        }

        public DeviceInfoBody(String serialCode, String buildingCode, String macAddress, String ipAddress) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.macAddress = macAddress;
            this.ipAddress = ipAddress;
        }
    }

    public static class WeatherBody implements Serializable {

        @SerializedName("builCode")
        private String buildingCode;

        public WeatherBody(String buildingCode) {
            this.buildingCode = buildingCode;

        }
    }


    public static class WorkRecordListBody implements Serializable {

        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;
        @SerializedName("page")
        private int currentPage;
        @SerializedName("startDate")
        private String startDate;
        @SerializedName("endDate")
        private String endDate;
        @SerializedName("managerPhone")
        private String workerPhoneNumber;
        @SerializedName("workType")
        private String workDivision;

        public WorkRecordListBody(String serialCode, String buildingCode, int currentPage, String startDate, String endDate, String workerPhoneNumber, String workDivision) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.currentPage = currentPage;
            this.startDate = startDate;
            this.endDate = endDate;
            this.workerPhoneNumber = workerPhoneNumber;
            this.workDivision = workDivision;
        }
    }

}


