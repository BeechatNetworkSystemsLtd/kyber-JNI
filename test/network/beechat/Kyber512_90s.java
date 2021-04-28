package network.beechat;

public class Kyber512_90s {

    static final int KYBER_PUBLICKEYBYTES = 800;
    static final int KYBER_SECRETKEYBYTES = 1632;
    static final int KYBER_SSBYTES = 32;
    static final int KYBER_CIPHERTEXTBYTES = 768;

    static {
        System.loadLibrary("libkyber512_90s_ref_jni");
    }

    // Declare a native methods of kyber512
    public static native int crypto_kem_keypair(byte []pk, byte []sk);
    public static native int crypto_kem_enc(byte []ct, byte []ss, byte []pk);
    public static native int crypto_kem_dec(byte []ss, byte []ct, byte []sk);

}

