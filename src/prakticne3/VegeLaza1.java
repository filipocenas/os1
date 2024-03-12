package prakticne3;
/*
 * 
 * Napisati program u Javi koji simulira rad vrlo popularnog vegetarijanskog
 * restorana "Vege Laza".
 * 
 * U danasnjoj ponudi restorana su tri obroka: vegetarijanski sendvic (230 din),
 * potaz od bundeve (340 din) i grilovani tofu sa povrcem (520 din).
 * 
 * U restoranu u prodaji i pripremi porcija rade dve konobarice: Rada i Dara.
 * One svakih 10 sekundi obrade po jednu porudzbinu.
 * 
 * Za vegetarijanski sendvic je od sastojaka potrebno: dva parceta domaceg
 * hleba, jedan komad grilovanog tofu sira i 100 grama povrca.
 * 
 * Za porciju potaza od bundeve je potrebno pola litre potaza i jedno parce
 * domaceg hleba.
 * 
 * Za grilovani tofu sa povrcem je potreban jedan komad grilovanog tofu sira i
 * 300g povrca.
 * 
 * U kuhinji, na pripremi povrca radi Miki koji za 90 sekundi isecka kilu
 * povrca. Takodje, Mica svaka 4 minuta skuva 10 litara potaza, a Joki isece
 * 6 parcadi hleba svaki minut. Na rostilju rade Vule i Gule koji svaki minut
 * ispeku po komad tofu sira.
 * 
 * Napisati program koji kreira svakog od zaposlenih kao poseban proces i
 * pokrece ih. 5 minuta kasnije, prekida procese koji uredno zavrsavaju svoj
 * rad, posle cega program ispisuje ukupnu sumu novca koju je restoran zaradio.
 * 
 */
public class VegeLaza1 {
	public static void main(String[] args) throws InterruptedException {

		Thread.currentThread().setName("Laza");
		Kuhinja1 kuhinja = new Kuhinja1();

		Konobarica1 k1 = new Konobarica1(kuhinja);
		k1.setName("Rada");
		Konobarica1 k2 = new Konobarica1(kuhinja);
		k2.setName("Dara");
		Thread s = new Secko1(kuhinja);
		s.setName("Miki");
		Thread p = new PotazMajstorica1(kuhinja);
		p.setName("Mica");
		Thread h = new Hlebadzija(kuhinja);
		h.setName("Joki");

		Thread r1 = new Rostiljdzija1(kuhinja);
		r1.setName("Vule");
		Thread r2 = new Rostiljdzija1(kuhinja);
		r2.setName("Gule");

		k1.start(); k2.start();
		s.start(); p.start(); h.start();
		r1.start(); r2.start();

		Thread.sleep(300_00);

		k1.interrupt(); k2.interrupt();
		s.interrupt(); p.interrupt(); h.interrupt();
		r1.interrupt(); r2.interrupt();

		k1.join(); k2.join();
		s.join(); p.join(); h.join();
		r1.join(); r2.join();

		System.out.printf("Ukupan pazar: %d din%n", kuhinja.uzmiPazar());

	}
}

class Kuhinja1 {
	
	int parcadiHleba;
	int komadaSira;
	int gramaPovrca;
	int mlPotaza;
	int dinaraPazara;
	
	private final Object objekatZaHleb = new Object(); 
	private final Object objekatZaPovrce = new Object(); 
	private final Object objekatZaPotaz = new Object(); 
	private final Object objekatZaSir = new Object(); 
	private final Object objekatZaPazar = new Object();
	
	private void stampajPoruku(String poruka) {
		System.out.printf("%4s %-30s Stanje u kuhinji %5d %5d %2d %1d %5d%n",
				Thread.currentThread().getName(), poruka, gramaPovrca,
				mlPotaza, komadaSira, parcadiHleba, dinaraPazara);
	}
	
	public Kuhinja1() {
		stampajPoruku("je otvorio restoran");
	}
	
	public void dodajPovrce(int kolicina) {
		synchronized (objekatZaPovrce) {
			gramaPovrca += kolicina;
			objekatZaPovrce.notifyAll();
			stampajPoruku("je isekao povrce");
		}
	}
	
	public void dodajPotaz(int kolicina) {
		synchronized (objekatZaPotaz) {
			mlPotaza += kolicina;
			objekatZaPotaz.notifyAll();
			stampajPoruku("je skuvala potaz");
		}
	}
	
	public void dodajHleb(int kolicina) {
		synchronized (objekatZaHleb) {
			parcadiHleba += kolicina;
			objekatZaHleb.notifyAll();
			stampajPoruku("je isekao hleb");
		}
	}
	
	public void ispeciSir(int kolicina) {
		synchronized (objekatZaSir) {
			komadaSira += kolicina;
			objekatZaSir.notifyAll();
			stampajPoruku("je ispekao sir");
		}
	}
	
