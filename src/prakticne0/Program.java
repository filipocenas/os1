package prakticne0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/*
 *  Implementirati klasu 'Karta' sa osobinama 'boja' i 'rang' koje predstavljaju
 * standardne osobine karata klasicnog spila od 52+2 karte.
 * 
 * Potrebno je predstaviti sledece boje: pik, karo, herc i tref, dok su dozvo-
 * ljene vrednosti za rang poredjane po velicini: brojevi od 2 do 10, zandar,
 * kraljica, kralj i kec. Takodje je potrebno predstaviti i dva dzokera, jedan
 * u boji, jedan ne.
 * 
 * Implementirati klasu 'Spil' ciji konstruktor kreira nov spil koji sadrzi sve
 * 54 razlicite karte. Takodje, implementirati sledece operacije:
 * 
 *   int velicina()            - vraca broj karata trenutno u spilu
 *   Karta uzmiOdGore()        - ukljanja gornju kartu i vraca je kao rezultat
 *   Karta uzmiOdDole()        - ukljanja donju kartu i vraca je kao rezultat
 *   Karta uzmiIzSredine()     - ukljanja nasumicno izabranu kartu i vraca je
 *   void staviGore(Karta)     - dodaje kartu na vrh spila
 *   void staviDole(Karta)     - dodaje kartu na dno spila
 *   void staviUSredinu(Karta) - dodaje kartu na nasumicno izabrao mesto u spilu
 *   void promesaj()           - nasumicno rasporedjuje karte trenutno u spilu
 * 
 * Napisati program koji implementira sledecu igru za 12 igraca. Igraci redom
 * vuku po jednu kartu sa vrha spila i okrecu je. Program ispisuje koji igrac
 * je izvukao koju kartu. Pobednik je onaj igrac (ili igraci) cija je karta
 * najjaca, pri cemu se ne gleda boja karte a dzokeri su jaci od svih ostalih
 * karata. Ako je bilo vise pobednika igra se ponavlja samo sa pobednicima dok
 * ne ostane samo jedan. Program ispisuje ime konacnog pobednika.
 * 
 * Unapred smisliti imena za igrace, kreirati jedan spil i promesati ga pre
 * igre. Pretpostaviti da u toku igre nece nestati karata u spilu.
 */

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


/*
 * Napisati program koji implementira sledecu igru za 12 igraca. Igraci redom
 * vuku po jednu kartu sa vrha spila i okrecu je. Program ispisuje koji igrac
 * je izvukao koju kartu. Pobednik je onaj igrac (ili igraci) cija je karta
 * najjaca, pri cemu se ne gleda boja karte a dzokeri su jaci od svih ostalih
 * karata. Ako je bilo vise pobednika igra se ponavlja samo sa pobednicima dok
 * ne ostane samo jedan. Program ispisuje ime konacnog pobednika.
 * 
 * Unapred smisliti imena za igrace, kreirati jedan spil i promesati ga pre
 * igre. Pretpostaviti da u toku igre nece nestati karata u spilu.
 */

public class Program {
	public static void main(String[] args) {
		List<String> igraci = new ArrayList<>();
		for(int i = 1; i <= 12; i++) {
			igraci.add("Igrac " + i);
		}
		
		Spil s = new Spil();
		s.promesaj();
		
		while(igraci.size() > 1) {
			List<Karta> karte = new ArrayList<>();
			for(String igrac: igraci) {
				Karta k = s.uzmiOdGore();
				karte.add(k);
				System.out.println(igrac + ": " + k);
			}
			
			
			int najjaca = 0;
			for(Karta k: karte) {
				if(k.getRang() > najjaca) {
					najjaca = k.getRang();
				}
			}
			
			for(int i = igraci.size() - 1; i >= 0; i--) {
				if(karte.get(i).getRang() != najjaca) {
					igraci.remove(i);
				}
			}
			
			System.out.println();
		}
		
		System.out.println("Pobednik je: " + igraci.get(0));
		
	}
}

