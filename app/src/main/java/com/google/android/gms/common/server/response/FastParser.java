package com.google.android.gms.common.server.response;

import android.util.Log;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.common.util.Base64Utils;
import com.google.android.gms.common.util.JsonUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/* compiled from: com.google.android.gms:play-services-base@@17.1.0 */
/* loaded from: classes.dex */
public class FastParser<T extends FastJsonResponse> {
    private static final char[] zaqu = {'u', 'l', 'l'};
    private static final char[] zaqv = {'r', 'u', 'e'};
    private static final char[] zaqw = {'r', 'u', 'e', '\"'};
    private static final char[] zaqx = {'a', 'l', 's', 'e'};
    private static final char[] zaqy = {'a', 'l', 's', 'e', '\"'};
    private static final char[] zaqz = {'\n'};
    private static final zaa<Integer> zarb = new zab();
    private static final zaa<Long> zarc = new zaa();
    private static final zaa<Float> zard = new zad();
    private static final zaa<Double> zare = new zac();
    private static final zaa<Boolean> zarf = new zaf();
    private static final zaa<String> zarg = new zae();
    private static final zaa<BigInteger> zarh = new zah();
    private static final zaa<BigDecimal> zari = new zag();
    private final char[] zaqp = new char[1];
    private final char[] zaqq = new char[32];
    private final char[] zaqr = new char[1024];
    private final StringBuilder zaqs = new StringBuilder(32);
    private final StringBuilder zaqt = new StringBuilder(1024);
    private final Stack<Integer> zara = new Stack<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-base@@17.1.0 */
    /* loaded from: classes.dex */
    public interface zaa<O> {
        O zah(FastParser fastParser, BufferedReader bufferedReader) throws ParseException, IOException;
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.1.0 */
    /* loaded from: classes.dex */
    public static class ParseException extends Exception {
        public ParseException(String str) {
            super(str);
        }

        public ParseException(String str, Throwable th) {
            super(str, th);
        }

        public ParseException(Throwable th) {
            super(th);
        }
    }

