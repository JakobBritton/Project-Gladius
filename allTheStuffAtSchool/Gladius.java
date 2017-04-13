import java.util.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
//switch fitness functions in the middle of it yo
public class Gladius {// Rotation is cw
	public static int populationSize = 100;
	public static int generations = 120;

	public static void main(String[] args) throws FileNotFoundException {
		// make new generation
		Gladius[] gen = new Gladius[populationSize];
		for (int i = 0; i < populationSize; i++) { // make generation of 100
													// randoms\//fix the random math500 math1000
			gen[i] = new Gladius((int)(Math.random()*1000), (int)(Math.random() * 500), Math.random() * 360, 1000, 500,
					Integer.toString(i), 0);
		}
		// setup csv file
		PrintWriter pwTotalFitness = new PrintWriter(new File("Fitness.csv"));
		StringBuilder sbTotalFitness = new StringBuilder();
		String fitnessColumnNamesList = "Fitness";
		sbTotalFitness.append(fitnessColumnNamesList + "\n");
		PrintWriter pwAction = new PrintWriter(new File("Actions.csv"));
		PrintWriter weights = new PrintWriter(new File("Weights1.txt"));
		StringBuilder sbWeights = new StringBuilder();
		PrintWriter formattedWeights = new PrintWriter(new File("FWeights.txt"));
		StringBuilder sbFWeights = new StringBuilder();
		StringBuilder sbAction = new StringBuilder();
		String ActionColumnList = "Attack,Forward,Turn L, Turn R";
		sbAction.append(ActionColumnList + "\n");
		for (int i = 0; i < generations; i++) {
			System.out.println("Generation: " + Integer.toString(i + 1));
			sbWeights.append("Generation: " + Integer.toString(i + 1)
					+ "    *********************************************************************"
					+ System.getProperty("line.separator"));
			sbFWeights.append("Generation: " + Integer.toString(i + 1) + System.getProperty("line.separator"));
			int attacks = 0;
			int lefts = 0;
			int rights = 0;
			int forwards = 0;
			// each organism in gen fights each other
			for (int p = 0; p < gen.length; p++) {
				for (int j = 0; j < gen.length; j++) {
					battle(gen[p], gen[j], i);
					if (gen[p].action.equals("attack")) {
						attacks++;
					} else if (gen[p].action.equals("forward")) {
						forwards++;
					} else if (gen[p].action.equals("left")) {
						lefts++;
					} else {
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
			for (int q = 0; q < populationSize; q += 1) {
				sbWeights.append((q + 1) + System.getProperty("line.separator") + mostFit[q].toStringMat()
						+ System.getProperty("line.separator"));

			}

			sbFWeights.append(mostFit[populationSize - 1].toStringFormattedMat());
			sbFWeights.append(mostFit[populationSize / 4 * 3].toStringFormattedMat());
			mostFit[0].toStringMat();
			mostFit[populationSize / 4].toStringMat();
			mostFit[populationSize / 2].toStringMat();
			mostFit[populationSize / 4 * 3].toStringMat();

			for (int g = 0; g < populationSize; g++) {
				Gladius[] pair = pairMates(matingPool(ranking(gen)));
				// crossover pairs
				// pair[0].toStringMat();
				// System.out.println("*********************");
				// pair[1].toStringMat();
				// System.out.println("*********************");
				tempGen[g] = crossOver(pair[0], pair[1]);
				// tempGen[g].toStringMat();
				// System.out.println("*********************");
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
	public static final double PI = Math.PI;
	int worldW, worldH;
	int x;
	int y;
	double rotation;
	String action;
	String name;
	double[][] theta1;
	double[][] theta2;
	double[][] theta3;
	double[][] recur;
	double[][] recurActivated;
	double[] stuff = new double[5];
	double relX, relY, inCone, fitness;
	Line2D.Float hitFront;
	Line2D.Float hitLeft;
	Line2D.Float hitRight;
	Line2D.Float hurtFront;
	Line2D.Float hurtLeft;
	Line2D.Float hurtRight;

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
		recur = new double[3][5];
		recurActivated = new double[5][3];
		relX = 0;
		relY = 0;
		inCone = 0;
		hitFront = new Line2D.Float();
		hitLeft = new Line2D.Float();
		hitRight = new Line2D.Float();
		hurtFront = new Line2D.Float();
		hurtLeft = new Line2D.Float();
		hurtRight = new Line2D.Float();
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 4; c++) {
				theta1[r][c] = Math.random() * 1.1547001 - .5773502692;
			}

		}
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 5; c++) {
				recur[r][c] = Math.random() * 1.1547001 - .5773502692;
				recurActivated[c][r] = Math.random() * 1.1547001 - .5773502692;
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
			double[][] tThree, double[][] recur, double[][] recurActive, double fitness) {
		this.x = x;
		this.y = y;
		this.fitness = fitness;
		this.rotation = rotation; // Rotation goes cw, not ccw
		this.worldW = worldW;
		this.worldH = worldH;
		this.name = name;
		this.recur = recur;
		recurActivated = recurActive;
		theta1 = tOne;
		theta2 = tTwo;
		theta3 = tThree;
		relX = 0;
		relY = 0;
		inCone = 0;
		hitFront = new Line2D.Float();
		hitLeft = new Line2D.Float();
		hitRight = new Line2D.Float();
		hurtFront = new Line2D.Float();
		hurtLeft = new Line2D.Float();
		hurtRight = new Line2D.Float();

	}

	double getRelX() {
		return relX;
	}

	double getRelY() {
		return relY;
	}

	void updateHitFront(double x1, double y1, double x2, double y2) {
		//System.out.println();
		//System.out.println(" What is this coordinate: " + x1);
		//System.out.println();
		this.hitFront.setLine(x1, y1, x2, y2);
	}

	void updateHitLeft(double x1, double y1, double x2, double y2) {
		this.hitLeft.setLine(x1, y1, x2, y2);
	}

	void updateHitRight(double x1, double y1, double x2, double y2) {
		this.hitRight.setLine(x1, y1, x2, y2);
	}

	void updateHurtFront(double x1, double y1, double x2, double y2) {
		this.hurtFront.setLine(x1, y1, x2, y2);
	}

	void updateHurtLeft(double x1, double y1, double x2, double y2) {
		this.hurtLeft.setLine(x1, y1, x2, y2);
	}

	void updateHurtRight(double x1, double y1, double x2, double y2) {
		this.hurtRight.setLine(x1, y1, x2, y2);
	}

	Line2D.Float getHitFront() {
		return hitFront;
	}

	Line2D.Float getHitLeft() {
		return hitLeft;
	}

	Line2D.Float getHitRight() {
		return hitRight;
	}

	Line2D.Float getHurtFront() {
		return hurtFront;
	}

	Line2D.Float getHurtLeft() {
		return hurtLeft;
	}

	Line2D.Float getHurtRight() {
		return hurtRight;
	}

	boolean intersects(Gladius enemy) {
		if (hurtFront.intersectsLine(enemy.getHitFront()))
		{
			//System.out.println();
			//System.out.println("Front Hurt: Front Hit");
			//System.out.printf("Attacker HurtF: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtFront().getX1(),getHurtFront().getY1(),getHurtFront().getX2(),getHurtFront().getY2());
			//System.out.printf("Enemy HitF: (%.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitFront().getX1(),enemy.getHitFront().getY1(),enemy.getHitFront().getX2(),enemy.getHitFront().getY2());
			//System.out.println();
			return true;
		}
		if (hurtLeft.intersectsLine(enemy.getHitFront()))
		{
			//System.out.println();
			//System.out.println("Left Hurt: Front Hit");
			//System.out.printf("Attacker HurtL: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtLeft().getX1(),getHurtLeft().getY1(),getHurtLeft().getX2(),getHurtLeft().getY2());
			//System.out.printf("Enemy HitF: (%.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitFront().getX1(),enemy.getHitFront().getY1(),enemy.getHitFront().getX2(),enemy.getHitFront().getY2());
			//System.out.println();
			return true;
		}
		if (hurtRight.intersectsLine(enemy.getHitFront()))
		{
			//System.out.println();
			//System.out.println("Right Hurt: Front Hit");
			//System.out.printf("Attacker HurtR: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtRight().getX1(),getHurtRight().getY1(),getHurtRight().getX2(),getHurtRight().getY2());
			//System.out.printf("Enemy HitF: %.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitFront().getX1(),enemy.getHitFront().getY1(),enemy.getHitFront().getX2(),enemy.getHitFront().getY2());
			//System.out.println();
			return true;
		}
		if (hurtFront.intersectsLine(enemy.getHitLeft()))
		{
			//System.out.println();
			//System.out.println("Front Hurt: Left Hit");
			//System.out.printf("Attacker HurtF: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtFront().getX1(),getHurtFront().getY1(),getHurtFront().getX2(),getHurtFront().getY2());
			//System.out.printf("Enemy HitL: %.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitLeft().getX1(),enemy.getHitLeft().getY1(),enemy.getHitLeft().getX2(),enemy.getHitLeft().getY2());
			//System.out.println();
			return true;
		}
		if (hurtLeft.intersectsLine(enemy.getHitLeft()))
		{
			//System.out.println();
			//System.out.println("Left Hurt: Left Hit");
			//System.out.printf("Attacker HurtL: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtLeft().getX1(),getHurtLeft().getY1(),getHurtLeft().getX2(),getHurtLeft().getY2());
			//System.out.printf("Enemy HitL: %.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitLeft().getX1(),enemy.getHitLeft().getY1(),enemy.getHitLeft().getX2(),enemy.getHitLeft().getY2());
			//System.out.println();
			return true;
		}
		if (hurtRight.intersectsLine(enemy.getHitLeft()))
		{
			//System.out.println();
			//System.out.println("Right Hurt: Left Hit");
			//System.out.printf("Attacker HurtR: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtRight().getX1(),getHurtRight().getY1(),getHurtRight().getX2(),getHurtRight().getY2());
			//System.out.printf("Enemy HitL: %.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitLeft().getX1(),enemy.getHitLeft().getY1(),enemy.getHitLeft().getX2(),enemy.getHitLeft().getY2());
			//System.out.println();
			return true;
		}
		if (hurtFront.intersectsLine(enemy.getHitRight()))
		{
			//System.out.println();
			//System.out.println("Front Hurt: Right Hit");
			//System.out.printf("Attacker HurtF: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtFront().getX1(),getHurtFront().getY1(),getHurtFront().getX2(),getHurtFront().getY2());
			//System.out.printf("Enemy HitR: %.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitRight().getX1(),enemy.getHitRight().getY1(),enemy.getHitRight().getX2(),enemy.getHitRight().getY2());
			//System.out.println();
			return true;
		}
		if (hurtLeft.intersectsLine(enemy.getHitRight()))
		{
			//System.out.println();
			//System.out.println("Left Hurt: Right Hit");
			//System.out.printf("Attacker HurtL: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtLeft().getX1(),getHurtLeft().getY1(),getHurtLeft().getX2(),getHurtLeft().getY2());
			//System.out.printf("Enemy HitR: %.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitRight().getX1(),enemy.getHitRight().getY1(),enemy.getHitRight().getX2(),enemy.getHitRight().getY2());
			//System.out.println();
			return true;
		}
		if (hurtRight.intersectsLine(enemy.getHitRight()))
		{
			//System.out.println();
			//System.out.println("Right Hurt: Right Hit");
			//System.out.printf("Attacker HurtR: (%.2f, %.2f) -> (%.2f, %.2f)\n",getHurtRight().getX1(),getHurtRight().getY1(),getHurtRight().getX2(),getHurtRight().getY2());
			//System.out.printf("Enemy HitR: %.2f, %.2f) -> (%.2f, %.2f)\n",enemy.getHitRight().getX1(),enemy.getHitRight().getY1(),enemy.getHitRight().getX2(),enemy.getHitRight().getY2());
			//System.out.println();
			return true;
		}
		return false;
	}

	public static void battle(Gladius one, Gladius two, int gener) {
		int iter = 300;
		for (int i = 0; i < iter; i++) {
			one.sees(two,gener);
			two.sees(one,gener);
			one.act(two, gener);
			two.act(one, gener);
			if (i == 150) {
				one.setX((int) (Math.random() * one.worldW));
				one.setY((int) (Math.random() * one.worldH));
				one.setRotation(Math.random() * 360);
				two.setX((int) (Math.random() * two.worldW));
				two.setY((int) (Math.random() * two.worldH));
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
		int first = (int) (Math.random() * (Math.pow(populationSize, 2) / 2 + populationSize / 2 - 1));
		int second = (int) (Math.random() * (Math.pow(populationSize, 2) / 2 + populationSize / 2 - 1));
		while (first == second) {
			second = (int) (Math.random() * (Math.pow(populationSize, 2) / 2 + populationSize / 2 - 1));
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
		double[][] r1;
		double[][] rA;
		tc1 = new double[3][4];
		tc2 = new double[4][4];
		tc3 = new double[4][4];
		r1 = new double[3][5];
		rA = new double[5][3];
		double[][] oneT1 = one.theta1;
		double[][] oneT2 = one.theta2;
		double[][] oneT3 = one.theta3;
		double[][] oner1 = one.recur;
		double[][] oneRa = one.recurActivated;
		double[][] twoT1 = two.theta1;
		double[][] twoT2 = two.theta2;
		double[][] twoT3 = two.theta3;
		double[][] twor1 = two.recur;
		double[][] twoRa = two.recurActivated;
		double[] unrolledOne = unRoll(oneT1, oneT2, oneT3, oner1, oneRa);
		double[] unrolledTwo = unRoll(twoT1, twoT2, twoT3, twor1, twoRa);
		double[] childU = new double[74];
		int spot1 = (int) (Math.random() * 72) + 1;
		int spot2 = (int) (Math.random() * 72) + 1;
		while (spot1 == spot2) {
			spot2 = (int) (Math.random() * 72) + 1;

		}
		if (spot1 > spot2) {
			for (int i = 0; i < spot2; i++) {
				childU[i] = unrolledTwo[i];
			}
			for (int i = spot2; i < spot1; i++) {
				childU[i] = unrolledOne[i];
			}
			for (int i = spot1; i < 74; i++) {
				childU[i] = unrolledTwo[i];
			}
		} else {
			for (int i = 0; i < spot1; i++) {
				childU[i] = unrolledOne[i];
			}
			for (int i = spot1; i < spot2; i++) {
				childU[i] = unrolledTwo[i];
			}
			for (int i = spot2; i < 74; i++) {
				childU[i] = unrolledOne[i];

			}

		}
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 4; c++) {
				double rand = Math.random();
				if (rand >= .99) {
					tc1[r][c] = Math.random() * 1.1547001 - .5773502692;
				} else {
					tc1[r][c] = childU[c + r * 4];
				}
			}

		}
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				double rand = Math.random();
				if (rand >= .99) {
					tc2[r][c] = Math.random() * 1.1547001 - .5773502692;
				} else {
					tc2[r][c] = childU[c + 12 + r * 4];
				}
			}

		}
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				double rand = Math.random();
				if (rand >= .99) {
					tc3[r][c] = Math.random() * 1.1547001 - .5773502692;
				} else {
					tc3[r][c] = childU[c + 28 + r * 4];
				}
			}

		}
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 5; c++) {
				double rand = Math.random();
				if (rand >= .99) {
					r1[r][c] = Math.random() * 1.1547001 - .5773502692;
				} else {
					r1[r][c] = childU[c + 44 + r * 5];
				}
			}

		}

		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 3; c++) {
				double rand = Math.random();
				if (rand >= .99) {
					rA[r][c] = Math.random() * 1.1547001 - .5773502692;
				} else {
					rA[r][c] = childU[c + 59 + r * 3];
				}
			}

		}

		return new Gladius((int) (Math.random() * one.getWorldW()), (int) (Math.random() * one.getWorldH()), Math.random() * 360,
				one.worldW, one.worldH, "child", tc1, tc2, tc3, r1, rA, 0);
		// random slice = 2
		// 50/50 of taking stuff

	}

	public static double[] unRoll(double[][] one, double[][] two, double[][] three, double[][] rec, double[][] rA) {
		double[] unRolled = new double[74];
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
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 5; c++) {
				unRolled[c + 44 + r * 5] = rec[r][c];
			}
		}
		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 3; c++) {
				unRolled[c + 59 + r * 3] = rA[r][c];
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

	void setX(int x) {
		this.x = x;
	}

	void setY(int y) {
		this.y = y;
	}

	void act(Gladius enemy, int genFunction) {

		int decision = feedForward(); // 0 Attack, 1 Forward, 2 Left, 3 Right::

		// decision
		switch (decision) {
		case 0: {
			// attack
			action = "attack";
			updateHitFront(Math.sqrt(576) * Math.cos((45 + 360 - rotation)/180*Math.PI) + this.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - rotation)/180*Math.PI) + this.getY(),
					Math.sqrt(576) * Math.cos((360 - rotation - 45)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((360 - rotation - 45)/180*PI) + this.getY());
			enemy.updateHitFront(Math.sqrt(576) * Math.cos((45 + 360 - enemy.rotation)/180*PI) + enemy.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - enemy.rotation)/180*PI) + enemy.getY(),
					Math.sqrt(576) * Math.cos((360 - enemy.rotation - 45)/180*PI) + enemy.getX(),
					Math.sqrt(576) * Math.sin((360 - enemy.rotation - 45)/180*PI) + enemy.getY());

			updateHitLeft(Math.sqrt(576) * Math.cos((45 + 360 - rotation)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - rotation)/180*PI) + this.getY(),
					Math.sqrt(450) * Math.cos((360 - rotation + 45)/180*PI) + this.getX(),
					Math.sqrt(450) * Math.sin((360 - rotation + 45)/180*PI) + this.getY());
			enemy.updateHitLeft(Math.sqrt(576) * Math.cos((45 + 360 - enemy.rotation)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - enemy.rotation)/180*PI) + this.getY(),
					Math.sqrt(450) * Math.cos((360 - enemy.rotation + 45)/180*PI) + this.getX(),
					Math.sqrt(450) * Math.sin((360 - enemy.rotation + 45)/180*PI) + this.getY());

			updateHitRight(Math.sqrt(450) * Math.cos((-45 + 360 - rotation)/180*PI) + this.getX(),
					Math.sqrt(450) * Math.sin((-45 + 360 - rotation)/180*PI) + this.getY(),
					Math.sqrt(576) * Math.cos((360 - rotation - 45)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((360 - rotation - 45)/180*PI) + this.getY());
			enemy.updateHitRight(Math.sqrt(450) * Math.cos((-45 + 360 - enemy.rotation)/180*PI) + enemy.getX(),
					Math.sqrt(450) * Math.sin((-45 + 360 - enemy.rotation)/180*PI) + enemy.getY(),
					Math.sqrt(576) * Math.cos((360 - enemy.rotation - 45)/180*PI) + enemy.getX(),
					Math.sqrt(576) * Math.sin((360 - enemy.rotation - 45)/180*PI) + enemy.getY());

			updateHurtFront(Math.sqrt(576) * Math.cos((45 + 360 - rotation + 180)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - rotation + 180)/180*PI) + this.getY(),
					Math.sqrt(576) * Math.cos((360 - rotation + 180 - 45)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((360 - rotation + 180 - 45)/180*PI) + this.getY());
			enemy.updateHurtFront(Math.sqrt(576) * Math.cos((45 + 360 - enemy.rotation + 180)/180*PI) + enemy.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - enemy.rotation + 180)/180*PI) + enemy.getY(),
					Math.sqrt(576) * Math.cos((360 - enemy.rotation + 180 - 45)/180*PI) + enemy.getX(),
					Math.sqrt(576) * Math.sin((360 - enemy.rotation + 180 - 45)/180*PI) + enemy.getY());

			updateHurtLeft(Math.sqrt(576) * Math.cos((45 + 360 - rotation + 180)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - rotation + 180)/180*PI) + this.getY(),
					Math.sqrt(450) * Math.cos((360 - rotation + 180 + 45)/180*PI) + this.getX(),
					Math.sqrt(450) * Math.sin((360 - rotation + 180 + 45)/180*PI) + this.getY());
			enemy.updateHitLeft(Math.sqrt(576) * Math.cos((45 + 360 - enemy.rotation + 180)) + this.getX(),
					Math.sqrt(576) * Math.sin((45 + 360 - enemy.rotation + 180)/180*PI) + this.getY(),
					Math.sqrt(450) * Math.cos((360 - enemy.rotation + 180 + 45)/180*PI) + this.getX(),
					Math.sqrt(450) * Math.sin((360 - enemy.rotation + 180 + 45)/180*PI) + this.getY());

			updateHurtRight(Math.sqrt(450) * Math.cos((-45 + 360 - rotation + 180)/180*PI) + this.getX(),
					Math.sqrt(450) * Math.sin((-45 + 360 - rotation + 180)/180*PI) + this.getY(),
					Math.sqrt(576) * Math.cos((360 - rotation + 180 - 45)/180*PI) + this.getX(),
					Math.sqrt(576) * Math.sin((360 - rotation + 180 - 45)/180*PI) + this.getY());
			enemy.updateHurtRight(Math.sqrt(450) * Math.cos((-45 + 360 - enemy.rotation + 180)) + enemy.getX(),
					Math.sqrt(450) * Math.sin((-45 + 360 - enemy.rotation + 180)/180*PI) + enemy.getY(),
					Math.sqrt(576) * Math.cos((360 - enemy.rotation + 180 - 45)/180*PI) + enemy.getX(),
					Math.sqrt(576) * Math.sin((360 - enemy.rotation + 180 - 45)/180*PI) + enemy.getY());

			if (intersects(enemy)&& genFunction > 0)
			{
				fitness += 15;
				setX((int) (Math.random() * worldW));
				setY((int) (Math.random() * worldH));
				setRotation(Math.random() * 360);
				enemy.setX((int) (Math.random() * enemy.worldW));
				enemy.setY((int) (Math.random() * enemy.worldH));
				enemy.setRotation(Math.random() * 360);
				
					//System.out.println("Attacker Coordinates and Rotation: " + getX() + ", " + getY() + ", " + getRotation());
					
					//System.out.println("Enemy Coordinates and Rotation: " + enemy.getX() + ", " + enemy.getY() + ", " + enemy.getRotation());
					
				
			}
			break;

		}
		case 1: {
			updateX();
			updateY();
			action = "forward";
			//if (inCone == 1 && genFunction <=100) {
			//	fitness += 2;
			//}
			if (inCone == 1) {
				fitness += 2 ;
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

	void sees(Gladius glad, int gen) {
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
			//if(gen <=100)
			fitness+= 2;
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
		double[] z4 = new double[5];
		double[] a1 = new double[4];
		double[] a2 = new double[4];
		double[] a3 = new double[4];
		double[] a4 = new double[5];
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
		a1 = sigmoid(z1,4);
		// next layer
		for (int c = 0; c < 4; c++) {
			for (int r = 0; r < 4; r++) {
				z2[c] += theta2[r][c] * a1[r];
			}
		}
		// activation
		a2 = sigmoid(z2,4);
		// next layer
		for (int c = 0; c < 4; c++) {
			for (int r = 0; r < 4; r++) {
				z3[c] += theta3[r][c] * a2[r];
			}
		}
		for (int c = 0; c < 5; c++) {
			for (int r = 0; r < 3; r++) {
				z3[r + 1] += recurActivated[c][r] * stuff[c];
			}
		}
		a3 = sigmoid(z3,4);
		for (int c = 0; c < 5; c++) {
			for (int r = 0; r < 3; r++) {
				z4[c] += recur[r][c] * a3[r + 1];
			}
		}

		a4 = sigmoid(z4,5);
		stuff[0] = a4[0];
		stuff[1] = a4[1];
		stuff[2] = a4[2];
		stuff[3] = a4[3];
		stuff[4] = a4[4];
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
    public int getWorldW(){
    	return worldW;
    }
    public int getWorldH()
    {
    	return worldH;
    }
	public String toStringFormattedMat() {
		StringBuilder bob = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			// System.out.print("{");
			// bob.append("{");
			for (int j = 0; j < 4; j++) {
				// System.out.printf("%.6f ", (float) theta1[i][j]);
				Double gah = new Double(theta1[i][j]);
				bob.append(gah.toString().substring(0, 9) + " ");
				if (j != 3) {
					// System.out.print(", ");
					// bob.append(", ");
				}
			}
			// System.out.print("}");
			// bob.append("}");
			bob.append(System.getProperty("line.separator"));
			// System.out.println();
		}
		// System.out.println();
		// System.out.println();
		// bob.append(System.getProperty("line.separator"));
		bob.append(System.getProperty("line.separator"));
		for (int i = 0; i < 4; i++) {
			// System.out.print("{");
			// bob.append("{");
			for (int j = 0; j < 4; j++) {
				// System.out.printf("%.6f ", (float) theta2[i][j]);
				Double gah = new Double(theta2[i][j]);
				bob.append(gah.toString().substring(0, 9) + " ");
				// if(j!=3)
				// {
				// System.out.print(", ");
				// bob.append(", ");
				// }
			}
			// System.out.print("}");
			// bob.append("}");
			bob.append(System.getProperty("line.separator"));
			// System.out.println();
		}
		// System.out.println();
		// System.out.println();
		// bob.append(System.getProperty("line.separator"));
		bob.append(System.getProperty("line.separator"));
		for (int i = 0; i < 4; i++) {
			// System.out.print("{");
			// bob.append("{");
			for (int j = 0; j < 4; j++) {
				// System.out.printf("%.6f ", (float) theta3[i][j]);
				Double gah = new Double(theta3[i][j]);
				bob.append(gah.toString().substring(0, 9) + " ");
				// if(j!=3)
				// {
				// System.out.print(", ");
				// bob.append(", ");
				// }
			}
			// System.out.print("}");
			// bob.append("}");
			bob.append(System.getProperty("line.separator"));
			// System.out.println();
		}
		bob.append(System.getProperty("line.separator"));
		for (int i = 0; i < 3; i++) {/////////recurrent
			//System.out.print("{");
			//bob.append(System.getProperty("line.separator"));
			for (int j = 0; j < 5; j++) {
				System.out.printf("%.6f ", (float) recur[i][j]);
				Double gah = new Double(recur[i][j]);
				bob.append(gah.toString().substring(0, 9) + " ");
				//if (j != 4) {
					//System.out.print(", ");
					//bob.append(", ");
				//}
			}
			//System.out.print("}");
			//bob.append("}");
			bob.append(System.getProperty("line.separator"));
			System.out.println();
		}
		//System.out.println();
		//System.out.println();
		bob.append(System.getProperty("line.separator"));
		for (int i = 0; i < 5; i++) {/////////recurrent
			//System.out.print("{");
			//bob.append("{");
			for (int j = 0; j < 3; j++) {
				System.out.printf("%.6f ", (float) recurActivated[i][j]);
				Double gah = new Double(recurActivated[i][j]);
				bob.append(gah.toString().substring(0, 9) + " ");
				//if (j != 2) {
				//	System.out.print(", ");
				//	bob.append(", ");
				//}
			}
			//System.out.print("}");
			//bob.append("}");
			bob.append(System.getProperty("line.separator"));
			//System.out.println();
		}
		//System.out.println();
		//System.out.println();
		bob.append(System.getProperty("line.separator"));
		//bob.append(System.getProperty("line.separator"));
		//System.out.println("-----------------------");
		//bob.append("----------------------------------------------");
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
				bob.append(gah.toString().substring(0, 9));
				if (j != 3) {
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
				bob.append(gah.toString().substring(0, 9));
				if (j != 3) {
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
				Double gah = new Double(theta3[i][j]);
				bob.append(gah.toString().substring(0, 9));
				if (j != 3) {
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
		for (int i = 0; i < 3; i++) {/////////recurrent
			System.out.print("{");
			bob.append("{");
			for (int j = 0; j < 5; j++) {
				System.out.printf("%.6f ", (float) recur[i][j]);
				Double gah = new Double(recur[i][j]);
				bob.append(gah.toString().substring(0, 9));
				if (j != 4) {
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
		for (int i = 0; i < 5; i++) {/////////recurrent
			System.out.print("{");
			bob.append("{");
			for (int j = 0; j < 3; j++) {
				System.out.printf("%.6f ", (float) recurActivated[i][j]);
				Double gah = new Double(recurActivated[i][j]);
				bob.append(gah.toString().substring(0, 9));
				if (j != 2) {
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

	
	
	public double[] sigmoid(double[] unactivated, int b) {
		double[] activated = new double[b];
		for (int i = 0; i < b; i++) {
			activated[i] = 1.0 / (1.0 + Math.pow(Math.E, (-1 * unactivated[i])));
		}
		return activated;
	}

	public Gladius clone() {

		return new Gladius(x, y, rotation, worldW, worldH, name, theta1, theta2, theta3, recur, recurActivated, 0);
	}

}

