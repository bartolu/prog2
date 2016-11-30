package hra;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Zed {
	public static final int SIRKA = 40;
	// rychlost pohybu zdi
	public static final int RYCHLOST = -6;
	public static final int MEZERA_MEZI_HORNI_A_DOLNI_CASTI_ZDI = 200;
	public static final int BODY_ZA_ZED = 1;
	private static int vzdalenostPosledniZdi = 0;
	private Random random;

	// TODO
	// ruzne zdi ruzne obrazky nelze => nelze pouzit static
	private static BufferedImage img = null;
	// x-ova souøadnice zdi meni se z prava doleva
	private int x;
	// y-ova souradnice zdi (horni souradnice spodni casti zdi)
	private int y;
	private int vyska;

	public Zed(int vzdaleostZdiOdZacatkuHraciPlochy) {
		this.x = vzdaleostZdiOdZacatkuHraciPlochy;
		random = new Random();
		vygenerujNahodneHodnotyProZed();
	}

	private void vygenerujNahodneHodnotyProZed() {
		// y-ova souradnice horni casti zdi
		y = random.nextInt(HraciPlocha.VYSKA - 400) + MEZERA_MEZI_HORNI_A_DOLNI_CASTI_ZDI;
		// vyska spodni casti zdi
		vyska = HraciPlocha.VYSKA - y;
	}

	public void paint(Graphics g) {
		// spodni cast zdi
		g.drawImage(img, x, y, null);
		g.drawImage(img, x, (-HraciPlocha.VYSKA) + (y - MEZERA_MEZI_HORNI_A_DOLNI_CASTI_ZDI), null);
		
		
	}

	public void posun() {
		// posun zdi
		x += Zed.RYCHLOST;

		// kdyz se zed posunem hraci plochy z prava do leva dostane za konec
		// okna , tak nastav nove hodnoty prop umisteni zdi
		if (x <= 0 - Zed.SIRKA) {
			x = Zed.vzdalenostPosledniZdi;
			// TODO
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public static void setVzdalenostposlednizdi(int vzdalenostPosledniZdi) {
		Zed.vzdalenostPosledniZdi = vzdalenostPosledniZdi;
	}

	public static int getVzdalenostposlednizdi() {
		return vzdalenostPosledniZdi;
	}

	public static void setObrazek(BufferedImage img) {
		Zed.img = img;
	}

	public Rectangle getMezSpodniCastiZdi() {
		return new Rectangle(x,y,SIRKA,vyska);
	}

	public Rectangle getMezHorniCastiZdi() {
		return new Rectangle(x,0,SIRKA,HraciPlocha.VYSKA - ( vyska + MEZERA_MEZI_HORNI_A_DOLNI_CASTI_ZDI));
	}
	
	public void reset(){
		vygenerujNahodneHodnotyProZed();
	}
	
}