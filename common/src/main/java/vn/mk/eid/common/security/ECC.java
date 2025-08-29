package vn.mk.eid.common.security;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import sun.security.x509.*;

import javax.crypto.Cipher;
import javax.crypto.NullCipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;

/**
 *
 * @author HoaiNT
 */
public class ECC {
    private static String default_algorithm = "EC";
    private static String default_provider = "BC";
    private static String default_namedCurve = "secp256r1";
    private static String default_sign_algorithm = "SHA256withECDSA";
    private static int valid_days = 3650;

    static {
//        Security.addProvider(new BouncyCastleProvider());
    }

    static X509Certificate generateCertificate(KeyPair pair, String dn, int days, String algorithm)
            throws GeneralSecurityException, IOException {
        PrivateKey privkey = pair.getPrivate();
        X509CertInfo info = new X509CertInfo();
        Date from = new Date();
        Date to = new Date(from.getTime() + days * 86400000l);
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name(dn);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
        info.set(X509CertInfo.SUBJECT, owner);
        info.set(X509CertInfo.ISSUER, owner);
        info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        AlgorithmId algo = new AlgorithmId(AlgorithmId.EC_oid);
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(privkey, algorithm);

        // Update the algorith, and resign.
        algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
        cert = new X509CertImpl(info);
        cert.sign(privkey, algorithm);
        return cert;
    }

    public static boolean generateKeyStore(String filePath, String password, String dn) {
        String alias = getCNfromDN(dn);
        if (alias==null || alias.trim().isEmpty())
            alias = "alias";
        return generateKeyStore(filePath, password, dn, alias);
    }

    public static boolean generateKeyStore(String filePath, String password, String dn, String alias) {
        return generateKeyStore(filePath, password, dn, alias, valid_days);
    }

