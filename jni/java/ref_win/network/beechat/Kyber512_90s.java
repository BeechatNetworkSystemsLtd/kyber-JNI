package network.beechat;

public class Kyber512_90s {

    static {
        System.loadLibrary("kyber512_90s_ref_jni.dll");
    }

    // Declare a native methods of kyber512_90s
    private static native int crypto_kem_keypair(char []pk, char []sk);
    private static native int crypto_kem_enc(char []ct, char []ss, char []pk);
    private static native int crypto_kem_dec(char []ss, char []ct, char []sk);

}

