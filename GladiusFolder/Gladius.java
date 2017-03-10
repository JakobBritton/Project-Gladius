import java.util.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Gladius {// Rotation is cw
     public static int populationSize = 100;
	  public static int generations = 2;
   public static void main(String[] args) throws FileNotFoundException {
      // make new generation
	  
      Gladius[] gen = new Gladius[populationSize];
      for (int i = 0; i < populationSize; i++) { // make generation of 100 randoms
         gen[i] = new Gladius((int) Math.random() * 1000, (int) Math.random() * 500, Math.random() * 360, 1000, 500,
               Integer.toString(i), 0);
      }
      // setup csv file
      PrintWriter pwTotalFitness = new PrintWriter(new File("Fitness.csv"));
      StringBuilder sbTotalFitness = new StringBuilder();
      String fitnessColumnNamesList = "Fitness";
      sbTotalFitness.append(fitnessColumnNamesList + "\n");
      PrintWriter pwAction = new PrintWriter(new File("Actions.csv"));
      PrintWriter weights = new PrintWriter(new File("Weights.txt"));
      StringBuilder sbWeights = new StringBuilder();
      PrintWriter formattedWeights = new PrintWriter(new File("FWeights.txt"));
      StringBuilder sbFWeights = new StringBuilder();
      StringBuilder sbAction = new StringBuilder();
      String ActionColumnList = "Attack,Forward,Turn L, Turn R";
      sbAction.append(ActionColumnList + "\n");
      for (int i = 0; i < generations; i++) {
         System.out.println("Generation: " + Integer.toString(i+1));
         sbWeights.append("Generation: " + Integer.toString(i+1) +  "    *********************************************************************" + System.getProperty("line.separator"));
         sbFWeights.append("Generation: " + Integer.toString(i+1) + System.getProperty("line.separator"));
         int attacks = 0;
         int lefts = 0;
         int rights = 0;
         int forwards = 0;
         // each organism in gen fights each other
         for (int p = 0; p < gen.length; p++) {
            for (int j = 0; j < gen.length; j++) {
               battle(gen[p], gen[j]);
               if(gen[p].action.equals("attack"))
               {
                   attacks++;
               }
               else if(gen[p].action.equals("forward"))
               {
                   forwards++;
               }
               else if(gen[p].action.equals("left"))
               {
                   lefts++;
               }
               else
               {
                   rights++;
               }
            }
         }
         
         sbAction.append(attacks + "," + forwards + "," + lefts + "," + rights + "\n");
         // output fitness to csv file
         int genFit = 0;
         for (int d = 0; d < populationSize; d++) {
            genFit += gen[d].getFitness();
         }
         
         sbTotalFitness.append(Double.toString(genFit) + ",");
         sbTotalFitness.append("\n");
         // create mating pool
         Gladius[] tempGen = new Gladius[populationSize];
         Gladius[] mostFit = ranking(gen);
         for(int q = 0; q < populationSize; q+=1)
         {
        	 sbWeights.append((q+1) + System.getProperty("line.separator") + mostFit[q].toStringMat() + System.getProperty("line.separator"));
          
         }
         
         sbFWeights.append(mostFit[populationSize-1].toStringFormattedMat());
         sbFWeights.append(mostFit[populationSize/4*3].toStringFormattedMat());
         mostFit[0].toStringMat();
         mostFit[populationSize/4].toStringMat();
         mostFit[populationSize/2].toStringMat();
         mostFit[populationSize/4*3].toStringMat();
         
         for (int g = 0; g < populationSize; g++) {
            Gladius[] pair = pairMates(matingPool(ranking(gen)));
            // crossover pairs
            //pair[0].toStringMat();
            //System.out.println("*********************");
            //pair[1].toStringMat();
            //System.out.println("*********************");
            tempGen[g] = crossOver(pair[0], pair[1]);
            //tempGen[g].toStringMat();
            //System.out.println("*********************");
         }
         // fill gen with tempGen
         for (int k = 0; k < populationSize; k++) {
            gen[k] = tempGen[k];
         }

      }
      pwAction.write(sbAction.toString());
      pwAction.close();
      formattedWeights.write(sbFWeights.toString());
      formattedWeights.close();
      weights.write(sbWeights.toString());
      weights.close();
      pwTotalFitness.write(sbTotalFitness.toString());
      pwTotalFitness.close();
   }

   // new gladius w/ random weights
   
   int worldW, worldH;
   int x;
   int y;
   double rotation;
   String action;
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

   // child gladius with inherited weights
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

   double getRelX() {
      return relX;
   }

   double getRelY() {
      return relY;
   }

   public static void battle(Gladius one, Gladius two) {
      int iter = 100;
      for (int i = 0; i < iter; i++) {
         one.sees(two);
         two.sees(one);
         one.act();
         two.act();
         if(i == 300)
         {
            one.setX((int)(Math.random() * one.worldW));
            one.setY((int)(Math.random() * one.worldH));
            one.setRotation(Math.random() * 360);
            two.setX((int)(Math.random() * two.worldW));
            two.setY((int)(Math.random() * two.worldH));
            two.setRotation(Math.random() * 360);
         }
      }
   }

   // order the generation by fitness
   public static Gladius[] ranking(Gladius[] generation) {
      Gladius temp;
      for (int i = 1; i < generation.length; i++) {
         for (int j = i; j > 0; j--) {
            if (generation[j].getFitness() < generation[j - 1].getFitness()) {
               temp = generation[j];
               generation[j] = generation[j - 1];
               generation[j - 1] = temp;
            }
         }
      }
      return generation;
   }

   // creates arrayList of mates w/ number of each individual based
   // on their fitness level
   public static ArrayList<Gladius> matingPool(Gladius[] ranked) {
      ArrayList<Gladius> pool = new ArrayList<Gladius>();
      for (int i = 0; i < populationSize; i++) {
         for (int j = 0; j <= i; j++) {
            pool.add(ranked[i]);
         }
      }
      return pool;
   }

   public static Gladius[] pairMates(ArrayList<Gladius> mates) {
      Gladius[] pair = new Gladius[2];
      int first = (int) (Math.random() * (Math.pow(populationSize,2)/2+ populationSize/2 - 1));
      int second = (int) (Math.random() * (Math.pow(populationSize,2)/2+ populationSize/2 - 1));
      while (first == second) {
         second = (int) (Math.random() * (Math.pow(populationSize,2)/2+ populationSize/2 - 1));
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
            double rand = Math.random();
            if (rand >= .995) {
               tc1[r][c] = Math.random() * 1.1547001 - .5773502692;
            } else {
               tc1[r][c] = childU[c + r * 4];
            }
         }

      }
      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            double rand = Math.random();
            if (rand >= .995) {
               tc2[r][c] = Math.random() * 1.1547001 - .5773502692;
            } else {
               tc2[r][c] = childU[c + 12 + r * 4];
            }
         }

      }
      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            double rand = Math.random();
            if (rand >= .995) {
               tc3[r][c] = Math.random() * 1.1547001 - .5773502692;
            } else {
               tc3[r][c] = childU[c + 28 + r * 4];
            }
         }

      }
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

   void updateX() {
      int xInc = (int) (Math.cos((rotation / 180 * Math.PI)) * 5);
      x = x + xInc;
      xInc = 0;
   }

   void updateY() {
      int yInc = (int) (Math.sin((rotation / 180 * Math.PI)) * 5);
      y = y + yInc;
      yInc = 0;
   }
   void setX(int x)
   {
       this.x = x;
   }
   void setY(int y)
   {
       this.y = y;
   }
   void act() {

      int decision = feedForward(); // 0 Attack, 1 Forward, 2 Left, 3 Right::
      
      
      
      // decision
      switch (decision) {
      case 0: {
         // attack
         action = "attack";
         break;
      }
      case 1: {
         updateX();
         updateY();
         action = "forward";
         if (inCone == 1) {
            fitness+=2;
         }
         break;
      }
      case 2: {
         action = "left";
         rotation -= 10;
         break;
      }
      case 3: {
         action = "right";
         rotation += 10;
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
      setRotation(rotation);
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
         fitness++;
         inCone = 1;
         relX = enemyX - x;
         relY = y - enemyY;
      } else {
         inCone = 0;
         relX = 1000000;
         relY = 1000000;
      }
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
   
   public String toStringFormattedMat() {
	   StringBuilder bob = new StringBuilder();
	      for (int i = 0; i < 3; i++) {
	         //System.out.print("{");
	         //bob.append("{");
	         for (int j = 0; j < 4; j++) {
	            //System.out.printf("%.6f ", (float) theta1[i][j]);
	            Double gah = new Double(theta1[i][j]);
	            bob.append(gah.toString().substring(0,9) + " ");
	            if(j!=3)
	            {
	            //System.out.print(", ");
	            //bob.append(", ");
	         }
	         }
	         //System.out.print("}");
	         //bob.append("}");
	         bob.append(System.getProperty("line.separator"));
	         //System.out.println();
	      }
	      //System.out.println();
	      //System.out.println();
	      //bob.append(System.getProperty("line.separator"));
	      bob.append(System.getProperty("line.separator"));
	      for (int i = 0; i < 4; i++) {
	      //System.out.print("{");
	      //bob.append("{");
	         for (int j = 0; j < 4; j++) {
	            //System.out.printf("%.6f ", (float) theta2[i][j]);
	            Double gah = new Double(theta2[i][j]);
	            bob.append(gah.toString().substring(0,9)+ " ");
	            //if(j!=3)
	            //{
	            //System.out.print(", ");
	            //bob.append(", ");
	            //}
	         }
	         //System.out.print("}");
	         //bob.append("}");
	         bob.append(System.getProperty("line.separator"));
	         //System.out.println();
	      }
	      //System.out.println();
	      //System.out.println();
	      //bob.append(System.getProperty("line.separator"));
	      bob.append(System.getProperty("line.separator"));
	      for (int i = 0; i < 4; i++) {
		      //System.out.print("{");
		      //bob.append("{");
		         for (int j = 0; j < 4; j++) {
		            //System.out.printf("%.6f ", (float) theta3[i][j]);
		            Double gah = new Double(theta3[i][j]);
		            bob.append(gah.toString().substring(0,9) + " ");
		            //if(j!=3)
		            //{
		            //System.out.print(", ");
		            //bob.append(", ");
		            //}
		         }
		         //System.out.print("}");
		         //bob.append("}");
		         bob.append(System.getProperty("line.separator"));
		         //System.out.println();
		      }
		      //System.out.println();
		      //System.out.println();
		      //bob.append(System.getProperty("line.separator"));
		      //bob.append(System.getProperty("line.separator"));
	      //System.out.println("-----------------------");
	      bob.append("----------------------------------------------");
         bob.append(System.getProperty("line.separator"));
	      return bob.toString();
	   }
   public String toStringMat() {
	   StringBuilder bob = new StringBuilder();
	      for (int i = 0; i < 3; i++) {
	         System.out.print("{");
	         bob.append("{");
	         for (int j = 0; j < 4; j++) {
	            System.out.printf("%.6f ", (float) theta1[i][j]);
	            Double gah = new Double(theta1[i][j]);
	            bob.append(gah.toString().substring(0,9));
	            if(j!=3)
	            {
	            System.out.print(", ");
	            bob.append(", ");
	         }
	         }
	         System.out.print("}");
	         bob.append("}");
	         bob.append(System.getProperty("line.separator"));
	         System.out.println();
	      }
	      System.out.println();
	      System.out.println();
	      bob.append(System.getProperty("line.separator"));
	      bob.append(System.getProperty("line.separator"));
	      for (int i = 0; i < 4; i++) {
	      System.out.print("{");
	      bob.append("{");
	         for (int j = 0; j < 4; j++) {
	            System.out.printf("%.6f ", (float) theta2[i][j]);
	            Double gah = new Double(theta2[i][j]);
	            bob.append(gah.toString().substring(0,9));
	            if(j!=3)
	            {
	            System.out.print(", ");
	            bob.append(", ");
	            }
	         }
	         System.out.print("}");
	         bob.append("}");
	         bob.append(System.getProperty("line.separator"));
	         System.out.println();
	      }
	      System.out.println();
	      System.out.println();
	      bob.append(System.getProperty("line.separator"));
	      bob.append(System.getProperty("line.separator"));
	      for (int i = 0; i < 4; i++) {
		      System.out.print("{");
		      bob.append("{");
		         for (int j = 0; j < 4; j++) {
		            System.out.printf("%.6f ", (float) theta3[i][j]);
		            Double gah = new Double(theta2[i][j]);
		            bob.append(gah.toString().substring(0,9));
		            if(j!=3)
		            {
		            System.out.print(", ");
		            bob.append(", ");
		            }
		         }
		         System.out.print("}");
		         bob.append("}");
		         bob.append(System.getProperty("line.separator"));
		         System.out.println();
		      }
		      System.out.println();
		      System.out.println();
		      bob.append(System.getProperty("line.separator"));
		      bob.append(System.getProperty("line.separator"));
	      System.out.println("-----------------------");
	      bob.append("----------------------------------------------");
	      return bob.toString();
	   }


   public double[] sigmoid(double[] unactivated) {
      double[] activated = new double[4];
      for (int i = 0; i < 4; i++) {
         activated[i] = 1.0 / (1.0 + Math.pow(Math.E, (-1 * unactivated[i])));
      }
      return activated;
   }

}
