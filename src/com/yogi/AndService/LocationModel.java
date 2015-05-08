package com.yogi.AndService;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SOFT
 * Date: 6/5/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocationModel {
    private int id;
    private String Tgl;
    private String City;
    private String Longtitude;
    private String Latitude;

    public LocationModel(){}

    public LocationModel(String tgl,String city, String longitude, String latitude) {
        super();
        this.City = city;
        this.Tgl= tgl;
        this.Longtitude = longitude;
        this.Latitude = latitude;
    }

    //getters & setters

    @Override
    public String toString() {
        return "Book [id=" + id +", tgl="+ Tgl + ", city=" + City + ", longitude=" + Longtitude + ", latitude=" + Latitude + "]";
    }

    public int getId() {
        return id;
    }

    public String getTgl() {
        return Tgl;
    }

    public String getCity() {
        return City;
    }

    public String getLongtitude() {
        return Longtitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTgl(String tgl) {
        Tgl = tgl;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setLongtitude(String longtitude) {
        Longtitude = longtitude;
    }
}
