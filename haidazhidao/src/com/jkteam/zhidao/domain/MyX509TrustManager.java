package com.jkteam.zhidao.domain;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
/**
 * 证书信任管理器，用于判断证书是否被信任
 * @author 郭灶鹏
 *
 */
public class MyX509TrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {

	}
/**
 * return null表示所有证书都被信任。
 */
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}
