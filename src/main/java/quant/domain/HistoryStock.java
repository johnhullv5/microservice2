package quant.domain;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@Component("StockServiceHistory")
public class HistoryStock extends Stock{
	
	private BigDecimal close;
	private BigDecimal open;
	private long vol;
	private BigDecimal adj;
	private BigDecimal high;
	private BigDecimal low;
	
	public HistoryStock() {
		
	}
	
	public HistoryStock(String symbol, String companyName, BigDecimal open, BigDecimal close, Long volume, BigDecimal adjClose,
			BigDecimal high, BigDecimal low, String date) {
		this.symbol = symbol;
		this.setCompanyName(companyName);
		this.open = open;
		this.close = close;
		this.vol = volume;
		this.adj = adjClose;
		this.high = high;
		this.low = low;
		this.date = date;

	}
	
	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public long getVol() {
		return vol;
	}

	public void setVol(long vol) {
		this.vol = vol;
	}

	public BigDecimal getAdj() {
		return adj;
	}

	public void setAdj(BigDecimal adj) {
		this.adj = adj;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public List<Stock> getHistoryStockInterval(String quote, Calendar from, Calendar to, Interval interval) {

		List<HistoricalQuote> history = null;
		try {
			history =  (YahooFinance.get(quote, from, to, interval)).getHistory();
		} catch (IOException e) {
			logger.error(e.toString());
			logger.error(e.getMessage());
		}
		List<Stock> historyStocks = new ArrayList<Stock>();
		for(HistoricalQuote historyQuote : history) {
			symbol = historyQuote.getSymbol();
			
			try {
				historyStocks.add(new HistoryStock(symbol, YahooFinance.get(symbol).getName(), historyQuote.getOpen(), 
						historyQuote.getClose(), historyQuote.getVolume(),
						historyQuote.getAdjClose(), historyQuote.getHigh(), historyQuote.getLow(),
						new SimpleDateFormat("yyyy-MM-dd").format(historyQuote.getDate().getTime())));
			} catch (IOException e) {
				logger.error(e.toString());
				logger.error(e.getMessage());
			}
		}
	
		return historyStocks;
	}
	
	public List<Stock> getHistoryStock(String quote) {
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.MONTH, -1);
		return getHistoryStockInterval(quote, from, to, Interval.DAILY);
	}

}
