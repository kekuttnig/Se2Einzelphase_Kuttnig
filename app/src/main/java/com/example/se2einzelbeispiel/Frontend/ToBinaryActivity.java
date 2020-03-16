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

import java.lang.ref.WeakReference;

public class ToBinaryActivity extends AppCompatActivity {
    EditText userInput;
    TextView convertedNumber;
    Button computeButton;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tobinary);

        //link widgets
        userInput = findViewById(R.id.toBinary_editText_userInput);
        convertedNumber = findViewById(R.id.toBinary_textView_convertedNumber);
        computeButton = findViewById(R.id.toBinary_button_compute);

        //define toolbar
        Toolbar myToolbar = findViewById(R.id.toBinary_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("ToBinary");
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

    //onClick executes async task (ConvertToBinary)
    public void onClickComputeButton(View v){
        int studentNumber = Integer.parseInt(userInput.getText().toString());
        new ConvertToBinaryTask(convertedNumber).execute(studentNumber);
    }

    //define action for click on menuItem -> start new activity (MainActivity)
    public void onClickNetworkTest(MenuItem item){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    //app crashes if method doesn't exist
    public void onClickToBinary(MenuItem item){

    }

}

class ConvertToBinaryTask extends AsyncTask <Integer, Void, String>{
    //define weak reference to avoid memory leaks
    private WeakReference <TextView> convertedNumberReference;

    ConvertToBinaryTask (TextView convertedNumber){
        convertedNumberReference = new WeakReference<>(convertedNumber);
    }

    @Override
    protected String doInBackground(Integer... integers) {
        //function to convert sum of digits to a binary number
        int studentNumber = integers[0];
        int sum = 0;
        int binaryDigits = 32;

        //compute sum of digits
        while (studentNumber != 0){
            sum += (studentNumber % 10);
            studentNumber /= 10;
        }

        StringBuilder result = new StringBuilder("");

        //convert sum of digits to binary
        while (sum != 0){
            result.append((sum % 2));
            binaryDigits--;
            sum /= 2;
        }

        //fill with zero's
        while (binaryDigits != 0){
            result.append(0);
            binaryDigits --;
        }

        //reverse result
        result.reverse();

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        TextView convertedNumber = convertedNumberReference.get();
        if (convertedNumber != null){
            convertedNumber.setText(result);
        }

    }
}
