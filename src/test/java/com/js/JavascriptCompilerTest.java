package com.example;

import com.example.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Assert;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class JavascriptCompilerTest {
  private Compiler compiler;
  private String jsContent;

  @Before
  public void setUp() throws IOException {
    compiler = new Compiler();
    jsContent = new String(Files.readAllBytes(Paths.get("./main.js")));
  }

  @Test
  public void testCompile() {
    String result = compiler.execute(jsContent);
    assertEquals(jsContent, result);
  }
}
