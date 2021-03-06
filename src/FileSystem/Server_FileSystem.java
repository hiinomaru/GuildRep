package FileSystem;

import java.io.*;
import com.google.gson.Gson;

public class Server_FileSystem {
    private class Stream_info{
        Stream_info(String id, ReverseLineInputStream str){
            ID = id;
            FIS = str;
        }
        String ID;
        ReverseLineInputStream FIS;
    }
    Stream_info[] Streams; // 4 streams for dialogs
    int OldStream;
    Gson gson = new Gson();
    File Dialog_info;
    File User_info;

    public void AddUser(User user) throws IOException {
        FileWriter Users_info = new FileWriter(User_info, true);
        gson.toJson(user, Users_info);
        Users_info.write("\n");
        Users_info.close();
    }

    public Server_FileSystem() throws IOException {
        Streams = new Stream_info[3]; // 4 streams for dialogs
        OldStream = 0;
        Dialog_info = new File("sources_server/Dialogs_info.txt");
        User_info = new File("sources_server/Users_info.txt");
        FileWriter out = new FileWriter(Dialog_info, true);
        out.close();
        out = new FileWriter(User_info, true);
        out.close();
    }

    public Message ReadDialog(String ID) throws IOException {
        int c;
        String Result = "";
        Stream_info Stream = null;
        for (int i = 0; i < 4; i++) {
            if (Streams[i] == null) {
                Streams[i] = new Stream_info(ID, new ReverseLineInputStream(new File("sources_server/" + ID + ".txt")));
                Stream = Streams[i];
                break;
            }
            else if (Streams[i].ID == ID) {
                Stream = Streams[i];
                break;
            }
        }
        if(Stream == null) {
            Streams[OldStream].FIS.close();
            Streams[OldStream] = new Stream_info(ID, new ReverseLineInputStream(new File("sources_server/" + ID + ".txt")));
            OldStream = (OldStream + 1) % 4;
        }

        while(((c=Stream.FIS.read())!= -1)&&(c != 125)){
            Result += (char)c;
            //System.out.print((char)c);
        }
        if(c == -1){
            Stream.FIS.close();
            Stream.ID = "";
            return null;
        }
        else {
            Result += '}';
            //System.out.print(Result);
            return gson.fromJson(Result, Message.class);
        }
    } //read last message from FileSystem.Dialog ID

    public Message ReadDialog(String ID, String Last_msg_time) throws IOException {
        int c;
        String Result = "";
        Stream_info Stream = null;
        for (int i = 0; i < 4; i++) {
            if (Streams[i] == null) {
                Streams[i] = new Stream_info(ID, new ReverseLineInputStream(new File("sources_server/" + ID + ".txt")));
                Stream = Streams[i];
                break;
            }
            else if (Streams[i].ID == ID) {
                Stream = Streams[i];
                break;
            }
        }
        if(Stream == null) {
            Streams[OldStream].FIS.close();
            Streams[OldStream] = new Stream_info(ID, new ReverseLineInputStream(new File("sources_server/" + ID + ".txt")));
            OldStream = (OldStream + 1) % 4;
        }
        Last_msg_time = "{\"Time\":\"" + Last_msg_time;
        while(!Result.equals(Last_msg_time)){
            while(((c=Stream.FIS.read())!= -1)&&(c != 123)){}
            Result = "{";
            for (int i = 0; i < 13; i++){
                Result += (char)Stream.FIS.read();
            }
        }
        Result ="{";
        while(((c=Stream.FIS.read())!= -1)&&(c != 123)){}
        while(((c=Stream.FIS.read())!= -1)&&(c != 125)){
            Result += (char)c;
        }
        if(c == -1){
            Stream.FIS.close();
            Stream.ID = "";
            return null;
        }
        else {
            Result += '}';
            //System.out.print(Result);
            return gson.fromJson(Result, Message.class);
        }
    }  //read message before time = Last_msg_time from FileSystem.Dialog ID

    public Message ReadDialogByTime(String ID, String Msg_time) throws IOException {
        int c;
        String Result = "";
        Stream_info Stream = null;
        for (int i = 0; i < 4; i++) {
            if (Streams[i] == null) {
                Streams[i] = new Stream_info(ID, new ReverseLineInputStream(new File("sources_server/" + ID + ".txt")));
                Stream = Streams[i];
                break;
            }
            else if (Streams[i].ID == ID) {
                Stream = Streams[i];
                break;
            }
        }
        if(Stream == null) {
            Streams[OldStream].FIS.close();
            Streams[OldStream] = new Stream_info(ID, new ReverseLineInputStream(new File("sources_server/" + ID + ".txt")));
            OldStream = (OldStream + 1) % 4;
        }
        Msg_time = "{\"Time\":\"" + Msg_time;
        while(!Result.equals(Msg_time)){
            while(((c=Stream.FIS.read())!= -1)&&(c != 123)){}
            Result = "{";
            for (int i = 0; i < 13; i++){
                Result += (char)Stream.FIS.read();
            }
        }

        while(((c=Stream.FIS.read())!= -1)&&(c != 125)){
            Result += (char)c;
        }
        if(c == -1){
            Stream.FIS.close();
            Stream.ID = "";
            return null;
        }
        else {
            Result += '}';
            System.out.print(Result);
            return gson.fromJson(Result, Message.class);
        }
    } //read message, time = FileSystem.Message from FileSystem.Dialog ID

    public void CreateDialog(Dialog D) throws IOException {
        FileWriter out = new FileWriter("sources_server/" + String.valueOf(D.DialogID) + ".txt", true);
        out.close();
        FileWriter Dialogs_info = new FileWriter(Dialog_info, true);
        gson.toJson(D, Dialogs_info);
        Dialogs_info.write("\n");
        Dialogs_info.close();
    } // ..

    public void WriteDialog(Message M, String ID) throws IOException {
        FileWriter out = new FileWriter("sources_server/" + ID + ".txt", true);
        gson.toJson(M, out);
        out.write("\n");
        out.close();
    } // write M in FileSystem.Dialog ID
}
