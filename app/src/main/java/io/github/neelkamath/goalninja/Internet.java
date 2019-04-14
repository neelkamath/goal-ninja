package io.github.neelkamath.goalninja;

import android.content.Context;
import android.net.ConnectivityManager;

class Internet {
    Internet() {
    }

    static boolean isNotConnected(Context context) {
        context = context.getSystemService("connectivity");
        boolean z = true;
        if (context == null) {
            return true;
        }
        context = ((ConnectivityManager) context).getActiveNetworkInfo();
        if (context != null) {
            if (context.isConnected() != null) {
                z = false;
            }
        }
        return z;
    }
}