    public void parse(InputStream inputStream, T t) throws ParseException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1024);
        try {
            try {
                this.zara.push(0);
                char zaj = zaj(bufferedReader);
                if (zaj != 0) {
                    if (zaj == '[') {
                        this.zara.push(5);
                        Map<String, FastJsonResponse.Field<?, ?>> fieldMappings = t.getFieldMappings();
                        if (fieldMappings.size() == 1) {
                            FastJsonResponse.Field<?, ?> value = fieldMappings.entrySet().iterator().next().getValue();
                            t.addConcreteTypeArrayInternal(value, value.zaqj, zaa(bufferedReader, value));
                        } else {
                            throw new ParseException("Object array response class must have a single Field");
                        }
                    } else if (zaj == '{') {
                        this.zara.push(1);
                        zaa(bufferedReader, t);
                    } else {
                        StringBuilder sb = new StringBuilder(19);
                        sb.append("Unexpected token: ");
                        sb.append(zaj);
                        throw new ParseException(sb.toString());
                    }
                    zak(0);
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.w("FastParser", "Failed to close reader while parsing.");
                    }
                } else {
                    throw new ParseException("No data to parse");
                }
            } catch (IOException e2) {
                throw new ParseException(e2);
            }
        } catch (Throwable th) {
            try {
                bufferedReader.close();
            } catch (IOException e3) {
                Log.w("FastParser", "Failed to close reader while parsing.");
            }
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final boolean zaa(BufferedReader bufferedReader, FastJsonResponse fastJsonResponse) throws ParseException, IOException {
        HashMap hashMap;
        Map<String, FastJsonResponse.Field<?, ?>> fieldMappings = fastJsonResponse.getFieldMappings();
        String zaa2 = zaa(bufferedReader);
        if (zaa2 == null) {
            zak(1);
            return false;
        }
        while (zaa2 != null) {
            FastJsonResponse.Field<?, ?> field = fieldMappings.get(zaa2);
            if (field == null) {
                zaa2 = zab(bufferedReader);
            } else {
                this.zara.push(4);
                switch (field.zaqf) {
                    case 0:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zad(bufferedReader));
                            break;
                        } else {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, (ArrayList<Integer>) zaa(bufferedReader, zarb));
                            break;
                        }
                    case 1:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zaf(bufferedReader));
                            break;
                        } else {
                            fastJsonResponse.zab((FastJsonResponse.Field) field, (ArrayList<BigInteger>) zaa(bufferedReader, zarh));
                            break;
                        }
                    case 2:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zae(bufferedReader));
                            break;
                        } else {
                            fastJsonResponse.zac(field, zaa(bufferedReader, zarc));
                            break;
                        }
                    case 3:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zag(bufferedReader));
                            break;
                        } else {
                            fastJsonResponse.zad(field, zaa(bufferedReader, zard));
                            break;
                        }
                    case 4:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa(field, zah(bufferedReader));
                            break;
                        } else {
                            fastJsonResponse.zae(field, zaa(bufferedReader, zare));
                            break;
                        }
                    case 5:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zai(bufferedReader));
                            break;
                        } else {
                            fastJsonResponse.zaf(field, zaa(bufferedReader, zari));
                            break;
                        }
                    case 6:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa(field, zaa(bufferedReader, false));
                            break;
                        } else {
                            fastJsonResponse.zag(field, zaa(bufferedReader, zarf));
                            break;
                        }
                    case 7:
                        if (!field.zaqg) {
                            fastJsonResponse.zaa((FastJsonResponse.Field) field, zac(bufferedReader));
                            break;
                        } else {
                            fastJsonResponse.zah(field, zaa(bufferedReader, zarg));
                            break;
                        }
                    case 8:
                        fastJsonResponse.zaa((FastJsonResponse.Field) field, Base64Utils.decode(zaa(bufferedReader, this.zaqr, this.zaqt, zaqz)));
                        break;
                    case 9:
                        fastJsonResponse.zaa((FastJsonResponse.Field) field, Base64Utils.decodeUrlSafe(zaa(bufferedReader, this.zaqr, this.zaqt, zaqz)));
                        break;
                    case 10:
                        char zaj = zaj(bufferedReader);
                        if (zaj == 'n') {
                            zab(bufferedReader, zaqu);
                            hashMap = null;
                        } else if (zaj == '{') {
                            this.zara.push(1);
                            hashMap = new HashMap();
                            while (true) {
                                char zaj2 = zaj(bufferedReader);
                                if (zaj2 == 0) {
                                    throw new ParseException("Unexpected EOF");
                                } else if (zaj2 == '\"') {
                                    String zab = zab(bufferedReader, this.zaqq, this.zaqs, null);
                                    if (zaj(bufferedReader) != ':') {
                                        String valueOf = String.valueOf(zab);
                                        throw new ParseException(valueOf.length() != 0 ? "No map value found for key ".concat(valueOf) : new String("No map value found for key "));
                                    } else if (zaj(bufferedReader) != '\"') {
                                        String valueOf2 = String.valueOf(zab);
                                        throw new ParseException(valueOf2.length() != 0 ? "Expected String value for key ".concat(valueOf2) : new String("Expected String value for key "));
                                    } else {
                                        hashMap.put(zab, zab(bufferedReader, this.zaqq, this.zaqs, null));
                                        char zaj3 = zaj(bufferedReader);
                                        if (zaj3 != ',') {
                                            if (zaj3 == '}') {
                                                zak(1);
                                            } else {
                                                StringBuilder sb = new StringBuilder(48);
                                                sb.append("Unexpected character while parsing string map: ");
                                                sb.append(zaj3);
                                                throw new ParseException(sb.toString());
                                            }
                                        }
                                    }
                                } else if (zaj2 == '}') {
                                    zak(1);
                                }
                            }
                        } else {
                            throw new ParseException("Expected start of a map object");
                        }
                        fastJsonResponse.zaa((FastJsonResponse.Field) field, (Map<String, String>) hashMap);
                        break;
                    case 11:
                        if (field.zaqg) {
                            char zaj4 = zaj(bufferedReader);
                            if (zaj4 == 'n') {
                                zab(bufferedReader, zaqu);
                                fastJsonResponse.addConcreteTypeArrayInternal(field, field.zaqj, null);
                                break;
                            } else {
                                this.zara.push(5);
                                if (zaj4 == '[') {
                                    fastJsonResponse.addConcreteTypeArrayInternal(field, field.zaqj, zaa(bufferedReader, field));
                                    break;
                                } else {
                                    throw new ParseException("Expected array start");
                                }
                            }
                        } else {
                            char zaj5 = zaj(bufferedReader);
                            if (zaj5 == 'n') {
                                zab(bufferedReader, zaqu);
                                fastJsonResponse.addConcreteTypeInternal(field, field.zaqj, null);
                                break;
                            } else {
                                this.zara.push(1);
                                if (zaj5 == '{') {
                                    try {
                                        FastJsonResponse zacn = field.zacn();
                                        zaa(bufferedReader, zacn);
                                        fastJsonResponse.addConcreteTypeInternal(field, field.zaqj, zacn);
                                        break;
                                    } catch (IllegalAccessException e) {
                                        throw new ParseException("Error instantiating inner object", e);
                                    } catch (InstantiationException e2) {
                                        throw new ParseException("Error instantiating inner object", e2);
                                    }
                                } else {
                                    throw new ParseException("Expected start of object");
                                }
                            }
                        }
                    default:
                        int i = field.zaqf;
                        StringBuilder sb2 = new StringBuilder(30);
                        sb2.append("Invalid field type ");
                        sb2.append(i);
                        throw new ParseException(sb2.toString());
                }
                zak(4);
                zak(2);
                char zaj6 = zaj(bufferedReader);
                if (zaj6 == ',') {
                    zaa2 = zaa(bufferedReader);
                } else if (zaj6 == '}') {
                    zaa2 = null;
                } else {
                    StringBuilder sb3 = new StringBuilder(55);
                    sb3.append("Expected end of object or field separator, but found: ");
                    sb3.append(zaj6);
                    throw new ParseException(sb3.toString());
                }
            }
        }
        zak(1);
        return true;
    }

    private final String zaa(BufferedReader bufferedReader) throws ParseException, IOException {
        this.zara.push(2);
        char zaj = zaj(bufferedReader);
        if (zaj == '\"') {
            this.zara.push(3);
            String zab = zab(bufferedReader, this.zaqq, this.zaqs, null);
            zak(3);
            if (zaj(bufferedReader) == ':') {
                return zab;
            }
            throw new ParseException("Expected key/value separator");
        } else if (zaj == ']') {
            zak(2);
            zak(1);
            zak(5);
            return null;
        } else if (zaj == '}') {
            zak(2);
            return null;
        } else {
            StringBuilder sb = new StringBuilder(19);
            sb.append("Unexpected token: ");
            sb.append(zaj);
            throw new ParseException(sb.toString());
        }
    }

    private final String zab(BufferedReader bufferedReader) throws ParseException, IOException {
        bufferedReader.mark(1024);
        char zaj = zaj(bufferedReader);
        if (zaj != '\"') {
            if (zaj != ',') {
                int i = 1;
                if (zaj == '[') {
                    this.zara.push(5);
                    bufferedReader.mark(32);
                    if (zaj(bufferedReader) == ']') {
                        zak(5);
                    } else {
                        bufferedReader.reset();
                        boolean z = false;
                        boolean z2 = false;
                        while (i > 0) {
                            char zaj2 = zaj(bufferedReader);
                            if (zaj2 == 0) {
                                throw new ParseException("Unexpected EOF while parsing array");
                            } else if (!Character.isISOControl(zaj2)) {
                                if (zaj2 == '\"' && !z) {
                                    z2 = !z2;
                                }
                                if (zaj2 == '[' && !z2) {
                                    i++;
                                }
                                if (zaj2 == ']' && !z2) {
                                    i--;
                                }
                                if (zaj2 != '\\' || !z2) {
                                    z = false;
                                } else {
                                    z = !z;
                                }
                            } else {
                                throw new ParseException("Unexpected control character while reading array");
                            }
                        }
                        zak(5);
                    }
                } else if (zaj != '{') {
                    bufferedReader.reset();
                    zaa(bufferedReader, this.zaqr);
                } else {
                    this.zara.push(1);
                    bufferedReader.mark(32);
                    char zaj3 = zaj(bufferedReader);
                    if (zaj3 == '}') {
                        zak(1);
                    } else if (zaj3 == '\"') {
                        bufferedReader.reset();
                        zaa(bufferedReader);
                        do {
                        } while (zab(bufferedReader) != null);
                        zak(1);
                    } else {
                        StringBuilder sb = new StringBuilder(18);
                        sb.append("Unexpected token ");
                        sb.append(zaj3);
                        throw new ParseException(sb.toString());
                    }
                }
            } else {
                throw new ParseException("Missing value");
            }
        } else if (bufferedReader.read(this.zaqp) != -1) {
            char c = this.zaqp[0];
            boolean z3 = false;
            do {
                if (c != '\"' || z3) {
                    if (c == '\\') {
                        z3 = !z3;
                    } else {
                        z3 = false;
                    }
                    if (bufferedReader.read(this.zaqp) != -1) {
                        c = this.zaqp[0];
                    } else {
                        throw new ParseException("Unexpected EOF while parsing string");
                    }
                }
            } while (!Character.isISOControl(c));
            throw new ParseException("Unexpected control character while reading string");
        } else {
            throw new ParseException("Unexpected EOF while parsing string");
        }
        char zaj4 = zaj(bufferedReader);
        if (zaj4 == ',') {
            zak(2);
            return zaa(bufferedReader);
        } else if (zaj4 == '}') {
            zak(2);
            return null;
        } else {
            StringBuilder sb2 = new StringBuilder(18);
            sb2.append("Unexpected token ");
            sb2.append(zaj4);
            throw new ParseException(sb2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final String zac(BufferedReader bufferedReader) throws ParseException, IOException {
        return zaa(bufferedReader, this.zaqq, this.zaqs, null);
    }

    private final <O> ArrayList<O> zaa(BufferedReader bufferedReader, zaa<O> zaaVar) throws ParseException, IOException {
        char zaj = zaj(bufferedReader);
        if (zaj == 'n') {
            zab(bufferedReader, zaqu);
            return null;
        } else if (zaj == '[') {
            this.zara.push(5);
            ArrayList<O> arrayList = new ArrayList<>();
            while (true) {
                bufferedReader.mark(1024);
                char zaj2 = zaj(bufferedReader);
                if (zaj2 == 0) {
                    throw new ParseException("Unexpected EOF");
                } else if (zaj2 != ',') {
                    if (zaj2 != ']') {
                        bufferedReader.reset();
                        arrayList.add(zaaVar.zah(this, bufferedReader));
                    } else {
                        zak(5);
                        return arrayList;
                    }
                }
            }
        } else {
            throw new ParseException("Expected start of array");
        }
    }

    private final String zaa(BufferedReader bufferedReader, char[] cArr, StringBuilder sb, char[] cArr2) throws ParseException, IOException {
        char zaj = zaj(bufferedReader);
        if (zaj == '\"') {
            return zab(bufferedReader, cArr, sb, cArr2);
        }
        if (zaj == 'n') {
            zab(bufferedReader, zaqu);
            return null;
        }
        throw new ParseException("Expected string");
    }

    private static String zab(BufferedReader bufferedReader, char[] cArr, StringBuilder sb, char[] cArr2) throws ParseException, IOException {
        boolean z;
        sb.setLength(0);
        bufferedReader.mark(cArr.length);
        boolean z2 = false;
        boolean z3 = false;
        while (true) {
            int read = bufferedReader.read(cArr);
            if (read != -1) {
                for (int i = 0; i < read; i++) {
                    char c = cArr[i];
                    if (Character.isISOControl(c)) {
                        if (cArr2 != null) {
                            for (char c2 : cArr2) {
                                if (c2 == c) {
                                    z = true;
                                    break;
                                }
                            }
                        }
                        z = false;
                        if (!z) {
                            throw new ParseException("Unexpected control character while reading string");
                        }
                    }
                    if (c != '\"' || z2) {
                        if (c == '\\') {
                            z2 = !z2;
                            z3 = true;
                        } else {
                            z2 = false;
                        }
                    } else {
                        sb.append(cArr, 0, i);
                        bufferedReader.reset();
                        bufferedReader.skip(i + 1);
                        if (z3) {
                            return JsonUtils.unescapeString(sb.toString());
                        }
                        return sb.toString();
                    }
                }
                sb.append(cArr, 0, read);
                bufferedReader.mark(cArr.length);
            } else {
                throw new ParseException("Unexpected EOF while parsing string");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int zad(BufferedReader bufferedReader) throws ParseException, IOException {
        boolean z;
        int i;
        int i2;
        int zaa2 = zaa(bufferedReader, this.zaqr);
        int i3 = 0;
        if (zaa2 == 0) {
            return 0;
        }
        char[] cArr = this.zaqr;
        if (zaa2 > 0) {
            if (cArr[0] == '-') {
                i2 = Integer.MIN_VALUE;
                i = 1;
                z = true;
            } else {
                i2 = -2147483647;
                i = 0;
                z = false;
            }
            if (i < zaa2) {
                int i4 = i + 1;
                int digit = Character.digit(cArr[i], 10);
                if (digit >= 0) {
                    int i5 = -digit;
                    i = i4;
                    i3 = i5;
                } else {
                    throw new ParseException("Unexpected non-digit character");
                }
            }
            while (i < zaa2) {
                int i6 = i + 1;
                int digit2 = Character.digit(cArr[i], 10);
                if (digit2 < 0) {
                    throw new ParseException("Unexpected non-digit character");
                } else if (i3 >= -214748364) {
                    int i7 = i3 * 10;
                    if (i7 >= i2 + digit2) {
                        i3 = i7 - digit2;
                        i = i6;
                    } else {
                        throw new ParseException("Number too large");
                    }
                } else {
                    throw new ParseException("Number too large");
                }
            }
            if (!z) {
                return -i3;
            }
            if (i > 1) {
                return i3;
            }
            throw new ParseException("No digits to parse");
        }
        throw new ParseException("No number to parse");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final long zae(BufferedReader bufferedReader) throws ParseException, IOException {
        long j;
        boolean z;
        int zaa2 = zaa(bufferedReader, this.zaqr);
        long j2 = 0;
        if (zaa2 == 0) {
            return 0L;
        }
        char[] cArr = this.zaqr;
        if (zaa2 > 0) {
            int i = 0;
            if (cArr[0] == '-') {
                j = Long.MIN_VALUE;
                i = 1;
                z = true;
            } else {
                j = -9223372036854775807L;
                z = false;
            }
            if (i < zaa2) {
                int i2 = i + 1;
                int digit = Character.digit(cArr[i], 10);
                if (digit >= 0) {
                    j2 = -digit;
                    i = i2;
                } else {
                    throw new ParseException("Unexpected non-digit character");
                }
            }
            while (i < zaa2) {
                int i3 = i + 1;
                int digit2 = Character.digit(cArr[i], 10);
                if (digit2 < 0) {
                    throw new ParseException("Unexpected non-digit character");
                } else if (j2 >= -922337203685477580L) {
                    long j3 = j2 * 10;
                    long j4 = digit2;
                    if (j3 >= j + j4) {
                        j2 = j3 - j4;
                        i = i3;
                    } else {
                        throw new ParseException("Number too large");
                    }
                } else {
                    throw new ParseException("Number too large");
                }
            }
            if (!z) {
                return -j2;
            }
            if (i > 1) {
                return j2;
            }
            throw new ParseException("No digits to parse");
        }
        throw new ParseException("No number to parse");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final BigInteger zaf(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zaqr);
        if (zaa2 == 0) {
            return null;
        }
        return new BigInteger(new String(this.zaqr, 0, zaa2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean zaa(BufferedReader bufferedReader, boolean z) throws ParseException, IOException {
        while (true) {
            char zaj = zaj(bufferedReader);
            if (zaj != '\"') {
                if (zaj == 'f') {
                    zab(bufferedReader, z ? zaqy : zaqx);
                    return false;
                } else if (zaj == 'n') {
                    zab(bufferedReader, zaqu);
                    return false;
                } else if (zaj == 't') {
                    zab(bufferedReader, z ? zaqw : zaqv);
                    return true;
                } else {
                    StringBuilder sb = new StringBuilder(19);
                    sb.append("Unexpected token: ");
                    sb.append(zaj);
                    throw new ParseException(sb.toString());
                }
            } else if (!z) {
                z = true;
            } else {
                throw new ParseException("No boolean value found in string");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final float zag(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zaqr);
        if (zaa2 == 0) {
            return 0.0f;
        }
        return Float.parseFloat(new String(this.zaqr, 0, zaa2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final double zah(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zaqr);
        if (zaa2 == 0) {
            return 0.0d;
        }
        return Double.parseDouble(new String(this.zaqr, 0, zaa2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final BigDecimal zai(BufferedReader bufferedReader) throws ParseException, IOException {
        int zaa2 = zaa(bufferedReader, this.zaqr);
        if (zaa2 == 0) {
            return null;
        }
        return new BigDecimal(new String(this.zaqr, 0, zaa2));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final <T extends FastJsonResponse> ArrayList<T> zaa(BufferedReader bufferedReader, FastJsonResponse.Field<?, ?> field) throws ParseException, IOException {
        ArrayList<T> arrayList = (ArrayList<T>) new ArrayList();
        char zaj = zaj(bufferedReader);
        if (zaj == ']') {
            zak(5);
            return arrayList;
        } else if (zaj == 'n') {
            zab(bufferedReader, zaqu);
            zak(5);
            return null;
        } else if (zaj == '{') {
            this.zara.push(1);
            while (true) {
                try {
                    FastJsonResponse zacn = field.zacn();
                    if (!zaa(bufferedReader, zacn)) {
                        return arrayList;
                    }
                    arrayList.add(zacn);
                    char zaj2 = zaj(bufferedReader);
                    if (zaj2 != ',') {
                        if (zaj2 == ']') {
                            zak(5);
                            return arrayList;
                        }
                        StringBuilder sb = new StringBuilder(19);
                        sb.append("Unexpected token: ");
                        sb.append(zaj2);
                        throw new ParseException(sb.toString());
                    } else if (zaj(bufferedReader) == '{') {
                        this.zara.push(1);
                    } else {
                        throw new ParseException("Expected start of next object in array");
                    }
                } catch (IllegalAccessException e) {
                    throw new ParseException("Error instantiating inner object", e);
                } catch (InstantiationException e2) {
                    throw new ParseException("Error instantiating inner object", e2);
                }
            }
        } else {
            StringBuilder sb2 = new StringBuilder(19);
            sb2.append("Unexpected token: ");
            sb2.append(zaj);
            throw new ParseException(sb2.toString());
        }
    }

    private final char zaj(BufferedReader bufferedReader) throws ParseException, IOException {
        if (bufferedReader.read(this.zaqp) == -1) {
            return (char) 0;
        }
        while (Character.isWhitespace(this.zaqp[0])) {
            if (bufferedReader.read(this.zaqp) == -1) {
                return (char) 0;
            }
        }
        return this.zaqp[0];
    }

    private final int zaa(BufferedReader bufferedReader, char[] cArr) throws ParseException, IOException {
        int i;
        char zaj = zaj(bufferedReader);
        if (zaj == 0) {
            throw new ParseException("Unexpected EOF");
        } else if (zaj == ',') {
            throw new ParseException("Missing value");
        } else if (zaj == 'n') {
            zab(bufferedReader, zaqu);
            return 0;
        } else {
            bufferedReader.mark(1024);
            if (zaj == '\"') {
                i = 0;
                boolean z = false;
                while (i < cArr.length && bufferedReader.read(cArr, i, 1) != -1) {
                    char c = cArr[i];
                    if (Character.isISOControl(c)) {
                        throw new ParseException("Unexpected control character while reading string");
                    } else if (c != '\"' || z) {
                        if (c == '\\') {
                            z = !z;
                        } else {
                            z = false;
                        }
                        i++;
                    } else {
                        bufferedReader.reset();
                        bufferedReader.skip(i + 1);
                        return i;
                    }
                }
            } else {
                cArr[0] = zaj;
                i = 1;
                while (i < cArr.length && bufferedReader.read(cArr, i, 1) != -1) {
                    if (cArr[i] == '}' || cArr[i] == ',' || Character.isWhitespace(cArr[i]) || cArr[i] == ']') {
                        bufferedReader.reset();
                        bufferedReader.skip(i - 1);
                        cArr[i] = 0;
                        return i;
                    }
                    i++;
                }
            }
            if (i == cArr.length) {
                throw new ParseException("Absurdly long value");
            }
            throw new ParseException("Unexpected EOF");
        }
    }

    private final void zab(BufferedReader bufferedReader, char[] cArr) throws ParseException, IOException {
        int i = 0;
        while (i < cArr.length) {
            int read = bufferedReader.read(this.zaqq, 0, cArr.length - i);
            if (read != -1) {
                for (int i2 = 0; i2 < read; i2++) {
                    if (cArr[i2 + i] != this.zaqq[i2]) {
                        throw new ParseException("Unexpected character");
                    }
                }
                i += read;
            } else {
                throw new ParseException("Unexpected EOF");
            }
        }
    }

    private final void zak(int i) throws ParseException {
        if (!this.zara.isEmpty()) {
            int intValue = this.zara.pop().intValue();
            if (intValue != i) {
                StringBuilder sb = new StringBuilder(46);
                sb.append("Expected state ");
                sb.append(i);
                sb.append(" but had ");
                sb.append(intValue);
                throw new ParseException(sb.toString());
            }
            return;
        }
        StringBuilder sb2 = new StringBuilder(46);
        sb2.append("Expected state ");
        sb2.append(i);
        sb2.append(" but had empty stack");
        throw new ParseException(sb2.toString());
    }
}
