package io.github.neelkamath.goalninja;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

public class AccountActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText pwdEditText;

    /* renamed from: io.github.neelkamath.goalninja.AccountActivity$1 */
    class C08661 implements LogInCallback {
        C08661() {
        }

        public void done(ParseUser parseUser, ParseException parseException) {
            AccountActivity.this.setLoadingVisibility(false);
            if (parseUser != null) {
                AccountActivity.this.openApp();
                return;
            }
            Log.w("login", parseException.getMessage());
            Toast.makeText(AccountActivity.this, parseException.getMessage(), 0).show();
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.AccountActivity$2 */
    class C08682 implements SignUpCallback {

        /* renamed from: io.github.neelkamath.goalninja.AccountActivity$2$1 */
        class C08671 implements LogOutCallback {
            C08671() {
            }

            public void done(ParseException parseException) {
                if (parseException != null) {
                    Log.e("logout", parseException.getMessage());
                    Toast.makeText(AccountActivity.this, parseException.getMessage(), 0).show();
                }
            }
        }

        C08682() {
        }

        public void done(ParseException parseException) {
            AccountActivity.this.setLoadingVisibility(false);
            if (parseException == null) {
                Toast.makeText(AccountActivity.this, C0406R.string.verify_email, 0).show();
                ParseUser.logOutInBackground(new C08671());
                return;
            }
            Log.w("signUp", parseException.getMessage());
            Toast.makeText(AccountActivity.this, parseException.getMessage(), 0).show();
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.AccountActivity$3 */
    class C08693 implements RequestPasswordResetCallback {
        C08693() {
        }

        public void done(ParseException parseException) {
            AccountActivity.this.setLoadingVisibility(false);
            if (parseException == null) {
                Toast.makeText(AccountActivity.this, C0406R.string.pwd_reset_sent, 0).show();
                return;
            }
            Log.w("reset", parseException.getMessage());
            Toast.makeText(AccountActivity.this, parseException.getMessage(), 0).show();
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.AccountActivity$4 */
    class C08704 implements LogInCallback {
        C08704() {
        }

        public void done(ParseUser parseUser, ParseException parseException) {
            AccountActivity.this.setLoadingVisibility(false);
            if (parseException == null) {
                AccountActivity.this.openApp();
                return;
            }
            Log.e("login", parseException.getMessage());
            Toast.makeText(AccountActivity.this, parseException.getMessage(), 0).show();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0406R.layout.activity_account);
        if (ParseUser.getCurrentUser() != null) {
            openApp();
        }
        this.emailEditText = findViewById(C0406R.id.emailEditText);
        this.pwdEditText = findViewById(C0406R.id.pwdEditText);
    }

    private void displayConnectionRequired() {
        Toast.makeText(this, C0406R.string.internet_needed, 0).show();
    }

    private void setLoadingVisibility(boolean z) {
        findViewById(C0406R.id.progressBar).setVisibility(!z);
    }

    public void login(View view) {
        if (Internet.isNotConnected(this) != null) {
            displayConnectionRequired();
            return;
        }
        setLoadingVisibility(true);
        ParseUser.logInInBackground(this.emailEditText.getText().toString(), this.pwdEditText.getText().toString(), new C08661());
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void signUp(View view) {
        if (Internet.isNotConnected(this) != null) {
            displayConnectionRequired();
            return;
        }
        view = this.emailEditText.getText().toString();
        String obj = this.pwdEditText.getText().toString();
        if (!view.isEmpty()) {
            if (!obj.isEmpty()) {
                setLoadingVisibility(true);
                ParseUser parseUser = new ParseUser();
                parseUser.setUsername(view);
                parseUser.setEmail(view);
                parseUser.setPassword(obj);
                parseUser.signUpInBackground(new C08682());
                return;
            }
        }
        Toast.makeText(this, C0406R.string.enter_email_pwd, 0).show();
    }

    public void resetPwd(View view) {
        if (Internet.isNotConnected(this) != null) {
            displayConnectionRequired();
            return;
        }
        setLoadingVisibility(true);
        ParseUser.requestPasswordResetInBackground(this.emailEditText.getText().toString(), new C08693());
    }

    public void signInAnonymously(View view) {
        if (Internet.isNotConnected(this) != null) {
            displayConnectionRequired();
            return;
        }
        setLoadingVisibility(true);
        ParseAnonymousUtils.logIn(new C08704());
    }

    private void openApp() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
