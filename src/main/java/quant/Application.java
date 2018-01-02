package quant;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import quant.config.InputParameterSet;
import quant.serviceImpl.LargerThanRule;
import quant.serviceImpl.MathOperatorService2;
import quant.serviceImpl.RESTdataReader;
import quant.serviceImpl.RESTdataReaderV;
import quant.serviceImpl.RuleImplService;
import rx.Observable;
import rx.functions.Action1;
import quant.config.InputParameterSet;
import quant.domain.HisData;
import quant.serviceImpl.CrossDownRule;
import quant.serviceImpl.CrossUpRule;

import quant.serviceImpl.RuleImplService;
import com.google.gson.Gson;

import quant.service.CrossRule;
import quant.config.MyConfiguration;

@SpringBootApplication
@Import(MyConfiguration.class)
@RestController
public class Application {
	
	
	@Autowired
	private CrossRule getCrossUPRule;
	
	@Autowired
	private CrossRule getCrossDownRule;

	@Autowired
	private RuleImplService getRuleImplService;


	@Autowired
	private MathOperatorService2 getMathOperatorService;

	@Autowired
	private InputParameterSet getInputParameterSet;

	@Autowired
	private Map<String, Map<String, Map<String, String>>> initData;

	@Autowired
	private DateTimeFormatter getDateTimeFormatter;

	@Autowired
	private LargerThanRule getLargerThanRule;

	@Autowired
	private List<String> getSymbols;

	@Autowired
	private DateFormat getDateFormat;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    @RequestMapping("/GOOG/SMA")
	public String signal() {
    	
    		Gson gson = new Gson();
    		Map<String, String> data = initData.get("GOOG").get("SMA");
    		String json = gson.toJson(data);
    		return json;
    	
	}
    
    @RequestMapping("/GOOG/KDJ")
	public String KDJ() {
    	
    		Gson gson = new Gson();
    		Map<String, String> data = initData.get("GOOG").get("KDJSIG");
    		String json = gson.toJson(data);
    		return json;
    	
	}
    
    @RequestMapping("/GOOG/WILLIAM")
   	public String WILLIAM() {
       	
       		Gson gson = new Gson();
       		Map<String, String> data = initData.get("GOOG").get("WILLIAMSIG");
       		String json = gson.toJson(data);
       		return json;
       	
   	}
    
    @RequestMapping("/GOOG/BOLL")
   	public String BOLLSIG() {
       	
       		Gson gson = new Gson();
       		Map<String, String> data = initData.get("GOOG").get("BOLLSIG");
       		String json = gson.toJson(data);
       		return json;
       	
   	}
    
