package com.malikapps.trakerr.DA;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import java.net.MalformedURLException;

/**
 * Created by abdulmalik_khan on 02/08/15.
 */
public class BaseTable {
    public static final String SUCCESS = "Success";
    public static final String USER_CANCELLED = "UserCancelled";

    public String error;

    public static MobileServiceClient getMobileServiceClient(Context context)
            throws MalformedURLException {
        return new MobileServiceClient(
                "https://trackerr.azure-mobile.net/",
                "VnGHLpkkRKWtHLTwTuLKGqxuUMVwTz56",
                context
        );

    }

    protected AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }


}
