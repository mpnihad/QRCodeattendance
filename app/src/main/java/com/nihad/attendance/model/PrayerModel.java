package com.nihad.attendance.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity()
public class PrayerModel implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int member_id;
    private int prev_member_id;
    private String name;
    private String phoneNo;
    private String address;
    private String qrCode;
    private String macAddress;


    public PrayerModel(String name, String phoneNo, String address, String qrCode, String macAddress) {

        this.name = name;
        this.phoneNo = phoneNo;
        this.address = address;
        this.qrCode = qrCode;
        this.macAddress = macAddress;
    }

    public int getPrev_member_id() {
        return prev_member_id;
    }

    public void setPrev_member_id(int prev_member_id) {
        this.prev_member_id = prev_member_id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
