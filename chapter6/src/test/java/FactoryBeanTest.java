import com.example.spring.factorybean.Message;
import com.example.spring.factorybean.MessageFactoryBean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {"/FactoryBeanTest_context.xml"}
)
public class FactoryBeanTest {
    @Autowired
    ApplicationContext context;

    public FactoryBeanTest() {
    }

    @Test
    public void getMessageFromFactoryBean() {
        Object message = this.context.getBean("message");
        Assert.assertThat(message, CoreMatchers.is(Message.class));
        Assert.assertThat(((Message)message).getText(), CoreMatchers.is("Factory Bean"));
    }

    @Test
    public void getFactoryBean() throws Exception {
        Object factory = this.context.getBean("&message");
        Assert.assertThat(factory, CoreMatchers.is(MessageFactoryBean.class));
    }
}