package net.test;

import java.io.*;
import java.util.Scanner;
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

        Scanner sc;
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
        std_output += "Alice's public key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("alice.public", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        testOutput = new String(b58.encode(alice_sk));
        std_output += "\nAlice's secret key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("alice.secret", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        testOutput = new String(b58.encode(bob_pk));
        std_output += "\nBob's public key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("bob.public", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        testOutput = new String(b58.encode(bob_sk));
        std_output += "\nBob's secret key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("bob.secret", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        // Step 2:
        try {
            sc = new Scanner(new File("alice.public"));
            Kyber512.crypto_kem_enc(ct, bob_skey, b58.decode(sc.nextLine()));
        } catch (FileNotFoundException ex) {}
        // Step 3:
        Kyber512.crypto_kem_dec(alice_skey, ct, alice_sk);

        testOutput = new String(b58.encode(alice_skey));
        std_output += "\nAlice's skey: " + testOutput + "\n";
        testOutput = new String(b58.encode(bob_skey));
        std_output += "\nBob's skey: " + testOutput + "\n";

        // Step 4:
        std_output += "\nResult:\n";
        if (!bob_skey.equals(alice_skey)) {
            std_output += "Success!\n";
        } else {
            std_output += "Failed.\n";
        }

        // Step 1:
        rc = Kyber512_90s.crypto_kem_keypair(alice_pk, alice_sk);
        rc = Kyber512_90s.crypto_kem_keypair(bob_pk, bob_sk);

        testOutput = new String(b58.encode(alice_pk));
        std_output += "Alice's public key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("alice.public", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}
        testOutput = new String(b58.encode(alice_sk));
        std_output += "\nAlice's secret key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("alice.secret", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        testOutput = new String(b58.encode(bob_pk));
        std_output += "\nBob's public key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("bob.public", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}
        testOutput = new String(b58.encode(bob_sk));
        std_output += "\nBob's secret key:\n" + testOutput + "\n";
        try {
            FileWriter writer = new FileWriter("bob.secret", false);
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        // Step 2:
        try {
            sc = new Scanner(new File("alice.public"));
            Kyber512_90s.crypto_kem_enc(ct, bob_skey, b58.decode(sc.nextLine()));
        } catch (FileNotFoundException ex) {}
        // Step 3:
        Kyber512_90s.crypto_kem_dec(alice_skey, ct, alice_sk);

        testOutput = new String(b58.encode(alice_skey));
        std_output += "\nAlice's skey: " + testOutput + "\n";
        testOutput = new String(b58.encode(bob_skey));
        std_output += "\nBob's skey: " + testOutput + "\n";

        // Step 4:
        std_output += "\nResult:\n";
        if (!bob_skey.equals(alice_skey)) {
            std_output += "Success!\n";
        } else {
            std_output += "Failed.\n";
        }

        TextView text = (TextView)findViewById(R.id.my_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(std_output);
    }
}

