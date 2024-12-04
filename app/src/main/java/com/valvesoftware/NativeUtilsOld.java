package com.valvesoftware;

import com.billflx.csgo.constant.Constants;
import com.valvesoftware.source.BuildConfig;

import java.util.List;

public class NativeUtilsOld {

    public static String getFlavor() {
        return BuildConfig.FLAVOR.replace("-", ".");
    }

    public static List<String> getMasterServers() {
        return Constants.Companion.getAppUpdateInfo().getValue().getLink().getServerRootLink();
    }

}
