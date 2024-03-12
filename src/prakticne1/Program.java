package prakticne1;

import java.util.ArrayList;
import java.util.List;

/*
 * Data je implementacija beskonacnog spila karata (klase 'Spil' i 'Karta').
 * 
 * Napraviti nit koja uzima jednu po jednu kartu iz spila i deli ih drugim
 * nitima.
 * 
 * Napraviti 12 niti koje predstavljaju igrace. One cekaju da dobiju kartu od
 * dilera, koju potom ispisuju na ekranu i zavrsavaju svoj rad.
 * 
 * Glavna nit kreira i pokrece sve ostale niti posle cega zavrsava svoj rad.
 */

import java.util.Random;

class Karta {

	private String vrednost;

	public Karta(String vrednost) {
		this.vrednost = vrednost;
	}

	@Override
	public String toString() {
		return vrednost;
	}
}

class Spil {

	private static final String[] BOJE = "\u2660,\u2665,\u2666,\u2663".split(",");
	private static final String[] RANGOVI = "2,3,4,5,6,7,8,9,10,J,Q,K,A".split(",");
	private static final String[] DZOKERI = "\u2605,\u2606".split(",");
	private static final Random random = new Random();

	public Karta uzmi() {
		int id = random.nextInt(54);
		if (id == 53) {
			return new Karta(DZOKERI[0]);
		}
		if (id == 52) {
			return new Karta(DZOKERI[1]);
		}
		String boja = BOJE[id / 13];
		String rang = RANGOVI[id % 13];
		return new Karta(rang + boja);
	}
}

class IgracThread extends Thread {
	private volatile Karta k;
	
	public IgracThread(String name) {
		setName(name);
	}
	
	public void primiKartu(Karta k) {
		this.k = k;
	}
	
	@Override
	public void run() {
		while(k == null) {
			
		}
		System.out.printf("%-8s je dobio: %3s%n", getName(), k);
	}
	
}

class DilerThread extends Thread {
	
	List<IgracThread> igraci;
	Spil s;
	
	public DilerThread(List<IgracThread> igraci) {
		this.igraci = igraci;
		this.s = new Spil();
	}
	
	@Override
	public void run() {
		for(IgracThread igrac : igraci) {
			Karta k = s.uzmi();
			igrac.primiKartu(k);
		}
	}
	
}


public class Program {
	public static void main(String[] args) {
		List<IgracThread> igraci = new ArrayList<>();
		for(int i = 1; i <= 12; i++) {
			IgracThread igrac = new IgracThread("Igrac " + i);
			igraci.add(igrac);
			igrac.start();
		}
		
		DilerThread d = new DilerThread(igraci);
		d.start();
		
	}
}





