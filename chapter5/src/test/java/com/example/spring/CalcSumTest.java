package com.example.spring;

import com.example.spring.template.Calculator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CalcSumTest {
    Calculator calculator;
    String numFilepath;

    public CalcSumTest() {
    }

    @Before
    public void setUp() {
        this.calculator = new Calculator();
        this.numFilepath = this.getClass().getResource("/numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        Assert.assertThat(this.calculator.calcSum(this.numFilepath), CoreMatchers.is(10));
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        Assert.assertThat(this.calculator.calcMultiply(this.numFilepath), CoreMatchers.is(24));
    }

    @Test
    public void concatenateStrings() throws IOException {
        Assert.assertThat(this.calculator.concatenate(this.numFilepath), CoreMatchers.is("1234"));
    }
}