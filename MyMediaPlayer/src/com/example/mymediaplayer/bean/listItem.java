package com.example.mymediaplayer.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hjz on 2016/10/12.
 */
public class listItem implements Parcelable {
    String name;
    int age;

    protected listItem(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }
    //反序列化
    public static final Creator<listItem> CREATOR = new Creator<listItem>() {
        @Override
        public listItem createFromParcel(Parcel in) {
            return new listItem(in);
        }

        @Override
        public listItem[] newArray(int size) {
            return new listItem[size];
        }
    };

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public listItem(int age, String name) {
        this.age = age;
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override   //序列化
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
    }
}
