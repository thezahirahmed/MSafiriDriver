package com.eleganzit.msafiridriver.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ServerFailureDialog {

    public static AlertDialog alertDialog;

    public static AlertDialog alertDialog(Context context)
    {

        alertDialog=new AlertDialog.Builder(context).setMessage("Server or Internet Error!").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

        return alertDialog;
    }

}
