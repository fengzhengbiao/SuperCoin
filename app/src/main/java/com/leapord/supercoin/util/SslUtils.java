package com.leapord.supercoin.util;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2017-09-19.
 */

public class SslUtils {

	public static SSLSocketFactory initSSLSocketFactory() {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("SSL");
			X509TrustManager[] xTrustArray = new X509TrustManager[] { initTrustManager() };
			sslContext.init(null, xTrustArray, new SecureRandom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sslContext.getSocketFactory();
	}

	public static X509TrustManager initTrustManager() {
		X509TrustManager mTrustManager = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		};
		return mTrustManager;
	}
}
