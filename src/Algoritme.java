import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

class Algoritme {
	// 
	double CurrentTime = 0;
	int i = 0;
	double CurrentHighestRR = 0;
	int IndexCurrentHighestRR = 0;
	int tempteller = 0;
	double tempRR;
	boolean wachtendProces;
	boolean eindProcessen;
	ArrayList<Process> processenLijstH = new ArrayList<>();

	

	// altijd
	private int clock;
	private int aantalProc;
	private int percentiel;
	private double normOmloopPPC;
	private double wachtPPC;
	private double gemOmloop;
	private double gemNormOmloop;
	private double gemWacht;
	// RR
	private int q;
	private int RT;

	// SJN
	private boolean legePrio;

	// RR en SRT
	private boolean stoppen;

	// RR en SJN
	private Process huidigProc;

	// overal
	private Process p;

	// RR
	private ArrayList<Process> processVolgorde;

	// RR en SRT
	private Comparator<Process> ATcomp;
	private Comparator<Process> RTcomp;

	// overal
	private PriorityQueue<Process> processenLijst;
	// SJN
	private PriorityQueue<Process> prioriteitLijst;

	// RR en SRT
	private PriorityQueue<Process> ATqueue;
	private PriorityQueue<Process> RTqueue;

	// constructor
	public Algoritme(int aantalProc, int q) {
		clock = 0;
		this.aantalProc = aantalProc;
		percentiel = aantalProc / 100;
		normOmloopPPC = 0;
		wachtPPC = 0;
		gemOmloop = 0;
		gemNormOmloop = 0;
		gemWacht = 0;
		this.q = q;
		RT = 0;
		legePrio = false;
		stoppen = false;
		huidigProc = new Process();
		processVolgorde = new ArrayList<Process>();
		RTcomp = new RemainingTimeComparator();
		ATcomp = new ArrivalTimeComparator();
		processenLijst = new PriorityQueue<Process>();
		prioriteitLijst = new PriorityQueue<Process>();
		ATqueue = new PriorityQueue<Process>((int) aantalProc, ATcomp);
		RTqueue = new PriorityQueue<Process>((int) aantalProc, RTcomp);
	}

	public double getNormOmloopPPC() {
		return normOmloopPPC;
	}

	public void setNormOmloopPPC(double normOmloopPPC) {
		this.normOmloopPPC = normOmloopPPC;
	}

	public double getWachtPPC() {
		return wachtPPC;
	}

	public void setWachtPPC(double wachtPPC) {
		this.wachtPPC = wachtPPC;
	}

	public double getGemOmloop() {
		return gemOmloop;
	}

	public void setGemOmloop(double gemOmloop) {
		this.gemOmloop = gemOmloop;
	}

	public double getGemNormOmloop() {
		return gemNormOmloop;
	}

	public void setGemNormOmloop(double gemNormOmloop) {
		this.gemNormOmloop = gemNormOmloop;
	}

	public double getGemWacht() {
		return gemWacht;
	}

	public void setGemWacht(double gemWacht) {
		this.gemWacht = gemWacht;
	}

	public void inlezenFCFS(int at, int pid, int st) {
		if (at > clock) {
			clock = at;
		}

		p = new Process(pid, at, st);

		clock += st;
		p.setEindtijd(clock);

		gemOmloop += p.setOmlooptijd();
		gemNormOmloop += p.setNormOmloopTijd();
		gemWacht += p.setWachtTijd();

		processenLijst.add(p);
	}

	public void inlezenSJN(int at, int pid, int st, int temp) {
		p = new Process(pid, at, st);
		processenLijst.add(p);

		if (temp == 0) {
			clock = at;
			clock += st;
			huidigProc = p;
			huidigProc.setEindtijd(clock);
			gemOmloop += huidigProc.setOmlooptijd();
			gemNormOmloop += huidigProc.setNormOmloopTijd();
			gemWacht += huidigProc.setWachtTijd();
		}

		if (at < clock) {
			prioriteitLijst.add(p);
		}

		else if (at == clock) {
			prioriteitLijst.add(p);
			huidigProc = prioriteitLijst.poll();
			clock += huidigProc.getServiceTime();
			huidigProc.setEindtijd(clock);
			gemOmloop += huidigProc.setOmlooptijd();
			gemNormOmloop += huidigProc.setNormOmloopTijd();
			gemWacht += huidigProc.setWachtTijd();
		} else {
			while (at > clock) {
				if (prioriteitLijst.peek() != null) {
					huidigProc = prioriteitLijst.poll();
					clock += huidigProc.getServiceTime();
					huidigProc.setEindtijd(clock);
					gemOmloop += huidigProc.setOmlooptijd();
					gemNormOmloop += huidigProc.setNormOmloopTijd();
					gemWacht += huidigProc.setWachtTijd();
				} else {
					huidigProc = p;
					clock = at + st;
					huidigProc.setEindtijd(clock);
					legePrio = true;
					gemOmloop += huidigProc.setOmlooptijd();
					gemNormOmloop += huidigProc.setNormOmloopTijd();
					gemWacht += huidigProc.setWachtTijd();
				}
			}
			if (!legePrio) {
				prioriteitLijst.add(p);
			}
			legePrio = false;
		}
	}

