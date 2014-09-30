package it.cnr.isti.smartfed.junit;


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
