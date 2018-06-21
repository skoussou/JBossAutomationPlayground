package com.skoussou.brms;

public class Moorings {

	private int totalMoorings;
	private int seaworthyMoorings;

	
	public Moorings(int totalMoorings, int seaworthyMoorings) {
		super();
		this.totalMoorings = totalMoorings;
		this.seaworthyMoorings = seaworthyMoorings;
		
	}


	public int getTotalMoorings() {
		return totalMoorings;
	}


	public void setTotalMoorings(int totalMoorings) {
		this.totalMoorings = totalMoorings;
	}


	public int getSeaworthyMoorings() {
		return seaworthyMoorings;
	}


	public void setSeaworthyMoorings(int seaworthyMoorings) {
		this.seaworthyMoorings = seaworthyMoorings;
	}

	
}
