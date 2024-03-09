package net.auva.tv;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import net.auva.tv.R;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {
    EditText txt1, txt2, txt3, txt4, txt5, txt6;
    private static final String TAG = "VerifyOTPActivity";
    //    private PrefsHelper prefs;
//    private ApiHelper api;
    Button btnVerify;
    Button btnResend;
    ProgressBar progressBar;
    private String verificationId;
    private String phone_number;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
//        prefs = new PrefsHelper(this);
//        api = new ApiHelper(this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ar");
        TextView txtPhone = findViewById(R.id.txtPhone);
        txtPhone.setText(String.format("+964 %s", getIntent().getStringExtra("phone")));
        txt1 = findViewById(R.id.inputCode1);
        txt2 = findViewById(R.id.inputCode2);
        txt3 = findViewById(R.id.inputCode3);
        txt4 = findViewById(R.id.inputCode4);
        txt5 = findViewById(R.id.inputCode5);
        txt6 = findViewById(R.id.inputCode6);
        btnVerify = findViewById(R.id.btnVerify);
        btnResend = findViewById(R.id.btnResend);
        progressBar = findViewById(R.id.progressBar);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                System.out.println("onVerificationCompleted:" + credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                System.out.println(e.getMessage());

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    System.out.println(e.getMessage());
                    Toast.makeText(VerifyOTPActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show();
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    System.out.println(e.getMessage());
                    Toast.makeText(VerifyOTPActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show();

                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String nverificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken ntoken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                Log.d(TAG, "onCodeSent:" + verificationId);
                System.out.println("onCodeSent:" + verificationId);
                Toast.makeText(VerifyOTPActivity.this, R.string.resend_msg,
                        Toast.LENGTH_SHORT).show();

                verificationId = nverificationId;
                token = ntoken;
            }
        };
        phone_number = getIntent().getStringExtra("phone");
        verificationId = getIntent().getStringExtra("verificationId");
        btnVerify.setOnClickListener(v -> {

            if (txt1.getText().toString().trim().isEmpty()
                    || txt2.getText().toString().trim().isEmpty()
                    || txt3.getText().toString().trim().isEmpty()
                    || txt4.getText().toString().trim().isEmpty()
                    || txt5.getText().toString().trim().isEmpty()
                    || txt6.getText().toString().trim().isEmpty()
            ) {
                Toast.makeText(VerifyOTPActivity.this,
                        R.string.must_enter_code, Toast.LENGTH_LONG).show();
                return;
            }

            String verify_code = appendTexts(txt1, txt2, txt3, txt4, txt5, txt6);

            if (verificationId != null) {
                progressBar.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.INVISIBLE);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(
                        verificationId,
                        verify_code
                );
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        btnVerify.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete:");
                            Intent intent = new Intent(VerifyOTPActivity.this, CountryActivity.class);
                            intent.putExtra("verified", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            VerifyOTPActivity.this.startActivity(intent);
                        } else {
                            Log.d(TAG, "Unable to verify response.");
                            Toast.makeText(VerifyOTPActivity.this,
                                    getString(R.string.toast_unverified), Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });

            }


        });
        btnResend.setOnClickListener(v -> {
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+964" + phone_number)       // Phone number to verify
                            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
        setupOTPInputs();
    }
    private String appendTexts(@NonNull TextView... txts) {
        String txtFinal = "";
        for (TextView txt : txts) {
            Log.d(TAG, "appendTexts: " + txt);
            txtFinal += txt.getText().toString().trim();
        }
        Log.d(TAG, "appendTexts: " + txtFinal);
        return txtFinal;
    }

    private void setupOTPInputs() {
        txt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    txt2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    txt3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    txt4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    txt5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    txt6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}