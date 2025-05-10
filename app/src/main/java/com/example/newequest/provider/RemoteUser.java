package com.example.newequest.provider;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.newequest.activity.MainActivity;
import com.example.newequest.model.Session;
import com.example.newequest.model.User;
import com.example.newequest.util.Connection;
import com.example.newequest.util.Encryption;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RemoteUser {

    private static DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser fbUser;

    public static DatabaseReference getDBRef(){
        if(db == null){
            db = FirebaseDatabase.getInstance().getReference().child("users");
        }
        return db;
    }

    public static void createUser(final User localUser, final Context context) {
        if(Connection.isConected(context)) {
            auth.createUserWithEmailAndPassword(localUser.getEmail(), localUser.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    String idUser = task.getResult().getUser().getUid();
                                    localUser.setUserId(idUser);
                                    db.child(idUser).setValue(localUser);
                                    fbUser = auth.getCurrentUser();

                                    Toast.makeText(context, "Sucesso ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                                    //TODO send password reset

                                } catch (Exception e) {
                                    Toast.makeText(context, "Cadastro de usuário incompleto." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String exception = "";
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    exception = "Digite uma senha mais forte!";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    exception = "Por favor, digite um email válido";
                                    Log.i("carol", localUser.getEmail());
                                } catch (FirebaseAuthUserCollisionException e) {
                                    exception = "Esta conta já foi cadastrada";
                                } catch (Exception e) {
                                    exception = "Erro ao cadastrar usuário: " + e.getMessage();
                                    e.printStackTrace();
                                }
                                Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(context, "Sem conexão com a internet, tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void login(final User loginUser, final Context context){
        logout();
        auth.signInWithEmailAndPassword(loginUser.getEmail(), loginUser.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    fbUser = auth.getCurrentUser();

                    db.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Log.i("user", dataSnapshot.toString());
                            User user = dataSnapshot.getValue(User.class);
                            Session.setUser(user, Encryption.encrypt(loginUser.getPassword()), context);

                            if(user.isActive()) {
                                Toast.makeText(context, "Sucesso ao fazer login!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            }else{
                                Toast.makeText(context, "Login não permitido.", Toast.LENGTH_SHORT).show();
                                logout();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    String exception = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        exception = "Usuário não cadastrado.";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        exception = "Email ou senha não correspondem a um usuário cadastrado";
                    }catch(Exception e){
                        exception = "Erro ao logar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isThereSomeUserLogged(){
        try {
            if (auth.getCurrentUser() != null) {
                if (fbUser == null) {
                    fbUser = auth.getCurrentUser();
                }
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String getLoggedUserId(){
        if (fbUser != null){
            return fbUser.getUid();
        }
        return "";
    }

    public static void logout(){
        auth.signOut();
        fbUser = null;
    }

    public static void deleteUser(String email, String password, final Context context){
        AuthCredential credential = EmailAuthProvider.getCredential(email,password);

        if(isThereSomeUserLogged()) {
            fbUser.reauthenticateAndRetrieveData(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        fbUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Sucesso ao excluir usuário!", Toast.LENGTH_SHORT).show();
                                    fbUser = null;
                                    //deletar usuário do nó users
                                    //redirecionar tela de login
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (Exception e) {//completar exceções
                                        e.printStackTrace();
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(context, "Erro ao excluir usuário!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(context, "Erro ao excluir usuário!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void deactivateUser(User user, Context context){
        if((Connection.isConected(context)) && (isThereSomeUserLogged())) {
            try {
                user.setActive(false);
                db.child(user.getUserId()).setValue(user);
                Toast.makeText(context, "Usuário excluido com sucesso.", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(context, "Erro, tente novamente.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context, "É necessário conexão à internet para finalizar essa operação.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateUser(User updatedUser, Context context){
        if((Connection.isConected(context)) && (isThereSomeUserLogged())) {
            try {
                db.child(updatedUser.getUserId()).setValue(updatedUser);
            }catch (Exception e){
                Toast.makeText(context, "Erro, tente novamente.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context, "É necessário conexão à internet para finalizar essa operação.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void getLoggedUserFromFirebase(String userId, final Context context){
        db.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.i("user", dataSnapshot.toString());
                Session.setUser(dataSnapshot.getValue(User.class), null, context);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void sendPasswordReset(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("user", "Email enviado.");
                        }else{
                            Log.i("user", "Erro ao enviar email, tente novamente.");
                        }
                    }
                });
    }

    //testar
    public static void verifyEmail() {
        fbUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("user", "Email sent.");
                        }
                    }
                });
    }
}