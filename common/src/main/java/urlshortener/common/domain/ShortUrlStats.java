package urlshortener.common.domain;

public class ShortUrlStats {

	private String hash;
	private Integer rtime_average, rtime_number, last_rtime, d_time, stime_average, stime_number;

	public ShortUrlStats(String hash, Integer rtime_average, Integer rtime_number, Integer last_rtime, Integer d_time, Integer stime_average, Integer stime_number) {
		this.hash = hash;
		this.rtime_average = rtime_average;
		this.rtime_number = rtime_number;
		this.last_rtime = last_rtime;
		this.d_time = d_time;
		this.stime_average = stime_average;
		this.stime_number = stime_number;
	}

	public String getHash() {
		return hash;
	}

	public Integer getRtime_average() {
		return rtime_average;
	}

	public Integer getRtime_number() {
		return rtime_number;
	}

	public Integer getLast_rtime() {
		return last_rtime;
	}

	public Integer getD_time() {
		return d_time;
	}

	public Integer getStime_average() {
		return stime_average;
	}

	public Integer getStime_number() {
		return stime_number;
	}

	public String calculateAverageResponseTime(){
		if(rtime_number == 0){
			return "N/A";
		} else return String.format("%.2f ms",((double) rtime_average)/((double) rtime_number));
	}

	public String calculateAverageServiceTime(){
		if(stime_number == 0){
			return "N/A";
		} else return String.format("%.2f ms",((double) stime_average)/((double) stime_number));
	}

	@Override
	public String toString() {
		return "ShortUrlStats{" +
				"hash='" + hash + '\'' +
				", rtime_average=" + rtime_average +
				", rtime_number=" + rtime_number +
				", last_rtime=" + last_rtime +
				", d_time=" + d_time +
				", stime_average=" + stime_average +
				", stime_number=" + stime_number +
				'}';
	}
}
