Manifest File:

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>

<service android:name=".FirebaseMsgService"
	android:exported="false">
	<intent-filter>
		<action android:name="com.google.firebase.MESSAGING_EVENT"/>
	</intent-filter>
</service>
```

<Project> build.gradle.kts

```
id("com.google.gms.google-services") version "4.4.3" apply false
````

<App> build.gradle.kts

```
plugin:

	id("com.google.gms.google-services")
	
dependencies

	implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.database)
```
My Contact Model:

```
package com.example.dilalpurrealtimedatabase;

public class ContactModel {
    private String name, phoneNumber;

    public ContactModel(String name, String phoneNumber)
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

```

FirebaseMsgService custom class for push notification:

```
package com.example.dilalpurrealtimedatabase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMsgService extends FirebaseMessagingService{

    public static final int REQUEST_CODE = 100;
    public static final String CHANNEL_ID = "Channel 1";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Refreshed Token: ", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if(message.getNotification() != null)
        {
            pushNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }
    }

    private void pushNotification(String title, String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "My Channel 1";
            String description = "Channel for Push Notification";
            int inportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, inportance);
            notificationChannel.setDescription(description);

            if(notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);

            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.face_1)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setSubText(msg)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .build();
        }
        else
        {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.face_1)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setSubText(msg)
                    .setAutoCancel(true)
                    .build();
        }

        if(notificationManager != null)
            notificationManager.notify(1, notification);

    }
}

```

Main Activity:

```
package com.example.dilalpurrealtimedatabase;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful())
                {
                    Log.e("Token Details: ", "Token failed to receive");
                    return;
                }
                String token = task.getResult();
                Log.d("Token", token);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Contact");
        String key = databaseReference.push().getKey();
        ContactModel contactModel = new ContactModel("Md Rokonuzzaman", "01724035774");
        if(key != null) databaseReference.child(key).setValue(contactModel);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
```

