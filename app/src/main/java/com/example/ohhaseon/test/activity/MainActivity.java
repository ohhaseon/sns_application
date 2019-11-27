package com.example.ohhaseon.test.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ohhaseon.test.R;
import com.example.ohhaseon.test.adapter.RecyclerAdapter;
import com.example.ohhaseon.test.item.RecyclerItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private NavigationView navRightView;
    private DrawerLayout layoutDrawer;
    private ConstraintLayout navRightMain;
    private static final String TAG = "Database firebase";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userColRef = db.collection("user");
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String myNickname;
    private String myEmail;

    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;

    private ArrayList<RecyclerItem> mItems = new ArrayList<>();

    private CollectionReference commentColRef = FirebaseFirestore.getInstance().collection("comment");
    private String[] names = {"Charlie","Andrew","Han","Liz","Thomas","Sky","Andy","Lee","Park"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        layoutDrawer = (DrawerLayout) findViewById(R.id.activityMain);
        navRightView = (NavigationView) findViewById(R.id.navRightView);


        findViewById(R.id.btnSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutDrawer.openDrawer(navRightView);
            }
        });

        navRightMain = (ConstraintLayout) navRightView.getHeaderView(0);



        if (user.getDisplayName() != null) {

            TextView navRightMainNickname = (TextView) navRightMain.findViewById(R.id.txtNickname0);
            navRightMainNickname.setText(user.getDisplayName());
        }
        if (user.getEmail() != null) {

            TextView navRightMainEmail = (TextView) navRightMain.findViewById(R.id.txtEmail0);
            navRightMainEmail.setText(user.getEmail());
        }

        if (myNickname != null) {

            TextView navRightMainNickname1 = (TextView) navRightMain.findViewById(R.id.txtNickname1);
            navRightMainNickname1.setText(myNickname);
        }

        if (myEmail != null) {

            TextView navRightMainEmail1 = (TextView) navRightMain.findViewById(R.id.txtEmail1);
            navRightMainEmail1.setText(myEmail);
        }

        EditText navRightMainEditNickname1 = (EditText) navRightMain.findViewById(R.id.EditTextNickname1);

        navRightMainEditNickname1.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.

                                Log.d(TAG, "navRightMainEditNickname1 text: " + v.getText());

                                String nick = v.getText().toString();

                                if(nick != null){
                                    saveUserInfo(nick);
                                }

                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        user = mAuth.getCurrentUser();


        userColRef.document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {


                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());


                            myNickname = new String(documentSnapshot.getData().get("nickname").toString());
                            myEmail = new String(documentSnapshot.getData().get("email").toString());


                            if (myNickname != null) {

                                TextView navRightMainNickname1 = (TextView) navRightMain.findViewById(R.id.txtNickname1);
                                navRightMainNickname1.setText(myNickname);
                            }

                            if (myEmail != null) {

                                TextView navRightMainEmail1 = (TextView) navRightMain.findViewById(R.id.txtEmail1);
                                navRightMainEmail1.setText(myEmail);
                            }


                        } else {
                            //Log.d(TAG,"Error");


                        }
                    }
                });

        findViewById(R.id.btnCommentComplete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentUpload();
            }
        });



        setRecyclerView();

    }

    private void commentUpload() {
        EditText eTComment = (EditText) findViewById(R.id.EditTextComment);



        if (eTComment.getText().toString().length() == 0) {
            Toast.makeText(this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }


        //dialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("comment", eTComment.getText().toString());
        if(myNickname == null){
            data.put("nickname", mAuth.getCurrentUser().getDisplayName());
        }
        else{
            data.put("nickname", myNickname);
        }
        data.put("userId", mAuth.getUid());
        data.put("timestamp", new Date().getTime());

        commentColRef //주소확보
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(MainActivity.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();

                        setData();

                        //dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        //dialog.dismiss();
                    }
                });
    }

    private void setRecyclerView(){


// RecyclerView에 Adapter를 설정해줍니다.
        adapter = new RecyclerAdapter(mItems);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewBulletin);
        recyclerView.setAdapter(adapter);

        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
// setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
// 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        recyclerView.setHasFixedSize(true);

// 다양한 LayoutManager 가 있습니다. 원하시는 방법을 선택해주세요.
// 지그재그형의 그리드 형식
//mainBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
// 그리드 형식
//mainBinding.recyclerView.setLayoutManager(new GridLayoutManager(this,4));
// 가로 또는 세로 스크롤 목록 형식
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        //mainBinding.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(48));

        setData();
    }

    private void setData(){


        mItems.clear();


// RecyclerView 에 들어갈 데이터를 추가합니다.

/*
        for(String name : names){
            mItems.add(new RecyclerItem(name));

        } */


        commentColRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int taskSize = task.getResult().size();

                            //int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {  //가져올 데이터가 복수일 수 있기 때문 그것을 파악하려면 for문을 활용해줘야함



 /*
                                        data.put("comment", eTComment.getText().toString());
                                        data.put("nickname", mAuth.getCurrentUser().getDisplayName());
                                        data.put("userId", mAuth.getUid());
                                        data.put("timestamp", new Date().getTime());
*/


                                String comment = document.getData().get("comment").toString();
                                String nickname = document.getData().get("nickname").toString();
                                String userId = document.getData().get("userId").toString();
                                Date date = new Date((long) document.getData().get("timestamp"));


                                mItems.add(new RecyclerItem(nickname, comment, date.toString()));


                                //count++;


                            }


                            // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
                            adapter.notifyDataSetChanged();

                        }
                    }

                });




    }

    @Override
    public void onBackPressed() {


        if (layoutDrawer.isDrawerOpen(navRightView)) {
            layoutDrawer.closeDrawer(navRightView);
            return;
        }
    }

    private void saveUserInfo(String nick) {

        if (user == null) return;

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("nickname", nick);

        userColRef.document(user.getUid())
                .update(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //dialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "write nickname failed", e);

                        //dialog.dismiss();

                        Toast.makeText(MainActivity.this, "write nickname failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }



}



