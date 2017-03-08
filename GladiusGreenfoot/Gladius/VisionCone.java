import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
import java.awt.*;

/**
 * Write a description of class VisionCone here.
 * Set alpha color stuff
 * @author (your name) 
 * @version (a version number or a date)
 */
public class VisionCone extends Actor
{
    /**
     * Act - do whatever the VisionCone wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    int x;
    int y;
    GreenfootImage img;
    public void act() 
    {
        Matrix ruff = new Matrix(2,2,5);
        //System.out.println(ruff.toString());
        // Add your action code here.
        //System.out.println("VisionCone");
    }
    public void display(double rot)
    {
        img.clear();
        int[] xs = new int[3];
        int[] ys = new int[3];
        xs[0] = x;
        xs[1] = (int)(Math.cos((rot/180*Math.PI) - Math.PI * .08) * 1200)+x;
        xs[2] = (int)(Math.cos((rot/180*Math.PI) + Math.PI * .08) * 1200)+x;
        ys[0] = y;
        ys[1] = (int)(Math.sin((rot/180*Math.PI) - Math.PI * .08) * 1200)+y;
        ys[2] = (int)(Math.sin((rot/180*Math.PI) + Math.PI * .08) * 1200)+y;
        img.setColor(Color.RED);
        img.drawPolygon(xs,ys,3);
        img.setColor(new Color(0,0,0,1));
        img.fillPolygon(xs,ys,3);
        setImage(img);
       // Math.sin((rot/180*Math.PI) * 100;
    }
    public VisionCone()
    {
         x = 0;
         y = 0;
         img = new GreenfootImage(1300,700);
    }
    public VisionCone(int xx, int yy)
    {
        x = xx;
        y = yy;
        img = new GreenfootImage(1300,700);
    }
    public void setCoord(int xx, int yy)
    {
        x = xx;
        y = yy;
    }
    
}
