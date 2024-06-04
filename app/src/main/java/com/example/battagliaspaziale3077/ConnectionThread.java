package com.example.battagliaspaziale3077;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ConnectionThread extends Thread implements Parcelable {
    protected ConnectionThread(Parcel in) {
    }

    public static final Creator<ConnectionThread> CREATOR = new Creator<ConnectionThread>() {
        @Override
        public ConnectionThread createFromParcel(Parcel in) {
            return new ConnectionThread(in);
        }

        @Override
        public ConnectionThread[] newArray(int size) {
            return new ConnectionThread[size];
        }
    };

    public ConnectionThread() {
    }

    public void InviaMessaggio(String messaggio) {

    }

    public void RiceviRisposta() {

    }

    public String GetMessage() {
        return "";
    }

    public void Abbandona() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
    }
}
