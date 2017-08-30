package com.tri.airr.hero;

/**
 * Created by Daniel on 8/29/2017.
 */

public class CommandBytes {
    /*
    Put base byte array commands here to be called from other views within the app
    Have a temp byte array command created that duplicates one of the requested ones that way it can be incrimented and changed from within the user input faster
     */

    public  byte[] turnOn = {0x01, 0x00, 0x01};
    public  byte[] maxFlexion = {0x03, 0x03, 0x02};
    public  byte [] maxExtension = {0x03,0x03, 0x01};
    public byte[] auto = {(byte)0x03, (byte)0x04, (byte)0x02};
    public byte[] autoThreshhold = {0x05,0x06,0x06};
}