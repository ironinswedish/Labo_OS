
public class Process implements Comparable<Process> {
	int pid;
	int arrivalTime;
	int serviceTime;
	int remainingTime;
	int startTime;
	int eindtijd = 0;
	int omloopTijd;
	int wachtTijd;
	int normOmloopTijd;
	int distributionTime = 0;

	public Process() {

	}

	public Process(int a, int b, int c) {
		pid = a;
		arrivalTime = b;
		serviceTime = c;
		// remainingTime = d;
	}

	public Process(int a, int b, int c, int d) {
		pid = a;
		arrivalTime = b;
		serviceTime = c;
		remainingTime = d;
	}
	
	public boolean FIFODistribution() {

		if (distributionTime < 4000) {
			distributionTime++;
			return false;
		} else {
			return true;
		}
	}

	public boolean addDistributionTime1() {
		if (distributionTime < 100) {
			distributionTime++;
			return false;
		} else {
			return true;
		}

	}

	public boolean addDistributionTime2() {
		if (distributionTime < 200) {
			distributionTime++;
			return false;
		} else {
			return true;
		}

	}

	public boolean checkEnd() {
		if (distributionTime == serviceTime) {
			return true;
		} else
			return false;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setStartTime(double currentTime) {
		startTime = (int) currentTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getServiceTime() {
		return serviceTime;
	}

	public void setEindtijd(int i) {
		eindtijd = i;
	}

	public int getEindtijd() {
		return eindtijd;
	}

	public int setOmlooptijd() {
		omloopTijd = eindtijd - arrivalTime;
    //    System.out.println("ot"+omloopTijd+" e: "+eindtijd+ " a: "+arrivalTime);
        
		return omloopTijd;
	}

	public int setWachtTijd() {
		wachtTijd = eindtijd - arrivalTime - serviceTime;
		return wachtTijd;
	}

	public int setNormOmloopTijd() {
		normOmloopTijd = (eindtijd - arrivalTime) / serviceTime;
		return normOmloopTijd;
	}

	public int getWachtTijd() {
		return wachtTijd;
	}

	public int getNormOmloopTijd() {
		return normOmloopTijd;
	}

	public int getOmlooptijd() {
		return omloopTijd;
	}
	public void setStartTimeAlt(double s) {
        startTime = (int) s;
        setEindtijd((int)s+serviceTime);
    }

	@Override
	public int compareTo(Process proces) {
		return this.getServiceTime() - proces.getServiceTime();
	}

	public boolean processed() {
		remainingTime--;
		if (remainingTime > 0) {
			return false;
		} else {
			return true;
		}
	}

	public int processed(int q) {
		remainingTime -= q;
		return remainingTime;
	}

}