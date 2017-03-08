import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Write a description of class Gladius here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Gladius extends Actor
{
    /**
     * Act - do whatever the Gladius wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
   
    VisionCone vc;
    String name;
    Color c;
    double[][] theta1;
    double[][] theta2;
    double[][] theta3;
    double relX, relY, inCone;
    public void act() 
    {
        
        Arena bob = (Arena)getOneIntersectingObject(Arena.class);
        int decision = feedForward();  //0 Attack, 1 Forward, 2 Left, 3 Right
        switch(decision)
        {
            case 0:
            {
                
                break;
            }
            
            case 1:
            {
                move(5);
                break;
            }
            case 2:
            {
                turn(20);
                break;
            }
            case 3:
            {
                turn(-20);
                break;
            }
            default:
                break;
            
        }
        if(bob == null)
        {
            turn(180);
            move(30);
            
        }
        
        
        
        
        GreenfootImage img = new GreenfootImage(30,30);
        img.setColor(c);
        img.fill(); 
        img.setColor(new Color(0,0,0));
        img.fillRect(27,0,2,30);
        setImage(img);
        vc.setCoord(getX(),getY());
        vc.display(getRotation());
        //System.out.println(vc.img.getColorAt(getX()+5,getY()));
        // Add your action code here.
        //System.out.println("hi");
        
    }
    Gladius()
    {
       
        vc = new VisionCone();
        name = "name";
        theta1 = new double[3][4];
        theta2 = new double[4][4];
        theta3 = new double[4][4];
        relX = 0;
        relY = 0;
        inCone = 0;
        for(int r = 0; r <3; r++)
        {
            for(int c = 0; c < 4; c++)
            {
                theta1[r][c] = Math.random()*1.1547001-.5773502692;
            }
            
        }
        
        for(int r = 0; r <4; r++)
        {
            for(int c = 0; c < 4; c++)
            {
                theta2[r][c] = Math.random()*1.1547001-.5773502692;
                theta3[r][c] = Math.random()*1.1547001-.5773502692;
            }
            
        }
        
    }
    Gladius(VisionCone v, String na)
    {
        
        vc = v; 
        name = na;
        theta1 = new double[3][4];
        theta2 = new double[4][4];
        theta3 = new double[4][4];
        relX = 0;
        relY = 0;
        inCone = 0;
        for(int r = 0; r <3; r++)
        {
            for(int c = 0; c < 4; c++)
            {
                theta1[r][c] = Math.random()*1.1547001-.5773502692;
            }
            
        }
        
        for(int r = 0; r <4; r++)
        {
            for(int c = 0; c < 4; c++)
            {
                theta2[r][c] = Math.random()*1.1547001-.5773502692;
                theta3[r][c] = Math.random()*1.1547001-.5773502692;
            }
            
        }
    }
    
    public void setColor(Color ah)
    {
        
        c = ah;
        
        
    }
    public boolean sees(Gladius enemy)
    {
        int x = enemy.getX();
        int y = enemy.getY();
        vc.display(getRotation());
        //System.out.println(enemy.getX() + " " + enemy.getY());
        //System.out.println(vc.img.getColorAt(x,y).toString());     
        return vc.img.getColorAt(x,y).equals(new Color(0,0,0,1));
        
       
    }
    public void setTheta(double[][] one, double[][] two, double[][] three)
    {
        theta1 = one;
        theta2 = two;
        theta3 = three;
    }
    public double[] getCoord(Gladius enemy)//not normalized yet
    {
        double[] list = new double[2];
        list[0] = Math.abs(getX() - enemy.getX());
        if(getX() > enemy.getX())
        {
            list[1] = getY() - enemy.getY();
        }
        else
        {
            list[1] = enemy.getY() - getY();
        }
        return list;
        
    }
    
    public void inputX(double x)
    {
        relX = x;
    }
    public void inputY(double y)
    {
        relY = y;
    }
    public void inputCone(double c)
    {
        inCone = c;
    }
    
    public int feedForward()
    {
        //add the products
        double[] z1 = new double[4];
        double[] z2 = new double[4];
        double[] z3 = new double[4];
        double[] a1 = new double[4];
        double[] a2 = new double[4];
        double[] a3 = new double[4];
        for(int i = 0; i<4; i++)
        {
            for(int j = 0; j<3; j++)
            {
                
                switch(j)
                {
                    case 0://VisionCone
                    {
                        z1[i] += inCone * theta1[j][i];
                        break;
                    }
                    case 1://relX
                    {
                        z1[i] += relX * theta1[j][i];
                        break;
                    }
                    case 2://relY
                    {
                        z1[i] += relY * theta1[j][i];
                        break;
                    }
                    default:
                        break;
                        
                    
                }
            }
        }
        //activation
        a1 = sigmoid(z1);
        //next layer
        for(int c = 0; c<4; c++)
        {
            for(int r = 0; r<4; r++)
            {
                z2[c] += theta2[r][c] * a1[r];
            }
        }
        //activation
        a2 = sigmoid(z2);
        //next layer
        for(int c = 0; c<4; c++)
        {
            for(int r = 0; r<4; r++)
            {
                z3[c] += theta3[r][c] * a2[r];
            }
        }
        a3 = sigmoid(z3);
        double largest = Double.MIN_VALUE;
        int spot = 0;
        for(int i = 0; i < 4; i++)
        {
            if(largest < a3[i])
            {
                spot= i;
                largest = a3[i];
            }
        }
        return spot;
    }
    
    
    public double[] sigmoid(double[] unactivated)
    {
        double[] activated = new double[4];
        for(int i = 0; i < 4; i++)
        {
            activated[i] = 1.0/(1.0 + Math.pow(Math.E, (-1 * unactivated[i])));
        }
        return activated;
    }
    
    
    
}



