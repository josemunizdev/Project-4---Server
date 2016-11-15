import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JoseM on 11/12/2016.
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        PrintWriter outToClient = null;
        BufferedReader inFromClient = null;
        BufferedReader fileReader = null;
        PrintWriter fileWriter = null;
        String line;
        String fromClient;
        String[] parts = null;
        String[] clientParts = null;
        ArrayList<String> fileList = new ArrayList(); //when creating new user, add user info to list

        Pattern username = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern password = Pattern.compile("[#&$*]");
        Pattern passwordUp = Pattern.compile("[A-Z]");
        Pattern passwordDig = Pattern.compile("[0-9]");


        String filePath = new String("C:\\Users\\JoseM\\Desktop\\" +
                "cs180\\Projects\\Project4\\Server\\database.txt");

        //change the file path to check on your side
        try {
            fileReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            System.out.println("ERROR 404: FILE NOT FOUND!");
        }

        while ((line = fileReader.readLine()) != null) {
            fileList.add(line);
            parts = line.split(":");
            System.out.println("Added user: " + parts[0]);
            System.out.println("Read in user: " + line);
            //method to say user already logged in/registered
        }

        try {
            System.out.println("Creating Socket");
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not create socket");
        }

        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Listening on socket 5000");

        try {
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outToClient = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fromClient = inFromClient.readLine();
        clientParts = fromClient.split("--");
        //message format check
        boolean counter = true;
        if (clientParts[0].equals("CREATENEWUSER")) { //parts are being formatted correctly
            for (int i = 0; i < fileList.size(); i++) {
                parts = fileList.get(i).split(":");
                if (parts[0].equals(clientParts[1])) {
                    outToClient.println("RESPONSE--CREATENEWUSER--USERALREADYEXISTS");
                    counter = false;
                    break;
                }
            }
            Matcher user = username.matcher(clientParts[1]);
            Matcher pwChar = username.matcher(clientParts[2]);
            Matcher pwSymb = password.matcher(clientParts[2]);
            Matcher pwUp = passwordUp.matcher(clientParts[2]);
            Matcher pwDig = passwordDig.matcher(clientParts[2]);
            boolean u = user.find();
            boolean p1 = pwChar.find();
            boolean p2 = pwSymb.find();
            boolean p3 = pwUp.find();
            boolean p4 = pwDig.find();
            boolean pwCreds;
            if (p1 && p2 && p3 && p4) {
                pwCreds = true;
            } else {
                pwCreds = false;
            }
            if (clientParts[1].equals("") || clientParts[1].length() >= 10 || u == false) {
                outToClient.println("RESPONSE--CREATENEWUSER--INVALIDUSERNAME");
                counter = false;
            } else if (clientParts[2].equals("") || clientParts[2].length() >= 10 || pwCreds == false) {
                outToClient.println("RESPONSE--CREATENEWUSER--INVALIDPASSWORD");
                counter = false;
            }
        } else if (counter) {
            outToClient.println("RESPONSE--CREATENEWUSER--SUCCESS");
            fileWriter.println(clientParts[1] + ":" + clientParts[2] + ":0:0:0");
            fileList.add(clientParts[1] + ":" + clientParts[2] + ":0:0:0");
        }



        outToClient.close();
        inFromClient.close();

        //comment

    }

}