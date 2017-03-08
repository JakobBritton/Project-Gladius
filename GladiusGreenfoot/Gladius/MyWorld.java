import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
import java.awt.*;
/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyWorld extends World
{
     //ArrayList<Gladius> listGladius = new ArrayList<Gladius>();
     //ArrayList<VisionCone> listVisionCones = new ArrayList<VisionCone>();
     VisionCone jamesVision;
     VisionCone jacobVision;
     Gladius james;
     Gladius jacob;
     double x;
     int gah = 0;
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1300, 700, 1); 
        getBackground().setColor(Color.BLACK);
        getBackground().fill();
        Arena arena = new Arena();
        addObject(arena,650,350);
        /*
        for(int i = 1; i< 3; i++)
        {
            VisionCone bobs = new VisionCone();
            listVisionCones.add(bobs);
            Gladius bob = new Gladius(listVisionCones.get(i-1), "Gladius " + i);
            bob.setColor(new Color(Greenfoot.getRandomNumber(100)+100,Greenfoot.getRandomNumber(100)+100,Greenfoot.getRandomNumber(155)+100));
            listGladius.add(bob);
            
        }
        */
        jamesVision = new VisionCone();
        jacobVision = new VisionCone();
        jacob = new Gladius(jacobVision,"jakob");
        double[][] jacT1 = {
            {0.025439 , -0.395890 , -0.405430 , -0.482099 },
{0.130041 , -0.363597 , 0.229037 , 0.075690 },
{-0.133685 , -0.064964 , 0.528379 , -0.071238 }



        };
        double[][] jacT2 = {
            {-0.338039 , 0.559858 , 0.427829 , -0.025963 },
{-0.288194 , -0.023856 , -0.402873 , 0.094395 },
{-0.000200 , 0.293783 , -0.447394 , 0.151326 },
{0.281617 , -0.414848 , -0.561784 , -0.291130 }


        };
        double[][] jacT3 = {
            {-0.102452 , -0.137189 , 0.467753 , -0.135545 },
{-0.356165 , 0.429548 , 0.181516 , -0.199737 },
{0.317059 , 0.119356 , -0.400957 , -0.208214 },
{0.336066 , 0.107630 , 0.041119 , 0.266461 }



        };
        jacob.setTheta(jacT1,jacT2,jacT3);
        jacob.setRotation(Greenfoot.getRandomNumber(365));
        james = new Gladius(jamesVision,"james");
        double[][] jamT1 = {
           {-0.317412 , 0.124747 , 0.293675 , -0.560276 },
{-0.419359 , 0.198373 , 0.490452 , -0.189522 },
{0.407520 , 0.288664 , -0.525771 , -0.068486 }
        };
        double[][] jamT2 = {
            {-0.160522 , 0.418129 , 0.252557 , -0.151458 },
{-0.215103 , -0.498460 , -0.408149 , 0.387344 },
{-0.234015 , 0.520056 , -0.348464 , 0.148167 },
{-0.163721 , 0.426297 , -0.286204 , -0.312950 }
        };
        double[][] jamT3 = {
            {0.546724 , 0.079207 , 0.469195 , 0.513164 },
{-0.366574 , 0.386518 , -0.282954 , -0.464756 },
{0.159888 , 0.044803 , 0.491946 , 0.409903 },
{-0.264782 , 0.500761 , 0.336408 , -0.129507 }
        };
        james.setTheta(jamT1,jamT2,jamT3);
        james.setRotation(Greenfoot.getRandomNumber(365));
        jacob.setColor(new Color(0,255,255));
        james.setColor(new Color(0,255,0));
        int jamesx = Greenfoot.getRandomNumber(999)+151;
        int jamesy = Greenfoot.getRandomNumber(499)+101;
        int jacx = Greenfoot.getRandomNumber(999)+151;
        int jacy = Greenfoot.getRandomNumber(499)+101;
        addObject(james,jamesx,jamesy);
        addObject(jacob,jacx,jacy);
        addObject(jamesVision,650,350);
        addObject(jacobVision,650,350);
        //listGladius.add(james);
        //listGladius.add(jacob);
        //listVisionCones.add(jamesVision);
        //listVisionCones.add(jacobVision);
        /*
        for(int i = 0; i < 2; i++)
        {
            addObject(listGladius.get(i),Greenfoot.getRandomNumber(1000)+150,Greenfoot.getRandomNumber(500)+100);
            addObject(listVisionCones.get(i),650,350);
            
        }
        */
        
        
        
        
    }
    
    public void act()
    {
           //System.out.println(getBackground().getColorAt(0,0));
           //System.out.println(getBackground().getColorAt(650,350));
         
         if(james.sees(jacob))
         {
             double[] see = james.getCoord(jacob);
             double xx = see[0]/1010;
             double yy = see[1]/510;
             james.inputX(jacob.getX() - james.getX());
             james.inputY(james.getY()-jacob.getY());
             james.inputCone(1);
         }
         else
         {
             james.inputX(1000000);
             james.inputY(1000000);
             james.inputCone(0);
         }
         
         if(jacob.sees(james))
         {
             double[] see = jacob.getCoord(james);
             double xx = see[0]/1010;
             double yy = see[1]/510;
             jacob.inputX(james.getX() - jacob.getX());
             jacob.inputY(jacob.getY() - james.getY());
             jacob.inputCone(1);
         }
         else
         {
             jacob.inputX(1000000);
             jacob.inputY(1000000);
             jacob.inputCone(0);
         }
         gah++;
         if(gah == 100)
         {
             gah = 0;
            int jamesx = Greenfoot.getRandomNumber(999)+151;
            int jamesy = Greenfoot.getRandomNumber(499)+101;
            james.setRotation(Greenfoot.getRandomNumber(365));
            james.setLocation(jamesx,jamesy);
            int jacx = Greenfoot.getRandomNumber(999)+151;
            int jacy = Greenfoot.getRandomNumber(499)+101;
            jacob.setRotation(Greenfoot.getRandomNumber(365));
             
         }
         //jacob.act();
         //jacobVision.act();
         //james.act();
         //jamesVision.act();
        
        // Color c = new Color(0,0,0,0);
        // System.out.println(c.equals(new Color(0,0,0,0)));
        //System.out.println("Rotation of triangle " + james.getRotation());
        //System.out.println("Location : " + james.getX() + ", " + james.getY() );

        
    }
}
