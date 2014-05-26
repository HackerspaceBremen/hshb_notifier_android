package de.hackerspacebremen.push;

import android.content.Context;

import de.hackerspacebremen.common.Constants;

/**
 * Created by sliedtke.
 */
public class NotificationRegistry {

    private Context context;

    public NotificationRegistry(Context context){
        this.context = context;
    }

    public void register() {
        // TODO start service to check status in intervall
    }
}
