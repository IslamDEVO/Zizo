package com.yahoo.eslam_m_abdelaziz.uber.common;

import android.os.RemoteCallbackList;

import com.yahoo.eslam_m_abdelaziz.uber.remote.IGoogleApi;
import com.yahoo.eslam_m_abdelaziz.uber.remote.RetrofitClient;

public class Common {
    public static final String baseUrl = "https://maps.googleapis.com";
    public static IGoogleApi getGoogleAPI(){
        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class);
    }
}
