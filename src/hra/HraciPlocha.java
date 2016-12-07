package hra;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import obrazek.ManagerObrazku;
import obrazek.Obrazek;
import obrazek.ZdrojObrazku;
import obrazek.ZdrojObrazkuSoubor;

public class HraciPlocha extends JPanel {
	private static final long serialVersionUID = 1L;
	public static final boolean DEBUG = true;
	public static final int VYSKA = 838;
	public static final int SIRKA = 600;

	// rychlost behu pozadi
	public static final int RYCHLOST = -2;

	// musi byt alespon 3 zdi jinak se prvni zed "nestihne posunout za levy
	// okraj = nestihne zajet za levy okraj hraci plochy drive nez potreba ji
	// posunout pred pravy okraj hraci plochy a vykreslit"

	public static final int POCET_ZDI = 4;
	private SeznamZdi seznamZdi;
	private Zed aktualniZed;
	private Zed predchoziZed;
	private int score;
	private JLabel lbScore;
	private JLabel lbZprava;
	private Font font;
	private Font fontZpravy;
	private Hrac hrac;
	private BufferedImage imgPozadi;
	private Timer casovacAnimace;
	private boolean pauza = false;
	private boolean hraBezi = false;
	private int posunPozadiX = 0;

	public HraciPlocha(ManagerObrazku mo) {
		imgPozadi = mo.getObrazek(Obrazek.POZADI);
		hrac = new Hrac(mo.getObrazek(Obrazek.HRAC));
		Zed.setObrazek(mo.getObrazek(Obrazek.ZED));
		seznamZdi = new SeznamZdi();
		this.vyrobFontyALabely();
	}

	private void vyrobZdi(int pocet) {
		Zed zed;
		int vzdalenost = HraciPlocha.SIRKA;

		for (int i = 0; i < pocet; i++) {
			zed = new Zed(vzdalenost);
			seznamZdi.add(zed);
			vzdalenost = vzdalenost + HraciPlocha.SIRKA / 2;
		}

		vzdalenost = vzdalenost - HraciPlocha.SIRKA - Zed.SIRKA;
		Zed.setVzdalenostposlednizdi(vzdalenost);

	}

	private void vyrobFontyALabely() {
		font = new Font("Arial", Font.BOLD, 40);
		fontZpravy = new Font("Arial", Font.BOLD, 20);

		this.setLayout(new BorderLayout());

		lbZprava = new JLabel("");
		lbZprava.setFont(fontZpravy);
		lbZprava.setForeground(Color.red);
		lbZprava.setHorizontalAlignment(SwingConstants.CENTER);

		lbScore = new JLabel("0");
		lbScore.setFont(font);
		lbScore.setForeground(Color.YELLOW);
		lbScore.setHorizontalAlignment(SwingConstants.CENTER);

		this.add(lbScore, BorderLayout.NORTH);
		this.add(lbZprava, BorderLayout.CENTER);
		System.out.println("a");
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
			g.drawString("posunPozadiX=" + posunPozadiX, 0, 10);
		}

		for (Zed zed : seznamZdi) {
			zed.paint(g);
		}
		hrac.paint(g);

		lbScore.paint(g);
		lbZprava.paint(g);
	}

	private void posun() {
		if (hraBezi && !pauza) {
			// nastav zed v poradi
			aktualniZed = seznamZdi.getAktualniZed();

			// nastav predchozi zed
			predchoziZed = seznamZdi.getPredchoziZed();

			// detekce kolizi

			if (isKolizeSeZdi(predchoziZed, hrac) || isKolizeSeZdi(aktualniZed, hrac)
					|| isKolizeSHraniciHraciPlochy(hrac)) {
				ukonciAVyresetujHruPoNarazu();
			} else {

				for (Zed zed : seznamZdi) {
					zed.posun();
				}
				hrac.posun();

				// hrac prosel zdi bez narazu
				// zjistit kde se nachazi, bud pred aktualnio zdi - nedelej nic
				// nebo za aktualni zdi -posun dalsi zed v poradi a prepocitej
				// score

				if (hrac.getX() >= aktualniZed.getX()) {
					seznamZdi.nastavDalsiZedNaAktualni();
					zvedniScore();
					lbScore.setText(score + "");

				}
			}

			// posun pozice pozadi hraci plochy (scrollovani)
			posunPozadiX = posunPozadiX + HraciPlocha.RYCHLOST;
			// kdyz se pozadi cele doposouva, zacni od zacatku
			if (posunPozadiX == -imgPozadi.getWidth()) {
				posunPozadiX = 0;
			}
		}
	}

	private void ukonciAVyresetujHruPoNarazu() {
		hraBezi = false;
		casovacAnimace.stop();
		casovacAnimace = null;
		vyresetujHru();
		nastavZpravyNarazuDoZdi();

	}

	private boolean isKolizeSeZdi(Zed zed, Hrac hrac) {
		return false;
		// return (zed.getMezHorniCastiZdi().intersects(hrac.getMez())) ||
		// (zed.getMezSpodniCastiZdi().intersects(hrac.getMez()));
	}

	private boolean isKolizeSHraniciHraciPlochy(Hrac hrac) {
		return (hrac.getY() <= 0) || (hrac.getY() >= HraciPlocha.VYSKA - hrac.getVyskaHrace());
	}

	private void spustHru() {
		casovacAnimace = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
				posun();
			}

		});

		nastavZpravuPrazdna();
		hraBezi = true;
		casovacAnimace.start();
	}

	public void pripravHraciPlochu() {
		nastavZpravuOvladani();
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					// skok hrace
					hrac.skok();

				}
				// pauza
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (hraBezi) {
						if (pauza) {
							nastavZpravuPrazdna();
							pauza = false;
						} else {
							nastavZpravuPauza();
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

	private void pripravNovouHru() {
		vyresetujHru();
	}

	private void vyresetujHru() {
		resetujVsechnyZdi();
		hrac.reset();
		// nejprve zobraz stare score, aby hrac videl kolik bodu nasbiral
		lbScore.setText(score + "");
		// ale score pak vynuluj
		vynujulScore();
	}

	private void vynujulScore() {
		score = 0;

	}

	private void zvedniScore() {
		score = score + Zed.BODY_ZA_ZED;
		;

	}

	private void resetujVsechnyZdi() {
		seznamZdi.clear();
		vyrobZdi(POCET_ZDI);

	}

	private void nastavZpravyNarazuDoZdi() {
		lbZprava.setFont(font);
		lbZprava.setText("Narazil jsi zkus to znovu");
	}

	private void nastavZpravuPauza() {
		lbZprava.setFont(font);
		lbZprava.setText("Pauza");
	}

	private void nastavZpravuOvladani() {
		lbZprava.setFont(fontZpravy);
		lbZprava.setText("pravy klik = start/stop, levy klik = skok");
	}

	private void nastavZpravuPrazdna() {
		lbZprava.setFont(font);
		lbZprava.setText("");
	}

}
