package com.umarabdul.util.wordlist;

import java.io.*;
import java.util.ArrayList;


/**
* Wordlist is a class for reading a wordlist file, one word per line, in a memory efficient manner.
* Useful when working with large wordlist files.
*
* @author Umar Abdul
* @version 1.0
* Date: 01/Sep/2020
*/

public class Wordlist implements Runnable{

  private int bufferSize;
  private BufferedReader reader;
  private ArrayList<String> buffer;
  private boolean alive;

  /**
  * Wordlist's constructor.
  * @param filename Name of file to read.
  * @throws IOException on IO error.
  */
  public Wordlist(String filename) throws IOException{

    reader = new BufferedReader(new FileReader(new File(filename)));
    buffer = new ArrayList<String>();
    bufferSize = 1000;
    alive = false;
  }

  /**
  * Check if the wordlist reader is running.
  * @return {@code true} if the reader is active.
  */
  public boolean isAlive(){
    return alive;
  }

  /**
  * Define the maximum number of words to load at a time.
  * @param bufferSize Max number of words.
  */
  public void setBufferSize(int bufferSize){
    this.bufferSize = Math.max(1, bufferSize);
  }

  /**
  * Remove and return a word from the wordlist output queue.
  * @return Obtained word, {@code null} if the wordlist is exhausted.
  */
  public synchronized String getWord(){

    if (buffer.size() != 0)
      return buffer.remove(0);
    return null;
  }

  /**
  * Obtain the ArrayList in which words read are stored.
  * Warning: Always use the {@code ArrayList.remove()} method to obtain a word, otherwise the
  * reader will block once the queue reached it's defined maximum size.
  * @return ArrayList of words.
  */
  public ArrayList<String> getOuputQueue(){
    return buffer;
  }

  /**
  * Stop the wordlist reader.
  */
  public void kill(){
    alive = false;
  }

  /**
  * The wordlist reader.
  */
  @Override
  public void run(){

    if (alive)
      return;
    alive = true;
    String word = null;
    try{
      while(alive){
        word = reader.readLine();
        if (word == null)
          break;
        buffer.add(word);
        while (buffer.size() >= bufferSize)
          Thread.sleep(50);
      }
    }catch(InterruptedException|IOException e){
      e.printStackTrace();
    }
    alive = false;
  }

  /**
  * Non-blocking method that starts the reader in a separate thread.
  */
  public void start(){

    Thread t = new Thread(this);
    t.start();
  }

}

