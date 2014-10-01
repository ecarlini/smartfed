package it.src.isti.smartfed.federation.mapping;


import it.src.isti.smartfed.federation.utils.GeneratorsRepeatability;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AllocRepeatabilityTest.class, ApplicationEdgeTest.class, GeneratorsRepeatability.class, SingleApplicationTest.class,
	ThreeTierSTRATOSApplicationTest.class})
public class AllocatorSuite {

	public AllocatorSuite() {
		// TODO Auto-generated constructor stub
	}

}
