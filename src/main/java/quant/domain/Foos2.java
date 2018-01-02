package quant.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Foos2 {

	@JsonIgnore
	private List<HistData> properties = new ArrayList<HistData>();

	@JsonAnyGetter
	public List<HistData> getProperties() {
	return this.properties;
	}

	@JsonAnySetter
	public void setProperty(List<HistData> value) {
	this.properties=value;
	}

	}