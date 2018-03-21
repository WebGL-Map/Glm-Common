/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Tyler Bucher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.reallifegames.glm.module;

import javax.annotation.Nonnull;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Helps with ssl related tasks.
 *
 * @author Tyler Bucher
 */
public class SslModule {

    /**
     * Attempts to get an {@link SSLContext} from the jvm parameters.<br></br>
     * JVM Parameters:
     * <ul>
     *     <li>-Djavax.net.ssl.keyStore</li>
     *     <li>-Djavax.net.ssl.keyStoreType</li>
     *     <li>-Djavax.net.ssl.keyStorePassword</li>
     * </ul>
     *
     * @return {@link SSLContext} from the given system properties.
     *
     * @throws IllegalStateException if an error occurred and the ssl context could not be created.
     */
    @Nonnull
    public static SSLContext getSSLContextFromKeystore() throws IllegalStateException {
        final String KEY_STORE_TYPE = System.getProperty("javax.net.ssl.keyStoreType", "JKS");
        final String KEY_STORE = System.getProperty("javax.net.ssl.keyStore");
        final String KEY_STORE_PASS = System.getProperty("javax.net.ssl.keyStorePassword");
        KeyStore ks;
        SSLContext sslContext;
        try {
            ks = KeyStore.getInstance(KEY_STORE_TYPE);
            ks.load(Files.newInputStream(Paths.get("..", KEY_STORE)), KEY_STORE_PASS.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, KEY_STORE_PASS.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
            throw new IllegalStateException();
        }
        return sslContext;
    }
}
