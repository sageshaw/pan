import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNGSimpleTest {

    @Test
    public void testAdd() {
        String str = "TestNG works!";
        Assert.assertEquals(str, "TestNG works!");
    }
}
