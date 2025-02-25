package com.google.firebase.database.tubesock;

import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;
import com.google.firebase.database.connection.ConnectionContext;
import com.google.firebase.database.logging.LogWrapper;
import com.google.firebase.database.logging.Logger;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Thread;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/* compiled from: com.google.firebase:firebase-database@@19.3.0 */
/* loaded from: classes.dex */
public class WebSocket {
    static final byte OPCODE_BINARY = 2;
    static final byte OPCODE_CLOSE = 8;
    static final byte OPCODE_NONE = 0;
    static final byte OPCODE_PING = 9;
    static final byte OPCODE_PONG = 10;
    static final byte OPCODE_TEXT = 1;
    private static final int SSL_HANDSHAKE_TIMEOUT_MS = 60000;
    private static final String THREAD_BASE_NAME = "TubeSock";
    private final int clientId;
    private WebSocketEventHandler eventHandler;
    private final WebSocketHandshake handshake;
    private final Thread innerThread;
    private final LogWrapper logger;
    private final WebSocketReceiver receiver;
    private volatile Socket socket;
    private final String sslCacheDirectory;
    private volatile State state;
    private final URI url;
    private final WebSocketWriter writer;
    private static final AtomicInteger clientCount = new AtomicInteger(0);
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private static ThreadInitializer intializer = new ThreadInitializer() { // from class: com.google.firebase.database.tubesock.WebSocket.1
        @Override // com.google.firebase.database.tubesock.ThreadInitializer
        public void setName(Thread t, String name) {
            t.setName(name);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.firebase:firebase-database@@19.3.0 */
    /* loaded from: classes.dex */
    public enum State {
        NONE,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ThreadInitializer getIntializer() {
        return intializer;
    }

    public static void setThreadFactory(ThreadFactory threadFactory2, ThreadInitializer intializer2) {
        threadFactory = threadFactory2;
        intializer = intializer2;
    }

    public WebSocket(ConnectionContext context, URI url) {
        this(context, url, null);
    }

    public WebSocket(ConnectionContext context, URI url, String protocol) {
        this(context, url, protocol, null);
    }

    public WebSocket(ConnectionContext context, URI url, String protocol, Map<String, String> extraHeaders) {
        this.state = State.NONE;
        this.socket = null;
        this.eventHandler = null;
        this.clientId = clientCount.incrementAndGet();
        this.innerThread = getThreadFactory().newThread(new Runnable() { // from class: com.google.firebase.database.tubesock.WebSocket.2
            @Override // java.lang.Runnable
            public void run() {
                WebSocket.this.runReader();
            }
        });
        this.url = url;
        this.sslCacheDirectory = context.getSslCacheDirectory();
        Logger logger = context.getLogger();
        this.logger = new LogWrapper(logger, "WebSocket", "sk_" + this.clientId);
        this.handshake = new WebSocketHandshake(url, protocol, extraHeaders);
        this.receiver = new WebSocketReceiver(this);
        this.writer = new WebSocketWriter(this, THREAD_BASE_NAME, this.clientId);
    }

    public void setEventHandler(WebSocketEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSocketEventHandler getEventHandler() {
        return this.eventHandler;
    }

    public synchronized void connect() {
        if (this.state != State.NONE) {
            this.eventHandler.onError(new WebSocketException("connect() already called"));
            close();
            return;
        }
        ThreadInitializer intializer2 = getIntializer();
        Thread innerThread = getInnerThread();
        intializer2.setName(innerThread, "TubeSockReader-" + this.clientId);
        this.state = State.CONNECTING;
        getInnerThread().start();
    }

    public synchronized void send(String data) {
        send(OPCODE_TEXT, data.getBytes(UTF8));
    }

    public synchronized void send(byte[] data) {
        send(OPCODE_BINARY, data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void pong(byte[] data) {
        send(OPCODE_PONG, data);
    }

    private synchronized void send(byte opcode, byte[] data) {
        if (this.state != State.CONNECTED) {
            this.eventHandler.onError(new WebSocketException("error while sending data: not connected"));
        } else {
            try {
                this.writer.send(opcode, true, data);
            } catch (IOException e) {
                this.eventHandler.onError(new WebSocketException("Failed to send frame", e));
                close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleReceiverError(WebSocketException e) {
        this.eventHandler.onError(e);
        if (this.state == State.CONNECTED) {
            close();
        }
        closeSocket();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: com.google.firebase:firebase-database@@19.3.0 */
    /* renamed from: com.google.firebase.database.tubesock.WebSocket$3  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$google$firebase$database$tubesock$WebSocket$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$com$google$firebase$database$tubesock$WebSocket$State = iArr;
            try {
                iArr[State.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$firebase$database$tubesock$WebSocket$State[State.CONNECTING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$firebase$database$tubesock$WebSocket$State[State.CONNECTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$firebase$database$tubesock$WebSocket$State[State.DISCONNECTING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$firebase$database$tubesock$WebSocket$State[State.DISCONNECTED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public synchronized void close() {
        int i = AnonymousClass3.$SwitchMap$com$google$firebase$database$tubesock$WebSocket$State[this.state.ordinal()];
        if (i == 1) {
            this.state = State.DISCONNECTED;
        } else if (i == 2) {
            closeSocket();
        } else if (i == 3) {
            sendCloseHandshake();
        } else if (i == 4) {
        } else {
            if (i == 5) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onCloseOpReceived() {
        closeSocket();
    }

    private synchronized void closeSocket() {
        if (this.state != State.DISCONNECTED) {
            this.receiver.stopit();
            this.writer.stopIt();
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.state = State.DISCONNECTED;
            this.eventHandler.onClose();
        }
    }

    private void sendCloseHandshake() {
        try {
            this.state = State.DISCONNECTING;
            this.writer.stopIt();
            this.writer.send(OPCODE_CLOSE, true, new byte[0]);
        } catch (IOException e) {
            this.eventHandler.onError(new WebSocketException("Failed to send close frame", e));
        }
    }

    private Socket createSocket() {
        String scheme = this.url.getScheme();
        String host = this.url.getHost();
        int port = this.url.getPort();
        if (scheme != null && scheme.equals("ws")) {
            if (port == -1) {
                port = 80;
            }
            try {
                Socket socket = new Socket(host, port);
                return socket;
            } catch (UnknownHostException uhe) {
                throw new WebSocketException("unknown host: " + host, uhe);
            } catch (IOException ioe) {
                throw new WebSocketException("error while creating socket to " + this.url, ioe);
            }
        } else if (scheme == null || !scheme.equals("wss")) {
            throw new WebSocketException("unsupported protocol: " + scheme);
        } else {
            if (port == -1) {
                port = 443;
            }
            SSLSessionCache sessionCache = null;
            try {
                if (this.sslCacheDirectory != null) {
                    sessionCache = new SSLSessionCache(new File(this.sslCacheDirectory));
                }
            } catch (IOException e) {
                this.logger.debug("Failed to initialize SSL session cache", e, new Object[0]);
            }
            try {
                SocketFactory factory = SSLCertificateSocketFactory.getDefault(SSL_HANDSHAKE_TIMEOUT_MS, sessionCache);
                SSLSocket sslSocket = (SSLSocket) factory.createSocket(host, port);
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                SSLSession sslSession = sslSocket.getSession();
                if (hv.verify(host, sslSession)) {
                    return sslSocket;
                }
                throw new WebSocketException("Error while verifying secure socket to " + this.url);
            } catch (UnknownHostException uhe2) {
                throw new WebSocketException("unknown host: " + host, uhe2);
            } catch (IOException ioe2) {
                throw new WebSocketException("error while creating secure socket to " + this.url, ioe2);
            }
        }
    }

    public void blockClose() throws InterruptedException {
        if (this.writer.getInnerThread().getState() != Thread.State.NEW) {
            this.writer.getInnerThread().join();
        }
        getInnerThread().join();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runReader() {
        Socket socket;
        try {
            try {
                socket = createSocket();
            } finally {
                close();
            }
        } catch (WebSocketException wse) {
            this.eventHandler.onError(wse);
        } catch (Throwable t) {
            WebSocketEventHandler webSocketEventHandler = this.eventHandler;
            webSocketEventHandler.onError(new WebSocketException("error while connecting: " + t.getMessage(), t));
        }
        synchronized (this) {
            this.socket = socket;
            if (this.state == State.DISCONNECTED) {
                try {
                    this.socket.close();
                    this.socket = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                OutputStream output = socket.getOutputStream();
                output.write(this.handshake.getHandshake());
                boolean handshakeComplete = false;
                byte[] buffer = new byte[1000];
                int pos = 0;
                ArrayList<String> handshakeLines = new ArrayList<>();
                while (!handshakeComplete) {
                    int b = input.read();
                    if (b != -1) {
                        buffer[pos] = (byte) b;
                        pos++;
                        if (buffer[pos - 1] == 10 && buffer[pos - 2] == 13) {
                            String line = new String(buffer, UTF8);
                            if (line.trim().equals("")) {
                                handshakeComplete = true;
                            } else {
                                handshakeLines.add(line.trim());
                            }
                            buffer = new byte[1000];
                            pos = 0;
                        } else if (pos == 1000) {
                            String line2 = new String(buffer, UTF8);
                            throw new WebSocketException("Unexpected long line in handshake: " + line2);
                        }
                    } else {
                        throw new WebSocketException("Connection closed before handshake was complete");
                    }
                }
                char c = 0;
                this.handshake.verifyServerStatusLine(handshakeLines.get(0));
                handshakeLines.remove(0);
                HashMap<String, String> lowercaseHeaders = new HashMap<>();
                Iterator<String> it = handshakeLines.iterator();
                while (it.hasNext()) {
                    String line3 = it.next();
                    String[] keyValue = line3.split(": ", 2);
                    lowercaseHeaders.put(keyValue[c].toLowerCase(Locale.US), keyValue[1].toLowerCase(Locale.US));
                    c = 0;
                }
                this.handshake.verifyServerHandshakeHeaders(lowercaseHeaders);
                this.writer.setOutput(output);
                this.receiver.setInput(input);
                this.state = State.CONNECTED;
                this.writer.getInnerThread().start();
                this.eventHandler.onOpen();
                this.receiver.run();
            }
        }
    }

    Thread getInnerThread() {
        return this.innerThread;
    }
}