	public void napraviSendvic() throws InterruptedException {
		synchronized (objekatZaHleb) {
			while (parcadiHleba < 2) {
				stampajPoruku("ceka hleb za sendvic");
				objekatZaHleb.wait();
			}
			parcadiHleba -= 2;
		}
		
		synchronized (objekatZaPovrce) {
			while (gramaPovrca < 100) {
				stampajPoruku("ceka povrce za sendvic");
				objekatZaPovrce.wait();
			}
			gramaPovrca -= 100;
		}
		
		synchronized (objekatZaSir) {
			while(komadaSira < 1) {
				stampajPoruku("ceka sir za sendvic");
				objekatZaSir.wait();
			}
			komadaSira -= 1;
		}
		
		synchronized (objekatZaPazar) {
			dinaraPazara += 230;
			stampajPoruku("je posluzila sendvic");
		}
	}
	
	public void napraviPorcijuTofua() throws InterruptedException {
		synchronized (objekatZaSir) {
			while(komadaSira < 1) {
				stampajPoruku("ceka tofu");
				objekatZaSir.wait();
			}
			komadaSira -= 1;
		}
		
		synchronized (objekatZaPovrce) {
			while(gramaPovrca < 300) {
				stampajPoruku("ceka povrce za porciju tofua");
			}
		}
		
		synchronized (objekatZaPazar) {
			dinaraPazara += 520;
			stampajPoruku("je posluzila porciju tofua");
		}
	}
	
	public void napraviPotaz() throws InterruptedException {
		synchronized (objekatZaPotaz) {
			while(mlPotaza < 500) {
				stampajPoruku("ceka potaz");
				objekatZaPotaz.wait();
			}
			mlPotaza -= 500;
		}
		
		synchronized (objekatZaHleb) {
			while(parcadiHleba < 1) {
				stampajPoruku("ceka hleb za potaz");
				objekatZaHleb.wait();
			}
			parcadiHleba -= 1;
		}
		
		synchronized (objekatZaPazar) {
			dinaraPazara += 340;
			stampajPoruku("posluzila je porciju potaza");
		}
	}
	
	public int uzmiPazar() {
		synchronized (objekatZaPazar) {
			int pare = dinaraPazara;
			dinaraPazara = 0;
			stampajPoruku("je uzeo pare od pazara");
			return pare;
		}
	}
	
}

class Konobarica1 extends Thread {

	private final Kuhinja1 kuhinja;

	public Konobarica1(Kuhinja1 kuhinja) {
		this.kuhinja = kuhinja;
	}

	@Override
	public void run() {
		try {
			while (!interrupted()) {
				sleep(10_00);
				double porudzbina = Math.random();
				if (porudzbina < 0.4) {
					kuhinja.napraviSendvic();
				} else if (porudzbina < 0.7) {
					kuhinja.napraviPotaz();;
				} else {
					kuhinja.napraviPorcijuTofua();
				}
			}
		} catch (InterruptedException e) {
			// Izlazimo iz petlje i zavrsavamo rad
		}
	}
}

class Secko1 extends Thread {

	private final Kuhinja1 kuhinja;

	public Secko1(Kuhinja1 kuhinja) {
		this.kuhinja = kuhinja;
	}

	@Override
	public void run() {
		try {
			while (!interrupted()) {
				sleep(90_00);
				kuhinja.dodajPovrce(1000);
			}
		} catch (InterruptedException e) {
			// Izlazimo iz petlje i zavrsavamo rad
		}
	}
}

class PotazMajstorica1 extends Thread {

	private final Kuhinja1 kuhinja;

	public PotazMajstorica1(Kuhinja1 kuhinja2) {
		this.kuhinja = kuhinja2;
	}

	@Override
	public void run() {
		try {
			while (!interrupted()) {
				sleep(240_00);
				kuhinja.dodajPotaz(10000);
			}
		} catch (InterruptedException e) {
			// Izlazimo iz petlje i zavrsavamo rad
		}
	}
}

class Hlebadzija extends Thread {

	private final Kuhinja1 kuhinja;

	public Hlebadzija(Kuhinja1 kuhinja2) {
		this.kuhinja = kuhinja2;
	}

	@Override
	public void run() {
		try {
			while (!interrupted()) {
				sleep(60_00);
				kuhinja.dodajHleb(6);
			}
		} catch (InterruptedException e) {
			// Izlazimo iz petlje i zavrsavamo rad
		}
	}
}

class Rostiljdzija1 extends Thread {

	private final Kuhinja1 kuhinja;

	public Rostiljdzija1(Kuhinja1 kuhinja) {
		this.kuhinja = kuhinja;
	}

	@Override
	public void run() {
		try {
			while (!interrupted()) {
				sleep(60_00);
				kuhinja.ispeciSir(1);
			}
		} catch (InterruptedException e) {
			// Izlazimo iz petlje i zavrsavamo rad
		}
	}
}







