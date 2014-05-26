package de.hackerspacebremen.push;

import android.content.Context;

import com.google.android.gcm.GCMRegistrar;

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
        GCMRegistrar.checkDevice(this.context);
        GCMRegistrar.checkManifest(this.context);
        String regId = GCMRegistrar.getRegistrationId(this.context);
        if (regId.equals("")) {
            GCMRegistrar.register(this.context, Constants.SENDER_ID);
        }
    }
}
