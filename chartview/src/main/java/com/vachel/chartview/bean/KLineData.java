package com.vachel.chartview.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.vachel.chartview.util.Constant;
import com.vachel.chartview.util.FormatUtils;

import java.util.Date;

public class KLineData implements Parcelable {
    /**
     * time : 2018-09-24 09:45:00
     * volume : 922792
     * price:
     * close : 1897.4700
     *
     *
     *
     * time : 2018-09-24 09:45:00
     * volume : 922792
     * open : 1904.0000
     * close : 1897.4700
     * hight : 1905.0000
     * low : 1866.1400

     */

    private String time;
    private String volume;
    private String open;
    private String close;
    private String hight;
    private String low;
    private String price;
    private Date date;

    protected KLineData(Parcel in) {
        time = in.readString();
        volume = in.readString();
        open = in.readString();
        close = in.readString();
        hight = in.readString();
        low = in.readString();
        price = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(volume);
        dest.writeString(open);
        dest.writeString(close);
        dest.writeString(hight);
        dest.writeString(low);
        dest.writeString(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KLineData> CREATOR = new Creator<KLineData>() {
        @Override
        public KLineData createFromParcel(Parcel in) {
            return new KLineData(in);
        }

        @Override
        public KLineData[] newArray(int size) {
            return new KLineData[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getHight() {
        return hight;
    }

    public void setHight(String hight) {
        this.hight = hight;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getDate() {
        if (date == null) {
            int formatDateType = FormatUtils.getFormatDateType(time);
            date = FormatUtils.formatStringToTime(time, Constant.SOURCE_TIME_STRING[formatDateType]);
        }
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
