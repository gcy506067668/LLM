package online.letmesleep.androidapplication.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import online.letmesleep.androidapplication.R;

public class ProgressBarUtil {
    public static AlertDialog getProgress(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setView(R.layout.dialog_progress_bar);
        AlertDialog dialog = builder.create();
        return  dialog;
    }
}
