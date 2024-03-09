package net.auva.tv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class Dialog {

    private static Context context = null;

    public Dialog(Context ctx) {
        context = ctx;
    }

    public static void showDialog() {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Please agree the permissions to continue using the AuvaTV");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    showPermissionsActivity();
                    dialog.cancel();
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private static void showPermissionsActivity() {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }


}
