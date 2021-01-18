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
import android.widget.TextView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ManageCards extends AppCompatActivity {
    private static String CardNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_manage_cards);
        TextView txt = findViewById(R.id.Fire);

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomerDetails");
        query.whereEqualTo("user", user);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    txt.setText(result.getString("CardNumber"));
                } else {
                    // Something is wrong
                }
            }
        });

        Button btn = findViewById(R.id.Save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ManageCards.this, CardDetails.class);
                startActivity(intent);
            }
        });

        Button up = findViewById(R.id.update);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ManageCards.this, UpdateCard.class);
                startActivity(intent);
            }
        });

        Button delete = findViewById(R.id.Delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ManageCards.this)
                        .setMessage("Are you sure you want to delete this card?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                ParseUser user = ParseUser.getCurrentUser();
                                boolean c = user.getBoolean("check");
                                if (c == true) {
                                    ProgressDialog progressdialog = new ProgressDialog(ManageCards.this);
                                    progressdialog.setMessage("Deleting....");
                                    progressdialog.setCancelable(false);
                                    progressdialog.show();
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomerDetails");
                                    query.whereEqualTo("user", user);
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null) {
                                                String objectID = object.getObjectId();
                                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("CustomerDetails");
                                                query2.getInBackground(objectID, new GetCallback<ParseObject>() {
                                                    public void done(ParseObject entity, ParseException e) {
                                                        if (e == null) {
                                                            // Update the fields we want to
                                                            try {
                                                                Boolean success = false;
                                                                entity.delete();
                                                                user.put("check", success);
                                                                user.save();
                                                                progressdialog.dismiss();
                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(ManageCards.this);
                                                                builder.setMessage("Card has been successfully deleted");
                                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                });

                                                                AlertDialog dialog = builder.create();
                                                                dialog.show();

                                                            } catch (ParseException ex) {

                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Something is wrong
                                            }
                                        }
                                    });
                                } else if (c == false) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ManageCards.this);
                                    builder.setMessage("There is no card available to delete");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    AlertDialog dialog1 = builder.create();
                                    dialog1.show();
                                }


                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


            }
        });
    }
}
