package com.abd.classroom1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageViewerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageViewerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Client client;
    private UserLogin iam;
    private String reciverID;
    private ListView listview;
    private List<ChatMessageModel> l1;
    private MessagesListAdapter mLAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageViewerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageViewerFragment newInstance(String param1, String param2) {
        MessageViewerFragment fragment = new MessageViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setReciverID(String rID){
        this.reciverID=rID;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listview = (ListView) getActivity().findViewById(R.id.list_view_messages);
        l1 = new ArrayList();
        mLAdapter = new MessagesListAdapter(getActivity(), l1);
        l1.add(new ChatMessageModel("ABD", "Hassan", "SIMPLE", "Hello There How?", true));
        l1.add(new ChatMessageModel("Hassan", "Hassan", "SIMPLE", "Nice OK .....", false));
        listview.setAdapter(mLAdapter);
        final EditText inputMsg = (EditText)getActivity().findViewById(R.id.inputMsg);
        Button btnsend = (Button) getActivity().findViewById(R.id.btnSend);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = inputMsg.getText().toString();
                //String savepath = Environment.getExternalStorageDirectory().getPath();
                // Toast.makeText(getActivity(), savepath, Toast.LENGTH_LONG).show();
                if (!(msg.equals(""))) {
                    sendTextMessage(msg);
                    inputMsg.setText("");
                }
            }
        });

    }

    private void sendTextMessage(String txtmsg) {
        TextMeesage currTm = new TextMeesage();
        currTm.setSenderID(iam.getUserID());
        currTm.setSenderName(iam.getUserName());
        currTm.setMessageType("TXT");
        currTm.setTextMessage(txtmsg);
        currTm.setRecivers(new String[]{reciverID});
        addNewMessage(new SimpleTextMessage(iam.getUserID(), iam.getUserName(), "TXT", txtmsg), true);
        client.sendTCP(currTm);

    }

    public void setClient(Client cl) {
        this.client = cl;
    }

    public void setUserlogin(UserLogin ul) {
        this.iam = ul;
    }

    public MessageViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    protected void addNewMessage(SimpleTextMessage simplem,boolean fromMe ) {
        if (simplem.getMessageType().equals("TXT")) {
            ChatMessageModel chm = new ChatMessageModel(simplem.getSenderName(), "", "TXT", simplem.getTextMessage(), fromMe);
            l1.add(chm);
            mLAdapter.notifyDataSetChanged();
        }

    }
    protected ChatMessageModel addNewImageMessage(FileChunkMessageV2 fcmv2,boolean fromMe ) {
        String fileType="FLE";
        if(SendUtil.checkIfFileIsImage(fcmv2.getFileName())){
            fileType = "IMG";
        }

        ChatMessageModel chm = new ChatMessageModel(fcmv2.getSenderName(), "", fileType,fcmv2.getFileName()+" Recived, Wait To Complete Loading", fromMe);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ring1);
        chm.setImage(bm);
        l1.add(chm);
        mLAdapter.notifyDataSetChanged();
        return  chm;

    }
    protected  void updateAdapterchanges(){
        mLAdapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_viewer, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
      /*  try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
