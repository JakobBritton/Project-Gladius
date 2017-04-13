import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.*;
/**
 * Write a description of class Repeat here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Repeat extends Actor
{
    /**
     * Act - do whatever the Repeat wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    boolean repeatGen = false;
    public void act() 
    {
        String str = new Boolean(repeatGen).toString();
        GreenfootImage img = new GreenfootImage(str,30,Color.BLACK, Color.WHITE);

        setImage(img);
        if(Greenfoot.isKeyDown("space"))
        {
            repeatGen = !repeatGen;
            Greenfoot.delay(1000000);
        }
        // Add your action code here.
    }    
    boolean get()
    {
        return repeatGen;
    }
    
}
