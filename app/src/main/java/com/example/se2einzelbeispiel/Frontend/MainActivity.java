package com.example.se2einzelbeispiel.Frontend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        sendButton = findViewById(R.id.main_button_send);
        userInput = findViewById(R.id.main_editText_userInput);
        serverResponse = findViewById(R.id.main_textView_serverResponse);

        //define toolbar
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("NetworkTest");
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    public void onClickSendButton(View v){
        String studentNumber = userInput.getText().toString();
        new TcpConnectionTask(serverResponse).execute(studentNumber);

    }

}

class TcpConnectionTask extends AsyncTask <String , Void, String>{

    //define a weak reference to avoid memory leaks
    private WeakReference <TextView> serverResponseReference;

    public TcpConnectionTask(TextView serverResponse){
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

            //send the message to the server -> first param of passed values <String, void, String>
            out.writeBytes(strings[0] + "\n");

            //receive message from server
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
        if (serverResponse != null){
            serverResponse.setText(result);
        }
    }
}
