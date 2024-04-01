package com.micewine.emu.core.services.xserver;

import static com.micewine.emu.coreutils.EnvVars.exportVariables;
import static com.micewine.emu.coreutils.EnvVars.setVariables;
import static com.micewine.emu.coreutils.ShellExecutorCmd.ExecuteCMD;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class XServerLoader extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            setVariables();

            ExecuteCMD(exportVariables() + "; pkill -f \"/system/bin/app_process\";" +
                    "unset LD_LIBRARY_PATH LIBGL_DRIVERS_PATH; " +
                    "chmod 400 $CLASSPATH; " +
                    "/system/bin/app_process / com.micewine.emu.Loader :0", "XServer");
        }).start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}