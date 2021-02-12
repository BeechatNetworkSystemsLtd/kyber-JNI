package network.beechat;

public class Kyber512_90s {

    static {
        System.loadLibrary("libkyber512_90s_ref_jni.so");
    }

    // Declare a native methods of kyber512_90s
    private native int crypto_kem_keypair(char []pk, char []sk);
    private native int crypto_kem_enc(char []ct, char []ss, char []pk);
    private native int crypto_kem_dec(char []ss, char []ct, char []sk);

}

