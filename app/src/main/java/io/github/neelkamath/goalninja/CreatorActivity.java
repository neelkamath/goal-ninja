package io.github.neelkamath.goalninja;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreatorActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private DeadlineFragment deadlineFragment;
    private Date deadlineSelected;
    private DeadlinesAdapter deadlinesAdapter;
    private DeadlinesFragment deadlinesFragment;
    private Goal goal;
    private ItemsAdapter ideasAdapter;
    private ItemFragment ideasFragment;
    private Button miniDeadlineButton;
    private ImageView miniDeadlineImageView;
    private Date miniDeadlineSelected;
    private EditText noteEditText;
    private ProgressAdapter progressAdapter;
    private ProgressFragment progressFragment;
    private Switch progressSwitch;
    private ItemsAdapter reasonsAdapter;
    private ItemFragment reasonsFragment;
    private EditText whatEditText;

    /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$2 */
    class C03862 implements OnDateChangeListener {
        C03862() {
        }

        public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i2, int i3) {
            calendarView = Calendar.getInstance();
            calendarView.set(1, i);
            calendarView.set(2, i2);
            calendarView.set(5, i3);
            CreatorActivity.this.deadlineSelected = new Date(calendarView.getTimeInMillis());
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$3 */
    class C03893 implements OnCheckedChangeListener {

        /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$3$1 */
        class C03871 implements OnClickListener {
            C03871() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                CreatorActivity.this.progressSwitch.setChecked(1);
            }
        }

        /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$3$2 */
        class C03882 implements OnClickListener {
            C03882() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                CreatorActivity.this.progressAdapter.entries.clear();
                CreatorActivity.this.progressAdapter.notifyDataSetChanged();
            }
        }

        C03893() {
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (CreatorActivity.this.goal != null) {
                if (z && CreatorActivity.this.progressAdapter.entries.size() == null) {
                    try {
                        CreatorActivity.this.goal.setProgress(new ArrayList());
                        CreatorActivity.this.progressAdapter.entries = CreatorActivity.this.goal.getProgress();
                    } catch (CompoundButton compoundButton2) {
                        Log.e("retrieve", compoundButton2.getMessage());
                    }
                    CreatorActivity.this.progressAdapter.notifyDataSetChanged();
                } else if (!z && CreatorActivity.this.progressAdapter.entries.size() > null) {
                    new Builder(CreatorActivity.this).setMessage(true).setPositiveButton(true, new C03882()).setNegativeButton(true, new C03871()).setCancelable(false).show();
                }
            }
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$8 */
    class C03908 implements OnDateChangeListener {
        C03908() {
        }

        public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i2, int i3) {
            calendarView = Calendar.getInstance();
            calendarView.set(1, i);
            calendarView.set(2, i2);
            calendarView.set(5, i3);
            CreatorActivity.this.miniDeadlineSelected = new Date(calendarView.getTimeInMillis());
        }
    }

    public static class DeadlineFragment extends Fragment {
        private Date date;

        public void onCreate(@Nullable Bundle bundle) {
            super.onCreate(bundle);
            setRetainInstance(true);
        }

        Date getDate() {
            return this.date;
        }

        void setDate(Date date) {
            this.date = date;
        }
    }

    public static class DeadlinesFragment extends Fragment {
        private List<MiniDeadline> deadlines;

        public void onCreate(@Nullable Bundle bundle) {
            super.onCreate(bundle);
            setRetainInstance(true);
        }

        public List<MiniDeadline> getDeadlines() {
            return this.deadlines;
        }

        public void setDeadlines(List<MiniDeadline> list) {
            this.deadlines = list;
        }
    }

    public static class ItemFragment extends Fragment {
        private List<String> items;

        public void onCreate(@Nullable Bundle bundle) {
            super.onCreate(bundle);
            setRetainInstance(true);
        }

        public List<String> getItems() {
            return this.items;
        }

        public void setItems(List<String> list) {
            this.items = list;
        }
    }

    public static class ProgressFragment extends Fragment {
        private List<ProgressEntry> entries;

        public List<ProgressEntry> getEntries() {
            return this.entries;
        }

        public void setEntries(List<ProgressEntry> list) {
            this.entries = list;
        }

        public void onCreate(@Nullable Bundle bundle) {
            super.onCreate(bundle);
            setRetainInstance(true);
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$7 */
    class C08057 implements GetDrawableCallback {
        C08057() {
        }

        public void done(@Nullable Drawable drawable, @Nullable ParseException parseException) {
            if (parseException == null) {
                CreatorActivity.this.miniDeadlineImageView.setImageDrawable(drawable);
            } else {
                Log.e("retrieve", parseException.getMessage());
            }
        }
    }

    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0406R.layout.activity_creator);
        getWindow().setSoftInputMode(2);
        this.whatEditText = findViewById(C0406R.id.whatEditText);
        this.noteEditText = findViewById(C0406R.id.noteEditText);
        this.reasonsAdapter = new ItemsAdapter();
        this.ideasAdapter = new ItemsAdapter();
        this.deadlinesAdapter = new DeadlinesAdapter(this);
        this.progressAdapter = new ProgressAdapter(new ArrayList());
        this.progressSwitch = findViewById(C0406R.id.progressSwitch);
        final CalendarView calendarView = findViewById(C0406R.id.calendarView);
        FragmentManager fragmentManager = getFragmentManager();
        String str = "deadline";
        this.deadlineFragment = (DeadlineFragment) fragmentManager.findFragmentByTag(str);
        if (this.deadlineFragment == null) {
            this.deadlineFragment = new DeadlineFragment();
            fragmentManager.beginTransaction().add(this.deadlineFragment, str).commit();
            this.deadlineFragment.setDate(new Date(calendarView.getDate()));
        }
        str = "reasons";
        this.reasonsFragment = (ItemFragment) fragmentManager.findFragmentByTag(str);
        if (this.reasonsFragment == null) {
            this.reasonsFragment = new ItemFragment();
            fragmentManager.beginTransaction().add(this.reasonsFragment, str).commit();
            this.reasonsFragment.setItems(this.reasonsAdapter.list);
        }
        str = "ideas";
        this.ideasFragment = (ItemFragment) fragmentManager.findFragmentByTag(str);
        if (this.ideasFragment == null) {
            this.ideasFragment = new ItemFragment();
            fragmentManager.beginTransaction().add(this.ideasFragment, str).commit();
            this.ideasFragment.setItems(this.ideasAdapter.list);
        }
        str = "deadlines";
        this.deadlinesFragment = (DeadlinesFragment) fragmentManager.findFragmentByTag(str);
        if (this.deadlinesFragment == null) {
            this.deadlinesFragment = new DeadlinesFragment();
            fragmentManager.beginTransaction().add(this.deadlinesFragment, str).commit();
            this.deadlinesFragment.setDeadlines(this.deadlinesAdapter.deadlines);
        }
        str = NotificationCompat.CATEGORY_PROGRESS;
        this.progressFragment = (ProgressFragment) fragmentManager.findFragmentByTag(str);
        if (this.progressFragment == null) {
            this.progressFragment = new ProgressFragment();
            fragmentManager.beginTransaction().add(this.progressFragment, str).commit();
            this.progressFragment.setEntries(this.progressAdapter.entries);
        }
        this.deadlineSelected = this.deadlineFragment.getDate();
        calendarView.setDate(this.deadlineSelected.getTime());
        this.reasonsAdapter.list = this.reasonsFragment.getItems();
        this.reasonsAdapter.notifyDataSetChanged();
        this.ideasAdapter.list = this.ideasFragment.getItems();
        this.ideasAdapter.notifyDataSetChanged();
        this.deadlinesAdapter.deadlines = this.deadlinesFragment.getDeadlines();
        this.deadlinesAdapter.notifyDataSetChanged();
        this.progressAdapter.entries = this.progressFragment.getEntries();
        this.progressAdapter.notifyDataSetChanged();
        RecyclerView recyclerView = findViewById(C0406R.id.reasonsRecyclerView);
        recyclerView.setAdapter(this.reasonsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView = findViewById(C0406R.id.ideasRecyclerView);
        recyclerView.setAdapter(this.ideasAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView = findViewById(C0406R.id.deadlinesRecyclerView);
        recyclerView.setAdapter(this.deadlinesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView = findViewById(C0406R.id.progressRecyclerView);
        recyclerView.setAdapter(this.progressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        String stringExtra = getIntent().getStringExtra("io.github.neelkamath.GOAL");
        ((Button) findViewById(C0406R.id.doneButton)).setText(stringExtra == null ? C0406R.string.create_goal : C0406R.string.update_goal);
        if (stringExtra != null) {
            ParseQuery.getQuery(Goal.class).fromLocalDatastore().whereEqualTo("goal", stringExtra).getFirstInBackground(new GetCallback<Goal>() {
                public void done(Goal goal, ParseException parseException) {
                    if (parseException == null) {
                        CreatorActivity.this.goal = goal;
                        CreatorActivity.this.whatEditText.setText(CreatorActivity.this.goal.getGoal());
                        CreatorActivity.this.noteEditText.setText(CreatorActivity.this.goal.getNote());
                        calendarView.setMinDate(CreatorActivity.this.goal.getCreatedDate().getTime());
                        if (bundle == null) {
                            CreatorActivity.this.deadlineSelected = CreatorActivity.this.goal.getDeadline();
                            calendarView.setDate(CreatorActivity.this.deadlineSelected.getTime());
                            CreatorActivity.this.reasonsAdapter.list = CreatorActivity.this.goal.getReasons();
                            CreatorActivity.this.reasonsAdapter.notifyDataSetChanged();
                            CreatorActivity.this.ideasAdapter.list = CreatorActivity.this.goal.getIdeas();
                            CreatorActivity.this.ideasAdapter.notifyDataSetChanged();
                            CreatorActivity.this.deadlinesAdapter.deadlines = CreatorActivity.this.goal.getMiniDeadlines();
                            CreatorActivity.this.deadlinesAdapter.notifyDataSetChanged();
                            goal = null;
                            try {
                                goal = CreatorActivity.this.goal.getProgress();
                            } catch (ParseException parseException2) {
                                Log.e("retrieve", parseException2.getMessage());
                            }
                            parseException2 = goal != null ? true : null;
                            CreatorActivity.this.progressSwitch.setChecked(parseException2);
                            if (parseException2 != null) {
                                CreatorActivity.this.progressAdapter.entries = goal;
                                CreatorActivity.this.progressAdapter.notifyDataSetChanged();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    Log.e("retrieve", parseException2.getMessage());
                }
            });
        } else {
            calendarView.setMinDate(new Date().getTime());
        }
        calendarView.setOnDateChangeListener(new C03862());
        this.progressSwitch.setOnCheckedChangeListener(new C03893());
    }

    private void addItem(boolean z) {
        TextView textView = findViewById(z ? C0406R.id.reasonEditText : C0406R.id.ideaEditText);
        String trim = textView.getText().toString().trim();
        if (trim.isEmpty()) {
            Toast.makeText(this, true, 0).show();
            return;
        }
        if ((z ? this.reasonsAdapter : this.ideasAdapter).list.contains(trim)) {
            Toast.makeText(this, true, 0).show();
            return;
        }
        (z ? this.reasonsAdapter : this.ideasAdapter).list.add(trim);
        (z ? this.reasonsAdapter : this.ideasAdapter).notifyItemInserted((z ? this.reasonsAdapter : this.ideasAdapter).list.size() - 1);
        textView.setText("");
    }

    private void editMiniDeadline(@Nullable final MiniDeadline miniDeadline) {
        View inflate = View.inflate(this, C0406R.layout.edit_mini_deadline, null);
        final EditText editText = inflate.findViewById(C0406R.id.deadlineEditText);
        final EditText editText2 = inflate.findViewById(C0406R.id.noteEditText);
        this.miniDeadlineButton = inflate.findViewById(C0406R.id.button);
        this.miniDeadlineImageView = inflate.findViewById(C0406R.id.imageView);
        CalendarView calendarView = inflate.findViewById(C0406R.id.calendarView);
        if (this.goal == null) {
            calendarView.setMinDate(new Date().getTime());
        } else {
            calendarView.setMinDate(this.goal.getCreatedDate().getTime());
            calendarView.setMaxDate(this.deadlineSelected.getTime());
        }
        this.miniDeadlineSelected = new Date(calendarView.getDate());
        if (this.goal != null) {
            editText2.setVisibility(0);
            this.miniDeadlineButton.setVisibility(0);
        }
        if (miniDeadline != null) {
            editText.setText(miniDeadline.getText());
            this.miniDeadlineSelected = miniDeadline.getDate();
            calendarView.setDate(this.miniDeadlineSelected.getTime());
            if (this.goal != null) {
                editText2.setText(miniDeadline.getNote());
                miniDeadline.getImage(new C08057(), this);
            }
        }
        if (miniDeadline != null) {
            switch (miniDeadline.getImageStatus(this)) {
                case NOT_RETRIEVABLE:
                    this.miniDeadlineButton.setText(C0406R.string.img_not_uploaded);
                    this.miniDeadlineButton.setEnabled(false);
                    break;
                case NOT_SET:
                    this.miniDeadlineButton.setText(C0406R.string.add_img);
                    break;
                case RETRIEVABLE:
                    this.miniDeadlineButton.setText(C0406R.string.remove_img);
                    break;
                default:
                    break;
            }
        }
        this.miniDeadlineButton.setText(C0406R.string.add_img);
        calendarView.setOnDateChangeListener(new C03908());
        final AlertDialog create = new Builder(this).setView(inflate).setPositiveButton(C0406R.string.save, null).setNegativeButton(miniDeadline == null ? C0406R.string.cancel : C0406R.string.delete, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (miniDeadline != null) {
                    CreatorActivity.this.deadlinesAdapter.deadlines.remove(miniDeadline);
                    CreatorActivity.this.deadlinesAdapter.notifyItemRemoved(CreatorActivity.this.deadlinesAdapter.deadlines.indexOf(miniDeadline));
                }
            }
        }).create();
        create.show();
        final MiniDeadline miniDeadline2 = miniDeadline;
        create.getButton(-1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view = editText.getText().toString().trim();
                if (view.isEmpty()) {
                    Toast.makeText(CreatorActivity.this, C0406R.string.no_milestone, 0).show();
                    return;
                }
                MiniDeadline miniDeadline = miniDeadline2 == null ? new MiniDeadline() : miniDeadline2;
                miniDeadline.setText(view);
                miniDeadline.setDate(CreatorActivity.this.miniDeadlineSelected);
                miniDeadline.setNote(editText2.getText().toString());
                miniDeadline.setImage(CreatorActivity.this.miniDeadlineImageView.getDrawable());
                if (miniDeadline2 == null) {
                    miniDeadline.setCompleted(false);
                    CreatorActivity.this.deadlinesAdapter.deadlines.add(miniDeadline);
                    CreatorActivity.this.deadlinesAdapter.notifyItemInserted(CreatorActivity.this.deadlinesAdapter.deadlines.size() - 1);
                } else {
                    view = CreatorActivity.this.deadlinesAdapter.deadlines.indexOf(miniDeadline2);
                    CreatorActivity.this.deadlinesAdapter.deadlines.remove(miniDeadline2);
                    CreatorActivity.this.deadlinesAdapter.deadlines.add(view, miniDeadline);
                    CreatorActivity.this.deadlinesAdapter.notifyItemChanged(view);
                }
                create.dismiss();
            }
        });
    }

    /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$5 */
    class C08735 implements SaveCallback {
        C08735() {
        }

        public void done(ParseException parseException) {
            if (parseException != null) {
                Log.e("save", parseException.getMessage());
            } else {
                parseException = new NotificationsSetter(CreatorActivity.this);
            }
        }
    }

    /* renamed from: io.github.neelkamath.goalninja.CreatorActivity$6 */
    class C08746 implements SaveCallback {
        C08746() {
        }

        public void done(ParseException parseException) {
            if (parseException != null) {
                Log.e("save", parseException.getMessage());
                Toast.makeText(CreatorActivity.this, parseException.getMessage(), 0).show();
            }
        }
    }

    private static class DeadlinesAdapter extends Adapter<ViewHolder> {
        private List<MiniDeadline> deadlines = new ArrayList();
        private WeakReference<CreatorActivity> reference;

        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            final MiniDeadline miniDeadline = this.deadlines.get(i);
            viewHolder.text.setText(miniDeadline.getText());
            viewHolder.date.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(miniDeadline.getDate()));
            if (this.reference.get().goal == null) {
                viewHolder.checkBox.setVisibility(8);
            } else {
                viewHolder.checkBox.setChecked(miniDeadline.isCompleted());
            }
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    DeadlinesAdapter.this.reference.get().editMiniDeadline(miniDeadline);
                }
            });
            viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    miniDeadline.setCompleted(z);
                }
            });
        }

        DeadlinesAdapter(CreatorActivity creatorActivity) {
            this.reference = new WeakReference(creatorActivity);
        }

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            CheckBox checkBox;
            TextView date;
            LinearLayout layout;
            TextView text;

            ViewHolder(View view) {
                super(view);
                this.layout = view.findViewById(C0406R.id.linearLayout);
                this.text = view.findViewById(C0406R.id.miniTextView);
                this.date = view.findViewById(C0406R.id.dateTextView);
                this.checkBox = view.findViewById(C0406R.id.checkBox);
            }
        }

        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0406R.layout.mini_deadlines_layout, viewGroup, false));
        }

        public int getItemCount() {
            return this.deadlines.size();
        }
    }

    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            getFragmentManager().beginTransaction().remove(this.deadlineFragment).remove(this.reasonsFragment).remove(this.ideasFragment).remove(this.deadlinesFragment).remove(this.progressFragment).commit();
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.deadlineFragment.setDate(this.deadlineSelected);
        this.reasonsFragment.setItems(this.reasonsAdapter.list);
        this.ideasFragment.setItems(this.ideasAdapter.list);
        this.deadlinesFragment.setDeadlines(this.deadlinesAdapter.deadlines);
        this.progressFragment.setEntries(this.progressAdapter.entries);
    }

    public void onBackPressed() {
        returnHome();
    }

    public void addReason(View view) {
        addItem(true);
    }

    public void addIdea(View view) {
        addItem(null);
    }

    private static class ItemsAdapter extends Adapter<ViewHolder> {
        private List<String> list = new ArrayList();

        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            viewHolder.textView.setText(this.list.get(i));
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ItemsAdapter.this.list.remove(viewHolder.getAdapterPosition());
                    ItemsAdapter.this.notifyItemRemoved(viewHolder.getAdapterPosition());
                }
            });
        }

        ItemsAdapter() {
        }

        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0406R.layout.items_layout, viewGroup, false));
        }

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            Button button;
            TextView textView;

            ViewHolder(View view) {
                super(view);
                this.textView = view.findViewById(C0406R.id.textView);
                this.button = view.findViewById(C0406R.id.button);
            }
        }

        public int getItemCount() {
            return this.list.size();
        }
    }

    public void verifyGoal(View view) {
        view = this.whatEditText.getText().toString().trim();
        if (view.isEmpty()) {
            Toast.makeText(this, C0406R.string.no_goal, 0).show();
        } else {
            ParseQuery.getQuery(Goal.class).whereEqualTo("goal", view).fromLocalDatastore().findInBackground(new FindCallback<Goal>() {
                public void done(List<Goal> list, ParseException parseException) {
                    if (parseException != null) {
                        Log.e("query", parseException.getMessage());
                        Toast.makeText(CreatorActivity.this, C0406R.string.goal_could_not_save, 0).show();
                    } else if ((CreatorActivity.this.goal == null || CreatorActivity.this.goal.getGoal().equals(view) == null) && list.size() != null) {
                        Toast.makeText(CreatorActivity.this, C0406R.string.goal_already_exists, 0).show();
                    } else {
                        CreatorActivity.this.saveGoal(CreatorActivity.this.goal, view);
                    }
                }
            });
        }
    }

    private void saveGoal(@Nullable Goal goal, String str) {
        boolean z = false;
        int i = goal == null ? 1 : false;
        if (i != 0) {
            goal = new Goal();
        }
        goal.setGoal(str);
        str = goal.getCreatedDate();
        if (str == null) {
            str = new Date();
        }
        goal.setCreatedDate(str);
        goal.setDeadline(this.deadlineSelected);
        if (this.reasonsAdapter.list.size() == null) {
            Toast.makeText(this, C0406R.string.no_reasons, 0).show();
            return;
        }
        goal.setReasons(this.reasonsAdapter.list);
        if (this.ideasAdapter.list.size() < 5) {
            Toast.makeText(this, C0406R.string.no_ideas, 0).show();
            return;
        }
        goal.setIdeas(this.ideasAdapter.list);
        for (MiniDeadline imageStatus : this.deadlinesAdapter.deadlines) {
            if (imageStatus.getImageStatus(this) == ImageStatus.NOT_RETRIEVABLE) {
                if (Internet.isNotConnected(this)) {
                    Toast.makeText(this, C0406R.string.images_cannot_save, 0).show();
                } else {
                    Toast.makeText(this, C0406R.string.wait_for_img_upload, 0).show();
                    return;
                }
            }
        }
        goal.setMiniDeadlines(this.deadlinesAdapter.deadlines);
        goal.setNote(this.noteEditText.getText().toString().trim());
        try {
            goal.setProgress(this.progressSwitch.isChecked() != null ? this.progressAdapter.entries : null);
        } catch (String str2) {
            Log.e("retrieve", str2.getMessage());
        }
        if (i == 0 && goal.isCompleted() != null) {
            z = true;
        }
        goal.setCompleted(z);
        goal.pinInBackground((SaveCallback) new C08735());
        goal.saveEventually(new C08746());
        if (i != 0) {
            Toast.makeText(this, C0406R.string.reasons_reason, 1).show();
        }
        returnHome();
    }

    private void returnHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void modifyImg(View view) {
        if (this.miniDeadlineImageView.getDrawable() == null) {
            view = new Intent("android.intent.action.GET_CONTENT").setType("image/*");
            if (view.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(Intent.createChooser(view, ""), 1);
                return;
            } else {
                Toast.makeText(this, C0406R.string.no_file_access, 0).show();
                return;
            }
        }
        this.miniDeadlineButton.setText(C0406R.string.add_img);
        this.miniDeadlineImageView.setImageDrawable(null);
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        if (this.miniDeadlineImageView == null) {
            new Builder(this).setMessage(C0406R.string.deadline_config_change).setPositiveButton(C0406R.string.ok, null).show();
            return;
        }
        if (i == 1 && i2 == -1 && intent != null) {
            this.miniDeadlineImageView.setImageURI(intent.getData());
            this.miniDeadlineButton.setText(C0406R.string.remove_img);
        }
    }

    public void addMiniDeadline(View view) {
        editMiniDeadline(null);
    }

    private static class ProgressAdapter extends Adapter<ViewHolder> {
        private List<ProgressEntry> entries;

        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            final ProgressEntry progressEntry = this.entries.get(i);
            viewHolder.checkBox.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(progressEntry.getDate()));
            viewHolder.reasonEditText.setText(progressEntry.getReason());
            viewHolder.reasonEditText.setVisibility(progressEntry.isCompleted() ? 4 : 0);
            viewHolder.checkBox.setChecked(progressEntry.isCompleted());
            viewHolder.reasonEditText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    progressEntry.setReason(charSequence.toString());
                }
            });
            viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    progressEntry.setCompleted(z);
                    if (z) {
                        progressEntry.setReason(null);
                        viewHolder.reasonEditText.setText(null);
                    }
                    viewHolder.reasonEditText.setVisibility(z);
                }
            });
        }

        ProgressAdapter(List<ProgressEntry> list) {
            this.entries = list;
        }

        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0406R.layout.progress_layout, viewGroup, false));
        }

        public int getItemCount() {
            return this.entries.size();
        }

        class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
            CheckBox checkBox;
            EditText reasonEditText;

            ViewHolder(View view) {
                super(view);
                this.reasonEditText = view.findViewById(C0406R.id.editText);
                this.checkBox = view.findViewById(C0406R.id.checkBox);
            }
        }
    }
}
