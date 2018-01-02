package quant.domain;

import java.math.BigDecimal;

import org.joda.time.DateTime;

//{"symbol":"TK","date":"2017-12-01","companyName":"Teekay Corporation","close":8.320000,"open":8.400000,"vol":520200,"adj":8.320000,"high":8.520000,"low":8.200000}

public class HistData {
	
	private String symbol;
	
	private String date;
	
	private String companyName;
	
	private BigDecimal open;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private BigDecimal close;
	
	private BigDecimal adj;
	
	private long vol;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getOpenPrice() {
		return open;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.open = openPrice;
	}

	public BigDecimal getHighPrice() {
		return high;
	}

	public void setHighPrice(BigDecimal highPrice) {
		this.high = highPrice;
	}

	public BigDecimal getLowPrice() {
		return low;
	}

	public void setLowPrice(BigDecimal lowPrice) {
		this.low = lowPrice;
	}

	public BigDecimal getClosePrice() {
		return close;
	}

	public void setClosePrice(BigDecimal closePrice) {
		this.close = closePrice;
	}

	public BigDecimal getAdjClosePrice() {
		return adj;
	}

	public void setAdjClosePrice(BigDecimal adjClosePrice) {
		this.adj = adjClosePrice;
	}

	public long getVolume() {
		return vol;
	}

	public void setVolume(long volume) {
		this.vol = volume;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
		

}