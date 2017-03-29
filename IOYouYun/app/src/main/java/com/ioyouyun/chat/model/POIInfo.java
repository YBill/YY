package com.ioyouyun.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 卫彪 on 2016/10/26.
 */

public class POIInfo implements Parcelable{

    private double longitude; // 经度
    private double latitude; // 纬度
    private String address;

    public POIInfo() {
    }

    public POIInfo(double longitude, double latitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    protected POIInfo(Parcel in) {
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
    }

    public static final Creator<POIInfo> CREATOR = new Creator<POIInfo>() {
        @Override
        public POIInfo createFromParcel(Parcel in) {
            return new POIInfo(in);
        }

        @Override
        public POIInfo[] newArray(int size) {
            return new POIInfo[size];
        }
    };

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
    }
}
