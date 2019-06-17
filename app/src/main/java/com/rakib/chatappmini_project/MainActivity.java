package com.rakib.chatappmini_project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rakib.chatappmini_project.adapter.ChatAdapter;
import com.rakib.chatappmini_project.model.Chat;
import com.rakib.chatappmini_project.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static String TAG = MainActivity.class.getSimpleName();

    Context mContext;

    private DatabaseReference mDatabase;
    private DatabaseReference mUsersReference;
    private DatabaseReference mAllUserReference;
    private DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    EditText messageEditText;
    ImageButton sendButton;
    ImageButton imgBtnSignOut;
    ImageView profileImage;
    TextView typingTextView, emptyTextView;
    ProgressBar progressBar;


    String message;
    String name;
    String photoUrl;
    String date;

    List<Chat> chatList = new ArrayList<>();

    ChatAdapter chatAdapter;

    RecyclerView recyclerView;


    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        initializeViews();


        if (firebaseUser == null) {
            signIn();
        } else {
            initUser();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("messages");

        if (firebaseUser != null) {
            mUsersReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        if (firebaseUser != null) {

        }


    }

    private void initUser() {
        Glide.with(mContext)
                .load(firebaseUser.getPhotoUrl())
                .circleCrop()
                .into(profileImage);
    }

    private void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);

    }

    private void signOut() {

        new AlertDialog.Builder(mContext)
                .setTitle("Are you sure want to sign out?")
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AuthUI.getInstance()
                                .signOut(mContext)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();

                                        finish();

                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ...
                    }
                })
                .show();

    }

    private void initializeViews() {
        messageEditText = findViewById(R.id.message_et);
        sendButton = findViewById(R.id.send_btn);
        recyclerView = findViewById(R.id.messageRecyclerView);
        profileImage = findViewById(R.id.profile_image);
        imgBtnSignOut = findViewById(R.id.img_btn_sign_out);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setStackFromEnd(true);

        chatAdapter = new ChatAdapter(getApplicationContext(), chatList);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(chatAdapter);


        typingTextView = findViewById(R.id.typing_tv);
        emptyTextView = findViewById(R.id.empty_tv);

        progressBar = findViewById(R.id.progressBar);


        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d(TAG, "onTextChanged: " + messageEditText.getText().toString() + messageEditText.getText().toString().length());

                if (messageEditText.getText().length() > 0) {
                    mUsersReference.child("typing").setValue(true);
                    sendButton.setColorFilter(Color.parseColor("#3498db"));
                } else {
                    mUsersReference.child("typing").setValue(false);
                    sendButton.setColorFilter(Color.parseColor("#000000"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }
        });


    }

    private void sendMessage() {
        message = messageEditText.getText().toString();

        if (TextUtils.isEmpty(message)) {

            Toast.makeText(this, "Write a message first", Toast.LENGTH_SHORT).show();

        } else {

            name = firebaseUser.getDisplayName();
            photoUrl = firebaseUser.getPhotoUrl().toString();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm aa");
            Date d = new Date();
            date = dateFormat.format(d).toString();

            Chat chat = new Chat(firebaseUser.getUid(), message, name, photoUrl, "null", date, false, d.getTime());
            mDatabase.push().setValue(chat);

            messageEditText.setText("");
            mUsersReference.child("typing").setValue(false);


        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        loadMessages();


        if (firebaseUser != null) {
            mAllUserReference = FirebaseDatabase.getInstance().getReference().child("users");
            mAllUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    typingTextView.setText("");
                    typingTextView.setVisibility(View.GONE);
                    Log.d(TAG, "all user: " + dataSnapshot.getValue());

                    boolean isFirst = true;

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        User user = dataSnapshot1.getValue(User.class);
                        Log.d(TAG, "all user typing: " + user.isTyping() + " " + dataSnapshot1.getKey());
                        if (user.isTyping()) {
                            if (!dataSnapshot1.getKey().equals(firebaseUser.getUid())) {

                                if (isFirst) {

                                    isFirst = false;

                                    typingTextView.append(user.getName() + " is typing...");

                                } else {

                                    typingTextView.append("\n" + user.getName() + " is typing...");

                                }

                                typingTextView.setVisibility(View.VISIBLE);
                            }

                        } else {
                            //typingTextView.setText("");
                            //typingTextView.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                databaseReference = FirebaseDatabase.getInstance().getReference();
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                    User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), false);
                    databaseReference.child("users").child(firebaseUser.getUid()).setValue(user);

                    if (response.isNewUser()) {
                        String joiningMessage = firebaseUser.getDisplayName() + " joined the chat.";

                        Chat chat = new Chat(null, joiningMessage, null, null, "null", null, true, (new Date()).getTime());
                        databaseReference.child("messages").push().setValue(chat);
                    }

                    Glide.with(mContext)
                            .load(user.getPhotoUrl())
                            .circleCrop()
                            .into(profileImage);

                }

                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
                loadMessages();
                //...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "There was an error. Try again later.", Toast.LENGTH_SHORT).show();

            }
        }
    }


    void loadMessages() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (firebaseUser != null) {
                    chatList.clear();
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());

                    if (!dataSnapshot.exists()) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        emptyTextView.setVisibility(View.INVISIBLE);
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Chat chat = dataSnapshot1.getValue(Chat.class);
                            Log.d(TAG, "onDataChange: " + chat.getText());
                            chatList.add(chat);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManager.setOrientation(RecyclerView.VERTICAL);
                        layoutManager.setStackFromEnd(true);

                        chatAdapter = new ChatAdapter(getApplicationContext(), chatList);
                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(chatAdapter);
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
