/*
Copyright 2014 ISTI-CNR
 
This file is part of SmartFed.

SmartFed is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
SmartFed is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with SmartFed. If not, see <http://www.gnu.org/licenses/>.

*/

package it.cnr.isti.smartfed.federation.generation;

public enum GenerationType 
{
	/* Generates a single value for every ranges.
	 */
	UNIFORM,
	
	/* Generates a random value for each of the range. 
	 */
	NON_UNIFORM;
	
	@Override
	  public String toString() {
	    switch(this) {
	      case UNIFORM: return "uniform";
	      case NON_UNIFORM: return "nonuniform";
	      default: throw new IllegalArgumentException();
	    }
	  }
}
