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
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ReigsterAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_reigster_account);
        getSupportActionBar().hide();
        final EditText _txtfullname = (EditText) findViewById(R.id.txtname_reg);
        final EditText _txtemail = (EditText) findViewById(R.id.txtemail_reg);
        final EditText _txtpass = (EditText) findViewById(R.id.txtpass_reg);
        final EditText _txtmobile = findViewById(R.id.txtmobile_reg);
        Button _btnreg = (Button) findViewById(R.id.btn_reg);
        _btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final ProgressDialog progressdialog = new ProgressDialog(ReigsterAccountActivity.this);
                progressdialog.setMessage("Registering....");
                progressdialog.setCancelable(false);
                progressdialog.show();
                //  Toast.makeText(RegisterAccountActivity.this, "Created User Successfully", Toast.LENGTH_SHORT).show();
                String fullname = _txtfullname.getText().toString();
                String email = _txtemail.getText().toString();
                String pass = _txtpass.getText().toString();
                String mobile = _txtmobile.getText().toString();
                boolean check = false;
                ParseUser user = new ParseUser();
                user.setUsername(fullname);
                user.setPassword(pass);
                user.setEmail(email);
                user.put("CellPhone", mobile);
                user.put("check",check);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            progressdialog.dismiss();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ReigsterAccountActivity.this);
                            builder.setMessage("Your Account is Successfully Created.");
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
                            ParseUser.logOut();
                            progressdialog.dismiss();
                            Toast.makeText(ReigsterAccountActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}
