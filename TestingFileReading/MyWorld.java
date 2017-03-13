import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
import java.io.*;
/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyWorld extends World 
{
    
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    Scanner scanner;
    int counter = 0;
    public MyWorld() throws Exception
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1); 
         scanner = new Scanner(new File("FWeights.txt"));
    }
    public void act()
    {
        counter++;
       if(counter ==100 && scanner.hasNextLine())
       {
           scanner.nextLine();
           String[][] theta1 = new String[3][4];
           theta1[0] = scanner.nextLine().split(" ");
           theta1[1] = scanner.nextLine().split(" ");
           theta1[2] = scanner.nextLine().split(" ");
           scanner.nextLine();
           String[][] theta2 = new String[4][4];
           theta2[0] = scanner.nextLine().split(" ");
           theta2[1] = scanner.nextLine().split(" ");
           theta2[2] = scanner.nextLine().split(" ");
           theta2[3] = scanner.nextLine().split(" ");
           scanner.nextLine();
           String[][] theta3 = new String[4][4];
           theta3[0] = scanner.nextLine().split(" ");
           theta3[1] = scanner.nextLine().split(" ");
           theta3[2] = scanner.nextLine().split(" ");
           theta3[3] = scanner.nextLine().split(" ");
           
           scanner.nextLine();
           String[][] theta11 = new String[3][4];
           theta11[0] = scanner.nextLine().split(" ");
           theta11[1] = scanner.nextLine().split(" ");
           theta11[2] = scanner.nextLine().split(" ");
           scanner.nextLine();
           String[][] theta22 = new String[4][4];
           theta22[0] = scanner.nextLine().split(" ");
           theta22[1] = scanner.nextLine().split(" ");
           theta22[2] = scanner.nextLine().split(" ");
           theta22[3] = scanner.nextLine().split(" ");
           scanner.nextLine();
           String[][] theta33 = new String[4][4];
           theta33[0] = scanner.nextLine().split(" ");
           theta33[1] = scanner.nextLine().split(" ");
           theta33[2] = scanner.nextLine().split(" ");
           theta33[3] = scanner.nextLine().split(" ");
           scanner.nextLine();
           for(int r = 0; r < 3; r++)
           {
               System.out.println(theta1[r][0]);
                              System.out.println(theta2[r][0]);
                                             System.out.println(theta3[r][0]);
                                                            System.out.println(theta11[r][0]);
                                                                           System.out.println(theta22[r][0]);
                                                                                          System.out.println(theta33[r][0]);
           }
       }
        
    }
    
}
