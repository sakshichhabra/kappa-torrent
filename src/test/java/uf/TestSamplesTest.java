package uf;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uf.TestSamples;

public class TestSamplesTest {
  @Test
  public void testGetTestString() {
    TestSamples testSamplesTest = new TestSamples();
    String testString = testSamplesTest.getTestString();
    Assert.assertEquals(testString, "TestString");
  }

  @Test
  public void mockGetTestString() {
    TestSamples testSamples = Mockito.mock(TestSamples.class);
    Mockito.when(testSamples.getTestString()).thenReturn("mockString");
    String ret = testSamples.getTestString();
    Assert.assertEquals(ret, "mockString");
  }

  @Test
  public void mockTestSampleCreation(){
    //setup mock object
    TestSamples testSamples = Mockito.mock(TestSamples.class);
    Mockito.when(testSamples.getTestString()).thenReturn("mockString");

    //setup getInstance
    Mockito.mockStatic(TestSamples.class);
    Mockito.when(TestSamples.getInstance()).thenReturn(testSamples);

    //Act
    TestSamples mockTestSamples = TestSamples.getInstance();
    String ret = mockTestSamples.getTestString();

    //Assert
    Assert.assertEquals(ret, "mockString");
  }
}
