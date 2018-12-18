import java.io.*;
import java.util.PriorityQueue;
import java.util.TreeMap;

/* Huffman coding , decoding */

public class  Huffman{
    static String interval="";
    static String codings="";
    static String encoding="";
    static String decoding="";
    Huffman()   {
        interval ="";
        codings="";
        encoding="";
        decoding="";
    }
            
    static final boolean readFromFile = false;
    static final boolean newTextBasedOnOldOne = false;

    static PriorityQueue<Node> nodes = new PriorityQueue<>((o1, o2) -> (o1.value < o2.value) ? -1 : 1);
    static TreeMap<Character, String> codes = new TreeMap<>();
    static String text = "";
    static String encoded = "";
    static String decoded = "";
    static int ASCII[] = new int[128];
    
    static String filepath;

    public static void clearOldData()   {
        encoded = "";
        decoded = "";
        interval = "";
        codings = "";
        encoding = "";
        decoding = "";
    }
    
    public static void process(String path) throws Exception {
        text = "";
        int k = path.lastIndexOf("/");
        if(k==-1)
            k = path.lastIndexOf("\\");
        filepath = path.substring(0, k);
        
        int c;
        FileInputStream in = null;
        try{
            in = new FileInputStream(path);
            System.out.println("file path is"+path);
            
            while((c = in.read()) != -1){
                text += (char)c;
                System.out.println((char)c);
            }
        }
        catch(Exception e){

        }
        handleNewText(text);
    }

    public static boolean handleNewText(String string) {
        clearOldData();
        text = string;
        // int oldTextLength = text.length();

            ASCII = new int[128];
            nodes.clear();
            codes.clear();
            encoded = "";
            decoded = "";
            System.out.println("Text: " + text);
            calculateCharIntervals(nodes, true);
            buildTree(nodes);
            generateCodes(nodes.peek(), "");
            printCodes();
            System.out.println("-- Encoding/Decoding --");
            encodeText();
            decodeText();
            return false;
    }

    // private static boolean IsSameCharacterSet() {
    //     boolean flag = true;
    //     for (int i = 0; i < text.length(); i++)
    //         if (ASCII[text.charAt(i)] == 0) {
    //             flag = false;
    //             break;
    //         }
    //     return flag;
    // }

    private static void decodeText() {
        decoded = "";
        Node node = nodes.peek();
        for (int i = 0; i < encoded.length(); ) {
            Node tmpNode = node;
            while (tmpNode.left != null && tmpNode.right != null && i < encoded.length()) {
                if (encoded.charAt(i) == '1')
                    tmpNode = tmpNode.right;
                else tmpNode = tmpNode.left;
                i++;
            }
            if (tmpNode != null)
                if (tmpNode.character.length() == 1)
                    decoded += tmpNode.character;
                else
                    System.out.println("Input not Valid");

        }
        System.out.println("Decoded Text: " + decoded);
        decoding = decoded;
    }

    private static void encodeText() {
        FileOutputStream out = null; 
            encoded = "";
            for (int i = 0; i < text.length(); i++) 
                encoded += codes.get(text.charAt(i));
            
            encoding= encoded;
            System.out.println("Encoded Text: " + encoded);
        try{
            out = new FileOutputStream(filepath+"/output.txt");
            out.write(encoded.getBytes());
            out.close();
        } catch(IOException e){}
    }

    private static void buildTree(PriorityQueue<Node> vector) {
        while (vector.size() > 1)
            vector.add(new Node(vector.poll(), vector.poll()));
    }

    private static void printCodes() {
        System.out.println("--- Printing Codes ---");
        codes.forEach((k, v) -> codings +=""+ k + " : " + v+"\n");
        System.out.println(codings);
    }

    private static void calculateCharIntervals(PriorityQueue<Node> vector, boolean printIntervals) {
        if (printIntervals) System.out.println("-- intervals --");

        for (int i = 0; i < text.length(); i++)
            ASCII[text.charAt(i)]++;

        for (int i = 0; i < ASCII.length; i++)
            if (ASCII[i] > 0) {
                vector.add(new Node(ASCII[i] / (text.length() * 1.0), ((char) i) + ""));
                if (printIntervals)
                    interval += "'" + ((char) i) + "' : " + ASCII[i] / (text.length() * 1.0) +"\n";
            }
        System.out.println(interval);
    }

    private static void generateCodes(Node node, String s) {
        if (node != null) {
            if (node.right != null)
                generateCodes(node.right, s + "1");

            if (node.left != null)
                generateCodes(node.left, s + "0");

            if (node.left == null && node.right == null)
                codes.put(node.character.charAt(0), s);
        }
    }
}

class Node {
    Node left, right;
    double value;
    String character;

    public Node(double value, String character) {
        this.value = value;
        this.character = character;
        left = null;
        right = null;
    }

    public Node(Node left, Node right) {
        this.value = left.value + right.value;
        character = left.character + right.character;
        if (left.value < right.value) {
            this.right = right;
            this.left = left;
        } else {
            this.right = left;
            this.left = right;
        }
    }
}