package com.example.newequest.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.newequest.activity.LoginActivity;
import com.example.newequest.activity.MainActivity;
import com.example.newequest.provider.RemoteUser;
import com.example.newequest.util.Connection;
import com.example.newequest.util.Encryption;
import com.google.android.material.transition.MaterialSharedAxis;

public class Session {

    private static User user = null;
    private static final String PREFERENCE_FILE = "PreferenceFile";

    private static final String CAMPOS = "Campos";
    private static final String SFI = "São Francisco do Itabapoana";
    private static final String SJB = "São João da Barra";
    private static final String QUISSAMA = "Quissamã";
    private static final String MACAE = "Macaé";
    private static final String CF = "Cabo Frio";
    private static final String AC = "Arraial do Cabo";
    private static final String RO = "Rio das Ostras";
    private static final String SENHA = "123456";


    public static void login(User loginUser, Context loginContext){
        if (Connection.isConected(loginContext)) {
            //se conectado - login remoto
            RemoteUser.login(loginUser, loginContext);
        }else{
            //se não conectado - login local TODO: verificar se loga dp com conexão
            User lastUser = getLastUserLoggedLocal(loginContext);
            if (lastUser.getEmail().equals(loginUser.getEmail())) {
                if (lastUser.getPassword().equals(Encryption.encrypt(Encryption.encrypt(loginUser.getPassword())))) {
                    setUser(lastUser,null, loginContext);
                    Intent intent = new Intent(loginContext, MainActivity.class);
                    loginContext.startActivity(intent);
                }
            }
            Toast.makeText(loginContext, "Sem conexão de internet, só é possível logar com a conta: " + lastUser.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void lightLogin(Context loginContext){
        User lastUser = getLastUserLoggedLocal(loginContext);
        setUser(lastUser,null, loginContext);
        Intent intent = new Intent(loginContext, MainActivity.class);
        loginContext.startActivity(intent);
    }

    public static void logout(Context logoutContext){
        RemoteUser.logout();
        user = null;
        Toast.makeText(logoutContext, "Desconectado com sucesso!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(logoutContext, LoginActivity.class);
        logoutContext.startActivity(intent);
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User newUserLogged, String password, Context context) {
        user = newUserLogged;
        if(password != null) {
            user.setPassword(password);
            saveLastUserLoggedLocal(user, context);
        }
    }

    public static boolean isUserAdmin(){
        return user.getType()>0;
    }

    public static boolean isUserLogged(){
        return user!=null;
    }

    private static void saveLastUserLoggedLocal(User user, Context context){
        final SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_FILE, 0);
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putString("userId", user.getUserId());
        editor.putString("userName", user.getName());
        editor.putString("userEmail", user.getEmail());
        editor.putInt("userType", user.getType());
        editor.putString("lastLogin", Encryption.encrypt(user.getPassword()));
        editor.putString("userCity", user.getCity());
        editor.putBoolean("userActive", user.isActive());
        editor.commit();
    }

    public static User getLastUserLoggedLocal(Context context){
        final SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_FILE, 0);

        User user = new User();
        user.setName(preferences.getString("userName", ""));
        user.setEmail(preferences.getString("userEmail", ""));
        user.setType(preferences.getInt("userType", 0));
        user.setPassword(preferences.getString("lastLogin", ""));
        user.setUserId(preferences.getString("userId", ""));
        user.setCity(preferences.getString("userCity", ""));
        user.setActive(preferences.getBoolean("userActive", false));
        return user;
    }

}
