package com.google.firebase.database.core.utilities;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.core.Path;
import com.google.firebase.database.core.ServerValues;
import com.google.firebase.database.core.ValidationPath;
import com.google.firebase.database.snapshot.ChildKey;
import com.google.firebase.database.snapshot.Node;
import com.google.firebase.database.snapshot.NodeUtilities;
import com.google.firebase.database.snapshot.PriorityUtilities;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/* compiled from: com.google.firebase:firebase-database@@19.3.0 */
/* loaded from: classes.dex */
public class Validation {
    private static final Pattern INVALID_PATH_REGEX = Pattern.compile("[\\[\\]\\.#$]");
    private static final Pattern INVALID_KEY_REGEX = Pattern.compile("[\\[\\]\\.#\\$\\/\\u0000-\\u001F\\u007F]");

    private static boolean isValidPathString(String pathString) {
        return !INVALID_PATH_REGEX.matcher(pathString).find();
    }

    public static void validatePathString(String pathString) throws DatabaseException {
        if (!isValidPathString(pathString)) {
            throw new DatabaseException("Invalid Firebase Database path: " + pathString + ". Firebase Database paths must not contain '.', '#', '$', '[', or ']'");
        }
    }

    public static void validateRootPathString(String pathString) throws DatabaseException {
        if (pathString.startsWith(".info")) {
            validatePathString(pathString.substring(5));
        } else if (pathString.startsWith("/.info")) {
            validatePathString(pathString.substring(6));
        } else {
            validatePathString(pathString);
        }
    }

    private static boolean isWritableKey(String key) {
        return key != null && key.length() > 0 && (key.equals(".value") || key.equals(".priority") || (!key.startsWith(".") && !INVALID_KEY_REGEX.matcher(key).find()));
    }

    private static boolean isValidKey(String key) {
        return key.equals(".info") || !INVALID_KEY_REGEX.matcher(key).find();
    }

    public static void validateNullableKey(String key) throws DatabaseException {
        if (key != null && !isValidKey(key)) {
            throw new DatabaseException("Invalid key: " + key + ". Keys must not contain '/', '.', '#', '$', '[', or ']'");
        }
    }

    private static void validateDoubleValue(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            throw new DatabaseException("Invalid value: Value cannot be NaN, Inf or -Inf.");
        }
    }

    private static boolean isWritablePath(Path path) {
        ChildKey front = path.getFront();
        return front == null || !front.asString().startsWith(".");
    }

    public static void validateWritableObject(Object object) {
        if (object instanceof Map) {
            Map<String, Object> map = (Map) object;
            if (!map.containsKey(ServerValues.NAME_SUBKEY_SERVERVALUE)) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    validateWritableKey(entry.getKey());
                    validateWritableObject(entry.getValue());
                }
            }
        } else if (object instanceof List) {
            List<Object> list = (List) object;
            for (Object child : list) {
                validateWritableObject(child);
            }
        } else if ((object instanceof Double) || (object instanceof Float)) {
            validateDoubleValue(((Double) object).doubleValue());
        }
    }

    public static void validateWritableKey(String key) throws DatabaseException {
        if (!isWritableKey(key)) {
            throw new DatabaseException("Invalid key: " + key + ". Keys must not contain '/', '.', '#', '$', '[', or ']'");
        }
    }

    public static void validateWritablePath(Path path) throws DatabaseException {
        if (!isWritablePath(path)) {
            throw new DatabaseException("Invalid write location: " + path.toString());
        }
    }

    public static Map<Path, Node> parseAndValidateUpdate(Path path, Map<String, Object> update) throws DatabaseException {
        Node parsedValue;
        SortedMap<Path, Node> parsedUpdate = new TreeMap<>();
        for (Map.Entry<String, Object> entry : update.entrySet()) {
            Path updatePath = new Path(entry.getKey());
            Object newValue = entry.getValue();
            ValidationPath.validateWithObject(path.child(updatePath), newValue);
            String childName = !updatePath.isEmpty() ? updatePath.getBack().asString() : "";
            if (childName.equals(ServerValues.NAME_SUBKEY_SERVERVALUE) || childName.equals(".value")) {
                throw new DatabaseException("Path '" + updatePath + "' contains disallowed child name: " + childName);
            }
            if (childName.equals(".priority")) {
                parsedValue = PriorityUtilities.parsePriority(updatePath, newValue);
            } else {
                parsedValue = NodeUtilities.NodeFromJSON(newValue);
            }
            validateWritableObject(newValue);
            parsedUpdate.put(updatePath, parsedValue);
        }
        Path prevPath = null;
        for (Path curPath : parsedUpdate.keySet()) {
            Utilities.hardAssert(prevPath == null || prevPath.compareTo(curPath) < 0);
            if (prevPath == null || !prevPath.contains(curPath)) {
                prevPath = curPath;
            } else {
                throw new DatabaseException("Path '" + prevPath + "' is an ancestor of '" + curPath + "' in an update.");
            }
        }
        return parsedUpdate;
    }
}
