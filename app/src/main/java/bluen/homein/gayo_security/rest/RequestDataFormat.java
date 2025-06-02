package bluen.homein.gayo_security.rest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RequestDataFormat {

    public static class ContactBody implements Serializable {

        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;
        @SerializedName("connDeviceName")
        private String connDeviceName;
        @SerializedName("connDeviceIP")
        private String connDeviceIP;
        @SerializedName("connSerialCode")
        private String  connSerialCode;
        @SerializedName("page")
        private int page;

        public ContactBody(String serialCode, String buildingCode) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
        }

        public ContactBody(String serialCode, String buildingCode, int page) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.page = page;
        }

        public ContactBody(String serialCode, String buildingCode, String connDeviceName, String connDeviceIP) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.connDeviceName = connDeviceName;
            this.connDeviceIP = connDeviceIP;
        }

        public String getConnSerialCode() {
            return connSerialCode;
        }

        public void setConnSerialCode(String connSerialCode) {
            this.connSerialCode = connSerialCode;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }

    public static class DeviceBody implements Serializable {

        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;
        @SerializedName("deviceToken")
        private String deviceToken;
        @SerializedName("deviceUI")
        private DeviceUIBody deviceUIBody;
        @SerializedName("deviceSound")
        private DeviceSoundBody deviceSoundBody;
        @SerializedName("deviceSleepMode")
        private DeviceSleepModeBody deviceSleepModeBody;
        @SerializedName("deviceNetwork")
        private DeviceNetworkBody deviceNetworkBody;

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

        public DeviceBody(String serialCode, String buildingCode, String deviceToken, DeviceUIBody deviceUIBody, DeviceSoundBody deviceSoundBody, DeviceSleepModeBody deviceSleepModeBody, DeviceNetworkBody deviceNetworkBody) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.deviceToken = deviceToken;
            this.deviceUIBody = deviceUIBody;
            this.deviceSoundBody = deviceSoundBody;
            this.deviceSleepModeBody = deviceSleepModeBody;
            this.deviceNetworkBody = deviceNetworkBody;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getSerialCode() {
            return serialCode;
        }

        public String getBuildingCode() {
            return buildingCode;
        }

        public DeviceUIBody getDeviceUIBody() {
            return deviceUIBody;
        }

        public DeviceSoundBody getDeviceSoundBody() {
            return deviceSoundBody;
        }

        public DeviceSleepModeBody getDeviceSleepModeBody() {
            return deviceSleepModeBody;
        }

        public DeviceNetworkBody getDeviceNetworkBody() {
            return deviceNetworkBody;
        }

        public void setDeviceNetworkBody(DeviceNetworkBody deviceNetworkBody) {
            this.deviceNetworkBody = deviceNetworkBody;
        }

        public void setDeviceSleepModeBody(DeviceSleepModeBody deviceSleepModeBody) {
            this.deviceSleepModeBody = deviceSleepModeBody;
        }

        public void setDeviceSoundBody(DeviceSoundBody deviceSoundBody) {
            this.deviceSoundBody = deviceSoundBody;
        }

        public void setDeviceUIBody(DeviceUIBody deviceUIBody) {
            this.deviceUIBody = deviceUIBody;
        }
    }

    public static class DeviceSoundBody implements Serializable {

        @SerializedName("intercomSound")
        private int interPhoneSound;
        @SerializedName("intercomBell")
        private String intercomBell;
        @SerializedName("callSound")
        private int callSound;
        @SerializedName("callBell")
        private String callBell;
        @SerializedName("entranceSound")
        private int entranceSound;
        @SerializedName("entranceBell")
        private String entranceBell;
        @SerializedName("notiSound")
        private int notiSound;
        @SerializedName("systemSound")
        private int systemSound;

        public DeviceSoundBody(int interPhoneSound, String intercomBell, int callSound, String callBell, int entranceSound, String entranceBell, int notiSound, int systemSound) {
            this.interPhoneSound = interPhoneSound;
            this.intercomBell = intercomBell;
            this.callSound = callSound;
            this.callBell = callBell;
            this.entranceSound = entranceSound;
            this.entranceBell = entranceBell;
            this.notiSound = notiSound;
            this.systemSound = systemSound;
        }

        public int getInterPhoneSound() {
            return interPhoneSound;
        }

        public String getIntercomBell() {
            return intercomBell;
        }

        public int getCallSound() {
            return callSound;
        }

        public String getCallBell() {
            return callBell;
        }

        public int getEntranceSound() {
            return entranceSound;
        }

        public String getEntranceBell() {
            return entranceBell;
        }

        public int getNotiSound() {
            return notiSound;
        }

        public int getSystemSound() {
            return systemSound;
        }
    }

    public static class IpAddressBody implements Serializable {
        @SerializedName("ipAddress")
        private String ipAddress;

        public IpAddressBody(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }


    public static class DeviceNetworkBody extends MyGson implements Serializable  {
        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;

        @SerializedName("facilityName")
        private String facilityName;
        @SerializedName("ipAddress")
        private String ipAddress;
        @SerializedName("macAddress")
        private String macAddress;
        @SerializedName("gateWayIP")
        private String gateWayIP;
        @SerializedName("subNet")
        private String subNet;
        @SerializedName("serverIP")
        private String serverIP;
        @SerializedName("serverPort")
        private int serverPort;
        @SerializedName("ipPermission")
        private List<IpAddressBody> allowedIpList;

        public String getSerialCode() {
            return serialCode;
        }

        public String getFacilityName() {
            return facilityName;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public String getGateWayIP() {
            return gateWayIP;
        }

        public String getSubNet() {
            return subNet;
        }

        public String getServerIP() {
            return serverIP;
        }

        public int getServerPort() {
            return serverPort;
        }

        public List<IpAddressBody> getAllowedIpList() {
            return allowedIpList;
        }

        public DeviceNetworkBody(String facilityName, String ipAddress, String macAddress, String gateWayIP, String subNet, String serverIP, int serverPort, List<IpAddressBody> allowedIpList) {
            this.facilityName = facilityName;
            this.ipAddress = ipAddress;
            this.macAddress = macAddress;
            this.gateWayIP = gateWayIP;
            this.subNet = subNet;
            this.serverIP = serverIP;
            this.serverPort = serverPort;
            this.allowedIpList = allowedIpList;
        }

        public DeviceNetworkBody(String serialCode, String buildingCode, String facilityName, String ipAddress, String macAddress, String gateWayIP, String subNet, String serverIP, int serverPort, List<IpAddressBody> allowedIpList) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.facilityName = facilityName;
            this.ipAddress = ipAddress;
            this.macAddress = macAddress;
            this.gateWayIP = gateWayIP;
            this.subNet = subNet;
            this.serverIP = serverIP;
            this.serverPort = serverPort;
            this.allowedIpList = allowedIpList;
        }
    }

    public static class DeviceSleepModeBody implements Serializable {
        @SerializedName("mainReturnTime")
        private int mainReturnTime;
        @SerializedName("sleepTime")
        private int sleepTime;

        public DeviceSleepModeBody(int mainReturnTime, int sleepTime) {
            this.mainReturnTime = mainReturnTime;
            this.sleepTime = sleepTime;
        }

        public int getMainReturnTime() {
            return mainReturnTime;
        }

        public int getSleepTime() {
            return sleepTime;
        }

        public void setMainReturnTime(int mainReturnTime) {
            this.mainReturnTime = mainReturnTime;
        }

        public void setSleepTime(int sleepTime) {
            this.sleepTime = sleepTime;
        }
    }

    public static class DeviceUIBody implements Serializable {
        @SerializedName("brightness")
        private int brightness;
        @SerializedName("uiMode")
        private String uiMode;

        public DeviceUIBody(int brightness, String uiMode) {
            this.brightness = brightness;
            this.uiMode = uiMode;
        }

        public int getBrightness() {
            return brightness;
        }

        public String getUiMode() {
            return uiMode;
        }
    }

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

    public static class WorkerBody implements Serializable {

        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;
        @SerializedName("managerIdx")
        private String managerIdx;
        @SerializedName("managerPhone")
        private String managerPhone;

        public WorkerBody(String buildingCode) {
            this.buildingCode = buildingCode;
        }

        public WorkerBody(String serialCode, String buildingCode, String managerIdx, String managerPhone) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.managerIdx = managerIdx;
            this.managerPhone = managerPhone;
        }
    }

    public static class WorkerListBody implements Serializable {

        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;
        @SerializedName("page")
        private int currentPage;

        public WorkerListBody(String serialCode, String buildingCode, int currentPage) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.currentPage = currentPage;
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
        private String workType;

        public WorkRecordListBody(String serialCode, String buildingCode, int currentPage, String startDate, String endDate, String workerPhoneNumber, String workType) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.currentPage = currentPage;
            this.startDate = startDate;
            this.endDate = endDate;
            this.workerPhoneNumber = workerPhoneNumber;
            this.workType = workType;
        }
    }

    public static class CallRecordListBody implements Serializable {

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

        @SerializedName("callType")
        private String callType;
        @SerializedName("callSeq")
        private Integer callSeq;

        public CallRecordListBody(String serialCode, String buildingCode, int currentPage, String startDate, String endDate, String callType) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.currentPage = currentPage;
            this.startDate = startDate;
            this.endDate = endDate;
            this.callType = callType;
        }

        public CallRecordListBody(String serialCode, String buildingCode, Integer callSeq) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.callSeq = callSeq;
        }
    }

    public static class VisitorRecordListBody implements Serializable {

        @SerializedName("serialCode")
        private String serialCode;
        @SerializedName("builCode")
        private String buildingCode;
        @SerializedName("page")
        private int currentPage;

        @SerializedName("hisStartDate")
        private String hisStartDate;
        @SerializedName("hisEndDate")
        private String hisEndDate;
        @SerializedName("hisType")
        private String hisType;
        @SerializedName("hisSeq")
        private String hisSeq;

        public VisitorRecordListBody(String serialCode, String buildingCode, String hisSeq) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.hisSeq = hisSeq;
        }

        public VisitorRecordListBody(String serialCode, String buildingCode, int currentPage, String hisStartDate, String hisEndDate, String hisType) {
            this.serialCode = serialCode;
            this.buildingCode = buildingCode;
            this.currentPage = currentPage;
            this.hisStartDate = hisStartDate;
            this.hisEndDate = hisEndDate;
            this.hisType = hisType;
        }
    }


}


