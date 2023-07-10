package it.polito.tdp.yelp.model;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private List<String> citta;
	private List<Business> locali;
	private Graph<Review,DefaultWeightedEdge> grafo;
	
	public Model() {
		
		this.dao = new YelpDao();
		this.citta = this.dao.getCitta();
		
	}


	public Graph<Review, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}



	public void creaGrafo(String citta, Business locale) {
		clearGraph();
		Graphs.addAllVertices(this.grafo, this.dao.getVertices(locale));
		for (Review r1 : this.grafo.vertexSet()) {
			for (Review r2: this.grafo.vertexSet()) {
				if (!r1.equals(r2)) {
					if (r2.getDate().isAfter(r1.getDate())) {
						long giorni = ChronoUnit.DAYS.between(r1.getDate(), r2.getDate()) ;
						if (giorni!=0) {
							Graphs.addEdge(this.grafo, r1, r2, giorni);
						}
					}
				}
			}
		}
	}


	private void clearGraph() {
		this.grafo = new SimpleDirectedWeightedGraph<Review,DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}


	public List<Business> getLocaliCitta(String citta) {
		this.locali = new ArrayList<Business>(this.dao.getLocaliCitta(citta));
		Collections.sort(locali);
		return this.locali;
	}


	public List<String> getCitta() {
		this.citta = new ArrayList<String>(this.dao.getCitta());
		Collections.sort(citta);
		return citta;
	}
	
	public Set<Review> getMaxArchi(){
		int nUscenti = 0;
		Set<Review> reces = new HashSet<Review>();
		for (Review r: this.grafo.vertexSet()) {
			int n = this.grafo.outgoingEdgesOf(r).size();
			if(n>nUscenti) {
				nUscenti = n;
			}
		}
		for (Review r1: this.grafo.vertexSet()) {
			if(this.grafo.outgoingEdgesOf(r1).size()==nUscenti) {
				reces.add(r1);
			}
		}
		
		return reces;
	}
	
}
