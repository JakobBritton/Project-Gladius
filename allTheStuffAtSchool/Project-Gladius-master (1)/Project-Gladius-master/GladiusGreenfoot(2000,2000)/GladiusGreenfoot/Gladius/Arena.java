import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.*;
/**
 * Write a description of class Arena here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Arena extends Actor
{
    /**
     * Act - do whatever the Arena wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        
        // Add your action code here.
    }    
    public Arena()
    {
        GreenfootImage arena = new GreenfootImage(2000,2000);
        arena.setColor(Color.WHITE);
        arena.fill();
        setImage(arena);
    }
}
