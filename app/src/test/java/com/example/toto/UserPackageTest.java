package com.example.toto;

import com.example.toto.interfaces.DatabaseHelper;
import com.example.toto.sessions.Status;
import com.example.toto.subjects.Subject;
import com.example.toto.users.Role;
import com.example.toto.users.User;
import com.example.toto.users.UserController;
import com.example.toto.users.UserDatabaseHelper;
import com.example.toto.users.UserManager;
import com.google.android.gms.internal.firebase_auth.zzao;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(MockitoJUnitRunner.class)
public class UserPackageTest {
    //private Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void flattenTest(){
        Map<String, Map<String,Object>> mock = new HashMap<>();
        Map<String,Object> mockSubjects = new HashMap<>();
        mockSubjects.put("subject-id1",new Subject("subject-id1","spanish"));
        mockSubjects.put("subject-id2",new Subject("subject-id2","french"));
        mockSubjects.put("Subjects",mockSubjects);

        Map<String, Subject> expected = new HashMap<>();
        expected.put("subject-id1",new Subject("subject-id1","spanish"));
        expected.put("subject-id2",new Subject("subject-id2","french"));

        //String username,String email, Role role,String id, Status status
        User user = new User("foo","foo@gmail.com", Role.STUDENT, "id-1", Status.ACCEPTED);
        Map<String, Subject> actual = user.flatten2(mock);
        for(Map.Entry<String,Subject> entry : actual.entrySet()) {
            assertTrue(expected.containsKey(entry.getKey()));
        }
    }

}
