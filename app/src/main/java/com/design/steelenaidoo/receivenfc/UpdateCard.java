package com.design.steelenaidoo.receivenfc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class UpdateCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_update_card);
        getSupportActionBar().hide();
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                boolean c = user.getBoolean("check");
                if (c == true) {
                    EditText Card = findViewById(R.id.Card);
                    String CardNumber = Card.getText().toString();
                    EditText EX = findViewById(R.id.ExpiryYear);
                    String EXS = EX.getText().toString();
                    EditText EXM = findViewById(R.id.ExpiryMonth);
                    String EXMS = EXM.getText().toString();
                    EditText CVV = findViewById(R.id.CVC);
                    String CVVNumber = CVV.getText().toString();
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
                                            entity.put("CardNumber", CardNumber);
                                            entity.put("ExpirationMonth", EXMS);
                                            entity.put("ExpirationYear", EXS);
                                            entity.put("CVVNumber", CVVNumber);
                                            entity.put("user", ParseUser.getCurrentUser());

                                            // All other fields will remain the same
                                            entity.saveInBackground();
                                        }
                                    }
                                });
                            } else {
                                // Something is wrong
                            }
                        }
                    });
                }
                else if (c == false)
                {
                    Toast.makeText(getApplicationContext(),"No Card available to update",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
