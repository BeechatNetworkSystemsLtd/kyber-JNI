package network.beechat;

public class Kyber512 {

    static {
        System.loadLibrary("libkyber512_ref_jni.so");
    }

    // Declare a native methods of kyber512
    private native int crypto_kem_keypair(char []pk, char []sk);
    private native int crypto_kem_enc(char []ct, char []ss, char []pk);
    private native int crypto_kem_dec(char []ss, char []ct, char []sk);

}

