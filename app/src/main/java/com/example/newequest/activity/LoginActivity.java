package com.example.newequest.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.newequest.R;
import com.example.newequest.model.Session;
import com.example.newequest.model.User;
import com.example.newequest.provider.RemoteUser;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.i("carol", "iniciando aplicativo...");

        try {
            if (RemoteUser.isThereSomeUserLogged()) {
                //TODO: verificar se é o melhor local para ficar
                Session.lightLogin(LoginActivity.this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Typeface openSansType = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                openSansType = getResources().getFont(R.font.opensansregular);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        final RelativeLayout emailShapeView = findViewById(R.id.email_shape);
        RelativeLayout passwordShapeView = findViewById(R.id.password_shape);

        final EditText emailView = findViewById(R.id.email);
        final EditText passwordView = findViewById(R.id.password);
        Button enterButton = findViewById(R.id.enter_button);

        try {
            emailView.setTypeface(openSansType);
            passwordView.setTypeface(openSansType);
            enterButton.setTypeface(openSansType);
        }catch (Exception e){
            e.printStackTrace();
        }

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enterButton.setEnabled(false);
                enterButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_round_gray));

                if(!emailView.getText().toString().equals("")){
                    if(!passwordView.getText().toString().equals("")){
                        Session.login(new User(emailView.getText().toString(),passwordView.getText().toString()), LoginActivity.this);
                    }else{
                        passwordView.requestFocus();
                        Toast.makeText(LoginActivity.this, "Digite a senha.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    emailView.requestFocus();
                    Toast.makeText(LoginActivity.this, "Digite o email.", Toast.LENGTH_SHORT).show();
                }

                enterButton.setEnabled(true);
                enterButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_round_orange));

            }
        });

        emailShapeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(LoginActivity.this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });

        passwordShapeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(LoginActivity.this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        });

    }
}