package vn.mk.eid.common.security;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import sun.security.x509.*;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class RSA {
    private static String default_algorithm = "RSA";
    private static String default_provider = "BC";
    private static String default_sign_algorithm = "SHA256withDSA";
    private static int default_size = 2048;
    private static int valid_days = 3650;

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
        AlgorithmId algo = new AlgorithmId(AlgorithmId.SHA_oid);
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

            java.security.cert.Certificate[] chain = new java.security.cert.Certificate[] {cert};
            ks.setKeyEntry(alias, kp.getPrivate(), password.toCharArray(), chain);
            ks.store(new FileOutputStream(file), password.toCharArray());

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(default_algorithm);
    }

    public static KeyPair generateKeyPair(String algorithm) {
        return generateKeyPair(algorithm, null);
    }

    public static KeyPair generateKeyPair(String algorithm, String provider) {
        return generateKeyPair(algorithm, provider, default_size);
    }

    public static KeyPair generateKeyPair(String algorithm, String provider, int size) {
        try {
            KeyPairGenerator kpg;
            if (provider == null)
                kpg = KeyPairGenerator.getInstance(algorithm);
            else
                kpg = KeyPairGenerator.getInstance(algorithm, provider);
            kpg.initialize(size, new SecureRandom());
            KeyPair kp = kpg.genKeyPair();
            return kp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static KeyPair generateKeyPair(int size) {
        try {
            SecureRandom sr = new SecureRandom();
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(size, sr);

            KeyPair kp = kpg.genKeyPair();
            return kp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException ex) {
            log.error("getPublicKey NoSuchAlgorithmException: " + ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            log.error("getPublicKey InvalidKeySpecException: " + ex.getMessage());
        }
        return publicKey;
    }

    public static PublicKey getPublicKey(String base64PublicKey, String provider) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", provider);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException ex) {
            log.error("getPublicKey NoSuchAlgorithmException: " + ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            log.error("getPublicKey InvalidKeySpecException: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("getPublicKey Exception: " + ex.getMessage());
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;

        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException ex) {
            log.error("getPrivateKey NoSuchAlgorithmException: " + ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            log.error("getPrivateKey InvalidKeySpecException: " + ex.getMessage());
        }
        return privateKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey, String provider) {
        PrivateKey privateKey = null;

        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", provider);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException ex) {
            log.error("getPrivateKey NoSuchAlgorithmException: " + ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            log.error("getPrivateKey InvalidKeySpecException: " + ex.getMessage());
        }catch (Exception ex) {
            log.error("getPrivateKey Exception: " + ex.getMessage());
        }
        return privateKey;
    }

    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
        return cipher.doFinal(data);
    }

    public static String encrypt(String data, String base64PublicKey) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(data.getBytes(), getPublicKey(base64PublicKey)));
    }

    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(data.getBytes(), publicKey));
    }

    public static String encrypt(byte[] data, String base64PublicKey) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(data, getPublicKey(base64PublicKey)));
    }

    public static byte[] encrypt2Bytes(String data, String base64PublicKey) throws Exception {
        return encrypt(data.getBytes(), getPublicKey(base64PublicKey));
    }

    public static byte[] encrypt2Bytes(String data, PublicKey publicKey) throws Exception {
        return encrypt(data.getBytes(), publicKey);
    }

    public static byte[] encrypt2Bytes(byte[] data, String base64PublicKey) throws Exception {
        return encrypt(data, getPublicKey(base64PublicKey));
    }
