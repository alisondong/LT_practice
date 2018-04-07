import java.util.Scanner;

public class A5_1 {
	
	    static int REGSIZE1 = 19;
	    static int REGSIZE2 = 22;
	    static int REGSIZE3 = 23;

	    static int[] reg1 = new int[REGSIZE1];
	    static int[] reg2 = new int[REGSIZE2];
	    static int[] reg3 = new int[REGSIZE3];

	    static String key;

	    public static void main(String[] args) {
	        // default key argument
	        key = "11011100 11010011 10010010 00000111 10101010 10101100 01100110 11001000".replaceAll(" ", "").replaceAll("\n", "");

	        // default run through to show functionality without requiring user input
//	        defaultRunThrough();

	        // capture user input for control
	        Scanner scanner = new Scanner(System.in);
	        String input;
	        printHomepage();
	        // main control loop
	        while (!(input = scanner.nextLine()).equals("0")) {
	            switch (input) {
	                case "0": break;
	                case "1": System.out.println("Enter plain text to encrypt: ");
	                    String plainText = scanner.nextLine();
	                    System.out.printf("%s encrypted is: %s\n", plainText, encrypt(plainText));
	                    break;
	                case "2": System.out.println("Enter cipher text to decrypt: ");
	                    String cipherText = scanner.nextLine();
	                    System.out.printf("%s decrypted is: %s\n", cipherText, decrypt(cipherText));
	                    break;
	                case "3": System.out.println("Enter new 64 bit key: ");
	                    String newKey = scanner.nextLine().replaceAll(" ", "").replaceAll("\n", "");
	                    if(newKey.length() != 64){
	                        System.out.println("Invalid input: Key must be 64 bits.");
	                        break;
	                    }
	                    else
	                        key = newKey;
	                    break;
	                default:
	                    System.out.println("Invalid input. Please enter 1, 2, 3, or 0");
	                    break;
	            }
	            printHomepage();
	        }
	        scanner.close();
	        System.out.println("Goodbye");
	    }

	    private static String decrypt(String cipherText) {
	        registerSetup();
	        String plainText = "";
	        String plainTextBinary = "";

	        String key = generateKey(cipherText.length());

	        for (int i = 0; i < cipherText.length(); i++) {
	            plainTextBinary += Character.getNumericValue(cipherText.charAt(i)) ^ Character.getNumericValue(key.charAt(i));
	        }

	        for (int i = 0; i < plainTextBinary.length() / 8; i++) {
	            int charBinary = Integer.parseInt(plainTextBinary.substring(8 * i, (i + 1) * 8), 2);
	            plainText += (char) charBinary;
	        }

	        return plainText;
	    }

	    private static String encrypt(String plainText) {
	        registerSetup();
	        // construct binary representation of plainText
	        String cipherText = "";
	        String plainTextBinary = "";
	        for (char c : plainText.toCharArray()) {
	            String charBinary = Integer.toBinaryString(c);
	            // ensure binary representation has 8 digits (prepend 0s if necessary)
	            charBinary = String.format("%08d", Integer.parseInt(charBinary));
	            plainTextBinary += charBinary;
	        }

	        // generate keystream
	        String key = generateKey(plainTextBinary.length());

	        for (int i = 0; i < plainTextBinary.length(); i++) {
	            cipherText += Character.getNumericValue(plainTextBinary.charAt(i)) ^ Character.getNumericValue(key.charAt(i));
	        }

	        return cipherText;
	    }

	    /**
	     * Populates reg1, reg2, and reg3 with key values
	     */
	    private static void registerSetup() {
	        // split key up into three registers
	        for (int i = 0; i < 64; i++) {
	            if (i < REGSIZE1) {
	                reg1[i] = Character.getNumericValue(key.charAt(i));
	            } else if (i < REGSIZE1 + REGSIZE2) {
	                reg2[i - REGSIZE1] = Character.getNumericValue(key.charAt(i));
	            } else {
	                reg3[i - (REGSIZE1 + REGSIZE2)] = Character.getNumericValue(key.charAt(i));
	            }
	        }
	    }

	    private static String generateKey(int length) {
	        String keyStream = "";
	        for (int i = 0; i < length; i++) {
	            stepRegisters();
	            keyStream += reg1[REGSIZE1 - 1] ^ reg2[REGSIZE2 - 1] ^ reg3[REGSIZE3 - 1];
	        }
	        return keyStream;
	    }

	    private static void stepRegisters() {
	        // determine which registers step
	        int reg1ComparisonBit = reg1[8];
	        int reg2ComparisonBit = reg2[10];
	        int reg3ComparisonBit = reg1[10];
	        // calculate majority bit
	        int majorityBit = (reg1ComparisonBit + reg2ComparisonBit + reg3ComparisonBit >= 2) ? 1 : 0;
	        // shift if register has majority bit
	        if (reg1ComparisonBit == majorityBit) {
	            // step reg1
	            int newBit = reg1[13] ^ reg1[16] ^ reg1[17] ^ reg1[18];
	            for (int i = REGSIZE1 - 2; i >= 0; i--) {
	                reg1[i + 1] = reg1[i];
	            }
	            reg1[0] = newBit;
	        }
	        if (reg2ComparisonBit == majorityBit) {
	            // step reg2
	            int newBit = reg2[20] ^ reg2[21];
	            for (int i = REGSIZE2 - 2; i >= 0; i--) {
	                reg2[i + 1] = reg2[i];
	            }
	            reg2[0] = newBit;

	        }
	        if (reg3ComparisonBit == majorityBit) {
	            // step reg3
	            int newBit = reg3[7] ^ reg3[20] ^ reg3[21] ^ reg3[22];
	            for (int i = REGSIZE3 - 2; i >= 0; i--) {
	                reg3[i + 1] = reg3[i];
	            }
	            reg3[0] = newBit;
	        }
	    }

	    private static void printHomepage() {
	        System.out.println("To encrypt, enter 1:");
	        System.out.println("To decrypt, enter 2:");
	        System.out.println("To change key, enter 3:");
	        System.out.println("To quit, enter 0:");
	    }

//	    private static void defaultRunThrough(){
//	        System.out.println("Beginning default run through with default key\nEncrypting \"Hello World!\"");
	//
//	        String plainText = "Hello World!";
//	        String cipherText = encrypt(plainText);
//	        System.out.println(plainText + " encrypted is: " + cipherText);
	//
//	        System.out.println("Decrypting " + cipherText);
//	        plainText = decrypt(cipherText);
//	        System.out.println(cipherText + " decrypted is: " + plainText);
//	        System.out.println("Ending default run through.\n");
	//
//	    }

//	    // for debug purposes
//	    private static void printRegisters() {
//	        System.out.printf("reg1: [");
//	        for (int i : reg1)
//	            System.out.printf("%d, ", i);
//	        System.out.printf("]\nreg2: [");
//	        for (int i : reg2)
//	            System.out.printf("%d, ", i);
//	        System.out.printf("]\nreg3: [");
//	        for (int i : reg3)
//	            System.out.printf("%d, ", i);
//	        System.out.printf("]\n");
//	    }
	}


}
