package com.example.se2einzelbeispiel.Frontend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.se2einzelbeispiel.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    EditText userInput;
    TextView serverResponse;
    Button sendButton;

    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link widgets
        sendButton = findViewById(R.id.main_button_send);
        userInput = findViewById(R.id.main_editText_userInput);
        serverResponse = findViewById(R.id.main_textView_serverResponse);

        //define toolbar
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("NetworkTest");
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        //set up menu navigation
        myToolbar.setNavigationIcon(R.drawable.ic_action_menu);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                PopupMenu mainMenu = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.main_menu, mainMenu.getMenu());
                mainMenu.show();
            }
        } );
    }

    //onClick executes async task (TcpConnection)
    public void onClickSendButton(View v){
        String studentNumber = userInput.getText().toString();
        new TcpConnectionTask(serverResponse).execute(studentNumber);

    }

    //define action for click on menuItem -> start new activity (ToBinary)
    public void onClickToBinary(MenuItem item){
        Intent intent = new Intent(this, ToBinaryActivity.class);
        startActivity(intent);
    }

    //app crashes if method doesn't exist
    public void onClickNetworkTest(MenuItem item){

    }

}

class TcpConnectionTask extends AsyncTask <String , Void, String>{

    //define a weak reference to avoid memory leaks
    private WeakReference <TextView> serverResponseReference;

    TcpConnectionTask(TextView serverResponse){
        this.serverResponseReference = new WeakReference<>(serverResponse);
    }

    //define task which should be computed in background
    @Override
    protected String doInBackground(String... strings) {
        try{
            //establish a new socket to target server
            Socket connectionSocket = new Socket("se2-isys.aau.at", 53212);

            //set input + output stream for connection
            DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());

            //send the message to the server -> first param of passed values <String, void, String> -> String ends with a new line
            out.writeBytes(strings[0] + "\n");

            //receive message from server
            //todo implement logic to read more than one line from response
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String result = r.readLine();

            //close connection
            connectionSocket.close();

            //return the result to method onPostExecute
            return result;

        } catch (UnknownHostException e){
            return new String("Invalid ip address of host.");
        } catch (IOException e){
            return new String("Failed I/O.");
        }
    }


    //invoked in ui thread after background computation finishes
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        TextView serverResponse = serverResponseReference.get();
        //check if reference exists or already been garbage collected
        if (serverResponse != null){
            serverResponse.setText(result);
        }
    }


}
