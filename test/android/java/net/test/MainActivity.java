package net.test;

import java.io.*;
import java.util.Arrays;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import network.beechat.*;
import android.text.method.ScrollingMovementMethod;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Base58 b58 = new Base58();
        String std_output = "";
        byte[] alice_pk = new byte[Kyber512.KYBER_PUBLICKEYBYTES];
        byte[] alice_sk = new byte[Kyber512.KYBER_SECRETKEYBYTES];
        byte[] bob_pk = new byte[Kyber512.KYBER_PUBLICKEYBYTES];
        byte[] bob_sk = new byte[Kyber512.KYBER_SECRETKEYBYTES];
        byte[] alice_skey = new byte[Kyber512.KYBER_SSBYTES];
        byte[] bob_skey = new byte[Kyber512.KYBER_SSBYTES];
        byte[] ct = new byte[Kyber512.KYBER_CIPHERTEXTBYTES];

        // Step 1:
        int rc = Kyber512.crypto_kem_keypair(alice_pk, alice_sk);
        rc = Kyber512.crypto_kem_keypair(bob_pk, bob_sk);

        String testOutput = new String(b58.encode(alice_pk));
        std_output += "===Kyber512===\nAlice's public key:\n" + testOutput + "\n";
        createFile("alice.public", testOutput);

        testOutput = new String(b58.encode(alice_sk));
        std_output += "\nAlice's secret key:\n" + testOutput + "\n";
        createFile("alice.secret", testOutput);

        testOutput = new String(b58.encode(bob_pk));
        std_output += "\nBob's public key:\n" + testOutput + "\n";
        createFile("bob.public", testOutput);

        testOutput = new String(b58.encode(bob_sk));
        std_output += "\nBob's secret key:\n" + testOutput + "\n";
        createFile("bob.secret", testOutput);

        // Step 2:
        Kyber512.crypto_kem_enc(ct, bob_skey, b58.decode(readFile("alice.public")));
        // Step 3:
        Kyber512.crypto_kem_dec(alice_skey, ct, alice_sk);

        testOutput = new String(b58.encode(alice_skey));
        std_output += "\nAlice's skey: " + testOutput + "\n";
        testOutput = new String(b58.encode(bob_skey));
        std_output += "\nBob's skey: " + testOutput + "\n";

        // Step 4:
        std_output += "\nResult:\n";
        if (Arrays.equals(alice_skey, bob_skey)) {
            std_output += "Success!\n";
        } else {
            std_output += "Failed.\n";
        }

        // ----Kyber_90s----:
        // Step 1:
        rc = Kyber512_90s.crypto_kem_keypair(alice_pk, alice_sk);
        rc = Kyber512_90s.crypto_kem_keypair(bob_pk, bob_sk);

        testOutput = new String(b58.encode(alice_pk));
        std_output += "\n\n===Kyber512_90s===\n\nAlice's public key:\n" + testOutput + "\n";
        createFile("alice.public", testOutput);

        testOutput = new String(b58.encode(alice_sk));
        std_output += "\nAlice's secret key:\n" + testOutput + "\n";
        createFile("alice.secret", testOutput);

        testOutput = new String(b58.encode(bob_pk));
        std_output += "\nBob's public key:\n" + testOutput + "\n";
        createFile("bob.public", testOutput);

        testOutput = new String(b58.encode(bob_sk));
        std_output += "\nBob's secret key:\n" + testOutput + "\n";
        createFile("bob.secret", testOutput);

        // Step 2:
        Kyber512_90s.crypto_kem_enc(ct, bob_skey, b58.decode(readFile("alice.public")));
        // Step 3:
        Kyber512_90s.crypto_kem_dec(alice_skey, ct, alice_sk);

        testOutput = new String(b58.encode(alice_skey));
        std_output += "\nAlice's skey: " + testOutput + "\n";
        testOutput = new String(b58.encode(bob_skey));
        std_output += "\nBob's skey: " + testOutput + "\n";

        // Step 4:
        std_output += "\nResult:\n";
        if (Arrays.equals(alice_skey, bob_skey)) {
            std_output += "Success!\n";
        } else {
            std_output += "Failed.\n";
        }

        TextView text = (TextView)findViewById(R.id.my_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(std_output);
    }

    public void createFile(String path, String value) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(
                    new File(
                        getFilesDir() + File.separator + path
                    )
                )
            );
            bufferedWriter.write(value);
            bufferedWriter.close();
        } catch (IOException ex){}
    }

    public String readFile(String path) {
        String value = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(
                new FileReader(
                    new File(
                        getFilesDir() + File.separator + path
                    )
                )
            );
            value = bufferedReader.readLine();
            //Kyber512.crypto_kem_enc(ct, bob_skey, b58.decode(bufferedReader.readLine()));
            bufferedReader.close();
        } catch (FileNotFoundException ex) {} catch (IOException ex2) {}

        return value;
    }
}

