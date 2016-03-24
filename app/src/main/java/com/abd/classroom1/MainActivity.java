package com.abd.classroom1;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.net.InetAddress;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoginFragment.OnFragmentInteractionListener,Runnable {

    // begin note : should be save later in the bundle;
    Client client;
    Kryo kryo;
    FragmentManager fm;
    FragmentTransaction ft;
    LoginFragment loginfrag;
    ExamViewerFragment examfrag;
    MessageViewerFragment messageViewerFragmentFragment;
    ChatMessageModel ImageChatModel = null;
    BuildFileFromBytesV2 buildExamFromBytes;
    Thread conectionThread;
    private UserLogin iam = null;
    private BuildFileFromBytesV2 buildfromBytesV2;

    private  int mDstWidth; // required image Width
    private  int mDstHeight; // required Image Hight


    ///// end note

    // lock code
    private static final int ADMIN_INTENT = 15;
    private static final String description = "Sample Administrator description";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        handler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // lock code
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, description);
        startActivityForResult(intent, ADMIN_INTENT);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // define device width and hight for image decoding
        mDstWidth = getResources().getDimensionPixelSize(R.dimen.destination_width);
        mDstHeight = getResources().getDimensionPixelSize(R.dimen.destination_height);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fm = getFragmentManager();
        loginfrag = (LoginFragment) fm.findFragmentByTag("LOGIN");
        if (loginfrag == null) {
            loginfrag = new LoginFragment();
        }
        ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, loginfrag, "LOGIN");
        ft.commit();
        // loginfrag.hideErrorMessage();


        //open the connection for the first TIME
        prepareConnection();
         conectionThread = new Thread(this);
        conectionThread.start();



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {

            dealWithExamMessage(null);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /// ABd Add this code
    private void prepareConnection(){
        client = new Client(16384, 8192);
        kryo = client.getKryo();
        kryo.register(byte[].class);
        kryo.register(String[].class);
        kryo.register(UserLogin.class);
        kryo.register(TextMeesage.class);
        kryo.register(SimpleTextMessage.class);
        kryo.register(FileChunkMessageV2.class);
        kryo.register(LockMessage.class);
    }

    public boolean openConnection() throws Exception {

        client.start();
        InetAddress address = client.discoverHost(54777, 5000);
        client.connect(5000, address, 9995, 54777);

        client.addListener(new Listener() {
            public void received(Connection c, Object ob) {
                if (ob instanceof UserLogin) {
                    if (!((UserLogin) ob).isLogin_Succesful()) {
                        showInvalidUserNameOrPassword();
                        return;

                    }
                    if (((UserLogin) ob).getUserType().equals("STUDENT")) {
                        System.out.println("Login Message Recived");
                        if (((UserLogin) ob).isLogin_Succesful() && (iam == null)) {
                            setSuccessfulLogin(((UserLogin) ob));
                            fm = getFragmentManager();
                            ft = fm.beginTransaction();
                            if (messageViewerFragmentFragment == null) {
                                messageViewerFragmentFragment = new MessageViewerFragment();
                            }
                            ft.replace(R.id.fragment_container, messageViewerFragmentFragment, "ACTIVE");
                            messageViewerFragmentFragment.setClient(client);
                            messageViewerFragmentFragment.setUserlogin((UserLogin) ob);
                            ft.commit();
                            Log.d("INFO", "Succesfull Log IN");
                        }
                    }
                } else if (ob instanceof SimpleTextMessage) {

                    if (messageViewerFragmentFragment != null) {
                        final Object ot = ob;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageViewerFragmentFragment.addNewMessage((SimpleTextMessage) ot, false);
                            }
                        });

                    }
                } else if (ob instanceof FileChunkMessageV2) {

                    if (((FileChunkMessageV2) ob).getFiletype().equals(FileChunkMessageV2.FILE)) {
                        Log.d("FILE", "New File Recived");
                        dealWithFileMessage(((FileChunkMessageV2) ob));
                    } else if (((FileChunkMessageV2) ob).getFiletype().equals(FileChunkMessageV2.EXAM)) {
                        dealWithExamMessage((FileChunkMessageV2) ob);

                    }
                } else if (ob instanceof LockMessage) {
                    Log.d("FILE", "Lock Message received");
                    LockMessage msg = (LockMessage) ob;
                    if (msg.isLock()) {
                        boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                        if (isAdmin) {
                            mDevicePolicyManager.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                            mDevicePolicyManager.setPasswordMinimumLength(mComponentName, 5);
                            mDevicePolicyManager.resetPassword("123456", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                            mDevicePolicyManager.lockNow();

                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                                    final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                                }
                            });


                        } else {
                        }
                    } else {
                        mDevicePolicyManager.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                        mDevicePolicyManager.setPasswordMinimumLength(mComponentName, 0);
                        mDevicePolicyManager.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                            }
                        });

                    }
                }
            }
        });
        return true;


    }
    private void showInvalidUserNameOrPassword() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginfrag.showInvalidLoginMessage();
            }
        });
    }


    public void dealWithFileMessage(FileChunkMessageV2 fcmv2) {
        try {

            String savepath = Environment.getExternalStorageDirectory().getPath();
            Log.d("INFO", "File Chunk Recived");
            //recive the first packet from new file
            if (fcmv2.getChunkCounter() == 1L) {
                final FileChunkMessageV2 tfcmv2 = fcmv2;

                Log.d("INFO PAth=", savepath + "/Classrom");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // we put loading Image Here
                        ImageChatModel = messageViewerFragmentFragment.addNewImageMessage(tfcmv2, false);
                    }
                });
                buildfromBytesV2 = new BuildFileFromBytesV2(savepath + "/Classrom/");
                buildfromBytesV2.constructFile(fcmv2);

            } else if (buildfromBytesV2 != null) {
                Log.d("INFO", "Current File Chunk: " + Long.toString(fcmv2.getChunkCounter()));
                if (buildfromBytesV2.constructFile(fcmv2)) {
                    if (SendUtil.checkIfFileIsImage(fcmv2.getFileName())) {
                       // Bitmap bm = BitmapFactory.decodeFile(savepath + "/Classrom/" + fcmv2.getFileName());
                        String tempImagePath = savepath + "/Classrom/" + fcmv2.getFileName();
                       // Bitmap bm = ScalingUtilities.fitImageDecoder(tempImagePath,mDstWidth,mDstHeight);
                        Bitmap bm = ScalDownImage.decodeSampledBitmapFromResource(tempImagePath,mDstWidth,mDstHeight);
                        ImageChatModel.setImage(bm);
                        ImageChatModel.setSimpleMessage(fcmv2.getFileName() + " COMPLETE");
                    } else {
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.filecompleteicon);
                        ImageChatModel.setImage(bm);
                        ImageChatModel.setSimpleMessage(fcmv2.getFileName() + " COMPLETE");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // we put loading Image Here
                            messageViewerFragmentFragment.updateAdapterchanges();
                        }
                    });
                    // we detect that file completed
                    Log.d("INFO", "EOF, FILE REcived Completely");
                }
                /// SendUtil.sendFileChunkToRecivers(clientTable, fcmv2, tRecivers);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dealWithExamMessage(FileChunkMessageV2 efMessage) {
        try {
            Log.d("INFO", "EXAM File  Recived");
            String internalSavePath = this.getApplicationContext().getFilesDir().getPath();
            String tempfilename = "";
            //recive the first packet from new file
            if (efMessage.getChunkCounter() == 1L) {
                Log.d("INFO", "New EXAM File  recived");
                internalSavePath = this.getApplicationContext().getFilesDir().getPath();
                Log.d("INFO", "Save Path " + internalSavePath);
                tempfilename = efMessage.getFileName();
                buildExamFromBytes = new BuildFileFromBytesV2(internalSavePath + "/");
            }
            Log.d("INFO", "Current File Chunk: " + Long.toString(efMessage.getChunkCounter()));
            Log.d("INFO", "Current File Chunk: " + efMessage.getSenderID());
            Log.d("INFO", "Current File Chunk: " + efMessage.getSenderName());
            if (buildExamFromBytes.constructFile(efMessage)) {
                Log.d("INFO", "FILE COMPLETE ");

            }


            List<QuestionItem> tQuestionsList = XmlExamParser.examParser(internalSavePath + "/" + tempfilename);
            Log.d("EXAM INFO", "List Size = : " + Integer.toString(tQuestionsList.size()));
            for (QuestionItem qqi : tQuestionsList) {
                Log.d("EXAM TEXT", qqi.getQuestionText());
                Log.d("EXAM Type", qqi.getQuestionType());
                Log.d("Exam Answer", qqi.getQuestionAnswer());

            }
            ft = fm.beginTransaction();
            examfrag = new ExamViewerFragment();
            examfrag.setQuestionsList(tQuestionsList);
            ft.replace(R.id.fragment_container, examfrag, "EXAM");
            //examfrag.updateQuestionsList(tQuestionsList);
            ft.commit();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    // lock code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setSuccessfulLogin(UserLogin ul) {
        this.iam = ul;
    }

    @Override
    public void run() {
        boolean flag= true;
        while(flag) {
            Log.d("INFO","hello thread");

            try {
                Thread.sleep(100);
                if (openConnection()) {
                    Log.d("Info", "Connectione done");
                    loginfrag.setClient(client);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginfrag.hideErrorMessage();
                        }
                    });
                    ;
                    flag = false;
                }

            } catch (Exception e) {
                //  Toast.makeText(MainActivity.this,
                // getResources().getText(R.string.unable_to_connect_server), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }
    }
}
