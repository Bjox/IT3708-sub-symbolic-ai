package project4.ea.selection;

import java.util.ArrayList;
import project4.Util;

/**
 *
 * @author Bjørnar W. Alvestad
 * @param <E>
 */
public class RouletteWheel<E> {
	
	private final ArrayList<Entry> entries;
	
	private double globalValue;
	
	
	public RouletteWheel(int size) {
		entries = new ArrayList<>(size);
		
		globalValue = 0.0;
	}
	
	public void addElement(E element, double value) {
		globalValue += value;
		entries.add(new Entry(element, value));
	}
	
	public E spin(boolean remove) {
		if (entries.isEmpty())
			throw new RuntimeException("No entries in RouletteWheel.");
		
		double r = Util.randomDouble() * globalValue;
		double cumulativeValue = 0.0;
		Entry e = entries.get(0);
		
		for (int i = 0; i < entries.size(); i++) {
			e = entries.get(i);
			cumulativeValue += e.value;
			
			if (r <= cumulativeValue) {
				if (remove) {
					entries.remove(i);
					globalValue -= e.value;
				}
				break;
			}
		}
		
		return (E)e.o;
	}
	
	public int getNumEntries() {
		return entries.size();
	}
	
	
	class Entry {
		public Object o;
		public double value;

		public Entry(Object o, double value) {
			this.o = o;
			this.value = value;
		}

		@Override
		public String toString() {
			return o.toString() + ", value=" + value; 
		}
		
	}
	
}
