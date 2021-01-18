package com.design.steelenaidoo.receivenfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_login_success);

        Button update = findViewById(R.id.manage);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginSuccessActivity.this,ManageCards.class);
                startActivity(intent);
            }
        });

        Button Tap = findViewById(R.id.Tap);
        Tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginSuccessActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.options_menu, menu);

        return true;

    }
    public boolean onOptionsItemSelected(MenuItem item) {

//respond to menu item selection
        switch (item.getItemId()) {

            case R.id.Logout:

                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ParseUser.logOut();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();



                return true;

            case R.id.Delete:

                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to delete this account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ProgressDialog progressdialog = new ProgressDialog(LoginSuccessActivity.this);
                                progressdialog.setMessage("Deleting....");
                                progressdialog.setCancelable(false);
                                progressdialog.show();
                                ParseUser currentUser = ParseUser.getCurrentUser();

                                if (currentUser != null) {
                                    // Deletes the user.

                                    // Notice that the DeleteCallback is totally optional!
                                    currentUser.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                        }
                                    });
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomerDetails");
                                    query.whereEqualTo("user", currentUser);
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
                                                                entity.delete();

                                                            } catch (ParseException ex) {
                                                                ex.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Something is wrong
                                            }
                                        }
                                    });
                                    progressdialog.dismiss();
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(LoginSuccessActivity.this);
                                    builder.setMessage("Your Account has been successfully deleted.");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            //Finishing the dialog and removing Activity from stack.
                                            dialogInterface.dismiss();
                                            finish();
                                        }
                                    });

                                    AlertDialog dialog1 = builder.create();
                                    dialog1.show();

                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