    @Bean
	InitializingBean compute() {
		return () -> {
			DateFormat df = getDateFormat;
			//List<String> symbols = getSymbols;
			List<String> symbols = new ArrayList<String>();
			symbols.add("TK");
			InputParameterSet params = getInputParameterSet;
			DateTimeFormatter fmt = getDateTimeFormatter;
			RuleImplService util = getRuleImplService;
			RESTdataReaderV reader = new RESTdataReaderV();
	    	Observable<Pair<DateTime, HisData>> data = reader.readOneSymbol("http://localhost:8090/api/stockhis", "TK");
			MathOperatorService2 mathService = getMathOperatorService;

//			long startTime_ = System.nanoTime();
//
			for (String symbol : symbols) {
				long startTime = System.nanoTime();
				try {

					CrossRule bollRule = new CrossDownRule();
//
					Observable<Pair<DateTime, Double>> close = mathService.CLOSE(data).takeLast(params.getTake_N());
					
					

					Observable<Pair<DateTime, Double>> high = mathService.HIGH(data).takeLast(params.getTake_N());

					Observable<Pair<DateTime, Double>> low = mathService.LOW(data).takeLast(params.getTake_N());

					Observable<Pair<DateTime, Double>> vol = mathService.VOLUME(data).takeLast(params.getTake_N());

					Observable<Pair<DateTime, Double>> hoh = mathService.HOH(high, params.getWILL_N())
							.takeLast(params.getTake_N());

					Observable<Pair<DateTime, Double>> lol = mathService.LOL(low, params.getWILL_N())
							.takeLast(params.getTake_N());

					
					
					Observable<Pair<DateTime, Double>> sma = mathService.SMA(close, params.getSMA_N()).takeLast(params.getTake_N());
					
					
					//Observable<Pair<DateTime, Double>> ema = mathService.EMA(close, params.getSMA_N()).takeLast(params.getTake_N());
					Observable<Pair<DateTime, Double>> rsi = mathService.RSI(close, params.getRSI_N())
							.takeLast(params.getTake_N());

					Observable<Pair<DateTime, Triple<Double, Double, Double>>> boll = mathService.BOLL(close,
							params.getBBAND_N(), params.getBBAND_A());

					Observable<Pair<DateTime, Double>> boll_down = boll
							.map(x -> new ImmutablePair(x.getLeft(), x.getRight().getLeft()));

					Observable<Pair<DateTime, Double>> william = mathService
							.WILLIAM(close, hoh, lol, params.getWILL_N()).takeLast(params.getTake_N());

					Observable<Triple<DateTime, Double, Double>> kdj = mathService.KDJ(close, high, low,
							params.getKDJ_N(), params.getKDJ_A());

					LargerThanRule williamRule = new LargerThanRule();

					williamRule.setClose(william);

					williamRule.setLine1(params.getWillLn1());

					williamRule.setLine2(params.getWillLn2());
					CrossRule kDrule = new CrossUpRule();


					LargerThanRule rsiRule = new LargerThanRule();

					rsiRule.setClose(rsi);

					rsiRule.setLine1(params.getRSI_Line1());

					rsiRule.setLine2(params.getRSI_Line2());

					bollRule.setClose(close.skip(params.getBBAND_N() - 1));

					bollRule.setBase(boll_down);

					kDrule.setClose(kdj.map(x -> new ImmutablePair(x.getLeft(), x.getMiddle())));

					kDrule.setBase(kdj.map(x -> new ImmutablePair(x.getLeft(), x.getRight())));

					Observable<Pair<DateTime, Triple<Double, Double, Double>>> macd = MathOperatorService2.MACD(close,
							params.getMACDN1(), params.getMACDN2(), params.getMACDS());

					LargerThanRule macdRule = new LargerThanRule();

					macdRule.setClose(macd.map(x -> new ImmutablePair(x.getLeft(), x.getRight().getRight())));

					macdRule.setLine1(params.getMacdLine1());

					Observable<Pair<DateTime, Double>> macdSignal = macdRule.runRule(util);

					List<Pair<DateTime, Double>> macdSignalList = macdSignal.toList().toBlocking().single();

					Observable<Pair<DateTime, Double>> williamSignal = williamRule.run2Rule(util);

					Observable<Pair<DateTime, Double>> bollSignal = bollRule.runRule(util);

					Observable<Pair<DateTime, Double>> kdjSignal = kDrule.runRule(util);

					Observable<Pair<DateTime, Double>> rsiSignal = rsiRule.run2Rule(util);

					List<Pair<DateTime, Double>> rsiSignalList = rsiSignal.toList().toBlocking().single();

					List<Pair<DateTime, Double>> williamList = william.toList().toBlocking().single();

					List<Pair<DateTime, Double>> williamSignalList = williamSignal.toList().toBlocking().single();

					List<Pair<DateTime, Double>> bollSignalList = bollSignal.toList().toBlocking().single();

					List<Pair<DateTime, Double>> kdjSignalList = kdjSignal.toList().toBlocking().single();

					List<Pair<DateTime, Triple<Double, Double, Double>>> macdList = macd.toList().toBlocking().single();

					Map<String, String> williamCrossUpMap2 = new TreeMap<String, String>();
					Map<String, String> bollCrossDownMap2 = new TreeMap<String, String>();

					Map<String, String> rsiSigMap = new TreeMap<String, String>();
					Map<String, String> williamMap = new TreeMap<String, String>();

					Map<String, String> macdMap = new TreeMap<String, String>();
					Map<String, String> macdSigMap = new TreeMap<String, String>();

					Map<String, String> kdjCrossMap = new TreeMap<String, String>();

					for (int i = 0; i < rsiSignalList.size(); i++) {
						Pair<DateTime, Double> pair = rsiSignalList.get(i);
						rsiSigMap.put(pair.getKey().toString(fmt), String.format("%.2f", pair.getValue()));
					}

					for (int i = 0; i < kdjSignalList.size(); i++) {
						Pair<DateTime, Double> pair = kdjSignalList.get(i);
						kdjCrossMap.put(pair.getKey().toString(fmt), String.format("%.2f", pair.getValue()));
					}

					for (int i = 0; i < macdSignalList.size(); i++) {
						Pair<DateTime, Double> pair = macdSignalList.get(i);
						macdSigMap.put(pair.getKey().toString(fmt), String.format("%.2f", pair.getValue()));
					}

					for (int i = 0; i < bollSignalList.size(); i++) {
						Pair<DateTime, Double> pair = bollSignalList.get(i);
						bollCrossDownMap2.put(pair.getKey().toString(fmt), String.format("%.2f", pair.getValue()));
					}

					for (int i = 0; i < williamSignalList.size(); i++) {
						Pair<DateTime, Double> pair = williamSignalList.get(i);
						williamCrossUpMap2.put(pair.getKey().toString(fmt), String.format("%.2f", pair.getValue()));
					}

					for (int i = 0; i < williamList.size(); i++) {
						Pair<DateTime, Double> pair = williamList.get(i);
						williamMap.put(pair.getKey().toString(fmt), String.format("%.2f", pair.getValue()));
					}

					for (int i = 0; i < macdList.size(); i++) {
						Pair<DateTime, Triple<Double, Double, Double>> pair = macdList.get(i);
						macdMap.put(pair.getKey().toString(fmt),
								String.format("%.2f", pair.getValue().getLeft()) + ","
										+ String.format("%.2f", pair.getValue().getMiddle()) + ","
										+ String.format("%.2f", pair.getValue().getRight()));
					}


					CrossUpRule smaRule = new CrossUpRule();

					smaRule.setClose(close);

					smaRule.setBase(sma);
					
					smaRule.setN(params.getSMA_N());

					Observable<Pair<DateTime, Double>> smaSignal = smaRule.runRule2(util );



					//List<Pair<DateTime, Double>> smaList = william.toList().toBlocking().single();

					List<Pair<DateTime, Double>> smaSignalList = smaSignal.toList().toBlocking().single();




					Map<String, String> SMACrossUpMap = new TreeMap<String, String>();


					


					for (int i = 0; i < smaSignalList.size(); i++) {
						Pair<DateTime, Double> pair = smaSignalList.get(i);
						SMACrossUpMap.put(pair.getKey().toString(fmt), String.format("%.2f", pair.getValue()));
					}


					Map<String, Map<String, String>> ruleMap = new HashMap<String, Map<String, String>>();


					ruleMap.put("SMA", SMACrossUpMap);
					ruleMap.put("WILLIAM", williamMap);
					ruleMap.put("WILLIAMSIG", williamCrossUpMap2);
					ruleMap.put("BOLLSIG", bollCrossDownMap2);
					ruleMap.put("MACD", macdMap);
					ruleMap.put("MACDSIG", macdSigMap);
					ruleMap.put("KDJSIG", kdjCrossMap);

					initData.put(symbol, ruleMap);

				} catch (Exception e) {
					e.printStackTrace();
				}
				;

				long endtime = System.nanoTime();
				System.out.println(symbol + " run with " + (endtime - startTime));

			}
		};
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }

}