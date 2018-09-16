package net.jhoobin.bouncycastle.jcajce.provider.asymmetric.rsa;

import net.jhoobin.bouncycastle.crypto.params.RSAKeyParameters;

import java.security.interfaces.RSAPublicKey;

/**
 * utility class for converting java.security RSA objects into their
 * net.jhoobin.bouncycastle.crypto counterparts.
 */
public class RSAUtil {
    static RSAKeyParameters generatePublicKeyParameter(
            RSAPublicKey key) {
        return new RSAKeyParameters(false, key.getModulus(), key.getPublicExponent());

    }

}
