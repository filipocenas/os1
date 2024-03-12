package prakticne2;
/* Uzeti implementaciju klasa 'Karta' i 'Spil' iz prvog zadatka i adaptirati ih
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Program {
	
	public static void main(String[] args) {
		Spil spil = new Spil();
		Talon talon = new Talon();
		spil.promesaj();
		
		List<IgracSync> igraci = new ArrayList<>();
		for(int i = 1; i <= 12; i++) {
			IgracSync igrac = new IgracSync("Igrac " + i, spil, talon);
			igraci.add(igrac);
			igrac.start();
		}
		
	}
	
}

class IgracSync extends Thread {
	private Karta karta;
	private Spil spil;
	private Talon talon;
	
	public IgracSync(String ime, Spil spil, Talon talon) {
		setName(ime);
		this.spil = spil;
		this.talon = talon;
	}
	
	public void run() {
		karta = spil.uzmiOdGore();
		talon.staviKartu(karta);
		talon.cekajOstale();
		if(interrupted()) {
			poruka("je prekinut");
		} else if(talon.jeNajjaca(karta)) {
			poruka("je POBEDIO");
		} else {
			poruka("je izgubio");
		}
	}
	
	private void poruka(String poruka) {
		System.out.println(getName() + " " + poruka + " sa " + karta);
	}
}

class Talon {
	private Karta najjaca;
	private int br;
	
	
	public synchronized void staviKartu(Karta karta) {
		if(najjaca == null || karta.rang > najjaca.rang) {
			najjaca = karta;
		}
		br++;
	}
	
	public void cekajOstale() {
		while(imaJos()) {
			// cekamo ostale nista se ne desava
		}
	}
	
	public synchronized boolean imaJos() {
		return br < 12;
	}
	
	public synchronized boolean jeNajjaca(Karta karta) {
		return najjaca.rang == karta.rang;
	}
	
}

class Karta {
	public final int rang;
	public final String boja;
	
	public Karta(String boja, int rang) {
		if(rang < 2 || rang == 11 || rang > 15) {
			throw new IllegalArgumentException();
		}
		
		if(!"karo".equalsIgnoreCase(boja) && !"tref".equalsIgnoreCase(boja) && !"pik".equalsIgnoreCase(boja) && !"herc".equalsIgnoreCase(boja)) {
			throw new IllegalArgumentException();
		}
		
		this.boja = boja.toUpperCase();
		this.rang = rang;
	}
	
	public Karta(boolean uBoji) {
		this.rang = 16;
		if(uBoji)
			this.boja = "U BOJI";
		else
			this.boja = "BEZ BOJE";
	}
	
	public String getBoja() {
		return boja;
	}
	
	public int getRang() {
		return rang;
	}
	
	public boolean jeDzoker() {
		return this.rang == 16;
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

class Spil {
	
	private List<Karta> karte = new ArrayList<>();
	
	public Spil() {
		
		for(int rang = 2; rang <= 10; rang++) {
			karte.add(new Karta("herc", rang));
			karte.add(new Karta("karo", rang));
			karte.add(new Karta("pik", rang));
			karte.add(new Karta("tref", rang));
		}
		
		for(int rang = 12; rang <= 15; rang++) {
			karte.add(new Karta("herc", rang));
			karte.add(new Karta("karo", rang));
			karte.add(new Karta("pik", rang));
			karte.add(new Karta("tref", rang));
		}
		
		karte.add(new Karta(false));
		karte.add(new Karta(true));
	}
	
	
	public int velicina() {
		return karte.size();
	}
	
	public Karta uzmiOdGore() {
		return karte.remove(karte.size() - 1);
	}
	
	public Karta uzmiOdDole() {
		return karte.remove(0);
	}
	
	public Karta uzmiIzSredine() {
		return karte.remove((int) (Math.random() * (karte.size())));
	}
	
	public void staviGore(Karta k) {
		karte.add(k);
	}
	
	public void staviDole(Karta k) {
		karte.add(0, k);
	}
	
	public void staviUSredinu(Karta k) {
		karte.add((int) (Math.random() * (karte.size() + 1)) ,k);
	}
	
	public void promesaj() {
		Collections.shuffle(karte);
	}
}
