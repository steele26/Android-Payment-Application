package com.design.steelenaidoo.receivenfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_main2);
        Button send1 = findViewById(R.id.send);
        getSupportActionBar().hide();
        send1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressdialog = new ProgressDialog(Main2Activity.this);
                progressdialog.setMessage("Sending email....");
                progressdialog.setCancelable(false);
                progressdialog.show();
                EditText txt = findViewById(R.id.resetemail);
                String email = txt.getText().toString();
                ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            progressdialog.dismiss();
                            // An email was successfully sent with reset instructions.
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                            builder.setMessage("An email has been sent.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //Finishing the dialog and removing Activity from stack.
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            progressdialog.dismiss();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                            builder.setMessage(e.getLocalizedMessage());
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });

    }
}
