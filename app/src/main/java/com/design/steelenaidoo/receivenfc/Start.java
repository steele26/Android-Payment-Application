package com.design.steelenaidoo.receivenfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        ParseInstallation.getCurrentInstallation().saveInBackground();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_start);
        Button password = findViewById(R.id.Account);
        getSupportActionBar().hide();
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        TextView reg = findViewById(R.id.btnreg);
        Button login = findViewById(R.id.btnsignin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             final ProgressDialog progressdialog = new ProgressDialog(Start.this);
                progressdialog.setMessage("Logging In....");
                progressdialog.setCancelable(false);
                progressdialog.show();
                final EditText txtemail = (EditText) findViewById(R.id.txtemail);
                final EditText txtpass = (EditText) findViewById(R.id.txtpass);

                String email = txtemail.getText().toString();
                String pass = txtpass.getText().toString();
                ParseUser.logInInBackground(email, pass, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            progressdialog.dismiss();
                            Intent intent = new Intent(Start.this,LoginSuccessActivity.class);
                            startActivity(intent);
                        } else {
                            progressdialog.dismiss();
                            ParseUser.logOut();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(Start.this);
                            builder.setMessage(e.getLocalizedMessage());
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Start.this,ReigsterAccountActivity.class);
                startActivity(intent);
            }
        });

    }
}
