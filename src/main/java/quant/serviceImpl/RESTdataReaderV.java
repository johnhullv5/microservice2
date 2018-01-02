package quant.serviceImpl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import quant.domain.Foo;
import quant.domain.Foos;
import quant.domain.Foos2;
import quant.domain.HisData;
import quant.domain.HistData;
import quant.domain.HistoryStock;
import quant.domain.Stock;
import quant.service.DataReader;
import rx.Observable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RESTdataReaderV implements DataReader{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public  Observable<Pair<DateTime, HisData>> readOneSymbol(String restUrl, String symbol) throws ParseException {
		// TODO Auto-generated method stub
		String completeUrl = restUrl+"/"+symbol;
		System.out.println(completeUrl);
		ObjectMapper mapper = new ObjectMapper();
		//Foos2 o = new RestTemplate().getForObject(completeUrl, Foos2.class);
		try {
			//List<HistData> myObjects = mapper.readValue(completeUrl, new TypeReference<List<HistData>>(){});
			//org.springframework.boot.json.JsonParser springParser = JsonParserFactory.getJsonParser();
			//Foos2 o = new RestTemplate().getForObject(completeUrl, Foos2.class);
			//Map<String, Object> result = springParser.parseMap(new RestTemplate().getRequestFactory().);
			
			ResponseEntity < List<HistoryStock>> response = new RestTemplate().exchange(completeUrl,
				    HttpMethod.GET, null, new ParameterizedTypeReference < List<HistoryStock>> () {});

			List<HistoryStock> country = response.getBody(); 
			List<Pair<DateTime, HisData>> list = new ArrayList<Pair<DateTime, HisData>>();
			for (HistoryStock data : country) {
			DateTime t = new DateTime(DATE_FORMAT.parse(data.getDate()));
			HisData d = new HisData();
			d.setSymbol(data.getSymbol());
			d.setDate(t);
			d.setClosePrice(data.getClose().toString());
			d.setAdjClosePrice(data.getAdj().toString());
			d.setHighPrice(data.getHigh().toString());
			d.setLowPrice(data.getLow().toString());
			d.setOpenPrice(data.getOpen().toString());
			d.setVolume(Long.valueOf(data.getVol()).toString());
			
			Pair<DateTime, HisData> p = new ImmutablePair(t, d);
			list.add(p);
			}
			//SortedMap<DateTime, HisData> result = new TreeMap<DateTime,HisData>();
			return Observable.from(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		//List<HistData> symbols = o.getProperties();
		
		
		
//		while(symbols.hasNext())
//		{
//			String s = symbols.next();
//			List<Foo> f = o.getProperties().get(s);
//			for(Foo d : f)
//			{
//				HisData data = new HisData();
//				DateTime t = new DateTime(DATE_FORMAT.parse(d.getDate()));
//				data.setDate(t);
//				data.setAdjClosePrice(d.getAdj());
//				data.setClosePrice(d.getClose());
//				data.setHighPrice(d.getHigh());
//				data.setLowPrice(d.getLow());
//				data.setOpenPrice(d.getOpen());
//				data.setSymbol(d.getSymbol());
//				data.setVolume(d.getVol());
//				if (!result.keySet().contains(t)) {
//					result.put(t, data);
//				}
//				
//			}
//		}
		
		
		
		

//		for (Iterator<DateTime> i = result.keySet().iterator(); i.hasNext();) {
//			DateTime item = i.next();
//			
//			Pair<DateTime, HisData> p = new ImmutablePair(item, result.get(item));
//			list.add(p);
//		}
		
	}
	
	
	
} 