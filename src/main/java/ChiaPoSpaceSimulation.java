import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class ChiaPoSpaceSimulation {
    private static final int SECTOR_SIZE = 32; // 32 bytes per BLOCK
    private static final int BLOCK_SIZE = 108 * 1024 * 1024; // 108 MB per BLOCK
    private static final int MIN_DIFFICULTY = 190; // minimum difficulty for valid proofs



    private static final int PROOFS_PER_BLOCK = 512; // number of proofs per BLOCK
    private static final int HASH_SIZE = 32; // 32 byte hash

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(10000);

        long start = System.currentTimeMillis();

        int numBLOCKs = 10; // number of BLOCKs to create
        byte[][] BLOCKs = new byte[numBLOCKs][BLOCK_SIZE];
        Random random = new Random();

        for (int i = 0; i < numBLOCKs; i++) {
            byte[] BLOCKSeed = new byte[SECTOR_SIZE];
            random.nextBytes(BLOCKSeed);

            for (int j = 0; j < PROOFS_PER_BLOCK; j++) {
                byte[] challenge = generateChallenge();
                byte[] proof = generateProof(BLOCKSeed, challenge);
                byte[] hash = hash(proof);

                if (isValidProof(hash)) {
                    System.out.println("PoSpace proof found: " + Arrays.toString(proof));
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("PoSpace mining time: " + (end - start) + "ms");
    }

    private static byte[] generateChallenge() {
        byte[] challenge = new byte[HASH_SIZE];
        Random random = new Random();
        random.nextBytes(challenge);
        return challenge;
    }

    private static byte[] generateProof(byte[] BLOCKSeed, byte[] challenge) {
        byte[] proof = new byte[SECTOR_SIZE];
        byte[] input = new byte[SECTOR_SIZE + HASH_SIZE];

        for (int i = 0; i < SECTOR_SIZE; i++) {
            input[i] = (byte) (BLOCKSeed[i] ^ challenge[i]);
        }
        for (int i = 0; i < HASH_SIZE; i++) {
            input[i + SECTOR_SIZE] = (byte) (challenge[i] ^ i);
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        byte[] hash = input;
        for (int i = 0; i < 21; i++) {
            hash = digest.digest(hash);
        }

        System.arraycopy(hash, 0, proof, 0, SECTOR_SIZE);
        return proof;
    }

    private static byte[] hash(byte[] data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        return digest.digest(data);
    }

    private static boolean isValidProof(byte[] hash) {
        for (int i = 0; i < MIN_DIFFICULTY; i++) {
            if (hash[i] != 0) {
                return false;
            }
        }
        return true;
    }
}
