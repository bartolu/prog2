package hra;

import java.awt.image.BufferedImage;

public class Hrac {
	public static final int SIRKA = 40;
	public static final int VYSKA = 33;
	// velikost skoku hrace
	private static final int KOEF_ZRYCHLENI = 1;
	// rychlost padu hrace
	private static final int KOEF_RYCHLOST = 2;
	private BufferedImage img = null;
	// pocatecni X-ova pozice hrace, nemeni se protoze hrac neskace dopredu a
	// dozadu
	private int x;
	// pocatecni Y-ova pozice hrace, meni se protoze hrac skace nahoru a dolu
	private int y;
	private int rychlost;

	public Hrac(BufferedImage img) {
		this.img = img;
		x = (HraciPlocha.SIRKA / 2) - (SIRKA/2);
		y = HraciPlocha.VYSKA / 2;
		
		rychlost = KOEF_RYCHLOST;
	}
	
	//vola se po narazu do zdi, do kraje okna
	public void reset() {
		y = HraciPlocha.VYSKA / 2;
		rychlost = KOEF_RYCHLOST;
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	
	public void skok(){
		rychlost = -17;
	}
}
