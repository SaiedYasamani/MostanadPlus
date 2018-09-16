package com.mostanad.plus.pojo;

import com.google.gson.annotations.SerializedName;

public class VersionCheckModel {
    @SerializedName("Response")
    private VersionCheckResponse Response;

    public VersionCheckResponse getResponse() {
        return Response;
    }

    public void setResponse(VersionCheckResponse response) {
        Response = response;
    }

    public class VersionCheckResponse {
        @SerializedName("Version")
        private double Version;
        @SerializedName("URL")
        private String Url;
        @SerializedName("uptodate")
        private boolean uptodate;

        public VersionCheckResponse() {
        }

        public VersionCheckResponse(double version, String url, boolean Uptodate) {
            Version = version;
            Url = url;
            uptodate = Uptodate;
        }

        public boolean isUptodate() {
            return uptodate;
        }

        public void setUptodate(boolean uptodate) {
            this.uptodate = uptodate;
        }

        public double getVersion() {
            return Version;
        }

        public void setVersion(double version) {
            Version = version;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }

        @Override
        public String toString() {
            return "VersionCheckModel{" +
                    "Version=" + Version +
                    ", Url='" + Url + '\'' +
                    ", uptodate=" + uptodate +
                    '}';
        }
    }
}
