package net.auva.tv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import net.auva.tv.R;

import java.util.concurrent.TimeUnit;


public class SendOTPActivity extends AppCompatActivity {
    private static final String TAG = "SendOTPActivity";
    private String phoneNumber = "";
    private String permissions[] = {
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
    };
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        CheckForUpdatesService.checkForUpdate(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);
        mAuth = FirebaseAuth.getInstance();
        requestManageExternalStoragePermission();
        if (!isPermissionsGranted()) {
            requestPermissions(permissions, 201);
        }
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            Button btnGetOTP = findViewById(R.id.btnGotOTP);
            final EditText txtPhone = findViewById(R.id.inputPhone);

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                progressBar.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    System.out.println(e.getMessage());
                    Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    System.out.println(e.getMessage());
                    Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressBar.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button btnGetOTP = findViewById(R.id.btnGotOTP);
        final EditText txtPhone = findViewById(R.id.inputPhone);
        final CountryCodePicker countryCodePicker = findViewById(R.id.country_code);
        btnGetOTP.setOnClickListener(v -> {
            phoneNumber = txtPhone.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "ادخل رقم الهاتف", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!countryCodePicker.getFullNumber().equals("964")) return;

            if (isPermissionsGranted()) {
                progressBar.setVisibility(View.VISIBLE);
                btnGetOTP.setVisibility(View.INVISIBLE);
                if (phoneNumber.startsWith("0")) {
                    phoneNumber = phoneNumber.substring(1);
                }
                startVerifyFirebase("+964" + phoneNumber);
            } else {
                new Dialog(this);
                Dialog.showDialog();
            }
        });
    }

    private void startVerifyFirebase(String phoneNo) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNo)       // Phone number to verify
                .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = task.getResult().getUser();
                    Toast.makeText(SendOTPActivity.this, "success", Toast.LENGTH_SHORT).show();
                    // Update UI
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(SendOTPActivity.this, "failure", Toast.LENGTH_SHORT).show();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
        });
    }

    private boolean checkPermissions(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isPermissionsGranted() {
        boolean granted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkPermissions(permissions[0]) || !checkPermissions(permissions[2]) || !checkPermissions(permissions[3]) || !checkPermissions(permissions[4])) {
                granted = false;
            }
        } else {
            if (!checkPermissions(permissions[0]) || !checkPermissions(permissions[1])) {
                granted = false;
            }
        }
        return granted;
    }

    private void requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                return;
            }
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s",getPackageName())));
            startActivity(intent);
        }
    }
}