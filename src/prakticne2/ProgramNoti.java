package prakticne2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Uzeti implementaciju klasa 'Karta' i 'Spil' iz prvog zadatka i adaptirati ih
 * tako da se mogu koristiti od strane vise procesa istovremeno.
 * 
 * Napraviti i pokrenuti 12 niti koje predstavljaju igrace. Svaka nit uzima
 * jednu kartu sa vrha spila i smesta je u svoje privatno polje. Potom tu kartu
 * stavlja na talon (videti ispod) i ceka da to urade i svi ostali igraci.
 * 
 * Kada su svi igraci stavili svoje karte na talon, nastavljaju izvrsavanje.
 * Svako samostalno proverava da li je imao najjacu kartu i stampa prigodnu
 * poruku o tome. Moze biti vise igraca sa najjacom kartom, gleda se samo rang
 * karte kao i u prethodnim zadacima.
 * 
 * Implementirati klasu 'Talon' koja ima sledece metode i koristiti je za
 * sinhronizaciju igraca:
 * 
 *   void staviKartu(Karta)   - pomocu koje igrac stavlja kartu na talon
 *   void cekajOstale()       - blokira nit dok se na talon ne stavi 12 karata
 *                              ovaj metod baca InterruptedException ako neko
 *                              prekine nit u toku ovog cekanja
 *   boolean jeNajjaca(Karta) - utvrdjuje da li je prosledejna karta najjaca
 * 
 * Glavna nit kreira spil i talon, pokrece sve ostale niti, posle cega zavrsava
 * svoj rad.
 */

public class ProgramNoti {
	public static void main(String[] args) {
		TalonNoti talon = new TalonNoti();
		SpilNoti spil = new SpilNoti();
		spil.promesaj();
		
		List<IgracNoti> igraci = new ArrayList<>();
		for(int i = 1; i <= 12; i++) {
			IgracNoti igrac = new IgracNoti("Igrac " + i, spil, talon);
			igraci.add(igrac);
			igrac.start();
		}
	}
}

class KartaNoti {
	private int rang;
	private String boja;
	
	public KartaNoti(String boja, int rang) {
		if(rang < 2 || rang == 11 || rang > 15) {
			throw new IllegalArgumentException();
		}
		if(!"herc".equals(boja) && !"karo".equals(boja) && !"pik".equals(boja) && !"tref".equals(boja)) {
			throw new IllegalArgumentException();
		}
		
		this.rang = rang;
		this.boja = boja.toUpperCase();
	}
	
	public KartaNoti(boolean uBoji) {
		rang = 16;
		if(uBoji) {
			boja = "U BOJI";
		} else {
			boja = "BEZ BOJE";
		}
	}
	
	public int getRang() {
		return rang;
	}
	
	public String getBoja() {
		return boja;
	}
	
	public boolean jeDzoker() {
		if(rang == 16) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		if(rang == 16) {
			return "DZOKER " + boja;
		}
		if(rang == 15) {
			return "KEC " + boja;
		}
		if(rang == 14) {
			return "KRALJ " + boja;
		}
		if(rang == 13) {
			return "KRALJICA " + boja;
		}
		if(rang == 12) {
			return "ZANDAR " + boja;
		}
		return rang + " " + boja;
	}
	
}

class SpilNoti {
	private List<KartaNoti> karte = new ArrayList<>();
	
	public SpilNoti() {
		for(int rang = 2; rang <= 10; rang ++) {
			karte.add(new KartaNoti("karo", rang));
			karte.add(new KartaNoti("tref", rang));
			karte.add(new KartaNoti("pik", rang));
			karte.add(new KartaNoti("herc", rang));
		}
		for(int rang = 12; rang <= 15; rang ++) {
			karte.add(new KartaNoti("karo", rang));
			karte.add(new KartaNoti("tref", rang));
			karte.add(new KartaNoti("pik", rang));
			karte.add(new KartaNoti("herc", rang));
		}
		
		karte.add(new KartaNoti(false));
		karte.add(new KartaNoti(true));
		
	}
	
	public int velicina() {
		return karte.size();
	}
	
	public KartaNoti uzmiOdGore() {
		return karte.remove(karte.size() - 1);
	}
	
	public void promesaj() {
		Collections.shuffle(karte);
	}
}

/*
 * Implementirati klasu 'Talon' koja ima sledece metode i koristiti je za
 * sinhronizaciju igraca:
 * 
 *   void staviKartu(Karta)   - pomocu koje igrac stavlja kartu na talon
 *   void cekajOstale()       - blokira nit dok se na talon ne stavi 12 karata
 *                              ovaj metod baca InterruptedException ako neko
 *                              prekine nit u toku ovog cekanja
 *   boolean jeNajjaca(Karta) - utvrdjuje da li je prosledejna karta najjaca
 */
class TalonNoti {
	private int br;
	private KartaNoti najjaca;
	
	public synchronized void staviKartu(KartaNoti k) {
		if(najjaca == null || najjaca.getRang() < k.getRang()) {
			najjaca = k;
		}
		br++;
		if(br == 12) {
			notifyAll();
		}
	}
	
	public synchronized void cekajOstale() throws InterruptedException {
		while(br < 12) {
			wait();
		}
	}
	
	public synchronized boolean jeNajjaca(KartaNoti karta) {
		return najjaca.getRang() == karta.getRang();
	}
	
}


/*
 * Kada su svi igraci stavili svoje karte na talon, nastavljaju izvrsavanje.
 * Svako samostalno proverava da li je imao najjacu kartu i stampa prigodnu
 * poruku o tome. Moze biti vise igraca sa najjacom kartom, gleda se samo rang
 * karte kao i u prethodnim zadacima.
 */
class IgracNoti extends Thread {
	private SpilNoti spil;
	private TalonNoti talon;
	private KartaNoti karta;
	
	public IgracNoti(String ime, SpilNoti spil, TalonNoti talon) {
		setName(ime);
		this.spil = spil;
		this.talon = talon;
	}
	
	public void run() {
		karta = spil.uzmiOdGore();
		talon.staviKartu(karta);
		try {
			talon.cekajOstale();
			if(talon.jeNajjaca(karta)) {
				poruka("JE POBEDIO");
			} else {
				poruka("nije pobedio");
			}
		} catch (InterruptedException e) {
			poruka("je prekinut");
		}
	}
	
	private void poruka(String poruka) {
		System.out.println(getName() + " " + poruka + " sa " + karta);
	}
	
}
































