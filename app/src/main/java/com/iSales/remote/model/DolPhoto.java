package com.iSales.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by netserve on 30/08/2018.
 */

public class DolPhoto implements Parcelable {
    private String filename;
    private String content;
    private String encoding;

    public DolPhoto() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filename);
        dest.writeString(this.content);
        dest.writeString(this.encoding);
    }

    protected DolPhoto(Parcel in) {
        this.filename = in.readString();
        this.content = in.readString();
        this.encoding = in.readString();
    }

    public static final Creator<com.iSales.remote.model.DolPhoto> CREATOR = new Creator<com.iSales.remote.model.DolPhoto>() {
        @Override
        public com.iSales.remote.model.DolPhoto createFromParcel(Parcel source) {
            return new com.iSales.remote.model.DolPhoto(source);
        }

        @Override
        public com.iSales.remote.model.DolPhoto[] newArray(int size) {
            return new com.iSales.remote.model.DolPhoto[size];
        }
    };
}
