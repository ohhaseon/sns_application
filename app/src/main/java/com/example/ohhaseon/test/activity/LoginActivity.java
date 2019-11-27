package com.example.ohhaseon.test.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ohhaseon.test.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Google SignIn Firebase";
    private static final int RC_SIGN_IN = 1000;
    private ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userColRef = db.collection("user");
   // private CollectionReference userColRef = FirebaseFirestore.getInstance().collection("user");

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
/*
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this , this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
*/
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("로딩중..");
            dialog.setCancelable(false);


            findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //dialog.show();


                    googleSignIn();
                }
            });

        }
        @Override
        public void onBackPressed() {



            finish();
         }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {

                Log.d(TAG, "onActivityResult 0");

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {

                    Log.d(TAG, "onActivityResult 1");

                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);

                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);

                    dialog.dismiss();

                }
            }
        }
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            //updateUI(currentUser);
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            Log.d("LoginAct", "firebaseAuthWithGoogle s ");


            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d("LoginAct", "firebaseAuthW+ithGoogle 1 "+task.isSuccessful());

                            if (task.isSuccessful()) {
                                Log.d("LoginAct", "firebaseAuthWithGoogle 1 ");
                                loadUserInfo();
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

    private void googleSignIn() {


        Log.d("LoginAct", "googleSignIn s ");




        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        dialog.dismiss();
    }

    private void loadUserInfo() {

        Log.d("LoginAct", "loadUserInfo s ");


        user = mAuth.getCurrentUser();
        //goMainActivity();
        userColRef.document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();

                            goMainActivity();

                        } else {

                            saveUserInfo();
                        }
                    }
                });
/*
        userColRef.document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            goMainActivity();
                            //dialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
*/


    }


    private void saveUserInfo() {


        Map<String, Object> userMap = new HashMap<>();

        if (user == null) return;

        if (user.getEmail() != null) {
            userMap.put("email", user.getEmail());
        }else {
            userMap.put("email", "none@studysemina");
        }

        if (user.getDisplayName() != null) {
            userMap.put("nickname", user.getDisplayName());
        } else {
            userMap.put("nickname", "none");
        }

        userColRef.document(user.getUid())
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        dialog.dismiss();

                        goMainActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "saveUserInfo failed", e);

                        dialog.dismiss();


                        Toast.makeText(LoginActivity.this, "saveUserInfo failed", Toast.LENGTH_SHORT).show();


                    }
                });




    }



    private void goMainActivity() {

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}



;