//    public static String decrypt(String base64Data) throws Exception {
//        return decrypt(Base64.getDecoder().decode(base64Data.getBytes()), serverPrivateKey);
//    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

        return new String(cipher.doFinal(data));
    }

    public static String decrypt(byte[] data, String base64PrivateKey) throws Exception {
        return decrypt(data, getPrivateKey(base64PrivateKey));
    }

    public static String decrypt(String base64Data, String base64PrivateKey) throws Exception {
        return decrypt(Base64.getDecoder().decode(base64Data.getBytes()), getPrivateKey(base64PrivateKey));
    }

    public static String decrypt(String base64Data, PrivateKey privateKey) throws Exception {
        return decrypt(Base64.getDecoder().decode(base64Data.getBytes()), privateKey);
    }

    public static byte[] decrypt2Bytes(byte[] data, PrivateKey privateKey) throws Exception {
        //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt2Bytes(byte[] data, String base64PrivateKey) throws Exception {
        return decrypt2Bytes(data, getPrivateKey(base64PrivateKey));
    }
    public static byte[] decrypt2Bytes(String base64Data, String base64PrivateKey) throws Exception {
        return decrypt2Bytes(base64Data.getBytes(), getPrivateKey(base64PrivateKey));
    }
    public static byte[] decrypt2Bytes(String base64Data, PrivateKey privateKey) throws Exception {
        return decrypt2Bytes(base64Data.getBytes(), privateKey);
    }

    public static String sign(String data, String base64PrivateKey) throws Exception {
        return sign(data, getPrivateKey(base64PrivateKey));
    }

    public static String sign(String data, PrivateKey privateKey) throws Exception {
        return sign(data, privateKey, "SHA256withRSA");
    }

    public static String sign(String data, PrivateKey privateKey, String signingAlgorithm) throws Exception {
        Signature privateSignature = Signature.getInstance(signingAlgorithm);
        privateSignature.initSign(privateKey);
        privateSignature.update(data.getBytes(StandardCharsets.UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    public static String sign(byte[] data, String base64PrivateKey) throws Exception {
        return sign(data, getPrivateKey(base64PrivateKey));
    }

    public static String sign(byte[] data, PrivateKey privateKey) throws Exception {
        return sign(data, privateKey, "SHA256withRSA");
    }

    public static String sign(byte[] data, PrivateKey privateKey, String signingAlgorithm) throws Exception {
        Signature privateSignature = Signature.getInstance(signingAlgorithm);
        privateSignature.initSign(privateKey);
        privateSignature.update(data);

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    public static String sign2Hex(String data, String base64PrivateKey) throws Exception {
        return sign2Hex(data, getPrivateKey(base64PrivateKey));
    }

    public static String sign2Hex(String data, PrivateKey privateKey) throws Exception {
        return sign2Hex(data, privateKey, "SHA256withRSA");
    }

    public static String sign2Hex(byte[] data, PrivateKey privateKey, String signingAlgorithm) throws Exception {
        Signature privateSignature = Signature.getInstance(signingAlgorithm);
        privateSignature.initSign(privateKey);
        privateSignature.update(data);

        byte[] signature = privateSignature.sign();
        return Hex.encodeHexString(signature);
    }

    public static String sign2Hex(byte[] data, String base64PrivateKey) throws Exception {
        return sign2Hex(data, getPrivateKey(base64PrivateKey));
    }

    public static String sign2Hex(byte[] data, PrivateKey privateKey) throws Exception {
        return sign2Hex(data, privateKey, "SHA256withRSA");
    }

    public static String sign2Hex(String data, PrivateKey privateKey, String signingAlgorithm) throws Exception {
        Signature privateSignature = Signature.getInstance(signingAlgorithm);
        privateSignature.initSign(privateKey);
        privateSignature.update(data.getBytes());

        byte[] signature = privateSignature.sign();
        return Hex.encodeHexString(signature);
    }

    public static String signHash(String data, String base64PrivateKey) throws Exception {
        String hash = SHA.sha256(data);
        return sign(hash, getPrivateKey(base64PrivateKey));
    }

    public static String signHash(String data, PrivateKey privateKey) throws Exception {
        String hash = SHA.sha256(data);
        return sign(hash, privateKey);
    }

    public static String signHash(String data, PrivateKey privateKey, String signingAlgorithm) throws Exception {
        String hash = SHA.sha256(data);
        return sign(hash, privateKey, signingAlgorithm);
    }

    public static String signHash2Hex(String data, String base64PrivateKey) throws Exception {
        String hash = SHA.sha256(data);
        return sign2Hex(hash, getPrivateKey(base64PrivateKey));
    }

    public static String signHash2Hex(String data, PrivateKey privateKey) throws Exception {
        String hash = SHA.sha256(data);
        return sign2Hex(hash, privateKey);
    }

    public static String signHash2Hex(String data, PrivateKey privateKey, String signingAlgorithm) throws Exception {
        String hash = SHA.sha256(data);
        return sign2Hex(hash, privateKey, signingAlgorithm);
    }

    public static boolean verify(String data, String signature, String base64PublicKey) throws Exception {
        return verify(data, signature, getPublicKey(base64PublicKey));
    }

    public static boolean verify(String data, String signature, PublicKey publicKey) throws Exception {
        return verify(data, signature, publicKey, "SHA256withRSA");
    }

    public static boolean verify(String data, String signature, PublicKey publicKey, String signingAlgorithm) throws Exception {
        Signature publicSignature = Signature.getInstance(signingAlgorithm);
        publicSignature.initVerify(publicKey);
        publicSignature.update(data.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

    public static boolean verifyHash(String data, String signature, String base64PublicKey) throws Exception {
        String hash = SHA.sha256(data);
        return verify(hash, signature, getPublicKey(base64PublicKey));
    }

    public static boolean verifyHash(String data, String signature, PublicKey publicKey) throws Exception {
        String hash = SHA.sha256(data);
        return verify(hash, signature, publicKey);
    }

    public static String getThumbPrint(PublicKey publicKey) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = publicKey.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
//        String digestHex = EncodingUtil.bytes2Hex(digest);
        String digestHex = Hex.encodeHexString(digest);
        return digestHex.toLowerCase();
    }

    public static String getThumbPrint(String base64PublicKey) throws NoSuchAlgorithmException {
        PublicKey publicKey = getPublicKey(base64PublicKey);
        return getThumbPrint(publicKey);
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