    public static boolean generateKeyStore(String filePath, String password, String dn, String alias, int days) {
        try {
            KeyPair kp = generateKeyPair();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            File file = new File(filePath);
            if (file.exists()) {
                ks.load(new FileInputStream(file), password.toCharArray());
            } else {
                ks.load(null, null);
            }
            X509Certificate cert = generateCertificate(kp, dn, days, default_sign_algorithm);

            Certificate[] chain = new Certificate[] {cert};
            ks.setKeyEntry(alias, kp.getPrivate(), password.toCharArray(), chain);
            ks.store(new FileOutputStream(file), password.toCharArray());

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(default_algorithm, default_namedCurve);
    }

    public static KeyPair generateKeyPair(String algorithm, String namedCurve) {
        return generateKeyPair(algorithm, namedCurve, null);
    }

    public static KeyPair generateKeyPair(String algorithm, String namedCurve, String provider) {
        try {
            KeyPairGenerator kpg;
            if (provider == null)
                kpg = KeyPairGenerator.getInstance(algorithm);
            else
                kpg = KeyPairGenerator.getInstance(algorithm, provider);
            ECGenParameterSpec ecsp = new ECGenParameterSpec(namedCurve);
            kpg.initialize(ecsp, new SecureRandom());
            KeyPair kp = kpg.genKeyPair();
//            ECPublicKey pubKey = (ECPublicKey)kp.getPublic();
//            System.out.println("pubKey: " + Base64.getEncoder().encodeToString(pubKey.getEncoded()));
//            ECPrivateKey priKey = (ECPrivateKey)kp.getPrivate();
//            System.out.println("priKey: " + Base64.getEncoder().encodeToString(priKey.getEncoded()));
            return kp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ECPrivateKey getECPrivateKey(byte[] keyBytes) {
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPrivateKey priKey = (ECPrivateKey)keyFactory.generatePrivate(pkcs8KeySpec);
            return priKey;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ECPrivateKey getECPrivateKey(String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            return getECPrivateKey(keyBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ECPublicKey getECPublicKey(byte[] keyBytes) {
        try {
            // Obtain the public key
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPublicKey pubKey = (ECPublicKey)keyFactory.generatePublic(x509KeySpec);
            return pubKey;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ECPublicKey getECPublicKey(String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            return getECPublicKey(keyBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String encrypt(byte[] data, ECPublicKey pubKey) {
        try {
            ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(pubKey.getW(), pubKey.getParams());

            // Encryption of data
            // TODO Cipher does not support EC algorithm but fails to implement it
            Cipher cipher = new NullCipher();
            //Cipher cipher = Cipher.getInstance("SHA1withECDSA");
            // Cipher.getInstance(ALGORITHM, keyFactory.getProvider());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey, ecPublicKeySpec.getParams());

            return Base64.getEncoder().encodeToString(cipher.doFinal(data));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String encrypt(byte[] data, byte[] keyBytes) {
        return encrypt(data, getECPublicKey(keyBytes));
    }

    public static String encrypt(byte[] data, String key) {
        return encrypt(data, getECPublicKey(key));
    }

    public static String encrypt(String data, ECPublicKey pubKey) {
        try {
            return encrypt(data.getBytes("UTF-8"), pubKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String data, String key) {
        return encrypt(data, getECPublicKey(key));
    }

    public static String encrypt(String data, byte[] keyBytes) {
        return encrypt(data, getECPublicKey(keyBytes));
    }

    public static byte[] encrypt2Bytes(byte[] data, ECPublicKey pubKey) {
        try {
            ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(pubKey.getW(), pubKey.getParams());

            // Encryption of data
            // TODO Cipher does not support EC algorithm but fails to implement it
            Cipher cipher = new NullCipher();
            //Cipher cipher = Cipher.getInstance("SHA1withECDSA");
            // Cipher.getInstance(ALGORITHM, keyFactory.getProvider());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey, ecPublicKeySpec.getParams());

            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt2Bytes(byte[] data, byte[] keyBytes) {
        return encrypt2Bytes(data, getECPublicKey(keyBytes));
    }

    public static byte[] encrypt2Bytes(byte[] data, String key) {
        return encrypt2Bytes(data, getECPublicKey(key));
    }

    public static byte[] encrypt2Bytes(String data, ECPublicKey pubKey) {
        try {
            return encrypt2Bytes(data.getBytes("UTF-8"), pubKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt2Bytes(String data, String key) {
        return encrypt2Bytes(data, getECPublicKey(key));
    }

    public static byte[] encrypt2Bytes(String data, byte[] keyBytes) {
        return encrypt2Bytes(data, getECPublicKey(keyBytes));
    }

    public static byte[] decrypt2Bytes(byte[] data, ECPrivateKey priKey) {
        try {
            ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(priKey.getS(), priKey.getParams());

            // Encryption of data
            // TODO Cipher does not support EC algorithm but fails to implement it
            Cipher cipher = new NullCipher();
            //Cipher cipher = Cipher.getInstance("SHA1withECDSA");
            // Cipher.getInstance(ALGORITHM, keyFactory.getProvider());
            cipher.init(Cipher.DECRYPT_MODE, priKey, ecPrivateKeySpec.getParams());

            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt2Bytes(byte[] data, byte[] keyBytes) {
        return decrypt2Bytes(data, getECPrivateKey(keyBytes));
    }

    public static byte[] decrypt2Bytes(byte[] data, String key) {
        return decrypt2Bytes(data, getECPrivateKey(key));
    }

    public static byte[] decrypt2Bytes(String data, ECPrivateKey priKey) {
        return decrypt2Bytes(Base64.getDecoder().decode(data), priKey);
    }

    public static byte[] decrypt2Bytes(String data, String key) {
        return decrypt2Bytes(data, getECPrivateKey(key));
    }

    public static byte[] decrypt2Bytes(String data, byte[] keyBytes) {
        return decrypt2Bytes(data, getECPrivateKey(keyBytes));
    }

    public static String decrypt(byte[] data, ECPrivateKey priKey) {
        try {
            ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(priKey.getS(), priKey.getParams());

            // Encryption of data
            // TODO Cipher does not support EC algorithm but fails to implement it
            Cipher cipher = new NullCipher();
            //Cipher cipher = Cipher.getInstance("SHA1withECDSA");
            // Cipher.getInstance(ALGORITHM, keyFactory.getProvider());
            cipher.init(Cipher.DECRYPT_MODE, priKey, ecPrivateKeySpec.getParams());

            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(byte[] data, byte[] keyBytes) {
        return decrypt(data, getECPrivateKey(keyBytes));
    }

    public static String decrypt(byte[] data, String key) {
        return decrypt(data, getECPrivateKey(key));
    }

    public static String decrypt(String data, ECPrivateKey priKey) {
        return decrypt(Base64.getDecoder().decode(data), priKey);
    }

    public static String decrypt(String data, String key) {
        return decrypt(data, getECPrivateKey(key));
    }

    public static String decrypt(String data, byte[] keyBytes) {
        return decrypt(data, getECPrivateKey(keyBytes));
    }

    public static String sign(byte[] data, ECPrivateKey priKey, String sign_algorithm) {
        try {
            Signature ecdsaSign = Signature.getInstance(sign_algorithm);
            ecdsaSign.initSign(priKey);
            ecdsaSign.update(data);
            byte[] signature = ecdsaSign.sign();
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String sign(byte[] data, ECPrivateKey priKey) {
        return sign(data, priKey, default_sign_algorithm);
    }

    public static String sign(byte[] data, byte[] keyBytes) {
        return sign(data, getECPrivateKey(keyBytes));
    }

    public static String sign(byte[] data, String key) {
        return sign(data, getECPrivateKey(key));
    }

    public static String sign(String data, ECPrivateKey priKey) {
        try {
            byte[] dataBytes = data.getBytes("UTF-8");
            return sign(dataBytes, priKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String sign(String data, String key) {
        return sign(data, getECPrivateKey(key));
    }

    public static String sign(String data, byte[] keyBytes) {
        return sign(data, getECPrivateKey(keyBytes));
    }

    public static boolean verify(byte[] data, byte[] signature, ECPublicKey pubKey, String sign_algorithm) {
        try {
            Signature ecdsaVerify = Signature.getInstance(sign_algorithm);
            ecdsaVerify.initVerify(pubKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(signature);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean verify(byte[] data, byte[] signature, ECPublicKey pubKey) {
        return verify(data, signature, pubKey, default_sign_algorithm);
    }

    public static boolean verify(byte[] data, byte[] signature, byte[] keyBytes) {
        return verify(data, signature, getECPublicKey(keyBytes));
    }

    public static boolean verify(byte[] data, byte[] signature, String key) {
        return verify(data, signature, getECPublicKey(key));
    }

    public static boolean verify(byte[] data, String signature, String key) {
        return verify(data, Base64.getDecoder().decode(signature), key);
    }

    public static boolean verify(String data, String signature, String key) {
        try {
            byte[] dataBytes = data.getBytes("UTF-8");
            return verify(dataBytes, signature, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static String getCNfromDN(String dn) {
        String cn = "";
        if (dn==null || dn.isEmpty() || dn.trim().isEmpty())
            return cn;
        String[] arr = dn.split(",");
        for (int i=0; i<arr.length; i++) {
            String[] arr2 = arr[i].split("=");
            if (arr2.length == 2) {
                if ("CN".equals(arr2[0])) {
                    cn = arr2[1];
                    break;
                }
            }
        }
        return cn;
    }

}
