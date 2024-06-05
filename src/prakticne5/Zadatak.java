package prakticne5;

import java.util.ArrayList;
import java.util.List;

/*
 * Promene koje su izvršene:
 * 		1) Dodato je čekanje glavne niti 60 sekundi pre prekidanja ostalih niti.
 * 		2) Prekida se i sačekuje završetak svih niti nakon prekidanja.
 * 		3) Popravljena je metoda iznajmiKosilicu tako da pravilno postavlja zaradu i vraća kosilice čak i u slučaju prekida.
 * 		4) Dodati su odgovarajući try-catch blokovi da se prekidi pravilno obrađuju.
 * 		5) Osigurano je da se niti nazovu odgovarajućim imenima kako bi ispis poruka bio smislen.
 */
public class Zadatak {

	public static void main(String[] args) {
		Kosilica kosilica = new Kosilica();

		List<Thread> threads = new ArrayList<>();

		for (int i = 1; i <= 25; i++) {
			PrekidivaMusterija pm = new PrekidivaMusterija("Prekidiva " + i, kosilica);
			NeprekidivaMusterija nm = new NeprekidivaMusterija(kosilica);
			Thread t = new Thread(nm, "Neprekidiva " + i);

			threads.add(pm);
			threads.add(t);

			pm.start();
			t.start();
		}

		try {
			Thread.sleep(60000); // Glavna nit čeka 60 sekundi
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (Thread t : threads) {
			t.interrupt(); // prekida sve niti
		}

		for (Thread t : threads) {
			try {
				t.join(); // čeka završetak svih niti
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Ukupna zarada (20 din/s): " + kosilica.getZarada());
		System.out.println("Ukupna zarada (50 din/s): " + kosilica.getDrugaZarada());
	}
}

class Kosilica {
	private int dostupneKosilice = 5;
	private int zarada = 0;
	private final int cenaPoSekundi = 20;
	private final int visokaCena = 50;

	public synchronized void iznajmiKosilicu(int vreme) throws InterruptedException {
		while (dostupneKosilice == 0) {
			System.out.println(poruka(Thread.currentThread().getName() + " ceka kosilicu"));
			wait();
		}
		dostupneKosilice--;
		try {
			Thread.sleep(vreme * 1000);
			zarada += vreme * cenaPoSekundi;
		} finally {
			dostupneKosilice++;
			notifyAll();
		}
	}

	public String poruka(String poruka) {
		return poruka + " Ukupna zarada: " + getZarada() + " | Zarada (50rsd): " + getDrugaZarada();
	}

	public synchronized int getZarada() {
		return zarada;
	}

	public synchronized int getDrugaZarada() {
		return (zarada / cenaPoSekundi) * visokaCena;
	}
}

class PrekidivaMusterija extends Thread {
	private Kosilica kosilica;

	public PrekidivaMusterija(String name, Kosilica k) {
		super(name);
		this.kosilica = k;
	}

	@Override
	public void run() {
		int vremeIznajmljivanja = (int) (10 + Math.random() * 11);
		try {
			kosilica.iznajmiKosilicu(vremeIznajmljivanja);
			System.out.println(kosilica.poruka(Thread.currentThread().getName() + " kosi travu..."));
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getName() + " je prekinuta.");
			Thread.currentThread().interrupt();
		}
	}
}

class NeprekidivaMusterija implements Runnable {
	private Kosilica kosilica;

	public NeprekidivaMusterija(Kosilica kosilica) {
		this.kosilica = kosilica;
	}

	@Override
	public void run() {
		int vremeIznajmljivanja = (int) (10 + Math.random() * 11);
		try {
			kosilica.iznajmiKosilicu(vremeIznajmljivanja);
			System.out.println(kosilica.poruka(Thread.currentThread().getName() + " kosi travu..."));
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getName() + " je prekinuta.");
			Thread.currentThread().interrupt();
		}
	}
}
