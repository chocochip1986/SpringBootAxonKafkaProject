package axon.config.general;

import axon.aggregate.item.WeaponAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

@Configuration
public class GeneralConfig {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    GenericApplicationContext context;

    @Autowired
    public void weaponAggregate() {
        applicationContext.getApplicationName();

    }
}
