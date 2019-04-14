package io.github.neelkamath.goalninja;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Goal")
class Goal extends ParseObject {

    class CreatedDateNotSetException extends Exception {
        CreatedDateNotSetException(String str) {
            super(str);
        }
    }

    List<MiniDeadline> getMiniDeadlines() {
        try {
            return new MiniDeadlinesGetter(this).execute(new Void[0]).get();
        } catch (Exception e) {
            Log.e("retrieve", e.getMessage());
            return null;
        }
    }

    private class ParseFileDeserializer implements JsonDeserializer<ParseFile> {
        private ParseFileDeserializer() {
        }

        public ParseFile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            jsonElement = jsonElement.getAsJsonObject().get("url");
            if (jsonElement == null) {
                return null;
            }
            try {
                jsonDeserializationContext = new ByteArrayOutputStream();
                BitmapFactory.decodeStream(new URL(jsonElement.getAsString()).openStream()).compress(CompressFormat.PNG, 100, jsonDeserializationContext);
                jsonElement = new ParseFile("image.png", jsonDeserializationContext.toByteArray(), "File");
                jsonDeserializationContext.close();
                try {
                    jsonElement.save();
                } catch (JsonDeserializationContext jsonDeserializationContext2) {
                    Log.e("save", jsonDeserializationContext2.getMessage());
                }
                return jsonElement;
            } catch (JsonElement jsonElement2) {
                Log.e("retrieve", jsonElement2.getMessage());
                return null;
            }
        }
    }

    private class ParseFileSerializer implements JsonSerializer<ParseFile> {
        private ParseFileSerializer() {
        }

        public JsonElement serialize(ParseFile parseFile, Type type, JsonSerializationContext jsonSerializationContext) {
            type = new JsonObject();
            type.addProperty("__type", "File");
            type.addProperty("name", parseFile.getName());
            type.addProperty("url", parseFile.getUrl());
            return type;
        }
    }

    String getGoal() {
        return getString("goal");
    }

    void setGoal(String str) {
        put("goal", str);
    }

    Date getDeadline() {
        return getDate("deadline");
    }

    void setDeadline(Date date) {
        put("deadline", date);
    }

    private void setItems(boolean z, List<String> list) {
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jSONArray.put(list.get(i));
        }
        put(z ? "reasons" : "ideas", jSONArray);
    }

    private List<String> getItems(boolean z) {
        z = getJSONArray(z ? "reasons" : "ideas");
        List<String> arrayList = new ArrayList();
        for (int i = 0; i < z.length(); i++) {
            try {
                arrayList.add(z.getString(i));
            } catch (JSONException e) {
                Log.e("json", e.getMessage());
            }
        }
        return arrayList;
    }

    List<String> getReasons() {
        return getItems(true);
    }

    void setReasons(List<String> list) {
        setItems(true, list);
    }

    List<String> getIdeas() {
        return getItems(false);
    }

    void setIdeas(List<String> list) {
        setItems(false, list);
    }

    private static class MiniDeadlinesGetter extends AsyncTask<Void, Void, List<MiniDeadline>> {
        WeakReference<Goal> reference;

        MiniDeadlinesGetter(Goal goal) {
            this.reference = new WeakReference(goal);
        }

        protected List<MiniDeadline> doInBackground(Void... voidArr) {
            voidArr = ((Goal) this.reference.get()).getJSONArray("miniDeadlines");
            List<MiniDeadline> arrayList = new ArrayList();
            Goal goal = this.reference.get();
            goal.getClass();
            Gson create = new GsonBuilder().registerTypeAdapter(ParseFile.class, new ParseFileDeserializer()).create();
            for (int i = 0; i < voidArr.length(); i++) {
                try {
                    arrayList.add(create.fromJson(voidArr.get(i).toString(), MiniDeadline.class));
                } catch (JSONException e) {
                    Log.e("json", e.getMessage());
                }
            }
            return arrayList;
        }
    }

    void setMiniDeadlines(List<MiniDeadline> list) {
        JSONArray jSONArray = new JSONArray();
        Gson create = new GsonBuilder().registerTypeAdapter(ParseFile.class, new ParseFileSerializer()).create();
        for (int i = 0; i < list.size(); i++) {
            jSONArray.put(create.toJson(list.get(i)));
        }
        put("miniDeadlines", jSONArray);
    }

    String getNote() {
        return getString("note");
    }

    void setNote(String str) {
        put("note", str);
    }

    @Nullable
    List<ProgressEntry> getProgress() throws CreatedDateNotSetException {
        JSONArray jSONArray = getJSONArray(NotificationCompat.CATEGORY_PROGRESS);
        if (jSONArray == null) {
            return null;
        }
        List arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                arrayList.add(new Gson().fromJson(jSONArray.get(i).toString(), ProgressEntry.class));
            } catch (JSONException e) {
                Log.e("json", e.getMessage());
            }
        }
        return updateProgressEntries(arrayList);
    }

    void setProgress(@Nullable List<ProgressEntry> list) throws CreatedDateNotSetException {
        if (list == null) {
            remove(NotificationCompat.CATEGORY_PROGRESS);
            return;
        }
        list = updateProgressEntries(list);
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jSONArray.put(new Gson().toJson(list.get(i)));
        }
        put(NotificationCompat.CATEGORY_PROGRESS, jSONArray);
    }

    boolean isCompleted() {
        return getBoolean("isCompleted");
    }

    void setCompleted(boolean z) {
        put("isCompleted", Boolean.valueOf(z));
    }

    Date getCreatedDate() {
        return getDate("created");
    }

    void setCreatedDate(Date date) {
        put("created", date);
    }

    private List<ProgressEntry> updateProgressEntries(List<ProgressEntry> list) throws CreatedDateNotSetException {
        Object createdDate = getCreatedDate();
        if (createdDate == null) {
            throw new CreatedDateNotSetException("The goal's date hasn't been set.");
        }
        ReadablePartial localDate = new LocalDate();
        ReadablePartial localDate2 = new LocalDate(getDeadline());
        if (localDate.isAfter(localDate2)) {
            localDate = localDate2;
        }
        long days = (long) (Days.daysBetween(new LocalDate(createdDate), localDate).getDays() + 1);
        if (((long) list.size()) < days) {
            for (int size = list.size(); ((long) size) < days; size++) {
                list.add(new ProgressEntry(new Date(createdDate.getTime() + ((long) ((((size * 24) * 60) * 60) * 1000))), false, ""));
            }
        } else if (((long) list.size()) > days) {
            for (int size2 = list.size() - 1; ((long) size2) > days; size2--) {
                list.remove(size2);
            }
        }
        return list;
    }
}
