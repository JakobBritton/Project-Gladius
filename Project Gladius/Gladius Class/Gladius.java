import java.util.*;
import java.awt.*;

public class Gladius {// Rotation is cw
   public static void main(String[] args) {
      // testing random code 'n stuff
      Gladius james = new Gladius(300, 300, 0, 1000, 500, "james", 0);
      Gladius jakob = new Gladius(400, 400, 225, 1000, 500, "jakob", 0);
      System.out.println("relX is " + jakob.getCoordEnemy(james)[0]);
      System.out.println("relY is " + jakob.getCoordEnemy(james)[1]);
      Gladius[] gen = new Gladius[100];
      for (int i = 0; i < 100; i++) {                             //make generation of 100 randoms
         gen[i] = new Gladius((int) Math.random() * 1000, (int) Math.random() * 1000, Math.random() * 360, 1000, 1000,
               Integer.toString(i), 0);
      }
      for (int i = 0; i < gen.length; i++) {
         for (int j = 0; j < gen.length; j++) {
            battle(gen[i], gen[j]);
         }
      }
      for (int i = 0; i < gen.length; i++) {
         System.out.print(gen[i].getFitness() + ", ");
      }
   }

   int worldW, worldH;
   int x;
   int y;
   double rotation;
   String name;
   double[][] theta1;
   double[][] theta2;
   double[][] theta3;
   double relX, relY, inCone, fitness;
   Gladius(int x, int y, double rotation, int worldW, int worldH, String name, double fitness) {
      this.x = x;
      this.y = y;
      this.fitness = fitness;
      this.rotation = rotation; // Rotation goes cw, not ccw
      this.worldW = worldW;
      this.worldH = worldH;
      this.name = name;
      theta1 = new double[3][4];
      theta2 = new double[4][4];
      theta3 = new double[4][4];
      relX = 0;
      relY = 0;
      inCone = 0;
      for (int r = 0; r < 3; r++) {
         for (int c = 0; c < 4; c++) {
            theta1[r][c] = Math.random() * 1.1547001 - .5773502692;
         }

      }

      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            theta2[r][c] = Math.random() * 1.1547001 - .5773502692;
            theta3[r][c] = Math.random() * 1.1547001 - .5773502692;
         }

      }
   }

   Gladius(int x, int y, double rotation, int worldW, int worldH, String name, double[][] tOne, double[][] tTwo,
         double[][] tThree, double fitness) {
      this.x = x;
      this.y = y;
      this.fitness = fitness;
      this.rotation = rotation; // Rotation goes cw, not ccw
      this.worldW = worldW;
      this.worldH = worldH;
      this.name = name;
      theta1 = tOne;
      theta2 = tTwo;
      theta3 = tThree;
      relX = 0;
      relY = 0;
      inCone = 0;

   }
   
   public static void battle(Gladius one, Gladius two) {
      int iter = 100;
      for (int i = 0; i < iter; i++) {
         one.sees(two);
         two.sees(one);
         one.feedForward();
         two.feedForward();
         one.act();
         two.act();
      }
   }

   // creates array of mates w/ number of each individual based
   // on their fitness level
   public ArrayList<Gladius> matingPool(Gladius organism) {
      ArrayList<Gladius> pool = new ArrayList<Gladius>();
      for (int i = 0; i < organism.getFitness(); i++) // need to write or
                                                      // include
      // fitness()
      {
         pool.add(organism);
      }
      return pool;
   }
   
   public Gladius[] pairMates(ArrayList<Gladius> mates) {
      Gladius[] pair = new Gladius[2];
      int first = 0;
      int second = 0;
      while (first == second) {
         first = (int) Math.random() * 100;
         second = (int) Math.random() * 100;
      }
      pair[0] = mates.get(first);
      pair[1] = mates.get(second);
      return pair;
   }

   // creates child w/ 2 crossover points
   public static Gladius crossOver(Gladius one, Gladius two) {
      double[][] tc1;
      double[][] tc2;
      double[][] tc3;
      tc1 = new double[3][4];
      tc2 = new double[4][4];
      tc3 = new double[4][4];
      double[][] oneT1 = one.theta1;
      double[][] oneT2 = one.theta2;
      double[][] oneT3 = one.theta3;

      double[][] twoT1 = two.theta1;
      double[][] twoT2 = two.theta2;
      double[][] twoT3 = two.theta3;
      double[] unrolledOne = unRoll(oneT1, oneT2, oneT3);
      double[] unrolledTwo = unRoll(twoT1, twoT2, twoT3);
      double[] childU = new double[44];
      int spot1 = (int) (Math.random() * 43) + 1;
      int spot2 = (int) (Math.random() * 43) + 1;
      while (spot1 == spot2) {
         spot2 = (int) (Math.random() * 43) + 1;

      }
      if (spot1 > spot2) {
         for (int i = 0; i < spot2; i++) {
            childU[i] = unrolledTwo[i];
         }
         for (int i = spot2; i < spot1; i++) {
            childU[i] = unrolledOne[i];
         }
         for (int i = spot1; i < 44; i++) {
            childU[i] = unrolledTwo[i];
         }
      } else {
         for (int i = 0; i < spot1; i++) {
            childU[i] = unrolledOne[i];
         }
         for (int i = spot1; i < spot2; i++) {
            childU[i] = unrolledTwo[i];
         }
         for (int i = spot2; i < 44; i++) {
            childU[i] = unrolledOne[i];

         }

      }
      for (int r = 0; r < 3; r++) {
         for (int c = 0; c < 4; c++) {
            tc1[r][c] = childU[c + r * 4];

         }

      }
      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            tc2[r][c] = childU[c + 12 + r * 4];

         }

      }
      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            tc3[r][c] = childU[c + 28 + r * 4];

         }

      }

      System.out.println();
      System.out.println("spot1 is " + spot1);
      System.out.println("spot2 is " + spot2);
      System.out.println();
      return new Gladius((int) (Math.random() * one.worldW), (int) (Math.random() * one.worldH), Math.random() * 360,
            one.worldW, one.worldH, "child", tc1, tc2, tc3, 0);
      // random slice = 2
      // 50/50 of taking stuff

   }

   public static double[] unRoll(double[][] one, double[][] two, double[][] three) {
      double[] unRolled = new double[44];
      for (int r = 0; r < 3; r++) {
         for (int c = 0; c < 4; c++) {
            unRolled[c + r * 4] = one[r][c];

         }

      }
      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            unRolled[c + 12 + r * 4] = two[r][c];

         }

      }
      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            unRolled[c + 28 + r * 4] = three[r][c];

         }

      }

      return unRolled;
   }

   void act() {

      int decision = feedForward(); // 0 Attack, 1 Forward, 2 Left, 3 Right::
                                    // decision
      switch (decision) {
      case 0: {
         // attack
         break;
      }
      case 1: {
         int xInc = (int) (Math.cos((rotation / 180 * Math.PI) * 5));
         int yInc = (int) (Math.sin((rotation / 180 * Math.PI) * 5));
         x += xInc;
         y += yInc;
         if (inCone == 1) {
            fitness++;
         }
         break;
      }
      case 2: {
         rotation -= 5;
         break;
      }
      case 3: {
         rotation += 5;
         break;
      }
      default:
         break;

      }

      // turning if outside of world
      if (x >= worldW) {
         setRotation(rotation + 180);
         x = worldW - 1;
      } else if (x <= 0) {
         setRotation(rotation + 180);
         x = 1;
      } else {
      }
      if (y >= worldH) {
         setRotation(rotation + 180);
         y = worldH - 1;
      } else if (y <= 0) {
         setRotation(rotation + 180);
         y = 1;
      } else {
      }
      // <------- @tim what are these for? Some comment we forgot to make?

   }

   double getFitness() {
      return fitness;
   }

   int[] getXWidth() {
      int[] loc = new int[2];
      loc[0] = x - 15;
      loc[1] = x + 15;
      return loc;
   }

   int[] getYHeight() {
      int[] loc = new int[2];
      loc[0] = y - 15;
      loc[1] = y + 15;
      return loc;
   }

   int getX() {
      return x;
   }

   int getY() {
      return y;
   }

   double getRotation() {
      return rotation;
   }

   void setRotation(double rot) {
      rotation = rot % 360;
   }

   void sees(Gladius glad) {
      int enemyX = glad.getX();
      int enemyY = glad.getY();
      int[] xs = new int[3];
      int[] ys = new int[3];
      xs[0] = x;
      xs[1] = (int) (Math.cos((rotation / 180 * Math.PI) - Math.PI * .08) * 1200) + x;
      xs[2] = (int) (Math.cos((rotation / 180 * Math.PI) + Math.PI * .08) * 1200) + x;
      ys[0] = y;
      ys[1] = (int) (Math.sin((rotation / 180 * Math.PI) - Math.PI * .08) * 1200) + y;
      ys[2] = (int) (Math.sin((rotation / 180 * Math.PI) + Math.PI * .08) * 1200) + y;
      Polygon poly = new Polygon(xs, ys, 3);
      if (poly.contains(enemyX, enemyY)) {
         inCone = 1;
      }
   }

   int[] getCoordEnemy(Gladius bob) // getting the rel coord
   {
      int[] list = new int[2];
      // I: +,- II: -,- III: -,+ IV: +,+// sign of the array based on quadrants
      // relative to the Gladius
      list[0] = bob.getX() - x;
      list[1] = bob.getY() - y;
      return list;
   }

   public int feedForward() {
      // add the products
      double[] z1 = new double[4];
      double[] z2 = new double[4];
      double[] z3 = new double[4];
      double[] a1 = new double[4];
      double[] a2 = new double[4];
      double[] a3 = new double[4];
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 3; j++) {

            switch (j) {
            case 0:// VisionCone
            {
               z1[i] += inCone * theta1[j][i];
               break;
            }
            case 1:// relX
            {
               z1[i] += relX * theta1[j][i];
               break;
            }
            case 2:// relY
            {
               z1[i] += relY * theta1[j][i];
               break;
            }
            default:
               break;

            }
         }
      }
      // activation
      a1 = sigmoid(z1);
      // next layer
      for (int c = 0; c < 4; c++) {
         for (int r = 0; r < 4; r++) {
            z2[c] += theta2[r][c] * a1[r];
         }
      }
      // activation
      a2 = sigmoid(z2);
      // next layer
      for (int c = 0; c < 4; c++) {
         for (int r = 0; r < 4; r++) {
            z3[c] += theta3[r][c] * a2[r];
         }
      }
      a3 = sigmoid(z3);
      double largest = Double.MIN_VALUE;
      int spot = 0;
      for (int i = 0; i < 4; i++) {
         if (largest < a3[i]) {
            spot = i;
            largest = a3[i];
         }
      }
      return spot;
   }

   public void toStringMat() {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 4; j++) {
            System.out.printf("%.2f ", (float) theta1[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      System.out.println();

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            System.out.printf("%.2f ", (float) theta2[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      System.out.println();

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            System.out.printf("%.2f ", (float) theta3[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      System.out.println();
      System.out.println("-----------------------");

   }

   public double[] sigmoid(double[] unactivated) {
      double[] activated = new double[4];
      for (int i = 0; i < 4; i++) {
         activated[i] = 1.0 / (1.0 + Math.pow(Math.E, (-1 * unactivated[i])));
      }
      return activated;
   }

}