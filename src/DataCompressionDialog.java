import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class DataCompressionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCompress;
    private JButton buttonDecompress;
    private JTextField textField2;
    private JTextField textField1;

    private Node root;


   

    public static String convertFromBytes(byte[] bytes) {
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }

    public static byte[] convertToBytes(String binary) {
        int arrayLength = (int) Math.ceil((double) binary.length() / 8);
        byte[] bytes = new byte[arrayLength];
        int index = 0;
        int remainingBits = binary.length();
        while (remainingBits >= 8) {
            String byteString = binary.substring(index, index + 8);
            bytes[index / 8] = (byte) Integer.parseInt(byteString, 2);
            index += 8;
            remainingBits -= 8;
        }
        if (remainingBits > 0) {
            String byteString = binary.substring(index);
            byteString = String.format("%-8s", byteString).replace(' ', '0');
            bytes[arrayLength - 1] = (byte) Integer.parseInt(byteString, 2);
        }
        return bytes;
    }



    public DataCompressionDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCompress);
        this.setLocation(300, 200);
        this.getContentPane().setPreferredSize(new Dimension(500, 350));

        buttonCompress.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compress();
            }
        });

        buttonDecompress.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                decompress();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                decompress();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    class Node implements Comparable<Node> {
        char data;
        int frequency;
        private Node left;
        private Node right;

        public Node(char data, int frequency) {
            this.data = data;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }

        public Node getLeft() {
            return left;
        }

    }


    private void assign_zero_or_one(Node node, String code, Map<Character, String> huffmanCodes) {
        if (node != null) {
            if (node.getLeft() == null && node.right == null) {
                huffmanCodes.put(node.data, code);
            }
            assign_zero_or_one(node.left, code + "0", huffmanCodes);
            assign_zero_or_one(node.right, code + "1", huffmanCodes);
        }
    }
    Map<Character, String> huffmanCodes = new HashMap<>();

    private void compress() {
        File inputFile = new File(textField1.getText());
        String input = "";
        try {
            Scanner reader = new Scanner(inputFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                input += line;
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Input File Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<Character, Integer> frequencyCounter = new HashMap<>();
        for (char c : input.toCharArray()) {
            frequencyCounter.put(c, frequencyCounter.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> q = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyCounter.entrySet()) {
            q.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (q.size() > 1) {
            Node left = q.poll();
            Node right = q.poll();

            Node parent = new Node('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            q.add(parent);
        }

        root = q.poll();
        assign_zero_or_one(root, "", huffmanCodes);

        StringBuilder compressedString = new StringBuilder();
        for (char c : input.toCharArray()) {
            compressedString.append(huffmanCodes.get(c));
        }

        String output = String.valueOf(compressedString);
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream("compressed.bin"))) {
            outputStream.write(convertToBytes(output));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String readBinaryFileToString(String filePath) {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            StringBuilder binaryStringBuilder = new StringBuilder();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    binaryStringBuilder.append(convertFromBytes(new byte[]{buffer[i]}));
                }
            }

            return binaryStringBuilder.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private void decompress() {
        File inputFile = new File(textField2.getText());
        String input = readBinaryFileToString(inputFile.getPath());

        if (input == null) {
            JOptionPane.showMessageDialog(this, "Error reading from input file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String curr = "";
        String decompressedString = "";

        Map<String, Character> codes = new HashMap();
        for(Map.Entry<Character, String> entry : huffmanCodes.entrySet())
        {
            codes.put(entry.getValue(),entry.getKey());
        }
        for(int i = 0; i < input.length(); i++)
        {
            curr += input.charAt(i);
            if(codes.get(curr) != null)
            {
                decompressedString += codes.get(curr);
                curr="";
            }
        }
        File outputFile = new File("C:\\Users\\karum\\OneDrive\\Desktop\\untitled15 (2)\\untitled15\\decompressed.txt");
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(decompressedString);
            writer.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to output file!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static void main(String[] args) {
        DataCompressionDialog dialog = new DataCompressionDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
