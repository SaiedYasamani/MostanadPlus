package net.jhoobin.bouncycastle.jcajce.provider.asymmetric.rsa;

import net.jhoobin.bouncycastle.asn1.ASN1Encoding;
import net.jhoobin.bouncycastle.asn1.ASN1ObjectIdentifier;
import net.jhoobin.bouncycastle.asn1.DERNull;
import net.jhoobin.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import net.jhoobin.bouncycastle.asn1.x509.AlgorithmIdentifier;
import net.jhoobin.bouncycastle.asn1.x509.DigestInfo;
import net.jhoobin.bouncycastle.crypto.AsymmetricBlockCipher;
import net.jhoobin.bouncycastle.crypto.CipherParameters;
import net.jhoobin.bouncycastle.crypto.Digest;
import net.jhoobin.bouncycastle.crypto.digests.SHA1Digest;
import net.jhoobin.bouncycastle.crypto.encodings.PKCS1Encoding;
import net.jhoobin.bouncycastle.crypto.engines.RSABlindedEngine;
import net.jhoobin.bouncycastle.util.Arrays;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;

public class DigestSignatureSpi
        extends SignatureSpi {
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private AlgorithmIdentifier algId;

    public DigestSignatureSpi(
            ASN1ObjectIdentifier objId,
            Digest digest,
            AsymmetricBlockCipher cipher) {
        this.digest = digest;
        this.cipher = cipher;
        this.algId = new AlgorithmIdentifier(objId, DERNull.INSTANCE);
    }

    public void engineInitVerify(
            PublicKey publicKey)
            throws InvalidKeyException {
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new InvalidKeyException("Supplied key (" + getType(publicKey) + ") is not a RSAPublicKey instance");
        }

        CipherParameters param = RSAUtil.generatePublicKeyParameter((RSAPublicKey) publicKey);

        digest.reset();
        cipher.init(false, param);
    }

    public void engineInitSign(
            PrivateKey privateKey)
            throws InvalidKeyException {
        throw new UnsupportedOperationException("stub");
    }

    private String getType(
            Object o) {
        if (o == null) {
            return null;
        }

        return o.getClass().getName();
    }

    public void engineUpdate(
            byte b)
            throws SignatureException {
        digest.update(b);
    }

    public void engineUpdate(
            byte[] b,
            int off,
            int len)
            throws SignatureException {
        digest.update(b, off, len);
    }

    public void engineUpdate(
            byte[] b
    ) throws SignatureException {
        digest.update(b, 0, b.length);
    }


    public byte[] engineSign()
            throws SignatureException {
        throw new UnsupportedOperationException("stub");
    }

    public boolean engineVerify(
            byte[] sigBytes)
            throws SignatureException {
        byte[] hash = new byte[digest.getDigestSize()];

        digest.doFinal(hash, 0);

        byte[] sig;
        byte[] expected;

        try {
            sig = cipher.processBlock(sigBytes, 0, sigBytes.length);
            expected = derEncode(hash);
        } catch (Exception e) {
            return false;
        }

        if (sig.length == expected.length) {
            return Arrays.constantTimeAreEqual(sig, expected);
        } else if (sig.length == expected.length - 2)  // NULL left out
        {
            int sigOffset = sig.length - hash.length - 2;
            int expectedOffset = expected.length - hash.length - 2;

            expected[1] -= 2;      // adjust lengths
            expected[3] -= 2;

            int nonEqual = 0;

            for (int i = 0; i < hash.length; i++) {
                nonEqual |= (sig[sigOffset + i] ^ expected[expectedOffset + i]);
            }

            for (int i = 0; i < sigOffset; i++) {
                nonEqual |= (sig[i] ^ expected[i]);  // check header less NULL
            }

            return nonEqual == 0;
        } else {
            Arrays.constantTimeAreEqual(expected, expected);  // keep time "steady".

            return false;
        }
    }

    public void engineSetParameter(
            AlgorithmParameterSpec params) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }


    public void engineSetParameter(
            String param,
            Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated
     */
    public Object engineGetParameter(
            String param) {
        return null;
    }

    public AlgorithmParameters engineGetParameters() {
        return null;
    }

    private byte[] derEncode(
            byte[] hash)
            throws IOException {
        if (algId == null) {
            // For raw RSA, the DigestInfo must be prepared externally
            return hash;
        }

        DigestInfo dInfo = new DigestInfo(algId, hash);

        return dInfo.getEncoded(ASN1Encoding.DER);
    }

    static public class SHA1
            extends DigestSignatureSpi {
        public SHA1() {
            super(OIWObjectIdentifiers.idSHA1, new SHA1Digest(), new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

}
