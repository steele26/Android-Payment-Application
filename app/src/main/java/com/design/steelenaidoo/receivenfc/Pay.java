package com.design.steelenaidoo.receivenfc;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sendgrid.Method;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.stripe.Stripe;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Pay extends AppCompatActivity {
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    public static final String ACCOUNT_SID = "AC9296a128ae9bbf59f5b6b862a4efb132";
    public static final String AUTH_TOKEN = "d9dcafd959de4b4a6dd44a76a8916f8d";
    private static String CardNumber;
    private static String ExpiryMonth;
    private static String ExpiryDate;
    private static String CVVNumber;
    private static Integer StripeAmount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_pay);
        Button btn = findViewById(R.id.PayButton);
        getSupportActionBar().hide();
        TextView txt = findViewById(R.id.Amount);
        Intent intent = getIntent();
       String amount = intent.getStringExtra("price");
        txt.setText(amount);
        Integer Amount1 = Integer.parseInt(amount);
        StripeAmount = Amount1 * 100;

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CustomerDetails");
        query.whereEqualTo("user", user);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    CardNumber = result.getString("CardNumber");
                    ExpiryMonth = result.getString("ExpirationMonth");
                    ExpiryDate = result.getString("ExpirationYear");
                    CVVNumber  = result.getString("CVVNumber");
                } else {
                    // Something is wrong
                }
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate_payment();

            }
        });
    }

    private void authenticate_payment() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
           try {
               startActivityForResult(i, LOCK_REQUEST_CODE);
           }
           catch(Exception e)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Pay.this);
                builder.setMessage("Please setup device security to use this application");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                        startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOCK_REQUEST_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                ProgressDialog progressdialog = new ProgressDialog(Pay.this);
                progressdialog.setMessage("Connecting....");
                progressdialog.setCancelable(false);
                progressdialog.show();
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                // final String[] tokens = {"new"};
                Stripe.apiKey = "sk_test_nVh3GZlAXtPBK7C8CU7t6yJw00wm902T14";
                final Map<String, Object> tokenParams = new HashMap<String, Object>();
                Map<String, Object> cardParams = new HashMap<>();
                cardParams.put("number", CardNumber);
                cardParams.put("exp_month", ExpiryMonth);
                cardParams.put("exp_year", ExpiryDate);
                cardParams.put("cvc", CVVNumber);
                tokenParams.put("card", cardParams);

                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here
                    com.stripe.Stripe.apiKey = "sk_test_nVh3GZlAXtPBK7C8CU7t6yJw00wm902T14";
                    new AsyncTask<Void, Void, Token>() {
                        String response = null;

                        @Override
                        protected Token doInBackground(Void... params) {
                            try {
                                return Token.create(tokenParams);

                            } catch (AuthenticationException e) {
                                e.printStackTrace();
                                response = e.toString();

                                return null;
                            } catch (StripeException e) {
                                response = e.toString();
                                return null;
                            }
                        }

                        protected void onPostExecute(Token result) {
                            if (response != null) {
                                   progressdialog.dismiss();
                                final AlertDialog.Builder builder = new AlertDialog.Builder(Pay.this);
                                builder.setMessage(response);
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
                            else
                            if (response == null) {
                                String token = result.getId();
                                ParseUser user = ParseUser.getCurrentUser();
                                String email = user.getString("email");
                                Map<String, Object> customerParams = new HashMap<>();
                                customerParams.put("source", token);
                                customerParams.put("email", email);
                                try {
                                    Customer customer = Customer.create(customerParams);
                                    String ID = customer.getId();
                                    Stripe.apiKey = "sk_test_nVh3GZlAXtPBK7C8CU7t6yJw00wm902T14";
                                    Map<String, Object> params = new HashMap<>();
                                    params.put("amount",StripeAmount);
                                    params.put("currency", "zar");
                                    params.put("customer", ID);
                                    params.put("description", "Vending Machine Purchase");
                                    params.put("receipt_email", email);
                                    try {
                                        Charge.create(params);

                                        if (Build.VERSION.SDK_INT > 9) {
                                            StrictMode.setThreadPolicy( new StrictMode.ThreadPolicy.Builder().permitAll().build() );
                                            OkHttpClient client = new OkHttpClient();
                                            String url = "https://api.twilio.com/2010-04-01/Accounts/"+ACCOUNT_SID+"/SMS/Messages";
                                            String base64EncodedCredentials = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

                                            RequestBody body = new FormBody.Builder()
                                                    .add("From", "+12244354951")
                                                    .add("To", "+27794674792")
                                                    .add("Body", "Warning! Purchase has been made with your Bank Card")
                                                    .build();

                                            Request request = new Request.Builder()
                                                    .url(url)
                                                    .post(body)
                                                    .header("Authorization", base64EncodedCredentials)
                                                    .build();
                                            try {
                                                Response response = client.newCall(request).execute();
                                                Log.d("TAG", "sendSms: "+ response.body().string());
                                            } catch (IOException e) { e.printStackTrace(); }
                                        }



                                        progressdialog.dismiss();

                                        Intent intent = new Intent(Pay.this,TapToReceive.class);
                                        startActivity(intent);
                                    } catch (InvalidRequestException e) {
                                        //      progressdialog.dismiss();
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(Pay.this);
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
                                    } catch (StripeException e) {
                                             progressdialog.dismiss();
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(Pay.this);
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
                                } catch (Exception e) {
                                                progressdialog.dismiss();
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(Pay.this);
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
                            } else {
                                    progressdialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();

                } else {
                    //Authentication failed
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to go back?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Pay.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
