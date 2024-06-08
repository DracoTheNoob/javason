package fr.javason;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Json {
    private static final String SPLITTER = "/";
    private File file;
    private final JSONObject json;

    public Json() {
        this.file = null;
        this.json = new JSONObject();
    }

    public Json(File file) {
        this.file = file;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            this.json = (JSONObject) new JSONParser().parse(reader);
        } catch(IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Json(JSONObject object) {
        this.file = null;
        this.json = object;
    }

    private <E> E get(Object obj, Class<E> dataType) {
        if(dataType == Integer.class)
            return dataType.cast(Integer.parseInt(obj.toString()));
        if(dataType == Long.class)
            return dataType.cast(Long.parseLong(obj.toString()));
        if(dataType == Float.class)
            return dataType.cast(Float.parseFloat(obj.toString()));
        if(dataType == Double.class)
            return dataType.cast(Double.parseDouble(obj.toString()));
        if(dataType == Boolean.class)
            return dataType.cast(Boolean.parseBoolean(obj.toString()));
        if(dataType == Byte.class)
            return dataType.cast(Byte.parseByte(obj.toString()));
        if(dataType == Short.class)
            return dataType.cast(Short.parseShort(obj.toString()));

        if(dataType.isInstance(obj))
            return dataType.cast(obj);

        throw new IllegalArgumentException("Wrong type : " + obj.toString() + ", " + dataType.getName());
    }

    public <E> E get(String key, Class<E> dataType, E ifNull) {
        if((key == null || key.isEmpty()) && dataType.isInstance(json))
            return dataType.cast(json);

        if(key == null)
            return null;

        key = key.replace(".", SPLITTER);

        if(key.startsWith(SPLITTER))
            key = key.substring(1);

        if(!key.contains(SPLITTER)) {
            Object value = json.get(key);

            if(value == null)
                return ifNull;

            return get(value, dataType);
        }

        try {
            String[] keys = key.split(SPLITTER);
            JSONObject value = (JSONObject) json.get(keys[0]);

            for(int i = 1; i < keys.length - 1; i++)
                value = (JSONObject) value.get(keys[i]);

            if(value == null)
                return ifNull;

            String last = keys[keys.length - 1];
            Object lastValue = value.get(last);

            return get(lastValue, dataType);
        } catch(NullPointerException ignored) {
            return ifNull;
        }
    }

    public <E> List<E> getList(String key, Class<E> dataType, List<E> ifNull) throws RuntimeException {
        key = key.replace(".", SPLITTER);

        try {
            if(!key.contains(SPLITTER)) {
                Object value = json.get(key);

                if(value == null)
                    return ifNull;

                JSONArray list = (JSONArray) value;

                for(int i = 0; i < list.size(); i++) {
                    if(!dataType.isInstance(list.get(i))) {
                        throw new RuntimeException("Element n° " + i + "(" + list.get(i) + ") datatype is invalid :" + dataType.getName() + " expected ");
                    }
                }

                return (List<E>) list;
            }

            String[] keys = key.split(SPLITTER);
            JSONObject value = (JSONObject) json.get(keys[0]);

            for(int i = 1; i < keys.length - 1; i++)
                value = (JSONObject) value.get(keys[i]);

            if(value == null)
                return ifNull;

            String last = keys[keys.length - 1];

            JSONArray list = (JSONArray) value.get(last);

            for(int i = 0; i < list.size(); i++) {
                if(!dataType.isInstance(list.get(i))) {
                    throw new RuntimeException("Element n°" + i + "(" + list.get(i) + ") datatype is invalid :" + dataType.getName() + " expected ");
                }
            }

            return (List<E>) list;
        } catch(NullPointerException ignored) {
            return ifNull;
        }
    }

    public Json getJson(String key, Json ifNull) {
        key = key.replace(".", SPLITTER);

        try {
            if(!key.contains(SPLITTER)) {
                Object value = json.get(key);

                if(value == null)
                    return ifNull;

                return new Json((JSONObject) value);
            }

            String[] keys = key.split(SPLITTER);
            JSONObject value = (JSONObject) json.get(keys[0]);

            for(int i = 1; i < keys.length - 1; i++)
                value = (JSONObject) value.get(keys[i]);

            if(value == null)
                return ifNull;

            String last = keys[keys.length - 1];
            return new Json((JSONObject) value.get(last));
        } catch(NullPointerException ignored) {
            return ifNull;
        }
    }

    public <E> E get(String key, Class<E> dataType) { return get(key, dataType, null); }
    public <E> List<E> getList(String key, Class<E> dataType) { return getList(key, dataType, null); }
    public <E> Json getJson(String key) { return getJson(key, null); }

    public String getString(String key) { return getString(key, null); }
    public int getInt(String key) { return getInt(key, 0); }
    public byte getByte(String key) { return getByte(key, (byte) 0); }
    public short getShort(String key) { return getShort(key, (short) 0); }
    public long getLong(String key) { return getLong(key, 0L); }
    public float getFloat(String key) { return getFloat(key, 0f); }
    public double getDouble(String key) { return getDouble(key, 0.0); }
    public boolean getBoolean(String key) { return getBoolean(key, false); }

    public String getString(String key, String ifNull) { return get(key, String.class, ifNull); }
    public int getInt(String key, int ifNull) { return (int) getLong(key, ifNull); }
    public byte getByte(String key, byte ifNull) { return (byte) getLong(key, ifNull); }
    public short getShort(String key, short ifNull) { return (short) getLong(key, ifNull); }
    public long getLong(String key, long ifNull) { return get(key, Long.class, ifNull); }
    public float getFloat(String key, float ifNull) { return (float) getDouble(key, ifNull); }
    public double getDouble(String key, double ifNull) { return get(key, Double.class, ifNull); }
    public boolean getBoolean(String key, boolean ifNull) { return get(key, Boolean.class, ifNull); }

    public void set(String key, Object value) {
        final String baseKey = key;
        key = key.replace(".", SPLITTER);
        String[] split = key.split(SPLITTER);
        StringBuilder currentKey = new StringBuilder(split.length > 1 ? split[0] : "");

        if(get(split[0], JSONObject.class) == null) {
            this.json.put(split[0], new JSONObject());
        }

        for(int i = 1; i < split.length-1; i++) {
            if(get(currentKey.toString().replace(SPLITTER, ".") + SPLITTER + split[i], JSONObject.class) == null) {
                get(currentKey.toString(), JSONObject.class).put(split[i], new JSONObject());
            }

            currentKey.append(SPLITTER).append(split[i]);
        }

        get(currentKey.toString(), JSONObject.class).put(split[split.length-1], value);
    }

    public <E> void set(String key, E[] array) {
        List<E> list = new ArrayList<>();

        for(E value : array) {
            list.add(value);
        }

        this.set(key, list);
    }

    public void set(String key, int[] array) {
        List<Object> list = new ArrayList<>();

        for(int i : array)
            list.add(i);

        this.set(key, list);
    }

    public void set(String key, char[] array) {
        List<Object> list = new ArrayList<>();

        for(int value : array)
            list.add(String.valueOf(value));

        this.set(key, list);
    }

    public void set(String key, boolean[] array) {
        List<Object> list = new ArrayList<>();

        for(boolean value : array)
            list.add(value);

        this.set(key, list);
    }

    public void set(String key, byte[] array) {
        List<Object> list = new ArrayList<>();

        for(byte value : array)
            list.add(value);

        this.set(key, list);
    }

    public void set(String key, float[] array) {
        List<Object> list = new ArrayList<>();

        for(float value : array)
            list.add(value);

        this.set(key, list);
    }

    public void set(String key, double[] array) {
        List<Object> list = new ArrayList<>();

        for(double value : array)
            list.add(value);

        this.set(key, list);
    }

    public void set(String key, long[] array) {
        List<Object> list = new ArrayList<>();

        for(long value : array)
            list.add(value);

        this.set(key, list);
    }

    public void set(String key, short[] array) {
        List<Object> list = new ArrayList<>();

        for(short value : array)
            list.add(value);

        this.set(key, list);
    }

    public boolean save(File file) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if(!file.exists() && !file.createNewFile())
                return false;

            writer.write(this.toString());
            writer.flush();

            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(String path) {
        return save(new File(path));
    }

    public boolean save() {
        if(this.file == null)
            throw new RuntimeException("Unable to save json file without a specified file or path");

        return save(file);
    }

    @Override
    public String toString() {
        return json.toJSONString();
    }

    public JSONObject toJsonObject() {
        try {
            return (JSONObject) new JSONParser().parse(toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFile(File file) { this.file = file; }
}