package hra;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.Timer;

import obrazek.Obrazek;
import obrazek.ZdrojObrazku;
import obrazek.ZdrojObrazkuSoubor;

public class HraciPlocha extends JPanel {
	public static final boolean DEBUG = true;
	public static final int VYSKA = 838;
	public static final int SIRKA = 600;

	// rychlost behu pozadi
	public static final int RYCHLOST = -2;
	//TODO
	
	private Hrac hrac;
	private BufferedImage imgPozadi;
	private Timer casovacAnimace;
	private boolean pauza = false;
	private boolean hraBezi = false;
	private int posunPozadiX = 0;

	public HraciPlocha() {
		ZdrojObrazkuSoubor z = new ZdrojObrazkuSoubor();
		z.naplnMapu();
		z.setZdroj(Obrazek.POZADI.getKlic());
		
		try {
			imgPozadi = z.getObrazek();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		z.setZdroj(Obrazek.HRAC.getKlic());
		BufferedImage imgHrac;
		//hrac = new Hrac(null);
		try {
			imgHrac = z.getObrazek();
			hrac = new Hrac(imgHrac);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
		
	}

	public void paint(Graphics g) {
		super.paint(g);
		// dve pozadi za sebe pro plynule prechody
		// prvni
		g.drawImage(imgPozadi, posunPozadiX, 0, null);
		// druhe je posunuto o sirku obrazku
		g.drawImage(imgPozadi, posunPozadiX + imgPozadi.getWidth(), 0, null);
		if (HraciPlocha.DEBUG) {
			g.setColor(Color.RED);
			g.drawString("posunPozadiX="+posunPozadiX, 0, 10);
		}
		
		hrac.paint(g);
		
	}

	private void posun() {
		if (hraBezi && !pauza) {

			hrac.posun();
			// posun pozice pozadi hraci plochy (scrollovani)
			posunPozadiX = posunPozadiX + HraciPlocha.RYCHLOST;
			// kdyz se pozadi cele doposouva, zacni od zacatku
			if (posunPozadiX == -imgPozadi.getWidth()) {
				posunPozadiX = 0;
			}
		}
	}

	private void spustHru() {
		casovacAnimace = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
				posun();
			}
		});

		hraBezi = true;
		casovacAnimace.start();
	}

	public void pripravHraciPlochu() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					//skok hrace
					hrac.skok();
					
				}
				// pauza
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (hraBezi) {
						if (pauza) {
							
							pauza = false;
						} else {
							
							pauza = true;
						}
					} else {
						pripravNovouHru();
						spustHru();
					}
				}
			}
		});

		setSize(SIRKA, VYSKA);

	}

	protected void pripravNovouHru() {
		// TODO Auto-generated method stub

	}
}
