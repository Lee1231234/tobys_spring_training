package com.example.spring;

import com.example.spring.user.domain.Level;
import com.example.spring.user.domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
    User user;

    public UserTest() {
    }

    @Before
    public void setUp() {
        this.user = new User();
    }

    @Test
    public void upgradeLevel() {
        Level[] levels = Level.values();
        Level[] var5 = levels;
        int var4 = levels.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Level level = var5[var3];
            if (level.nextLevel() != null) {
                this.user.setLevel(level);
                this.user.upgradeLevel();
                Assert.assertThat(this.user.getLevel(), CoreMatchers.is(level.nextLevel()));
            }
        }

    }

    @Test(
            expected = IllegalStateException.class
    )
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        Level[] var5 = levels;
        int var4 = levels.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Level level = var5[var3];
            if (level.nextLevel() == null) {
                this.user.setLevel(level);
                this.user.upgradeLevel();
            }
        }

    }
}