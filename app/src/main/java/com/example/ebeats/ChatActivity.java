package com.example.ebeats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    static final int RC_PHOTO_PICKER = 1;
    static ArrayList<Uri> uriarray = new ArrayList<Uri>();

    private Button sendBtn;
    private EditText messageTxt;
    private RecyclerView messagesList;
    private ImageButton imageBtn;
    private TextView usernameTxt;
    private View loginBtn;
    private View logoutBtn;
    private ChatMessageAdapter adapter = new ChatMessageAdapter(this);
    Uri url;
    CollectionReference superintendants;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private FirebaseApp app;
    CollectionReference chats=db.collection("chats");
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private StorageReference instance = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        messageTxt = (EditText) findViewById(R.id.messageTxt);
        messagesList = (RecyclerView) findViewById(R.id.messagesList);
        usernameTxt = (TextView) findViewById(R.id.usernameTxt);

        messagesList.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messagesList.smoothScrollToPosition(adapter.getItemCount());

            }
        });
        db.collection("chats").whereEqualTo("name","Superintendent").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                {
                    ChatMessage chat=queryDocumentSnapshot.toObject(ChatMessage.class);
                    adapter.addMessage(chat);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // TODO: add authentication

        final String username = "akshay";
        this.usernameTxt.setText(username);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesList.setHasFixedSize(false);
        messagesList.setLayoutManager(layoutManager);


        // Show an image picker when the user wants to upload an imasge
//        imageBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
//            }
//        });
//        // Show a popup when the user asks to sign in
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                LoginDialog.showLoginPrompt(MainActivity.this, app);
//            }
//        });
//        // Allow the user to sign out
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                auth.signOut();
//            }
//        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChatMessage chat = new ChatMessage(username, messageTxt.getText().toString(),"1");
                // Push the chat message to the database
                adapter.addMessage(chat);
                db.collection("chats").add(chat)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("CHAT", username + " " + messageTxt.getText().toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("CHAT", e.toString());
                            }
                        });

                messageTxt.setText("");
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            final Uri selectedImageUri = data.getData();
            // Get a reference to the location where we'll store our photos
            // final StorageReference imageref = instance.child(selectedImageUri.toString());
            // Get a reference to store file at chat_photos/<FILENAME>
//                final StorageReference photoRef = imageref.child(selectedImageUri.getLastPathSegment());
            // Upload file to Firebase Storage
            File file = new File(data.toString());
            String name = file.getName();
            final StorageReference sr = instance.child("/images/"+name);
            if (selectedImageUri != null) {
                Log.d("upload", "/images/" + name);
                sr.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("FINAL URI", "onSuccess: " + selectedImageUri.toString());
                                // When the image has successfully uploaded, we get its download URL
                                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        url=uri;
                                        uriarray.add(uri);
                                        Log.d("uri", "onSuccess: " + uri.toString());
                                        Toast.makeText(ChatActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(ChatActivity.this, url.toString() + " Uploaded successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("error", e.toString());

                                    }
                                });
                                //String name = url.toString();
                                // Set the download URL to the message box, so that the user can send it to the database
                                //messageTxt.setText(url.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "An error Occurred " + e.toString(), Toast.LENGTH_LONG).show();
                        Log.d("FUCK", e.toString());
                    }
                });
            } else {
                Log.d("upload", "null");
            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.getItemId();
        if(item.getItemId()==R.id.image_upload) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent cameraintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(cameraintent, RC_PHOTO_PICKER);
                    return false;
                }
            });
        }
        else
        {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ToViewImages toViewImages=new ToViewImages(uriarray);
                    Intent intent=new Intent(ChatActivity.this,ToViewImages.class);
                    startActivity(intent);
                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}

