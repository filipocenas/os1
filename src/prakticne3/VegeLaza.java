package prakticne3;

public class VegeLaza {
	public static void main(String[] args) throws InterruptedException {
		Thread.currentThread().setName("Laza");
		Kuhinja kuhinja = new Kuhinja();
		
		Konobarica k1 = new Konobarica(kuhinja);
		k1.setName("Rada");
		Konobarica k2 = new Konobarica(kuhinja);
		k2.setName("Dara");
		Secko s = new Secko(kuhinja);
		s.setName("Miki");
		Kuvarica k = new Kuvarica(kuhinja);
		k.setName("Mica");
		Hlebaros h = new Hlebaros(kuhinja);
		h.setName("Joki");
		Rostiljdzija r = new Rostiljdzija(kuhinja);
		r.setName("Vule");
		Rostiljdzija r1 = new Rostiljdzija(kuhinja);
		r1.setName("Gule");
		
		k1.start(); k2.start();
		s.start(); k.start(); h.start();
		r.start(); r1.start();
		
		Thread.sleep(30_000);
		
		k1.interrupt(); k2.interrupt();
		s.interrupt(); k.interrupt(); h.interrupt();
		r1.interrupt(); r.interrupt();

		k1.join(); k2.join();
		s.join(); k.join(); h.join();
		r1.join(); r.join();

		System.out.printf("Ukupan pazar: %d din%n", kuhinja.uzmiPazar());
	}
}

class Kuhinja {
	private int parcadiHleba;
	private int komadaSira;
	private int gramaPovrca;
	private int mlPotaza;
	private int dinaraPazara;
	
	private void stampajPoruku(String poruka) {
		System.out.printf("%4s %-30s Stanje u kuhinji: %5d %5d %2d %2d %5d%n",
				Thread.currentThread().getName(),
				poruka,
				gramaPovrca,
				mlPotaza,
				parcadiHleba,
				komadaSira,
				dinaraPazara);
	}
	
	public Kuhinja() {
		stampajPoruku("je otvorio restoran");
	}
	
	public synchronized void dodajPovrce(int kolicina) {
		gramaPovrca += kolicina;
		notifyAll();
		stampajPoruku("je isekao povrce");
	}
	public synchronized void dodajHleb(int kolicina) {
		parcadiHleba += kolicina;
		notifyAll();
		stampajPoruku("je isekao hleb");
	}
	public synchronized void ispeciTofu(int kolicina) {
		komadaSira += kolicina;
		notifyAll();
		stampajPoruku("je ispekao tofu");
	}
	public synchronized void dodajPotaz(int kolicina) {
		mlPotaza += kolicina;
		notifyAll();
		stampajPoruku("je skuvala potaz");
	}
	
	public synchronized void napraviSendvic() throws InterruptedException {
		while(parcadiHleba < 2 || komadaSira < 1 || gramaPovrca < 100) {
			wait();
			stampajPoruku("ceka sastojke za sendvic");
		}
		parcadiHleba -= 2;
		komadaSira -= 1;
		gramaPovrca -= 100;
		dinaraPazara += 230;
		stampajPoruku("je posluzila sendvic");
	}
	
	public synchronized void napraviGrilovaniTofu() throws InterruptedException {
		while(komadaSira < 1 || gramaPovrca < 300) {
			wait();
			stampajPoruku("ceka sastojke za grilovani tofu");
		}
		komadaSira -= 1;
		gramaPovrca -= 300;
		dinaraPazara += 520;
		stampajPoruku("je posluzila grilovani tofu");
	}
	
	public synchronized void napraviPotaz() throws InterruptedException {
		while(mlPotaza < 500 || parcadiHleba < 1) {
			wait();
			stampajPoruku("ceka sastojeke za potaz");
		}
		mlPotaza -= 500;
		parcadiHleba -= 1;
		dinaraPazara += 340;
		stampajPoruku("je posluzila potaz");
	}
	
	public synchronized int uzmiPazar() {
		int pare = dinaraPazara;
		dinaraPazara = 0;
		stampajPoruku("je uzeo pare od pazara");
		return pare;
	}
	
}

class Konobarica extends Thread {
	private final Kuhinja kuhinja;
	
	public Konobarica(Kuhinja kuhinja) {
		this.kuhinja = kuhinja;
	}
	
	@Override
	public void run() {
		try {
			while(!interrupted()) {
				sleep(1000);
				double porudzbina = Math.random();
				if(porudzbina < 0.4) {
					kuhinja.napraviGrilovaniTofu();
				} else if(porudzbina < 0.7) {
					kuhinja.napraviPotaz();
				} else {
					kuhinja.napraviSendvic();
				}
			}
		} catch (InterruptedException e) {
			//Izlazimo iz petlje zavrsavamo rad
		}
	}
}

class Secko extends Thread {
	private final Kuhinja kuhinja;
	
	public Secko(Kuhinja kuhinja) {
		this.kuhinja = kuhinja;
	}
	
	@Override
	public void run() {
		try {
			while(!interrupted()) {
				sleep(9000);
				kuhinja.dodajPovrce(1000);
			}
		} catch (InterruptedException e) {

		}
	}
}

class Kuvarica extends Thread {
	private final Kuhinja kuhinja;
	
	public Kuvarica(Kuhinja kuhinja) {
		this.kuhinja = kuhinja;
	}
	
	@Override
	public void run() {
		try {
			while(!interrupted()) {
				sleep(24000);
				kuhinja.dodajPotaz(10000);
			}
		} catch (InterruptedException e) {
			
		}
	}
}

class Hlebaros extends Thread {
	private final Kuhinja kuhinja;
	
	public Hlebaros(Kuhinja kuhinja) {
		this.kuhinja = kuhinja;
	}
	
	@Override
	public void run() {
		try {
			while(!interrupted()) {
				sleep(6000);
				kuhinja.dodajHleb(6);
			}
		} catch (InterruptedException e) {
			
		}
	}
}

class Rostiljdzija extends Thread {
	private final Kuhinja kuhinja;
	
	public Rostiljdzija(Kuhinja kuhinja) {
		this.kuhinja = kuhinja;
	}
	
	@Override
	public void run() {
		try {
			while(!interrupted()) {
				sleep(6000);
				kuhinja.ispeciTofu(1);
			}
		} catch (InterruptedException e) {
		}
	}
}










































