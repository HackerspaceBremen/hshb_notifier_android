package de.hackerspacebremen.push;

import android.content.Context;

import com.nokia.push.PushRegistrar;

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
        PushRegistrar.checkDevice(this.context);
        PushRegistrar.checkManifest(this.context);
        String regId = PushRegistrar.getRegistrationId(this.context);
        if (regId.equals("")) {
            PushRegistrar.register(this.context, Constants.SENDER_ID);
        }
    }
}
