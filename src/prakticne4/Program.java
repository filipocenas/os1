package prakticne4;

/*
 * 
 * Napisati program koji kreira jedan izvidjacki kamp i 24 izvidjaca. 12 izvi-
 * djaca je definisano pomocu klase Thread kao pozadinski procesi a 12 pomocu
 * interfejsa Runnable kao korisnicki procesi. Kamp nije zaseban proces. (5 poena)
 * 
 * 12 izvidjaca definisano pomocu klase Thread po pokretanju idu u sumu da traze
 * pecurke, tj. pozivaju metod Suma::traziPecurke 20 puta, i posle svakog poziva
 * donose pecurke koje su pronasli nazad u kamp (Kamp::donesiPecurke). Ako ih
 * neko prekine u potrazi za pecurkama, hitno se vracaju u kamp bez pecuraka i
 * regularno zavrsavaju svoj rad. (5 poena)
 * 
 * 12 izvidjaca definisano pomocu interfejsa Runnable po pokretanju idu u sumu
 * da traze drva za korpe, tj. pozivaju metod Suma::traziDrva 25 puta, i posle
 * svakog poziva donose sakupljena drva nazad u kamp (Kamp::donesiDrva). Ako ih
 * neko prekine u potrazi, izvidjaci se ne obaziru na to i regularno zavrsavaju
 * svoj rad kada obave svih 25 potraga. (5 poena)
 * 
 * Sinhronizovati klasu Kamp tako da se ni u kom slucaju ne izgube pecurke ili
 * drva. Takodje, ne dozvoliti da u bilo kom trenutku u kampu bude vise saka
 * pecuraka nego sto je doneto drva za pravljenje korpi. (10 poena)
 * 
 * Obratiti paznju na elegantnost i objektnu orijentisanost realizacije i stil
 * resenja. Za program koji se ne kompajlira, automatski se dobija 0 poena bez
 * daljeg pregledanja.
 */


/*
 * Napisati program koji kreira jedan izvidjacki kamp i 24 izvidjaca. 12 izvi-
 * djaca je definisano pomocu klase Thread kao pozadinski procesi a 12 pomocu
 * interfejsa Runnable kao korisnicki procesi. Kamp nije zaseban proces. (5 poena)
 */
public class Program {
	public static void main(String[] args) {
		Suma suma = new Suma();
		Kamp kamp = new Kamp();
		
		for(int i = 1; i <= 12; i++) {
			SakupljacDrva d = new SakupljacDrva(suma, kamp);
			Thread t = new Thread(d);
			t.setName("Drvo " + i);
			SakupljacPecuraka p = new SakupljacPecuraka("Pecurka " + i, suma, kamp);
			
			t.start();
			p.start();
		}
	}
}

class Kamp {
	
	private int pecurke = 0;
	private int drva = 0;
	
	
	public synchronized void donesiPecurke(int kolicina) {
		try {
			while(pecurke + kolicina > drva) {
				System.out.println(Thread.currentThread().getName() + " ceka drva...");
				wait();
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		pecurke += kolicina;
	}
	
	public synchronized void donesiDrva(int kolicina) {
		drva += kolicina;
//		notifyAll();
	}
}

class Suma {
	public int traziPecurke() {
		System.out.println(Thread.currentThread().getName() + " trazi pecurke...");
		try {
			Thread.sleep((long) (500 + 500 * Math.random()));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		return (int) (Math.random() * 3);
	}
	
	public int traziDrva() {
		System.out.println(Thread.currentThread().getName() + " trazi drva");
		try {
			Thread.sleep((long) (500 + 500 * Math.random()));
		} catch (InterruptedException e) {
			// TODO: handle exception
			Thread.currentThread().interrupt();
		}
		return (int) (Math.random() * 3);
	}
}


/*
 * 12 izvidjaca definisano pomocu klase Thread po pokretanju idu u sumu da traze
 * pecurke, tj. pozivaju metod Suma::traziPecurke 20 puta, i posle svakog poziva
 * donose pecurke koje su pronasli nazad u kamp (Kamp::donesiPecurke). Ako ih
 * neko prekine u potrazi za pecurkama, hitno se vracaju u kamp bez pecuraka i
 * regularno zavrsavaju svoj rad. (5 poena)
 */
class SakupljacPecuraka extends Thread {
	private Suma suma;
	private Kamp kamp;
	
	public SakupljacPecuraka(String ime, Suma suma, Kamp kamp) {
		setName(ime);
		this.suma = suma;
		this.kamp = kamp;
	}
	
	@Override
	public void run() {
		int ukupno = 0;
		for(int i = 0; i < 20 && !Thread.interrupted(); i++) {
			int kolicina = suma.traziPecurke();
			kamp.donesiPecurke(kolicina);
			System.out.println(Thread.currentThread().getName() + " je doneo " + kolicina + " pecuraka.");
			ukupno += kolicina;
		}
		System.out.println(Thread.currentThread().getName() + " je UKUPNO doneo " + ukupno + " pecuraka.");
	}
}


/*
 * 12 izvidjaca definisano pomocu interfejsa Runnable po pokretanju idu u sumu
 * da traze drva za korpe, tj. pozivaju metod Suma::traziDrva 25 puta, i posle
 * svakog poziva donose sakupljena drva nazad u kamp (Kamp::donesiDrva). Ako ih
 * neko prekine u potrazi, izvidjaci se ne obaziru na to i regularno zavrsavaju
 * svoj rad kada obave svih 25 potraga. (5 poena)
 */
class SakupljacDrva implements Runnable {
	
	private Suma suma;
	private Kamp kamp;
	
	public SakupljacDrva(Suma suma, Kamp kamp) {
		this.kamp = kamp;
		this.suma = suma;
	}
	
	@Override
	public void run() {
		int count = 0;
		int ukupno = 0;
		for(int i = 0; i < 25; i++) {
			int kolicina = suma.traziDrva();
			count++;
			System.out.println("---> " + Thread.currentThread().getName() + " je nasao drva " + count + " puta.");
			kamp.donesiDrva(kolicina);
			System.out.println(Thread.currentThread().getName() + " je doneo " + kolicina + " drva.");
			ukupno += kolicina;
		}
		System.out.println(Thread.currentThread().getName() + " je UKUPNO doneo " + ukupno + " drva.");
	}
}












































