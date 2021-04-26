package network.beechat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class TestScript {

    public static void main(String[] args) throws IOException {

        Base58 b58 = new Base58();
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
        System.out.println("Alice's public key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("alice.public", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}
        testOutput = new String(b58.encode(alice_sk));
        System.out.println("\nAlice's secret key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("alice.secret", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        testOutput = new String(b58.encode(bob_pk));
        System.out.println("\nBob's public key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("bob.public", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}
        testOutput = new String(b58.encode(bob_sk));
        System.out.println("\nBob's secret key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("bob.secret", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        // Step 2: Load ciphertext key into ct, use alice.public to encapsulate, save key into bob_skey
        Scanner sc = new Scanner(new File("alice.public"));
        Kyber512.crypto_kem_enc(ct, bob_skey, b58.decode(sc.nextLine()));

        //write ct (ciphertext) to file. This would be sent to Alice.
        try (FileOutputStream fos = new FileOutputStream("ct.txt")) {
            fos.write(ct);
        }
        byte[] ct_loaded = Files.readAllBytes(Paths.get("ct.txt"));

        // Step 3: Alice uses the loaded ciphertext and her secret key to decapsulate the password into alice_skey
        Kyber512.crypto_kem_dec(alice_skey, ct_loaded, alice_sk);

        testOutput = new String(b58.encode(alice_skey));
        System.out.println("\nAlice's skey: " + testOutput);
        testOutput = new String(b58.encode(bob_skey));
        System.out.println("\nBob's skey: " + testOutput);

        // Step 4:
        System.out.println("\nResult:");
        if (!bob_skey.equals(alice_skey)) {
            System.out.println("Success!");
        } else {
            System.out.println("Failed.");
        }

        // Step 1:
        rc = Kyber512_90s.crypto_kem_keypair(alice_pk, alice_sk);
        rc = Kyber512_90s.crypto_kem_keypair(bob_pk, bob_sk);

        testOutput = new String(b58.encode(alice_pk));
        System.out.println("Alice's public key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("alice.public", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}
        testOutput = new String(b58.encode(alice_sk));
        System.out.println("\nAlice's secret key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("alice.secret", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        testOutput = new String(b58.encode(bob_pk));
        System.out.println("\nBob's public key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("bob.public", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}
        testOutput = new String(b58.encode(bob_sk));
        System.out.println("\nBob's secret key:\n" + testOutput);
        try (FileWriter writer = new FileWriter("bob.secret", false)) {
            writer.write(testOutput);
            writer.flush();
            writer.close();
        } catch (IOException ex){}

        // Step 2:
        sc = new Scanner(new File("alice.public"));
        Kyber512_90s.crypto_kem_enc(ct, bob_skey, b58.decode(sc.nextLine()));
        System.out.println("\nBob's ct:\n" + ct);
        // Step 3:
        Kyber512_90s.crypto_kem_dec(alice_skey, ct, alice_sk);

        testOutput = new String(b58.encode(alice_skey));
        System.out.println("\nAlice's skey: " + testOutput);
        testOutput = new String(b58.encode(bob_skey));
        System.out.println("\nBob's skey: " + testOutput);

        // Step 4:
        System.out.println("\nResult:");
        if (!bob_skey.equals(alice_skey)) {
            System.out.println("Success!");
        } else {
            System.out.println("Failed.");
        }

        System.exit(rc);
    }

}

