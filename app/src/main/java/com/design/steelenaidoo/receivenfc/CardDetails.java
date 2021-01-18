package com.design.steelenaidoo.receivenfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class CardDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_card_details);
        getSupportActionBar().hide();
        Button mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view)
            {     ProgressDialog progressdialog = new ProgressDialog(CardDetails.this);
                progressdialog.setMessage("Saving....");
                progressdialog.setCancelable(false);
                progressdialog.show();
                EditText Card = findViewById(R.id.Card);
                String CardNumber = Card.getText().toString();
                EditText EX = findViewById(R.id.ExpiryYear);
                String EXS = EX.getText().toString();
                Integer ExpiryDate = Integer.valueOf(EX.getText().toString());
                EditText EXM = findViewById(R.id.ExpiryMonth);
                String EXMS = EXM.getText().toString();
                Integer ExpiryMonth = Integer.valueOf(EXM.getText().toString());
                EditText CVV = findViewById(R.id.CVC);
                String CVVNumber = CVV.getText().toString();
                ParseUser user = ParseUser.getCurrentUser();
                boolean c = user.getBoolean("check");
                if (c == false) {


                    boolean success = true;
                    ParseObject post = new ParseObject("CustomerDetails");
                    post.put("CardNumber", CardNumber);
                    post.put("ExpirationMonth", EXMS);
                    post.put("ExpirationYear", EXS);
                    post.put("CVVNumber", CVVNumber);
                    post.put("user", user);
                    try{
                        post.save();
                        user.put("check", success);
                        try {
                            user.save();
                            progressdialog.dismiss();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(CardDetails.this);
                            builder.setMessage("Your Card Details have been successfully saved");
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
                        } catch (ParseException e) {
                            progressdialog.dismiss();
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    catch (Exception e) {
                        progressdialog.dismiss();
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                }
                else if (c == true)
                {
                    progressdialog.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CardDetails.this);
                    builder.setMessage("There is a card already saved for this account");
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
}