	public double RRalt(int i, double CT) {
		double rate = ((CT - processenLijstH.get(i).getArrivalTime()) + processenLijstH.get(i).getServiceTime())
				/ processenLijstH.get(i).getServiceTime();
		return rate;
	}

	// Hrrn
	public void HRRN() {
		while (processenLijstH.size() > 0) {
			i = 0;
			CurrentHighestRR = 0;
			wachtendProces = false;
			eindProcessen = false;
			while (processenLijstH.get(i).getArrivalTime() <= CurrentTime && !eindProcessen) {

				wachtendProces = true;
				tempRR = RRalt(i, CurrentTime);
				if (tempRR > CurrentHighestRR) {
					CurrentHighestRR = tempRR;
					IndexCurrentHighestRR = i;
				}
				if (i < processenLijstH.size() - 1)
					i++;
				else
					eindProcessen = true;
			}
			if (!wachtendProces) {
				CurrentTime++;
			} else {
				tempteller++;

				Process temp = processenLijstH.get(IndexCurrentHighestRR);
				temp.setStartTimeAlt(CurrentTime);

				gemOmloop += temp.setOmlooptijd();
				gemNormOmloop += temp.setNormOmloopTijd();
				gemWacht += temp.setWachtTijd();
				CurrentTime = CurrentTime + processenLijstH.get(IndexCurrentHighestRR).getServiceTime();
				processVolgorde.add(temp);
				processenLijstH.remove(IndexCurrentHighestRR);

			}
		}

		System.out.println(processVolgorde.size());
		for (int i = 0; i < processVolgorde.size(); i++) {
			// System.out.println(processVolgorde.get(i).pid);
			processenLijst.add(processVolgorde.get(i));
		}
	}

	public void inlezenHRRN(int at, int pid, int st) {
		p = new Process(pid, at, st);
		processenLijstH.add(p);
	}

	// FB
	public void FB() {

		stoppen = false;

		ArrayList<Process> Q1 = new ArrayList<>();
		ArrayList<Process> Q2 = new ArrayList<>();
		ArrayList<Process> Q3 = new ArrayList<>();
		
		int RT1;
		int RT2;
		int RT3;
		int q1 = 100;
		int q2 = 200;
		int q3 = 400;

		while (ATqueue.size() != 0) {

			if (ATqueue.size() != 0) {
				stoppen = false;
				while (!stoppen) {
					if (ATqueue.element().getArrivalTime() < clock) {

						Q1.add(ATqueue.remove());
						if (ATqueue.size() == 0) {
							stoppen = true;
						}

					} else {
						stoppen = true;
					}
				}
			}
			if (Q1.size() > 0) {
				huidigProc = Q1.get(0);
				RT1 = Q1.get(0).processed(q1);

				// System.out.println("Q1 "+Q1.size());
				if (RT1 <= 0) {
					clock += q1 + RT1;

					Q1.get(0).setEindtijd(clock);
					processenLijst.add(Q1.get(0));
					Q1.remove(0);
					huidigProc = null;

				} else {
					clock += q1;
					Q2.add(Q1.get(0));
					Q1.remove(0);
				}

			} else if (Q1.size() == 0 && Q2.size() > 0) {
				huidigProc = Q2.get(0);
				RT2 = Q2.get(0).processed(q2);

				if (RT2 <= 0) {
					clock += q2 + RT2;
					Q2.get(0).setEindtijd(clock);
					processenLijst.add(Q2.get(0));
					Q2.remove(0);
					huidigProc = null;

				} else {
					clock += q2;
					Q3.add(Q2.get(0));
					Q2.remove(0);
				}

			} else if (Q1.size() == 0 && Q2.size() == 0 && Q3.size() > 0) {
				huidigProc = Q3.get(0);
				RT3 = Q3.get(0).processed(q3);

				if (RT3 <= 0) {
					clock += q3 + RT3;
					Q3.get(0).setEindtijd(clock);
					processenLijst.add(Q3.get(0));
					Q3.remove(0);
					huidigProc = null;

				} else {
					clock += q3;
					Q3.add(Q3.get(0));
					Q3.remove(0);
				}
			}
			clock++;

		}
		while(Q1.size()!=0){
	
			RT1 =Q1.get(0).processed(q1);
			if (RT1 <= 0){
				clock+=q1 +RT1;
				Q1.get(0).setEindtijd(clock);
				processenLijst.add(Q1.get(0));
				Q1.remove(0);
			}
			else {
				clock += q1;
				Q2.add(Q1.get(0));
				Q1.remove(0);
			}			
		}
		while(Q2.size()!=0){
			
			RT2 =Q2.get(0).processed(q2);
			if (RT2 <= 0){
				clock+=q2 +RT2;
				Q2.get(0).setEindtijd(clock);
				processenLijst.add(Q2.get(0));
				Q2.remove(0);
			}
			else {
				clock += q2;
				Q3.add(Q2.get(0));
				Q2.remove(0);
			}			
		}
		while(Q3.size()!=0){
			RT3 = Q3.get(0).processed(q3);

			if (RT3 <= 0) {
				clock += q3 + RT3;
				Q3.get(0).setEindtijd(clock);
				processenLijst.add(Q3.get(0));
				Q3.remove(0);
				huidigProc = null;

			} else {
				clock += q3;
				Q3.add(Q3.get(0));
				Q3.remove(0);
			}
		}
	}

