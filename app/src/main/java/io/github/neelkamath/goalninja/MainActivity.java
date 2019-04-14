package io.github.neelkamath.goalninja;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.internal.view.SupportMenu;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final String EXTRA_GOAL = "io.github.neelkamath.GOAL";
    SharedPreferences preferences;

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$1 */
    class C03981 implements OnClickListener {
        C03981() {
        }

        public void onClick(View view) {
            MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), CreatorActivity.class));
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$6 */
    class C04016 implements OnTimeSetListener {
        C04016() {
        }

        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            MainActivity.this.preferences.edit().putInt("notifyHour", i).putInt("notifyMinute", i2).apply();
            timePicker = new NotificationsSetter(MainActivity.this);
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$8 */
    class C04028 implements DialogInterface.OnClickListener {
        C04028() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            MainActivity.this.logout();
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$9 */
    class C04039 implements DialogInterface.OnClickListener {

        /* renamed from: io.github.neelkamath.goalninja.MainActivity$9$1 */
        class C08811 implements DeleteCallback {
            C08811() {
            }

            public void done(ParseException parseException) {
                MainActivity.this.setLoaderVisibility(false);
                if (parseException != null) {
                    Log.e("delete", parseException.getMessage());
                    Toast.makeText(MainActivity.this, parseException.getMessage(), 0).show();
                }
            }
        }

        C04039() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            MainActivity.this.setLoaderVisibility(1);
            ParseUser.getCurrentUser().deleteEventually(new C08811());
            MainActivity.this.goToAccountPage();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0406R.layout.activity_main);
        this.preferences = getSharedPreferences("settings", 0);
        setSupportActionBar((Toolbar) findViewById(C0406R.id.my_toolbar));
        findViewById(C0406R.id.floatingActionButton).setOnClickListener(new C03981());
        if (this.preferences.getBoolean("isShown", false) == null) {
            showInstructions();
            this.preferences.edit().putBoolean("isShown", true).apply();
        }
        RecyclerView recyclerView = findViewById(C0406R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Adapter goalsAdapter = new GoalsAdapter(this);
        recyclerView.setAdapter(goalsAdapter);
        new ItemTouchHelper(new Callback() {

            /* renamed from: io.github.neelkamath.goalninja.MainActivity$2$3 */
            class C08763 implements SaveCallback {
                C08763() {
                }

                public void done(ParseException parseException) {
                    if (parseException != null) {
                        Log.e("save", parseException.getMessage());
                        Toast.makeText(MainActivity.this, parseException.getMessage(), 0).show();
                    }
                }
            }

            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
                return false;
            }

            public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
                return Callback.makeMovementFlags(null, 12);
            }

            public void onSwiped(ViewHolder viewHolder, int i) {
                viewHolder = viewHolder.getAdapterPosition();
                final Goal goal = (Goal) goalsAdapter.goals.get(viewHolder);
                if (i == 4) {
                    new Builder(MainActivity.this).setMessage(C0406R.string.verify_goal_deletion).setPositiveButton(C0406R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.this.deleteGoal(goal, goalsAdapter, viewHolder);
                        }
                    }).setNegativeButton(C0406R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            goalsAdapter.notifyItemChanged(viewHolder);
                        }
                    }).setCancelable(0).show();
                    viewHolder = new NotificationsSetter(MainActivity.this);
                } else if (i == 8) {
                    goalsAdapter.goals.remove(goal);
                    goal.setCompleted(goal.isCompleted() ^ 1);
                    goal.saveEventually(new C08763());
                    goalsAdapter.goals.add(viewHolder, goal);
                    goalsAdapter.notifyItemChanged(viewHolder);
                    viewHolder = new NotificationsSetter(MainActivity.this);
                }
            }

            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
                float left;
                float f3;
                float width;
                Object obj = f > 0.0f ? 1 : null;
                Paint paint = new Paint();
                paint.setColor(obj != null ? -16711936 : SupportMenu.CATEGORY_MASK);
                if (obj != null) {
                    left = (float) viewHolder.itemView.getLeft();
                } else {
                    left = ((float) viewHolder.itemView.getRight()) + f;
                }
                float f4 = left;
                float top = (float) viewHolder.itemView.getTop();
                if (obj != null) {
                    f3 = f;
                } else {
                    f3 = (float) viewHolder.itemView.getRight();
                }
                canvas.drawRect(f4, top, f3, (float) viewHolder.itemView.getBottom(), paint);
                Bitmap decodeResource = BitmapFactory.decodeResource(MainActivity.this.getResources(), obj != null ? C0406R.mipmap.check_foreground : C0406R.mipmap.delete_foreground);
                int bottom = (viewHolder.itemView.getBottom() - viewHolder.itemView.getTop()) - decodeResource.getHeight();
                if (obj != null) {
                    width = f - ((float) decodeResource.getWidth());
                } else {
                    width = ((float) viewHolder.itemView.getRight()) + f;
                }
                canvas.drawBitmap(decodeResource, width, (float) (viewHolder.itemView.getTop() + (bottom / 2)), paint);
                super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
            }
        }).attachToRecyclerView(recyclerView);
    }

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$3 */
    class C08773 implements DeleteCallback {
        C08773() {
        }

        public void done(ParseException parseException) {
            if (parseException != null) {
                Log.e("delete", parseException.getMessage());
            }
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$4 */
    class C08784 implements DeleteCallback {
        C08784() {
        }

        public void done(ParseException parseException) {
            if (parseException != null) {
                Log.e("delete", parseException.getMessage());
                Toast.makeText(MainActivity.this, parseException.getMessage(), 0).show();
            }
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$5 */
    class C08795 implements LogOutCallback {
        C08795() {
        }

        public void done(ParseException parseException) {
            MainActivity.this.setLoaderVisibility(false);
            if (parseException == null) {
                MainActivity.this.goToAccountPage();
                return;
            }
            Log.e("logout", parseException.getMessage());
            Toast.makeText(MainActivity.this, parseException.getMessage(), 0).show();
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.MainActivity$7 */
    class C08807 implements RequestPasswordResetCallback {
        C08807() {
        }

        public void done(ParseException parseException) {
            MainActivity.this.setLoaderVisibility(false);
            if (parseException == null) {
                Toast.makeText(MainActivity.this, C0406R.string.pwd_reset_sent, 0).show();
                return;
            }
            Log.e("reset", parseException.getMessage());
            Toast.makeText(MainActivity.this, parseException.getMessage(), 0).show();
        }
    }

    private void showInstructions() {
        new Builder(this).setMessage(C0406R.string.app_instructions).setPositiveButton(C0406R.string.ok, null).show();
    }

    private void deleteGoal(Goal goal, GoalsAdapter goalsAdapter, int i) {
        goalsAdapter.goals.remove(goal);
        goalsAdapter.notifyItemRemoved(i);
        goal.unpinInBackground((DeleteCallback) new C08773());
        goal.deleteEventually(new C08784());
    }

    private void displayConnectionRequired() {
        Toast.makeText(this, C0406R.string.internet_needed, 0).show();
    }

    private void logout() {
        setLoaderVisibility(true);
        ParseUser.logOutInBackground(new C08795());
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case C0406R.id.deleteAccount:
                if (Internet.isNotConnected(this) != null) {
                    displayConnectionRequired();
                    return true;
                }
                new Builder(this).setMessage(C0406R.string.verify_account_deletion).setPositiveButton(C0406R.string.yes, new C04039()).setNegativeButton(C0406R.string.no, null).show();
                return true;
            case C0406R.id.instructions:
                showInstructions();
                return true;
            case C0406R.id.logout:
                if (Internet.isNotConnected(this) != null) {
                    displayConnectionRequired();
                    return true;
                }
                if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser()) != null) {
                    new Builder(this).setMessage(C0406R.string.anonymous_data_lost).setPositiveButton(C0406R.string.yes, new C04028()).setNegativeButton(C0406R.string.no, null).show();
                } else {
                    logout();
                }
                return true;
            case C0406R.id.notifyTime:
                int i;
                menuItem = Calendar.getInstance();
                OnTimeSetListener c04016 = new C04016();
                SharedPreferences sharedPreferences = this.preferences;
                String str = "notifyHour";
                if (DateFormat.is24HourFormat(this)) {
                    i = menuItem.get(11);
                } else {
                    i = menuItem.get(10);
                }
                new TimePickerDialog(this, c04016, sharedPreferences.getInt(str, i), this.preferences.getInt("notifyMinute", menuItem.get(12)), DateFormat.is24HourFormat(this)).show();
                return true;
            case C0406R.id.resetPwd:
                if (Internet.isNotConnected(this) != null) {
                    displayConnectionRequired();
                    return true;
                }
                setLoaderVisibility(true);
                ParseUser.requestPasswordResetInBackground(ParseUser.getCurrentUser().getEmail(), new C08807());
                return true;
            case C0406R.id.signup:
                if (Internet.isNotConnected(this) != null) {
                    displayConnectionRequired();
                    return true;
                }
                View inflate = View.inflate(this, C0406R.layout.anonymous_signup_layout, null);
                final EditText editText = inflate.findViewById(C0406R.id.emailEditText);
                final EditText editText2 = inflate.findViewById(C0406R.id.pwdEditText);
                new Builder(this).setView(inflate).setPositiveButton(C0406R.string.signup, new DialogInterface.OnClickListener() {

                    /* renamed from: io.github.neelkamath.goalninja.MainActivity$10$1 */
                    class C08751 implements SignUpCallback {
                        C08751() {
                        }

                        public void done(ParseException parseException) {
                            MainActivity.this.setLoaderVisibility(false);
                            if (parseException == null) {
                                Toast.makeText(MainActivity.this, C0406R.string.verify_email, 0).show();
                                MainActivity.this.logout();
                                return;
                            }
                            Log.w("signUp", parseException.getMessage());
                            Toast.makeText(MainActivity.this, parseException.getMessage(), 0).show();
                        }
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.setLoaderVisibility(1);
                        dialogInterface = ParseUser.getCurrentUser();
                        dialogInterface.setUsername(editText.getText().toString());
                        dialogInterface.setPassword(editText2.getText().toString());
                        dialogInterface.signUpInBackground(new C08751());
                    }
                }).setNegativeButton(C0406R.string.cancel, null).show();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0406R.menu.main_menu_layout, menu);
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            menu.removeItem(C0406R.id.resetPwd);
            menu.removeItem(C0406R.id.deleteAccount);
        } else {
            menu.removeItem(C0406R.id.signup);
        }
        return true;
    }

    private void setLoaderVisibility(boolean z) {
        findViewById(C0406R.id.progressBar).setVisibility(!z);
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private static class GoalsAdapter extends Adapter<ViewHolder> {
        private List<Goal> goals = new ArrayList();
        private WeakReference<MainActivity> reference;

        private void handleException(Exception exception) {
            Log.e("query", exception.getMessage());
            Toast.makeText(this.reference.get(), C0406R.string.goal_not_loaded, 0).show();
        }

        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            final Goal goal = this.goals.get(i);
            viewHolder.goalTextView.setText(goal.getGoal());
            viewHolder.dateTextView.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(goal.getDeadline()));
            if (goal.isCompleted()) {
                viewHolder.goalTextView.setTextColor(-3355444);
                viewHolder.dateTextView.setTextColor(-3355444);
            }
            viewHolder.linearLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    view = new Intent(GoalsAdapter.this.reference.get(), CreatorActivity.class);
                    view.putExtra(MainActivity.EXTRA_GOAL, goal.getGoal());
                    ((MainActivity) GoalsAdapter.this.reference.get()).startActivity(view);
                }
            });
        }

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            TextView dateTextView;
            TextView goalTextView;
            LinearLayout linearLayout;

            ViewHolder(View view) {
                super(view);
                this.goalTextView = view.findViewById(C0406R.id.goalTextView);
                this.dateTextView = view.findViewById(C0406R.id.dateTextView);
                this.linearLayout = view.findViewById(C0406R.id.linearLayout);
            }
        }

        GoalsAdapter(MainActivity mainActivity) {
            this.reference = new WeakReference(mainActivity);
            ParseQuery.getQuery(Goal.class).whereExists("goal").fromLocalDatastore().findInBackground(new C08821());
        }

        private void setGoals(List<Goal> list) {
            this.goals = list;
            orderGoals();
        }

        private void queryOnline() {
            ParseQuery.getQuery(Goal.class).whereExists("goal").findInBackground(new C08832());
        }

        /* renamed from: io.github.neelkamath.goalninja.MainActivity$GoalsAdapter$1 */
        class C08821 implements FindCallback<Goal> {
            C08821() {
            }

            public void done(List<Goal> list, ParseException parseException) {
                if (Internet.isNotConnected(GoalsAdapter.this.reference.get())) {
                    GoalsAdapter.this.reference.get().setLoaderVisibility(false);
                }
                if (parseException != null) {
                    GoalsAdapter.this.handleException(parseException);
                    return;
                }
                GoalsAdapter.this.setGoals(list);
                GoalsAdapter.this.queryOnline();
            }
        }

        private void orderGoals() {
            List arrayList = new ArrayList();
            for (Goal goal : this.goals) {
                if (!goal.isCompleted()) {
                    arrayList.add(goal);
                }
            }
            for (Goal goal2 : this.goals) {
                if (goal2.isCompleted()) {
                    arrayList.add(goal2);
                }
            }
            this.goals = arrayList;
            notifyDataSetChanged();
        }

        /* renamed from: io.github.neelkamath.goalninja.MainActivity$GoalsAdapter$2 */
        class C08832 implements FindCallback<Goal> {
            C08832() {
            }

            public void done(List<Goal> list, ParseException parseException) {
                GoalsAdapter.this.reference.get().setLoaderVisibility(false);
                if (parseException != null) {
                    GoalsAdapter.this.handleException(parseException);
                } else {
                    GoalsAdapter.this.setGoals(list);
                }
            }
        }

        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0406R.layout.goal_layout, viewGroup, false));
        }

        public int getItemCount() {
            return this.goals.size();
        }
    }

    private void goToAccountPage() {
        startActivity(new Intent(this, AccountActivity.class));
        finish();
    }
}