	public void inlezenAT(int at, int pid, int st) {
		p = new Process(pid, at, st, st);
		ATqueue.add(p);
	}

	public void SJN() {
		for (int i = 0; i < prioriteitLijst.size(); i++) {
			huidigProc = prioriteitLijst.poll();
			clock += huidigProc.getServiceTime();
			huidigProc.setEindtijd(clock);

			gemOmloop += huidigProc.setOmlooptijd();
			gemNormOmloop += huidigProc.setNormOmloopTijd();
			gemWacht += huidigProc.setWachtTijd();

		}
	}

	public void SRT() {
		clock = ATqueue.peek().arrivalTime;
		while (ATqueue.size() != 0 || RTqueue.size() != 0) {
			if (ATqueue.size() != 0) {
				stoppen = false;
				while (!stoppen) {
					if (ATqueue.element().getArrivalTime() < clock) {
						RTqueue.add(ATqueue.remove());
						if (ATqueue.size() == 0) {
							stoppen = true;
						}
					} else {
						stoppen = true;
					}
				}
			}

			if (RTqueue.size() != 0) {
				if (RTqueue.element().processed()) {
					RTqueue.element().setEindtijd(clock);
					processenLijst.add(RTqueue.remove());

				}
			}
			clock++;

		}

	}

	public void RR() {
		clock = ATqueue.peek().arrivalTime;
		huidigProc = null;
		while (ATqueue.size() != 0 || processVolgorde.size() != 0 || huidigProc != null) {

			// Kijken voor nieuwe aangekomen processen
			if (ATqueue.size() != 0) {
				stoppen = false;
				while (!stoppen) {
					if (ATqueue.element().getArrivalTime() < clock) {

						processVolgorde.add(ATqueue.remove());
						if (ATqueue.size() == 0) {
							stoppen = true;
						}

					} else {
						stoppen = true;
					}
				}
			}
			if (huidigProc != null) {

				processVolgorde.add(huidigProc);

			}

			if (processVolgorde.size() != 0) {
				huidigProc = processVolgorde.get(0);
				RT = huidigProc.processed(q);
				if (RT <= 0) {

					clock += q + RT;
					processenLijst.add(huidigProc);
					huidigProc.setEindtijd(clock);
					huidigProc = null;

				} else {

					clock += q;
				}
				processVolgorde.remove(0);

			} else {
				clock++;
			}
		}

	}

	public void berekenGemiddelde() {
		gemOmloop += processenLijst.peek().setOmlooptijd();
		gemNormOmloop += processenLijst.peek().setNormOmloopTijd();
		gemWacht += processenLijst.peek().setWachtTijd();
	}

	public void berekenGrafieken() {
		normOmloopPPC += processenLijst.peek().getNormOmloopTijd();
		wachtPPC += processenLijst.poll().getWachtTijd();
	}

	public void resetPPC() {
		normOmloopPPC = 0;
		wachtPPC = 0;
	}

	public PriorityQueue<Process> getProcessenLijst() {
		return processenLijst;
	}
}